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

import org.jfree.data.category.DefaultCategoryDataset;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.activities.Activity;
import rfs0.aitam.activities.ActivityCategory;
import rfs0.aitam.activities.ActivityInitializer;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.individuals.Individual;
import rfs0.aitam.individuals.IndividualInitializer;
import rfs0.aitam.utilities.GeometryUtility;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;

public class Environment extends SimState {

	private static final long serialVersionUID = 1L;
	
	public static final GeometryFactory GEO_FACTORY = new GeometryFactory();
	public static HashMap<MasonGeometry, Node> BUILDING_TO_CLOSEST_NODE_MAP = new HashMap<>();
	public static HashMap<Node, MasonGeometry> NODE_TO_CLOSEST_BUILDING_MAP = new HashMap<>();
	
	// Activities
	private HashMap<String, Activity> m_activityDescriptionToActivityMap = new HashMap<>();
	private HashMap<ActivityCategory, ArrayList<Activity>> m_activityCategoryToActivityMap = new HashMap<>();
	private DefaultCategoryDataset m_activityCategoryDataset = new DefaultCategoryDataset();

	// Time
	private SimulationTime m_simulationTime = new SimulationTime();

	// GIS
	private GeomVectorField m_buildingsField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of buildings
	private GeomVectorField m_pathField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of paths
	private GeomPlanarGraph m_pathGraph = new GeomPlanarGraph();
	private HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> m_edgeTraffic = new HashMap<>(); // used to capture the
	
	// Individuals
	private GeomVectorField m_individualsField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // used to represent the individuals
	private ArrayList<Individual> m_individuals = new ArrayList<>();
	private ArrayList<MasonGeometry> m_currentLocationPoints = new ArrayList<>();
	private ArrayList<Node> m_currentNodes = new ArrayList<>();
	
	// Output
	private LinkedHashMap<String, Object> m_outputHolder = new LinkedHashMap<>();
	private EnvironmentObserver m_environmentObserver;
	
	public Environment(long seed) { // TODO: seed is only for dev purposes
		super(seed); 
		random.setSeed(seed); 
		initEnvironment();
		initActivities();
		initBuildings();
		initIndividuals();
		initOutput();
	}

	@Override
	public void start() {
		super.start();
		// schedule the individual via anonymus classes
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
					individual.executeActivity();
				}
			});
			schedule.scheduleRepeating(0.0, Integer.MAX_VALUE, new Steppable() {
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					Environment environment = (Environment) state;
					if (environment.getSimulationTime().getCurrentTime().equals(ISimulationSettings.START_OF_DAY)) {
						individual.initNewDay();
					}
				}
			});
		}
		schedule.scheduleRepeating(0.0, 10, m_individualsField.scheduleSpatialIndexUpdater());
		schedule.scheduleRepeating(0.0, 11, new Steppable() {
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					String title = "Activities";
					Integer totalNumberOfAgents = (Integer) m_outputHolder.get(ISimulationSettings.TOTAL_NUMBER_OF_AGENTS);
					for (ActivityCategory category: m_activityCategoryToActivityMap.keySet()) {
						ArrayList<Activity> activitiesOfCategory = m_activityCategoryToActivityMap.get(category);
						int totalNumberOfAgentsPerCategory = 0;
						for (Activity activityOfCategory: activitiesOfCategory) {
							if (m_outputHolder.get(activityOfCategory.getActivityDescription()) instanceof Integer) {
								totalNumberOfAgentsPerCategory += ((Integer) m_outputHolder.get(activityOfCategory.getActivityDescription())).intValue();
							}
						}
						double fractionOfCategory = (double) totalNumberOfAgentsPerCategory / totalNumberOfAgents * 100;
						m_activityCategoryDataset.addValue(fractionOfCategory, title, category.toString());
					}
				}
		});
		schedule.scheduleRepeating(0.0, 12, m_environmentObserver);
		schedule.scheduleRepeating(0.0, 13, m_simulationTime);
		schedule.scheduleRepeating(0.0, 14, new Steppable() {
			private static final long serialVersionUID = 1L;
			@Override
			public void step(SimState state) {
				Environment environment = (Environment) state;
				environment.getCurrentLocationPoints().clear();
				environment.getCurrentNodes().clear();
				for (Individual individual: environment.getIndividuals()) {
					environment.getCurrentNodes().add(individual.getCurrentNode());
					environment.getCurrentLocationPoints().add(individual.getCurrentLocationPoint());
				}
			}
	});
	}
	
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
	
	public static void main(String[] args) {
		doLoop(Environment.class, args);
		System.exit(0);
	}
	
	private void initEnvironment() {
		System.out.println("Initializing the environment...");
		long start = System.nanoTime();
		Envelope globalMBR = new Envelope();
		readShapeFiles(globalMBR);
		synchronizeMinimumBoundingRectangles(globalMBR);
		m_pathGraph.createFromGeomField(m_pathField);
		System.out.println(String.format("Initialized environment in %d ms", (System.nanoTime() - start) / 1000000));
	}
	
	private void readShapeFiles(Envelope globalMBR) {
		try {
			System.out.println("Reading building layer...");
			Bag attributesOfBuildings = initAttributesOfBuildings();
			readShapeFile(ISimulationSettings.BUILDINGS_FILE, m_buildingsField, globalMBR, attributesOfBuildings);

			System.out.println("Reading the path layer...");
			Bag attributesOfPaths = initializeAttributesOfPaths();
			readShapeFile(ISimulationSettings.PATHS_FILE, m_pathField, globalMBR, attributesOfPaths);
		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, "Failed to read shape files", e);
		}
	}
	
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

	private void readShapeFile(String relativePathToFile, GeomVectorField geometry, Envelope minimumBoundingRectangle, Bag attributes) {
		try {
			URL url = new File(System.getProperty("user.dir") + relativePathToFile).toURI().toURL();
			ShapeFileImporter.read(url, geometry, attributes);
			minimumBoundingRectangle.expandToInclude(geometry.getMBR());

		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, String.format("Failed to read GIS file with path: %s", relativePathToFile), e);
		}
	}
	
	private void synchronizeMinimumBoundingRectangles(Envelope minimumBoundingRectangle) {
		m_buildingsField.setMBR(minimumBoundingRectangle);
		m_pathField.setMBR(minimumBoundingRectangle);
		m_individualsField.setMBR(minimumBoundingRectangle);
	}
	
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

	private void initWorkActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_HOME_ALONE, ActivityInitializer.initWorkAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_WORK_PLACE_ALONE, ActivityInitializer.initWorkAtWorkPlaceAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_WORK_PLACE_WITH_COWORKERS, ActivityInitializer.initWorkAtWorkPlaceWithCoworkers());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_THIRD_PLACE_ALONE, ActivityInitializer.initWorkAtThirdPlaceForWorkAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_AT_THIRD_PLACE_WITH_COWORKERS, ActivityInitializer.initWortAtThirdPlaceForWorkWithCoworkers());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_DURING_TRAVEL_ALONE, ActivityInitializer.initWorkDuringTravelAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.WORK_DURING_TRAVEL_WITH_COWORKERS, ActivityInitializer.initWorkDuringTravelWithCoworkers());
	}

	private void initLeisureActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_HOME_ALONE_ACTIVITY, ActivityInitializer.initLeisureAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_HOME_WITH_HOUSEHOLD_MEMBERS, ActivityInitializer.initLeisureAtHomeWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_HOME_WITH_FRIENDS, ActivityInitializer.initLeisureAtHomeWithFriendsActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_THIRD_PLACE_ALONE, ActivityInitializer.initLeisureAtThirdPlaceForLeisureAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS, ActivityInitializer.initLeisureAtThirdPlaceForLeisureWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.LEISURE_AT_THIRD_PLACE_WITH_FRIENDS, ActivityInitializer.initLeisureAtThirdPlaceForLeisureWithFriendsActivity());
	}
	
	private void initPersonalCareActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_HOME_ALONE, ActivityInitializer.initPersonalCareAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS, ActivityInitializer.initPersonalCareAtHomeWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_HOME_WITH_FRIENDS, ActivityInitializer.initPersonalCareAtHomeWithFriendsActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_WORK_ALONE, ActivityInitializer.initPersonalCareAtWorkPlaceAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_WORK_WITH_COWORKERS, ActivityInitializer.initPersonalCareAtWorkPlaceWithCoworkersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_ALONE, ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS, ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_WITH_FRIENDS, ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity());
	}
	
	private void initHouseholdCareActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_ALONE, ActivityInitializer.initHouseholdAndFamilyCareAtHomeAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS, ActivityInitializer.initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_ALONE, ActivityInitializer.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS, ActivityInitializer.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembers());
	}
	
	private void initTravelActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.TRAVEL, ActivityInitializer.initTravelActivity());
	}
	
	private void initIdleActivities() {
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_HOME, ActivityInitializer.initIdleAtHomeActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_LEISURE, ActivityInitializer.initIdleAtLeisureActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_WORK, ActivityInitializer.initIdleAtWorkActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, ActivityInitializer.initIdleAtThirdPlaceForHouseholdAndFamilyCareActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_LEISURE, ActivityInitializer.initIdleAtThirdPlaceForLeisureActivity());
		m_activityDescriptionToActivityMap.put(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_WORK, ActivityInitializer.initIdleAtThirdPlaceForWorkActivity());
	}
	
	private void initIndividuals() {
		System.out.println("Initializing individuals...");
		long start = System.nanoTime();
		m_individuals = IndividualInitializer.initIndividuals(this);
		for (Individual individual: m_individuals) {
			MasonGeometry geometryOfIndividual = individual.getCurrentLocationPoint();
			geometryOfIndividual.setUserData(individual);
			m_individualsField.addGeometry(individual.getCurrentLocationPoint());
			
		}
		m_individualsField.updateSpatialIndex();
		System.out.println(String.format("Initialized individuals in %d ms", (System.nanoTime() - start) / 1000000));
	}
	
	private void initBuildings() {
		System.out.println("Initializing buildings...");
		long start = System.nanoTime();
		initBuildingToClosestNodeMap();
		System.out.println(String.format("Initialized buildings in %d ms", (System.nanoTime() - start) / 1000000));
	}
	
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
	
	private void initOutput() {
		m_outputHolder.put(ISimulationSettings.TIME_STAMP, m_simulationTime.getCurrentDateTime());
		m_outputHolder.put(ISimulationSettings.TOTAL_NUMBER_OF_AGENTS, ISimulationSettings.NUMBER_OF_INDIVIDUALS);
		for (ActivityCategory category: ActivityCategory.values()) {
			ArrayList<Activity> activitiesOfCategory = m_activityDescriptionToActivityMap.values().stream().filter(activity -> activity.getActivityCategory() == category).collect(Collectors.toCollection(ArrayList::new));
			if (m_activityCategoryToActivityMap.get(category) == null) {
				m_activityCategoryToActivityMap.put(category, new ArrayList<>());
			}
			m_activityCategoryToActivityMap.get(category).addAll(activitiesOfCategory);
		}
		m_environmentObserver = new EnvironmentObserver(getOutputHolder().keySet());
	}
	
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
		return m_activityCategoryToActivityMap;
	}

	public ArrayList<MasonGeometry> getCurrentLocationPoints() {
		return m_currentLocationPoints;
	}

	public void setCurrentPoints(ArrayList<MasonGeometry> currentPoints) {
		m_currentLocationPoints = currentPoints;
	}

	public ArrayList<Node> getCurrentNodes() {
		return m_currentNodes;
	}

	public void setCurrentNodes(ArrayList<Node> currentNodes) {
		m_currentNodes = currentNodes;
	}
}
