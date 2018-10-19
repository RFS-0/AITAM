package rfs0.aitam.model.ui;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

import bsh.ParseException;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.SimpleInspector;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.inspector.TabbedInspector;
import sim.portrayal.simple.CircledPortrayal2D;

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
		
		// Activity chart
		JFreeChart barChart = ChartFactory.createBarChart("Individual's Activity", "Activity", "Percentage", ((Environment) this.state).getActivityCategoryDataset(), PlotOrientation.VERTICAL, false, false, false);
		barChart.setBackgroundPaint(Color.white);
		barChart.getTitle().setPaint(Color.black);
		CategoryPlot categoryPlot = barChart.getCategoryPlot();
		categoryPlot.setBackgroundPaint(Color.white);
		categoryPlot.setRangeGridlinePaint(Color.red);
		NumberAxis numberAxis = (NumberAxis) categoryPlot.getRangeAxis();
		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberAxis.setRange(0, 100);
		ChartFrame frame = new ChartFrame("Activity Chart", barChart);
		frame.setVisible(true);
		frame.setSize(400, 400);
		frame.pack();
		controller.registerFrame(frame);
		
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
	
	@Override
	public Object getSimulationInspectedObject() {
		return state;
	}
	
	@Override
	public Inspector getInspector() {
		Environment environment = (Environment) state;
		TabbedInspector tabbedInspector = new TabbedInspector(true);
		tabbedInspector.addInspector(new SimpleInspector(environment.getSimulationTime(), this), "Time");
		tabbedInspector.addInspector(new SimpleInspector(environment.getIndividuals(), this), "Individuals");
		tabbedInspector.addInspector(new SimpleInspector(environment.getAllActivities(), this), "Activities");
		tabbedInspector.addInspector(new SimpleInspector(environment.getEdgeTraffic(), this), "Traffic");
		tabbedInspector.addInspector(new SimpleInspector(environment.getCurrentLocationPoints(), this), "Current location points");
		tabbedInspector.addInspector(new SimpleInspector(environment.getCurrentNodes(), this), "Current nodes");
		return tabbedInspector;
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
		m_agentPortrayal.setField(environment.getIndividualsField());
		m_agentPortrayal.setPortrayalForAll(
				new CircledPortrayal2D(
						new GeomPortrayal(ISimulationSettings.COLOR_OF_AGENT, ISimulationSettings.SIZE_OF_AGENT, true),
						ISimulationSettings.COLOR_OF_SELECTED_ENTITY, 
						true)
				);
	}

	private void setupPortrayalForPaths(Environment environment) {
		m_pathsPortrayal.setField(environment.getPathField());
		m_pathsPortrayal.setPortrayalForRemainder(
				new CircledPortrayal2D(
						new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH, true), 
						ISimulationSettings.COLOR_OF_SELECTED_ENTITY, 
						true)
				);
	}

	private void setupPortrayalForBuildings(Environment environment) {
		m_buildingsPortrayal.setField(environment.getBuildingsField());
		m_buildingsPortrayal.setPortrayalForRemainder(
				new CircledPortrayal2D(
						new BuildingLabelPortrayal(
							new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING, ISimulationSettings.SIZE_OF_BUILDING),
							ISimulationSettings.COLOR_OF_BUILDING),
					ISimulationSettings.SIZE_OF_BUILDING,
					ISimulationSettings.SIZE_OF_BUILDING,
					ISimulationSettings.COLOR_OF_SELECTED_ENTITY,
					true));
	}
}
