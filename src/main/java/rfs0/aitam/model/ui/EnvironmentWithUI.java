package rfs0.aitam.model.ui;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import bsh.ParseException;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import rfs0.aitam.model.Individual;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.util.Bag;
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
		m_agentPortrayal.setPortrayalForAll(new GeomPortrayal(ISimulationSettings.COLOR_OF_AGENT, ISimulationSettings.SIZE_OF_AGENT, true));
	}

	private void setupPortrayalForPaths(Environment environment) {
		m_pathsPortrayal.setField(environment.m_pathsGeomVectorField);
		colorPathToTarget(environment, environment.getIndividuals().get(0)); // index does not matter since both individuals follow the same path but in the opposite direction
		m_pathsPortrayal.setPortrayalForRemainder(new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH, true));
	}

	private void setupPortrayalForBuildings(Environment environment) {
		m_buildingsPortrayal.setField(environment.m_buildingsGeomVectorField);
		BuildingLabelPortrayal blP = new BuildingLabelPortrayal(new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING, ISimulationSettings.SIZE_OF_BUILDING), Color.DARK_GRAY);
		m_buildingsPortrayal.setPortrayalForRemainder(blP);
	}
	
	private void colorPathToTarget(Environment environment, Individual individual) {
		individual.getPathToNextTarget().forEach(p -> {			
			MasonGeometry closestPath = environment.getClosestPath(new MasonGeometry(Environment.GEO_FACTORY.createPoint(p.getCoordinate())));
			closestPath.setUserData(new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH_SELECTED, ISimulationSettings.SIZE_OF_PATH));
		});
	}
}
