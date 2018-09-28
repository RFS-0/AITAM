package rfs0.aitam.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.planargraph.Node;

import activities.Activity;
import activities.ActivityBuilder;
import activities.WeekDay;
import rfs0.aitam.commons.IDevSettings;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.utilities.GeometryUtility;
import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
import sim.portrayal.geo.GeomPortrayal;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;

public class Environment extends SimState {

	private static final long serialVersionUID = 1L;
	
	public static final String MASON_GEOMETRY_OF_CLOSEST_PATH = "masonGeometryOfClosestPath"; // TODO: check if this is really necessary
	public static final GeometryFactory GEO_FACTORY = new GeometryFactory();
	public static HashMap<MasonGeometry, MasonGeometry> BUILDING_TO_CLOSEST_PATH_MAP = new HashMap<>();
	
	// Activities
	public static ArrayList<WeekDay> WEEK = Stream.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY, WeekDay.SATURDAY, WeekDay.SUNDAY).collect(Collectors.toCollection(ArrayList::new));
	public static ArrayList<WeekDay> WORK_WEEK = Stream.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY).collect(Collectors.toCollection(ArrayList::new));
	public static ArrayList<WeekDay> WEEKEND = Stream.of(WeekDay.SATURDAY, WeekDay.SUNDAY).collect(Collectors.toCollection(ArrayList::new));
	public static Activity ACTIVITY_WORK_AT_HOME_ALONE;
	public static Activity ACTIVITY_WORK_AT_WORK_LOCATION_ALONE;
	public static Activity ACTIVITY_WORK_AT_WORK_LOCATION_WITH_COWORKERS;
	public static Activity ACTIVITY_WORK_AT_THIRD_WORK_LOCATION_ALONE;
	public static Activity ACTIVITY_WORK_AT_THIRD_WORK_LOCATION_WITH_COWORKERS;
	public static Activity ACTIVITY_WORK_DURING_TRAVEL_ALONE;
	public static Activity ACTIVITY_WORK_DURING_TRAVEL_WITH_COWORKERS;
	public static Activity ACTIVITY_LEISURE_AT_HOME_ALONE;
	public static Activity ACTIVITY_LEISURE_AT_HOME_WITH_HOUSEHOLD_MEMBERS;
	public static Activity ACTIVITY_LEISURE_AT_HOME_WITH_FRIENDS;
	public static Activity ACTIVITY_LEISURE_AT_THIRD_PLACE_ALONE;
	public static Activity ACTIVITY_LEISURE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS;
	public static Activity ACTIVITY_LEISURE_AT_THIRD_PLACE_WITH_FRIENDS;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_HOME_ALONE;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_HOME_WITH_FRIENDS;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_WORK_ALONE;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_WORK_WITH_COWORKERS;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_THIRD_PLACE_ALONE;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS;
	public static Activity ACTIVITY_PERSONAL_CARE_AT_THIRD_PLACE_WITH_FRIENDS;
	public static Activity ACTIVITY_HOUSEHOLD_CARE_AT_HOME_ALONE;
	public static Activity ACTIVITY_HOUSEHOLD_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS;
	public static Activity ACTIVITY_HOUSEHOLD_CARE_AT_THIRD_PLACE_ALONE;
	public static Activity ACTIVITY_HOUSEHOLD_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS;
	public static Activity ACTIVITY_TRAVEL;

	// Time
	// TODO probably replace this with corresponding class of joda time
	public static Calendar CALENDAR = new Calendar.Builder().setDate(0, 0, 0).setTimeOfDay(0, 0, 0).build();

	// GIS
	public GeomVectorField m_buildingsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of buildings
	public GeomVectorField m_pathsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of paths
	public GeomPlanarGraph m_pathNetworkGeomVectorField = new GeomPlanarGraph(); // represents graph all paths
	public GeomVectorField m_pathIntersectionsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // TODO: check if necessary at all (represents all crossings)
	public HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> m_edgeTrafficMap = new HashMap<>(); // used to capture the
	
	// Buildings
	public Set<Integer> m_buildingsNotInitialized = IntStream.range(0, m_buildingsGeomVectorField.getGeometries().size()).boxed().collect(Collectors.toSet());

	// Individuals
	public GeomVectorField m_individualsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // used to represent the individuals
	public ArrayList<Individual> m_individuals = new ArrayList<>();
	
	public Environment(long seed) {
		super(seed);
		initEnvironment();
		initActivities();
		initIndividuals();
		initBuildings();
	}

	@Override
	public void start() {
		super.start();
		m_individualsGeomVectorField.clear();
		m_individualsGeomVectorField.setMBR(m_buildingsGeomVectorField.getMBR());
		schedule.scheduleRepeating(m_individualsGeomVectorField.scheduleSpatialIndexUpdater(), Integer.MAX_VALUE, 1.0);
	}
	
	public static void main(String[] args) {
		doLoop(Environment.class, args);
		System.exit(0);
	}
	
	@SuppressWarnings("unchecked")
	private void initEnvironment() {
		Envelope globalMBR = m_buildingsGeomVectorField.getMBR();
		readShapeFiles(globalMBR);
		synchronizeMinimumBoundingRectangles(globalMBR);
		m_pathNetworkGeomVectorField.createFromGeomField(m_pathsGeomVectorField);
		addIntersectionNodes(m_pathNetworkGeomVectorField.nodeIterator());
	}
	
	private void readShapeFiles(Envelope globalMBR) {
		try {
			System.out.println("Reading building layer...");
			Bag attributesOfBuildings = initAttributesOfBuildings();
			readShapeFile(ISimulationSettings.BUILDINGS_FILE, m_buildingsGeomVectorField, globalMBR, attributesOfBuildings);

			System.out.println("Reading the path layer...");
			Bag attributesOfPaths = initializeAttributesOfPaths();
			readShapeFile(ISimulationSettings.PATHS_FILE, m_pathsGeomVectorField, globalMBR, attributesOfPaths);
		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, "Failed to construct simulation", e);
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

	private void readShapeFile(String relativePathToFile, GeomVectorField geometry, Envelope minimumBoundingRectangle,
			Bag attributes) {
		try {
			URL url = new File(System.getProperty("user.dir") + relativePathToFile).toURI().toURL();
			ShapeFileImporter.read(url, geometry, attributes);
			minimumBoundingRectangle.expandToInclude(geometry.getMBR());

		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName())
					.log(Level.SEVERE, String.format("Failed to read GIS file with path: %s", relativePathToFile), e);
		}
	}
	
	private void synchronizeMinimumBoundingRectangles(Envelope minimumBoundingRectangle) {
		m_buildingsGeomVectorField.setMBR(minimumBoundingRectangle);
		m_pathsGeomVectorField.setMBR(minimumBoundingRectangle);
	}
	
	// TODO: check if necessary at all
	private void addIntersectionNodes(Iterator<Node> nodeIterator) {
		nodeIterator.forEachRemaining(node -> {
			Point point = GEO_FACTORY.createPoint(node.getCoordinate());
			m_pathIntersectionsGeomVectorField.addGeometry(new MasonGeometry(point));
		});
	}
	
	private void initActivities() {
		initLeisureActivities();		
		initWorkActivities();
		initPersonalCareActivities();
		initHouseholdCareActivities();
		initTravelActivities();
	}

	private void initWorkActivities() {
		ACTIVITY_WORK_AT_HOME_ALONE = ActivityBuilder.initWorkAtHomeAloneActivity();
		ACTIVITY_WORK_AT_WORK_LOCATION_ALONE = ActivityBuilder.initWorkAtWorkLocationAloneActivity();
		ACTIVITY_WORK_AT_WORK_LOCATION_WITH_COWORKERS = ActivityBuilder.initWorkAtWorkLocationWithCoworkers();
		ACTIVITY_WORK_AT_THIRD_WORK_LOCATION_ALONE = ActivityBuilder.initWorkAtThirdWorkLocationAloneActivity();
		ACTIVITY_WORK_AT_THIRD_WORK_LOCATION_WITH_COWORKERS = ActivityBuilder.initWortAtThirdWorkLocationWithCoworkers();
		ACTIVITY_WORK_DURING_TRAVEL_ALONE = ActivityBuilder.initWorkDuringTravelAloneActivity();
		ACTIVITY_WORK_DURING_TRAVEL_WITH_COWORKERS = ActivityBuilder.initWorkDuringTravelWithCoworkers();
	}

	private void initLeisureActivities() {
		ACTIVITY_LEISURE_AT_HOME_ALONE = ActivityBuilder.initLeisureAtHomeAloneActivity();
		ACTIVITY_LEISURE_AT_HOME_WITH_HOUSEHOLD_MEMBERS = ActivityBuilder.initLeisureAtHomeWithHouseholdMembersActivity();
		ACTIVITY_LEISURE_AT_HOME_WITH_FRIENDS = ActivityBuilder.initLeisureAtHomeWithFriendsActivity();
		ACTIVITY_LEISURE_AT_THIRD_PLACE_ALONE = ActivityBuilder.initLeisureAtThirdPlaceAloneActivity();
		ACTIVITY_LEISURE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS = ActivityBuilder.initLeisureAtThirdPlaceWithHouseholdMembersActivity();
		ACTIVITY_LEISURE_AT_THIRD_PLACE_WITH_FRIENDS = ActivityBuilder.initLeisureAtThirdPlaceWithFriendsActivity();
	}
	
	private void initPersonalCareActivities() {
		ACTIVITY_PERSONAL_CARE_AT_HOME_ALONE = ActivityBuilder.initPersonalCareAtHomeAloneActivity();
		ACTIVITY_PERSONAL_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS = ActivityBuilder.initPersonalCareAtHomeWithHouseholdMembersActivity();
		ACTIVITY_PERSONAL_CARE_AT_HOME_WITH_FRIENDS = ActivityBuilder.initPersonalCareAtHomeWithFriendsActivity();
		ACTIVITY_PERSONAL_CARE_AT_WORK_ALONE = ActivityBuilder.initPersonalCareAtWorkAloneActivity();
		ACTIVITY_PERSONAL_CARE_AT_WORK_WITH_COWORKERS = ActivityBuilder.initPersonalCareAtWorkWithCoworkersActivity();
		ACTIVITY_PERSONAL_CARE_AT_THIRD_PLACE_ALONE = ActivityBuilder.initPersonalCareAtThirdPlaceAloneActivity();
		ACTIVITY_PERSONAL_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS = ActivityBuilder.initPersonalCareAtThirdPlaceWithHouseholdMembersActivity();
		ACTIVITY_PERSONAL_CARE_AT_THIRD_PLACE_WITH_FRIENDS = ActivityBuilder.initPersonalCareAtThirdPlaceWithFriendsActivity();
	}
	
	private void initHouseholdCareActivities() {
		ACTIVITY_HOUSEHOLD_CARE_AT_HOME_ALONE = ActivityBuilder.initHouseholdCareAtHomeAloneActivity();
		ACTIVITY_HOUSEHOLD_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS = ActivityBuilder.initHouseholdCareAtHomeWithHousholdMembersActivty();
		ACTIVITY_HOUSEHOLD_CARE_AT_THIRD_PLACE_ALONE = ActivityBuilder.initHouseholdCareAtThirdPlaceAloneActivity();
		ACTIVITY_HOUSEHOLD_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS = ActivityBuilder.initHouseholdCareAtThirdPlaceWithHouseholdMembers();
	}
	
	private void initTravelActivities() {
		ACTIVITY_TRAVEL = ActivityBuilder.initTravelActivity();
	}
	
	private void initIndividuals() {
		Individual.Builder individualBuilder = new Individual.Builder(this);
		Activity.Builder activityBuilder = new Activity.Builder();
		
		// setup test individual 1
		Individual individual1 = individualBuilder
				.withHomeLocation(IDevSettings.DEV_BUILDINGS.get(IDevSettings.INDEX_OF_HOME_BUILDING_1)) 
				.withTargetLocation(IDevSettings.DEV_BUILDINGS.get(IDevSettings.INDEX_OF_INDIVIDUAL_ACTIVITY_BUILDING_1))
				.withIndividualActivity(activityBuilder
						.build())
				.withIndividualActivity(activityBuilder
						.build())
				.build();
		m_individuals.add(individual1);
		m_individualsGeomVectorField.addGeometry(individual1.getCurrentLocation());
		schedule.scheduleRepeating(individual1);
		
		// setup test individual 2
		Individual individual2 = individualBuilder
				.withHomeLocation(IDevSettings.DEV_BUILDINGS.get(1)) // index of HOME_BUILDING_2
				.withTargetLocation(IDevSettings.DEV_BUILDINGS.get(3)) // index of INDIVIDUAL_ACTIVITY_BUILDING_2
				.build();
		m_individuals.add(individual2);
		m_individualsGeomVectorField.addGeometry(individual2.getCurrentLocation());
		schedule.scheduleRepeating(individual2);
	}
	
	private void initBuildings() {
		initDevBuilding(IDevSettings.HOME_BUILDING_1);
		initDevBuilding(IDevSettings.HOME_BUILDING_2);
		initDevBuilding(IDevSettings.INDIVIDUAL_ACTIVITY_BUILDING_1);
		initDevBuilding(IDevSettings.INDIVIDUAL_ACTIVITY_BUILDING_2);
		initDevBuilding(IDevSettings.GENERAL_ACTIVITY_BUILDING_1);
		initDevBuilding(IDevSettings.GENERAL_ACTIVITY_BUILDING_2);
	}

	private void initDevBuilding(int building) {
		MasonGeometry initBuilding = (MasonGeometry) m_buildingsGeomVectorField.getGeometries().get(building);
		IDevSettings.DEV_BUILDINGS.add(initBuilding);
		initBuilding.setUserData(new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING_SELECTED, ISimulationSettings.SIZE_OF_BUILDING));
		MasonGeometry closestPathToHome = getClosestPath(initBuilding);
		closestPathToHome.setUserData(new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH_SELECTED, ISimulationSettings.SIZE_OF_PATH));
		BUILDING_TO_CLOSEST_PATH_MAP.put(initBuilding, closestPathToHome);
		initBuilding.addAttribute(MASON_GEOMETRY_OF_CLOSEST_PATH, closestPathToHome);
		m_buildingsNotInitialized.remove(building);
	}

	public MasonGeometry getClosestPath(MasonGeometry geometry) {
		double searchDistance = 0.0;
		MasonGeometry closestPath = null;
		while (closestPath == null) {
			Bag withinDistance = m_pathsGeomVectorField.getObjectsWithinDistance(GEO_FACTORY.createPoint(geometry.getGeometry().getCoordinate()), searchDistance);
			closestPath = GeometryUtility.findClosestGeometry(geometry, withinDistance);
			searchDistance += 1.0;
		}
		return closestPath;
	}
	
	public ArrayList<Individual> getIndividuals() {
		return m_individuals;
	}
	
	public GeomVectorField getPathsGeomVectorField() {
		return m_pathsGeomVectorField;
	}
	
	public GeomVectorField getBuildingsGeomVectorField() {
		return m_buildingsGeomVectorField;
	}
	
	public GeomVectorField getPathIntersectionsGeomVectorField() {
		return m_pathIntersectionsGeomVectorField;
	}
	
	public GeomPlanarGraph getPathNetworkGeomVectorField() {
		return m_pathNetworkGeomVectorField;
	}
}
