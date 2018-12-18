package rfs0.aitam.model.ui;

import java.awt.Color;
import java.awt.Font;
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
import rfs0.aitam.individuals.Individual;
import rfs0.aitam.model.Environment;
import rfs0.aitam.settings.ISimulationSettings;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.SimpleInspector;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.inspector.TabbedInspector;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.util.geo.MasonGeometry;

/**
 * <p>This class is used to visalize the {@link Environment}.
 * As such it requires the following attributes:</p>
 * 
 * <p><b>Visualization</b></p>
 * 
 * <p>{@link EnvironmentWithUI#m_display}: The display used to visualize the model.</p>
 * <p>{@link EnvironmentWithUI#m_displayFrame}: The frame used to visualize the model.</p>
 * 
 * <p><b>Portrayals</b></p>
 * 
 * <p>{@link EnvironmentWithUI#m_buildingsPortrayal}: The portrayal that holds all buildings.</p>
 * <p>{@link EnvironmentWithUI#m_pathsPortrayal}: The portrayal that holds all paths.</p>
 * <p>{@link EnvironmentWithUI#m_agentPortrayal}: The portrayal that holds all agents.</p>
 * 
 */
public class EnvironmentWithUI extends GUIState {

	/**
	 * @category Visualization
	 * 
	 * <p>The display used to visualize the model.</p>
	 */
	private Display2D m_display;
	/**
	 * <p>The frame used to visualize the model.</p>
	 */
	private JFrame m_displayFrame;

	/**
	 * @category Portrayals
	 * 
	 * <p>{@link EnvironmentWithUI#m_buildingsPortrayal}: The portrayal that holds all buildings.</p>
	 */
	private GeomVectorFieldPortrayal m_buildingsPortrayal = new GeomVectorFieldPortrayal();
	/**
	 * <p>The portrayal that holds all paths.</p>
	 */
	private GeomVectorFieldPortrayal m_pathsPortrayal = new GeomVectorFieldPortrayal();
	/**
	 * <p>The portrayal that holds all agents.</p>
	 */
	private GeomVectorFieldPortrayal m_agentPortrayal = new GeomVectorFieldPortrayal();

	public EnvironmentWithUI() throws ParseException {
		super(new Environment(1L));
	}

	public EnvironmentWithUI(SimState state) {
		super(state);
	}

	public EnvironmentWithUI(long seed) throws ParseException {
		super(new Environment(seed));
	}

	/**
	 * <p>This method initializes all aspects related to the visualization of the {@link Environment}.</p>
	 */
	@Override
	public void init(Controller controller) {
		super.init(controller);
		m_display = new Display2D(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT, this) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean shouldUpdate() {
				return true;
			}
		};
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

	/**
	 * <p>This method starts the UI.</p>
	 */
	@Override
	public void start() {
		super.start();
		m_display.reset();
		setupPortrayals();
		m_display.setBackdrop(ISimulationSettings.COLOR_OF_BACKGROUND);
//		m_display.setBackdrop(null); this is faster -> use this if speed is important
		m_display.repaint();
	}
	
	/**
	 * <p>This method finshes the visualization.</p>
	 */
	@Override
	public void finish() {
		super.finish();
		m_display.quit();
	}
	
	/**
	 * <p>This method declares the state (i.e. the {@link Environment}) as the object to be inspected.
	 * Most of the properties of the simulation state can be inspected during the execution of the simulation.</p>
	 */
	@Override
	public Object getSimulationInspectedObject() {
		return state;
	}
	
	/**
	 * <p>This method adds additional inspectors for all of the properties of interest.</p>
	 */
	@Override
	public Inspector getInspector() {
		Environment environment = (Environment) state;
		TabbedInspector tabbedInspector = new TabbedInspector(true);
		tabbedInspector.addInspector(new SimpleInspector(environment.getSimulationTime(), this), "Time");
		tabbedInspector.addInspector(new SimpleInspector(environment.getIndividuals(), this), "Individuals");
		tabbedInspector.addInspector(new SimpleInspector(environment.getAllActivities(), this), "Activities");
		tabbedInspector.addInspector(new SimpleInspector(environment.getEdgeTraffic(), this), "Traffic");
		tabbedInspector.addInspector(new SimpleInspector(environment.getIndividualsField(), this), "Individuals field");
		tabbedInspector.addInspector(new SimpleInspector(environment.getBuildingsField(), this), "Buildings field");
		tabbedInspector.addInspector(new SimpleInspector(environment.getPathField(), this), "Paths field");
		return tabbedInspector;
	}

	/**
	 * <p>This method starts the UI.</p>
	 * 
	 * @param args - not used.
	 */
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

	/**
	 * <p>This method sets up all the portrayals.</p>
	 */
	private void setupPortrayals() {
		Environment environment = (Environment) state;
		setupPortrayalForBuildings(environment);
		setupPortrayalForPaths(environment);
		setupPortrayalForAgents(environment);
	}

	/**
	 * <p>This method sets up the portrayal for agents.</p>
	 * 
	 * @param environment - the environment to be visualized.
	 */
	private void setupPortrayalForAgents(Environment environment) {
		m_agentPortrayal.setField(environment.getIndividualsField());
		m_agentPortrayal.setPortrayalForRemainder(
				new CircledPortrayal2D(
						new LabelledPortrayal2D(
								new GeomPortrayal(ISimulationSettings.COLOR_OF_INDIVIDUAL, ISimulationSettings.SIZE_OF_INDIVIDUAL, true), 
								10,
								5,
								0.5,
								0.5,
								new Font("SansSerif",Font.BOLD, 15),
								LabelledPortrayal2D.ALIGN_LEFT,
								null, 
								ISimulationSettings.COLOR_OF_INDIVIDUAL, 
								false) {
							private static final long serialVersionUID = 1L;
							@Override
							public String getLabel(Object object, DrawInfo2D info) {
								if (object instanceof MasonGeometry && (((MasonGeometry) object).getUserData() instanceof Individual)) {
									MasonGeometry mgOfIndividual = (MasonGeometry) object;
									Individual individual = (Individual) mgOfIndividual.getUserData();
									StringBuilder stringBuilder = new StringBuilder();
									stringBuilder.append("ID:\t" + individual.getId() + "\n");
									return stringBuilder.toString();
								}
								else {
									return "";
								}
							}
							
						},
						ISimulationSettings.COLOR_OF_SELECTED_ENTITY, 
						true)
				);
	}

	/**
	 * <p>This method sets up the portrayal for paths.</p>
	 * 
	 * @param environment - the environment to be visualized.
	 */
	private void setupPortrayalForPaths(Environment environment) {
		m_pathsPortrayal.setField(environment.getPathField());
		m_pathsPortrayal.setPortrayalForRemainder(
				new CircledPortrayal2D(
						new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH, true), 
						ISimulationSettings.COLOR_OF_SELECTED_ENTITY, 
						true)
				);
	}

	/**
	 * <p>This method sets up the portrayal for buildings.</p>
	 * 
	 * @param environment - the environment to be visualized.
	 */
	private void setupPortrayalForBuildings(Environment environment) {
		m_buildingsPortrayal.setField(environment.getBuildingsField());
		m_buildingsPortrayal.setPortrayalForRemainder(
				new LabelledPortrayal2D(
						new CircledPortrayal2D(
								new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING, ISimulationSettings.SIZE_OF_BUILDING, true),
								ISimulationSettings.COLOR_OF_SELECTED_ENTITY,
								true), 
						null, 
						ISimulationSettings.COLOR_OF_BUILDING, 
						true) {
					private static final long serialVersionUID = 1L;
				});
	}
}
