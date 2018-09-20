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

public class EnvironmentWithUI extends GUIState {

	private Display2D m_display;
	private JFrame m_displayFrame;

	private GeomVectorFieldPortrayal m_buildingsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal m_pedestrianPathsPortrayal = new GeomVectorFieldPortrayal();
	private GeomVectorFieldPortrayal m_agentPortrayal = new GeomVectorFieldPortrayal();

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
		m_display.setMouseChangesOffset(true);
		m_display.attach(m_buildingsPortrayal, "Buildings", true);
		m_display.attach(m_pedestrianPathsPortrayal, "Pedestrian Paths", true);
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
		m_display.setBackdrop(Color.WHITE);
		m_display.repaint();
	}

	private void setupPortrayals() {
		Environment environment = (Environment) state;

		m_buildingsPortrayal.setField(environment.m_buildings);
		BuildingLabelPortrayal blP = new BuildingLabelPortrayal(new GeomPortrayal(new Color(30,144,255), 5.0), Color.DARK_GRAY);
		m_buildingsPortrayal.setPortrayalForRemainder(blP);

		m_pedestrianPathsPortrayal.setField(environment.m_paths);
		m_pedestrianPathsPortrayal.setPortrayalForRemainder(new GeomPortrayal(Color.LIGHT_GRAY, true));

		m_agentPortrayal.setField(environment.m_individuals);
		m_agentPortrayal.setPortrayalForAll(new GeomPortrayal(new Color(255,127,80), 5.0, true));
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
