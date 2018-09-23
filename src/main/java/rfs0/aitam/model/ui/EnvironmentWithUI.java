package rfs0.aitam.model.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import com.vividsolutions.jts.geom.Coordinate;

import bsh.ParseException;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import rfs0.aitam.model.Individual;
import rfs0.aitam.utilities.GeometryUtility;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.MasonGeometry;

public class EnvironmentWithUI extends GUIState {

	private Display2D m_display;
	private JFrame m_displayFrame;

	private GeomVectorFieldPortrayal m_buildingsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal m_pathsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal m_agentPortrayal = new GeomVectorFieldPortrayal();

	public EnvironmentWithUI() throws ParseException {
		super(new Environment(System.currentTimeMillis()));
	}

	public EnvironmentWithUI(SimState state) {
		super(state);
	}

	public EnvironmentWithUI(long seed) throws ParseException {
		super(new Environment(seed));
	}

	@Override
	public void init(Controller controller) {
		super.init(controller);
		m_display = new Display2D(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT, this);
		m_display.setMouseChangesOffset(true);
		m_display.attach(m_buildingsPortrayal, "Buildings", true);
		m_display.attach(m_pathsPortrayal, "Pedestrian Paths", true);
		m_display.attach(m_agentPortrayal, "Agents", true);

		m_displayFrame = m_display.createFrame();
		controller.registerFrame(m_displayFrame);
		m_displayFrame.setVisible(true);
	}

	@Override
	public void start() {
		super.start();
		setupPortrayals();
		m_display.reset();
		m_display.setBackdrop(ISimulationSettings.COLOR_OF_BACKGROUND);
		m_display.repaint();
	}

	public static void main(String[] args) {
		EnvironmentWithUI environmentGui = null;
		try {
			environmentGui = new EnvironmentWithUI();
		} catch (ParseException e) {
			Logger.getLogger(EnvironmentWithUI.class.getName()).log(Level.SEVERE, "Can not create simulation", e);
		}
		Console console = new Console(environmentGui);
		console.setVisible(true);
	}

	private void setupPortrayals() {
		Environment environment = (Environment) state;
		setupPortrayalForBuildings(environment);
		setupPortrayalForPaths(environment);
		setupPortrayalForAgents(environment);
	}

	private void setupPortrayalForAgents(Environment environment) {
		m_agentPortrayal.setField(environment.m_individualsGeomVectorField);
		m_agentPortrayal.setPortrayalForAll(
				new CircledPortrayal2D(
						new GeomPortrayal(ISimulationSettings.COLOR_OF_AGENT, ISimulationSettings.SIZE_OF_AGENT, true),
						ISimulationSettings.COLOR_OF_AGENT_SELECTED, 
						true)
				);
	}

	private void setupPortrayalForPaths(Environment environment) {
		m_pathsPortrayal.setField(environment.m_pathsGeomVectorField);
		colorPathToTarget(environment, environment.getIndividuals().get(0)); // index does not matter since both individuals follow the same path but in the opposite direction
		m_pathsPortrayal.setPortrayalForRemainder(
				new CircledPortrayal2D(
						new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH, true), 
						ISimulationSettings.COLOR_OF_PATH_SELECTED, 
						true)
				);
	}

	private void setupPortrayalForBuildings(Environment environment) {
		m_buildingsPortrayal.setField(environment.m_buildingsGeomVectorField);
		m_buildingsPortrayal.setPortrayalForRemainder(
				new CircledPortrayal2D(
						new BuildingLabelPortrayal(
							new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING, ISimulationSettings.SIZE_OF_BUILDING),
							ISimulationSettings.COLOR_OF_BUILDING),
					ISimulationSettings.SIZE_OF_BUILDING,
					ISimulationSettings.SIZE_OF_BUILDING,
					ISimulationSettings.COLOR_OF_BUILDING_SELECTED,
					true));
	}

	private void colorPathToTarget(Environment environment, Individual individual) {
		GeomVectorField field = environment.getPathsGeomVectorField();
		List<Coordinate> coordinatesOfPath = individual.getPathToNextTarget()
				.stream()
				.map(path -> path.getCoordinate())
				.collect(Collectors.toList());
		for (Coordinate coordinate : coordinatesOfPath) {
			ArrayList<MasonGeometry> coveringObjects = GeometryUtility
					.getCoveringObjects(new MasonGeometry(Environment.GEO_FACTORY.createPoint(coordinate)), field);
			coveringObjects.forEach(mg -> {
				mg.setUserData(
						new CircledPortrayal2D(
								new GeomPortrayal(
										ISimulationSettings.COLOR_OF_PATH_SELECTED,
										ISimulationSettings.SIZE_OF_PATH
										),
								ISimulationSettings.COLOR_OF_PATH_SELECTED,
								true
								));
			});
		}
	}
}
