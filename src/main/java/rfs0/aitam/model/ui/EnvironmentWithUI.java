package rfs0.aitam.model.ui;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import bsh.ParseException;
import rfs0.aitam.model.Environment;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.util.geo.MasonGeometry;

public class EnvironmentWithUI extends GUIState {

	private Display2D m_display;
	private JFrame m_displayFrame;

	private GeomVectorFieldPortrayal m_buildingsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal m_pedestrianPathsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal m_agentPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal m_homePortrayal = new GeomVectorFieldPortrayal(); // TODO: only for dev; remove later
	private GeomVectorFieldPortrayal m_workPortrayal = new GeomVectorFieldPortrayal(); // TODO: only for dev; remove later

	public EnvironmentWithUI(SimState state) {
		super(state);
	}

	public EnvironmentWithUI() throws ParseException {
		super(new Environment(System.currentTimeMillis()));
	}

	@Override
	public void init(Controller controller) {
		super.init(controller);
		m_display = new Display2D(Environment.WIDTH, Environment.HEIGHT, this);

		m_display.attach(m_buildingsPortrayal, "Buildings", true);
		m_display.attach(m_pedestrianPathsPortrayal, "Pedestrian Paths", true);
		m_display.attach(m_agentPortrayal, "Agents", true);
		m_display.attach(m_homePortrayal, "Home", true);
		m_display.attach(m_workPortrayal, "Work", true);
		

		m_displayFrame = m_display.createFrame();
		controller.registerFrame(m_displayFrame);
		m_displayFrame.setVisible(true);
	}

	@Override
	public void start() {
		super.start();
		setupPortrayals();
	}

	private void setupPortrayals() {
		Environment world = (Environment) state;

		m_buildingsPortrayal.setField(world.m_buildings);
		BuildingLabelPortrayal blP = new BuildingLabelPortrayal(new GeomPortrayal(Color.BLUE, 5.0), Color.DARK_GRAY);
		m_buildingsPortrayal.setPortrayalForAll(blP);
		m_buildingsPortrayal.setPortrayalForObject(world.m_buildings.getObjectsWithinDistance((MasonGeometry) world.m_buildings.getGeometries().get(0), 100000), new GeomPortrayal(Color.YELLOW, 100.0, true));

		m_pedestrianPathsPortrayal.setField(world.m_paths);
		m_pedestrianPathsPortrayal.setPortrayalForAll(new GeomPortrayal(Color.GRAY, true));

		m_agentPortrayal.setField(world.m_individuals);
		m_agentPortrayal.setPortrayalForAll(new GeomPortrayal(Color.RED, 15.0, true));
		
		m_display.reset();
		m_display.setBackdrop(Color.WHITE);
		m_display.repaint();
	}

	public static void main(String[] args) {
		EnvironmentWithUI zurichGui = null;
		try {
			zurichGui = new EnvironmentWithUI();
		} catch (ParseException e) {
			Logger.getLogger(EnvironmentWithUI.class.getName()).log(Level.SEVERE, null, e);
		}
		Console console = new Console(zurichGui);
		console.setVisible(true);
	}
}
