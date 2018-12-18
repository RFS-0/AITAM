package rfs0.aitam.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVPrinter;
import org.jfree.data.category.DefaultCategoryDataset;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.activities.Activity;
import rfs0.aitam.activities.ActivityAgenda;
import rfs0.aitam.activities.ActivityCategory;
import rfs0.aitam.activities.ActivityInitializer;
import rfs0.aitam.individuals.Individual;
import rfs0.aitam.individuals.IndividualInitializer;
import rfs0.aitam.model.needs.AbsoluteNeedTimeSplit;
import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.GeometryUtility;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
/**
 * p>This class is used to model the environment. 
 * As such it requires the following attributes:</p>
 * 
 * <p><b>Initializers</b></p>
 * 
 * <p> {@link Environment#ACTIVITY_INITIALIZER}: This initializer is used to create all activities.</p>
 * 
 * <p><b>Factories</b></p>
 * 
 * <p> {@link Environment#GEO_FACTORY}: This factory is used to create geometries.</p>
 * 
 * <p><b>Buildings and nodes</b></p>
 * 
 * <p> {@link Environment#BUILDING_TO_CLOSEST_NODE_MAP}: This map contains the closest node for each of the buildings.</p>
 * <p> {@link Environment#NODE_TO_CLOSEST_BUILDING_MAP}: A reversed mapping form node to their closest building. Contains only nodes that are closest to some building.</p>
 * 
 * <p><b>Activities</b></p>
 * 
 * <p>{@link Environment#m_activityDescriptionToActivityMap}: This map contains the activity for each of the activity descriptions (see {@link ISimulationSettings}).</p>
 * <p>{@link Environment#m_activityCategoryToActivitiesyMap}: This map contains a list of all activities that belong to each of the activity categories.</p>
 * <p>{@link Environment#m_activityCategoryDataset}: This represents that dataset which is used to create the bar chart that shows what fraction of individuals execute an activity which belongs to each of the activity categories.</p>
 * 
 * <p><b>Time</b></p>
 * 
 * <p>{@link Environment#m_simulationTime}: This variable is used to keep track of the current simulation time. It is updated after each simulation step.</p>
 * 
 * <p><b>GIS</b></p>
 * 
 * <p>{@link Environment#m_buildingsField}: This variable contains all the buildings as defined by the shape file for the buildings.</p>
 * <p>{@link Environment#m_pathField}: This variable contains all the paths as defined by the shape file for the paths.</p>
 * <p>{@link Environment#m_pathGraph}: This variable contains a graph representation of the paths.</p>
 * <p>{@link Environment#m_edgeTraffic}: This variable can be used to store the number of individuals on each of the edges which currently are being traversed (i.e. the traffic on each path).</p>
 * <p>{@link Environment#m_individualsField}: This variable contains all the geometries for all the individuals.</p>
 *
 * 
 * <p><b>Individuals</b></p>
 * 
 * <p>{@link Environment#m_individuals}: This variable contains all individuals in the environment.</p>
 * 
 * <p><b>Output</b></p>
 * 
 * <p>{@link Environment#m_outputHolder}: This variable contains all output variables. It is updated after each simulation step.</p>
 * <p>{@link Environment#m_environmentObserver}: The environment observer is used record the output by serializing each step as one line in a CSV-File which is generated for the whole simulation run.</p>
 */
public class Environment extends SimState {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @category Initializers
	 *
	 * <p>This initializer is used to create all activities.</p>
	 */
	public static final ActivityInitializer ACTIVITY_INITIALIZER = new ActivityInitializer();
	
	/**
	 * @category Factories
	 * 
	 * <p>This factory is used to create geometries.</p>
	 */
	public static final GeometryFactory GEO_FACTORY = new GeometryFactory();
	
	/**
	 * @category Buildings and nodes
	 * 
	 * <p>This map contains the closest node for each of the buildings.</p>
	 */
	public static HashMap<MasonGeometry, Node> BUILDING_TO_CLOSEST_NODE_MAP = new HashMap<>();
	/**
	 * <p>A reversed mapping form node to their closest building. Contains only nodes that are closest to some building./p>
	 */
	public static HashMap<Node, MasonGeometry> NODE_TO_CLOSEST_BUILDING_MAP = new HashMap<>();
	
	/**
	 *  @category Activities
	 *  
	 * <p>This map contains the activity for each of the activity descriptions (see {@link ISimulationSettings}).</p>
	 */
	private HashMap<String, Activity> m_activityDescriptionToActivityMap = new HashMap<>();
	/**
	 * <p>This map contains a list of all activities that belong to each of the activity categories.</p>
	 */
	private HashMap<ActivityCategory, ArrayList<Activity>> m_activityCategoryToActivitiesyMap = new HashMap<>();
	/**
	 * <p>This represents that dataset which is used to create the bar chart that shows what fraction of individuals execute an activity which belongs to each of the activity categories.</p>
	 */
	private DefaultCategoryDataset m_activityCategoryDataset = new DefaultCategoryDataset();

	/**
	 * @category Time
	 * 
	 * <p>This variable is used to keep track of the current simulation time. It is updated after each simulation step.</p>
	 */
	private SimulationTime m_simulationTime = new SimulationTime();

	/**
	 * @category Buildings and nodes
	 * 
	 * <p>This variable contains all the buildings as defined by the shape file for the buildings.</p>
	 */
	private GeomVectorField m_buildingsField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT);
	/**
	 * <p>This variable contains all the paths as defined by the shape file for the paths.</p>
	 */
	private GeomVectorField m_pathField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT);
	/**
	 * <p>This variable contains a graph representation of the paths.</p>
	 */
	private GeomPlanarGraph m_pathGraph = new GeomPlanarGraph();
	/**
	 * <p>This variable can be used to store the number of individuals on each of the edges which currently are being traversed (i.e. the traffic on each path).</p>
	 */
	private HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> m_edgeTraffic = new HashMap<>(); 
	/**
	 * <p>This variable contains all the geometries for all the individuals.</p>
	 */
	private GeomVectorField m_individualsField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT);

	/**
	 * @category Activities
	 * 
	 * <p>This variable contains all individuals in the environment.</p>
	 */
	private ArrayList<Individual> m_individuals = new ArrayList<>();
	
	/**
	 * @category Output
	 * 
	 * <p>This variable contains all output variables. It is updated after each simulation step.</p>
	 */
	private LinkedHashMap<String, Object> m_outputHolder = new LinkedHashMap<>();
	/**
	 * <p>The environment observer is used record the output by serializing each step as one line in a CSV-File which is generated for the whole simulation run.</p>
	 */
	private EnvironmentObserver m_environmentObserver;
	
	public Environment(long seed) { 
		super(seed); 
		random.setSeed(seed); 
		initEnvironment();
		initActivities();
		initBuildings();
		initIndividuals();
		initOutput();
	}

	/**
	 * This method schedules all {@link Steppable}'s of the simulation. This works as follows:
	 * <ol>
	 * 	<li>Schedule all the steps of the {@link Individual} as anonymous {@link Steppable}'s. <b>Note:</b> This is necessary since each of them must be executed for all {@link Individual}'s in the given order</li>
	 * 	<ol>
	 * 		<li>Plan joint {@link Activity}'s, if planning is possible. Write them into {@link Individual}'s joint {@link ActivityAgenda}</li>
	 * 		<li>Carry over joint activities to the {@link Individual}'s individual {@link ActivityAgenda}, if planning is possible</li>
	 * 		<li>Plan individual activities, if planning is possible.</li>
	 * 		<li>Choose the best of the generated {@link ActivityAgenda}'s</li>
	 * 		<li>At each point in time: Moving, if necessary.</li>
	 * 		<li>At each point in time: execute the activity scheduled for the interval overlapping the current point in time. <b>Note:</b> This includes in particular updating the {@link AbsoluteNeedTimeSplit}.
	 * 		<li>Finally, if the beginning of a new day is reached, reset (only) variables which are used to generate a new {@link ActivityAgenda}</li>
	 * 	</ol>
	 * 	<li>Schedule the {@link GeomVectorField} containing all {@link Point}'s wrapped in a {@link MasonGeometry}. These represent the {@link Individual}'s as dots.</li>
	 * 	<li>Schedule the {@link DefaultCategoryDataset} to be updated with the information of the aggregated number of {@link Individual}'s per {@link ActivityCategory}.</li>
	 * 	<li>Schedule the {@link EnvironmentObserver} to write all data in {@link Environment#m_outputHolder} to disk.
	 * 	<li>Schedule the {@link SimulationTime} to be incremented. <b>Note:</b>Each step takes exactly one minute. The start point of simulation is 01.01.2018 00:00.
	 *<ol>
	 */
	@Override
	public void start() {
		super.start();
		for (Individual individual: getIndividuals()) {
			schedule.scheduleRepeating(0.0, 0, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					if (individual.isPlanningPossible(ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES)) {
						individual.planJointActivities();
					}
				}
			});
			schedule.scheduleRepeating(0.0, 1, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					if (individual.isPlanningPossible(ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES)) {
						individual.carryOverJointActivities();
					}
				}
			});
			schedule.scheduleRepeating(0.0, 2, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					if (individual.isPlanningPossible(ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES)) {
						individual.planIndividualActivities();
					}
				}
			});
			schedule.scheduleRepeating(0.0, 3, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					if (individual.isPlanningPossible(ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES)) {
						individual.chooseBestAgenda();
					}
				}
			});
			schedule.scheduleRepeating(0.0, 4, new Steppable() {
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					individual.move();
				}
			});
			schedule.scheduleRepeating(0.0, 5, new Steppable() {
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					individual.executeActivity();
				}
			});
			schedule.scheduleRepeating(0.0, Integer.MAX_VALUE, new Steppable() {
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					Environment environment = (Environment) state;
					if (environment.getSimulationTime().getCurrentTime().equals(ISimulationSettings.END_OF_DAY)) {
						individual.initNewDay();
					}
				}
			});
		}
		schedule.scheduleRepeating(0.0, 10, m_individualsField.scheduleSpatialIndexUpdater());
		// update attributes for simulation output
		schedule.scheduleRepeating(0.0, 11, new Steppable() {
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					Integer totalNumberOfAgents = (Integer) m_outputHolder.get(ISimulationSettings.TOTAL_NUMBER_OF_AGENTS);
					for (ActivityCategory category: m_activityCategoryToActivitiesyMap.keySet()) {
						ArrayList<Activity> activitiesOfCategory = m_activityCategoryToActivitiesyMap.get(category);
						int totalNumberOfAgentsPerCategory = 0;
						for (Activity activityOfCategory: activitiesOfCategory) {
							if (m_outputHolder.get(activityOfCategory.getActivityDescription()) instanceof Integer) {
								totalNumberOfAgentsPerCategory += ((Integer) m_outputHolder.get(activityOfCategory.getActivityDescription())).intValue();
							}
						}
						double fractionOfCategory = (double) totalNumberOfAgentsPerCategory / totalNumberOfAgents * 100;
						m_activityCategoryDataset.addValue(fractionOfCategory, ISimulationSettings.TITLE_OF_BARCHART, category.toString());
					}
				}
		});
		schedule.scheduleRepeating(0.0, 12, m_environmentObserver);
		schedule.scheduleRepeating(0.0, 13, m_simulationTime);
	}
	
	/**
	 * <p>This method additionally closes the {@link CSVPrinter} once the simulation is finished.</p>
	 */
	@Override
	public void finish() {
		super.finish();
		try {
			m_environmentObserver.getCsvPrinter().close();
		}
		catch (IOException e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, "Failed to close file for simulation output", e);
		}
	}
	
	/**
	 * <p>This method executes the simulation.</p>
	 * 
	 * @param args - See {@link SimState#doLoop(sim.engine.MakesSimState, String[])} for details on args.
	 */
	public static void main(String[] args) {
		doLoop(Environment.class, args);
		System.exit(0);
	}
	
	/**
	 * <p>This method initializes the environment by reading the shape files for the buildings and the paths layer.</p>
	 */
	private void initEnvironment() {
		System.out.println("Initializing the environment...");
		long start = System.nanoTime();
		Envelope globalMBR = new Envelope();
		readShapeFiles(globalMBR);
		synchronizeMinimumBoundingRectangles(globalMBR);
		m_pathGraph.createFromGeomField(m_pathField);
		System.out.println(String.format("Initialized environment in %d ms", (System.nanoTime() - start) / 1000000));
	}
	
	/**
	 * <p>This method read the shape files used to represent GIS data and expands the global MBR accordingly.</p>
	 * 
	 * @param globalMBR - the global minimum bounding rectangle.
	 */
	private void readShapeFiles(Envelope globalMBR) {
		try {
			System.out.println("Reading building layer...");
			// use an empty Bag if the shape file for the buildings does not have additional attributes
			Bag attributesOfBuildings = initAttributesOfBuildings();
			readShapeFile(ISimulationSettings.BUILDINGS_FILE, m_buildingsField, globalMBR, attributesOfBuildings);

			System.out.println("Reading the path layer...");
			// use an empty Bag if the shape file for the paths does not have additional attributes
			Bag attributesOfPaths = initializeAttributesOfPaths();
			readShapeFile(ISimulationSettings.PATHS_FILE, m_pathField, globalMBR, attributesOfPaths);
		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, "Failed to read shape files", e);
		}
	}
	
	/**
	 * <p>This method initializes the attributes of the buildings as a provided via shape file.</p>
	 * 
	 * <p><b>Important:</b> This is specific to the shape file currently used for the buildings and the attributes are therefore the attributes are currently not used in the simulation.
	 * Thus it can either be removed if not needed or adapted to fit other attributes of another shape file.</p> 
	 * 
	 * @return Bag - all attributes that can be read out form the shape file for buildings.
	 */
	private Bag initAttributesOfBuildings() {
		Bag attributesOfBuildings = new Bag();
		attributesOfBuildings.add("OBJECTID");
		attributesOfBuildings.add("Sortierung");
		attributesOfBuildings.add("Adresse");
		attributesOfBuildings.add("Strassenna");
		attributesOfBuildings.add("Hausnummer");
		attributesOfBuildings.add("EGID");
		attributesOfBuildings.add("PLZ");
		attributesOfBuildings.add("Stadtkreis");
		attributesOfBuildings.add("Statistisc");
		attributesOfBuildings.add("Statisti_1");
		attributesOfBuildings.add("Schulkreis");
		attributesOfBuildings.add("Stadtquart");
		attributesOfBuildings.add("Kirchenkre");
		attributesOfBuildings.add("Kirchenk_1");
		attributesOfBuildings.add("ART");
		return attributesOfBuildings;
	}

	
	/**
	 * <p>This method initializes the attributes of the paths as a provided via shape file.</p>
	 * 
	 * <p><b>Important:</b> This is specific to the shape file currently used for the paths and the attributes are therefore the attributes are currently not used in the simulation.
	 * Thus it can either be removed if not needed or adapted to fit other attributes of another shape file.</p> 
	 * 
	 * @return Bag - all attributes that can be read out from the shape file for paths.
	 */
	private Bag initializeAttributesOfPaths() {
		Bag attributesOfPaths = new Bag();
		attributesOfPaths.add("OBJECTID");
		attributesOfPaths.add("Fahrrad");
		attributesOfPaths.add("Hoehe_Anfa");
		attributesOfPaths.add("Hoehe_Ende");
		attributesOfPaths.add("Strasse");
		attributesOfPaths.add("VELOWEGE_M");
		attributesOfPaths.add("Shape_Leng");
		return attributesOfPaths;
	}

	/**
	 * <p>This method reads the shape file which is located under the provided path into the provided geometry and expands the provided MBR to include this new geometry.
	 * Furthermore, it read out all the attributes as provided by the bag with attributes.</p>
	 * 
	 * 
	 * @param relativePathToFile - the relative path to the shape file (relative to the users current working directory).
	 * @param geometry - the geometry into which the shape file is read into.
	 * @param minimumBoundingRectangle - the minimum bounding rectangle which represents the boundaries of the simulation.
	 * @param attributes - a bag with attributes which are provided by the shape file. It can be empty if not needed or if the attributes are not relevant for the simulation.
	 */
	private void readShapeFile(String relativePathToFile, GeomVectorField geometry, Envelope minimumBoundingRectangle, Bag attributes) {
		try {
			URL url = new File(System.getProperty("user.dir") + relativePathToFile).toURI().toURL();
			ShapeFileImporter.read(url, geometry, attributes);
			minimumBoundingRectangle.expandToInclude(geometry.getMBR());

		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, String.format("Failed to read GIS file with path: %s", relativePathToFile), e);
		}
	}
	
	/**
	 * <p>This method synchronizes the MBR for all the fields, such that they all use the same boundaries.</p>
	 * 
	 * @param minimumBoundingRectangle - the simulation's boundaries.
	 */
	private void synchronizeMinimumBoundingRectangles(Envelope minimumBoundingRectangle) {
		m_buildingsField.setMBR(minimumBoundingRectangle);
		m_pathField.setMBR(minimumBoundingRectangle);
		m_individualsField.setMBR(minimumBoundingRectangle);
	}
	
	/**
	 * <p>This method initializes all activities.</p>
	 */
	private void initActivities() {
		System.out.println("Initializing activities...");
		long start = System.nanoTime();
		initLeisureActivities();		
		initWorkActivities();
		initPersonalCareActivities();
		initHouseholdCareActivities();
		initTravelActivities();
		initIdleActivities();
		System.out.println(String.format("Initialized activities in %d ms", (System.nanoTime() - start) / 1000000));
	}

	/**
	 * <p>This method initializes all activities that belong to the {@link ActivityCategory#WORK}.</p>
	 */
	private void initWorkActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_HOME_ALONE, ACTIVITY_INITIALIZER.initWorkAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_WORK_PLACE_ALONE, ACTIVITY_INITIALIZER.initWorkAtWorkPlaceAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_WORK_PLACE_WITH_COWORKERS, ACTIVITY_INITIALIZER.initWorkAtWorkPlaceWithCoworkers());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_THIRD_PLACE_ALONE, ACTIVITY_INITIALIZER.initWorkAtThirdPlaceForWorkAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_THIRD_PLACE_WITH_COWORKERS, ACTIVITY_INITIALIZER.initWorkAtThirdPlaceForWorkWithCoworkers());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_DURING_TRAVEL_ALONE, ACTIVITY_INITIALIZER.initWorkDuringTravelAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_DURING_TRAVEL_WITH_COWORKERS, ACTIVITY_INITIALIZER.initWorkDuringTravelWithCoworkers());
	}

	/**
	 * <p>This method initializes all activities that belong to the {@link ActivityCategory#LEISURE}.</p>
	 */
	private void initLeisureActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_HOME_ALONE_ACTIVITY, ACTIVITY_INITIALIZER.initLeisureAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_HOME_WITH_HOUSEHOLD_MEMBERS, ACTIVITY_INITIALIZER.initLeisureAtHomeWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_HOME_WITH_FRIENDS, ACTIVITY_INITIALIZER.initLeisureAtHomeWithFriendsActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_THIRD_PLACE_ALONE, ACTIVITY_INITIALIZER.initLeisureAtThirdPlaceForLeisureAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS, ACTIVITY_INITIALIZER.initLeisureAtThirdPlaceForLeisureWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_THIRD_PLACE_WITH_FRIENDS, ACTIVITY_INITIALIZER.initLeisureAtThirdPlaceForLeisureWithFriendsActivity());
	}
	
	/**
	 * <p>This method initializes all activities that belong to the {@link ActivityCategory#PERSONAL_CARE}.</p>
	 */
	private void initPersonalCareActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_HOME_ALONE, ACTIVITY_INITIALIZER.initPersonalCareAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS, ACTIVITY_INITIALIZER.initPersonalCareAtHomeWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_HOME_WITH_FRIENDS, ACTIVITY_INITIALIZER.initPersonalCareAtHomeWithFriendsActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_WORK_ALONE, ACTIVITY_INITIALIZER.initPersonalCareAtWorkPlaceAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_WORK_WITH_COWORKERS, ACTIVITY_INITIALIZER.initPersonalCareAtWorkPlaceWithCoworkersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_ALONE, ACTIVITY_INITIALIZER.initPersonalCareAtThirdPlaceForPersonalCareAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS, ACTIVITY_INITIALIZER.initPersonalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_WITH_FRIENDS, ACTIVITY_INITIALIZER.initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity());
	}
	
	/**
	 * <p>This method initializes all activities that belong to the {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE}.</p>
	 */
	private void initHouseholdCareActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_ALONE, ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS, ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_ALONE, ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS, ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembers());
	}
	
	/**
	 * <p>This method initializes all activities that belong to the {@link ActivityCategory#TRAVEL}.</p>
	 */
	private void initTravelActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.TRAVEL, ACTIVITY_INITIALIZER.initTravelActivity());
	}
	
	/**
	 * <p>This method initializes all activities that belong to the {@link ActivityCategory#IDLE}.</p>
	 */
	private void initIdleActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_HOME, ACTIVITY_INITIALIZER.initIdleAtHomeActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_LEISURE, ACTIVITY_INITIALIZER.initIdleAtLeisureActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_WORK, ACTIVITY_INITIALIZER.initIdleAtWorkActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, ACTIVITY_INITIALIZER.initIdleAtThirdPlaceForHouseholdAndFamilyCareActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_LEISURE, ACTIVITY_INITIALIZER.initIdleAtThirdPlaceForLeisureActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_WORK, ACTIVITY_INITIALIZER.initIdleAtThirdPlaceForWorkActivity());
	}
	
	/**
	 * <p>This method initializes all individuals.</p>
	 */
	private void initIndividuals() {
		System.out.println("Initializing individuals...");
		long start = System.nanoTime();
		m_individuals = IndividualInitializer.initIndividuals(this);
		for (Individual individual: m_individuals) {
			m_individualsField.addGeometry(individual.getCurrentLocationPoint());
			
		}
		m_individualsField.updateSpatialIndex();
		System.out.println(String.format("Initialized individuals in %d ms", (System.nanoTime() - start) / 1000000));
	}
	
	/**
	 * <p>This method initializes all buildings.</p>
	 */
	private void initBuildings() {
		System.out.println("Initializing buildings...");
		long start = System.nanoTime();
		initBuildingToClosestNodeMap();
		System.out.println(String.format("Initialized buildings in %d ms", (System.nanoTime() - start) / 1000000));
	}
	
	/**
	 * <p>This method initializes the maps related to buildings and nodes.</p>
	 */
	private void initBuildingToClosestNodeMap() {
		for (Object buildingObject: m_buildingsField.getGeometries()) {
			MasonGeometry building = (MasonGeometry) buildingObject;
			Node closestNodeToBuilding = null;
			double searchDistance = 0.0;
			while (closestNodeToBuilding == null) {
				Bag pathsWithinDistance = m_pathField.getObjectsWithinDistance(GEO_FACTORY.createPoint(building.getGeometry().getCoordinate()), searchDistance);
				closestNodeToBuilding = getClosestNodeToBuilding(building, pathsWithinDistance);
				searchDistance += 1;
			}
			BUILDING_TO_CLOSEST_NODE_MAP.put(building, closestNodeToBuilding);
			NODE_TO_CLOSEST_BUILDING_MAP.put(closestNodeToBuilding, building);
		}
	}
	
	/**
	 * <p>This method initializes the simulations output.</p>
	 */
	private void initOutput() {
		m_outputHolder.put(ISimulationSettings.TIME_STAMP, m_simulationTime.getCurrentDateTime());
		m_outputHolder.put(ISimulationSettings.TOTAL_NUMBER_OF_AGENTS, 0);
		for (ActivityCategory category: ActivityCategory.values()) {
			m_outputHolder.put(category.toString(), 0);
			ArrayList<Activity> activitiesOfCategory = m_activityDescriptionToActivityMap.values().stream()
					.filter(activity -> activity.getActivityCategory() == category)
					.collect(Collectors.toCollection(ArrayList::new));
			for (Activity activity: activitiesOfCategory) {
				m_outputHolder.put(activity.getActivityDescription(), 0);
			}
			if (m_activityCategoryToActivitiesyMap.get(category) == null) {
				m_activityCategoryToActivitiesyMap.put(category, new ArrayList<>());
			}
			m_activityCategoryToActivitiesyMap.get(category).addAll(activitiesOfCategory);
		}
		m_environmentObserver = new EnvironmentObserver(m_outputHolder.keySet());
	}
	
	/**
	 * <p>This method gets the closest node to the provided building based on the provided candidate paths.</p>
	 * 
	 * @param building
	 * @param candidatePaths
	 * @return
	 */
	private Node getClosestNodeToBuilding(MasonGeometry building, Bag candidatePaths) {
		Coordinate buildingCoordinate = building.getGeometry().getCoordinate();
		double minDistance = Double.MAX_VALUE;
		Node closestNodeToBuilding = null;
		for (Object pathObj: candidatePaths) {
			Coordinate pathCoordinate = ((MasonGeometry) pathObj).getGeometry().getCoordinate();
			Node node = m_pathGraph.findNode(pathCoordinate);
			if (node != null) {
				double distance = GeometryUtility.calculateDistance(buildingCoordinate, node.getCoordinate());
				if (distance < minDistance) {
					minDistance = distance;
					closestNodeToBuilding = node;
				}
			}
		}
		return closestNodeToBuilding;
	}
	
	/**
	 * @category Getters and setters
	 */
	public HashMap<String, Activity> getAllActivities() {
		return m_activityDescriptionToActivityMap;
	}

	public SimulationTime getSimulationTime() {
		return m_simulationTime;
	}

	public GeomVectorField getBuildingsField() {
		return m_buildingsField;
	}

	public GeomVectorField getPathField() {
		return m_pathField;
	}

	public GeomPlanarGraph getPathGraph() {
		return m_pathGraph;
	}

	public HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> getEdgeTraffic() {
		return m_edgeTraffic;
	}

	public GeomVectorField getIndividualsField() {
		return m_individualsField;
	}

	public ArrayList<Individual> getIndividuals() {
		return m_individuals;
	}

	public HashMap<String, Object> getOutputHolder() {
		return m_outputHolder;
	}
	
	public void incrementIntegerValueOfOutputHolder(String key) {
		if (m_outputHolder.get(key) == null) {
			m_outputHolder.put(key, new Integer(1));
		}
		else {
			m_outputHolder.put(key, new Integer(((Integer) m_outputHolder.get(key)).intValue() + 1));
		}
	}

	public EnvironmentObserver getEnvironmentObserver() {
		return m_environmentObserver;
	}

	public DefaultCategoryDataset getActivityCategoryDataset() {
		return m_activityCategoryDataset;
	}

	public HashMap<ActivityCategory, ArrayList<Activity>> getCategoryToActivities() {
		return m_activityCategoryToActivitiesyMap;
	}
}
