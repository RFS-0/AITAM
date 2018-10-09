package rfs0.aitam.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
import activities.ActivityInitializer;
import individuals.Individual;
import individuals.IndividualInitializer;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.utilities.GeometryUtility;
import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;

public class Environment extends SimState {

	private static final long serialVersionUID = 1L;
	
	public static final GeometryFactory GEO_FACTORY = new GeometryFactory();
	public static HashMap<MasonGeometry, MasonGeometry> BUILDING_TO_CLOSEST_PATH_MAP = new HashMap<>();
	
	// Activities
	private Activity m_workAtHomeAloneActivity;
	private Activity m_workAtWorkPlaceAloneActivity;
	private Activity m_workAtWorkPlaceWithCoworkersActivity;
	private Activity m_workAtThirdPlaceForWorkAloneActivity;
	private Activity m_workAtThirdPlaceForWorkWithCoworkersActivity;
	private Activity m_workDuringTravelAloneActivity;
	private Activity m_workDuringTravelWithCoworkersActivity;
	private Activity m_leisureAtHomeAloneActivity;
	private Activity m_leisureAtHomeWithHouseholdMembersActivity;
	private Activity m_leisureAtHomeWithFriends;
	private Activity m_leisureAtThirdPlaceForLeisureAlone;
	private Activity m_leisureAtThirdPlaceForLeisureWithHouseholdMembersActivity;
	private Activity m_leisureAtThirdPlaceForLeisureWithFriends;
	private Activity m_personalCareAtHomeAloneActivity;
	private Activity m_personalCareAtHomeWithHouseholdMembersActivity;
	private Activity m_personalCareAtHomeWithFriendsActivity;
	private Activity m_personalCareAtWorkPlaceAloneActivity;
	private Activity m_personalCareAtWorkPlaceWithCoworkersActivity;
	private Activity m_personalCareAtThirdPlaceForPersonalCareAloneActivity;
	private Activity m_personalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity;
	private Activity m_personalCareAtThirdPlaceForPersonalCareWithFriendsActivity;
	private Activity m_householdAndFamilyCareAtHomeAloneActivity;
	private Activity m_householdAndFamilyCareAtHomeWithHousholdMembersActivty;
	private Activity m_householdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity;
	private Activity m_householdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembersActivity;
	private Activity m_travelActivity;
	private ArrayList<Activity> m_allActivities;

	// Time
	// TODO probably replace this with corresponding class of joda time
	private SimulationTime m_simulationTime = new SimulationTime();

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
	public ArrayList<Integer> m_individualsNotInitialized = IntStream.range(0, ISimulationSettings.NUMBER_OF_INDIVIDUALS).boxed().collect(Collectors.toCollection(ArrayList::new));
	
	public Environment(long seed) {
		super(seed);
		initEnvironment();
		initActivities();
		initBuildings();
		initIndividuals();
	}

	@Override
	public void start() {
		super.start();
		m_individualsGeomVectorField.clear();
		m_individualsGeomVectorField.setMBR(m_buildingsGeomVectorField.getMBR());
		for (Individual individual: m_individuals) {
			schedule.scheduleRepeating(individual);
		}
		schedule.scheduleRepeating(m_individualsGeomVectorField.scheduleSpatialIndexUpdater(), Integer.MAX_VALUE, 1.0);
		schedule.scheduleRepeating(0.0, 2, m_simulationTime); // update clock after indivdual have executed their step
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
		m_allActivities = Stream.of(
				m_workAtHomeAloneActivity,
				m_workAtWorkPlaceAloneActivity,
				m_workAtWorkPlaceWithCoworkersActivity,
				m_workAtThirdPlaceForWorkAloneActivity,
				m_workAtThirdPlaceForWorkWithCoworkersActivity,
				m_workDuringTravelAloneActivity,
				m_workDuringTravelWithCoworkersActivity,
				m_leisureAtHomeAloneActivity,
				m_leisureAtHomeWithHouseholdMembersActivity,
				m_leisureAtHomeWithFriends,
				m_leisureAtThirdPlaceForLeisureAlone,
				m_leisureAtThirdPlaceForLeisureWithHouseholdMembersActivity,
				m_leisureAtThirdPlaceForLeisureWithFriends,
				m_personalCareAtHomeAloneActivity,
				m_personalCareAtHomeWithHouseholdMembersActivity,
				m_personalCareAtHomeWithFriendsActivity,
				m_personalCareAtWorkPlaceAloneActivity,
				m_personalCareAtWorkPlaceWithCoworkersActivity,
				m_personalCareAtThirdPlaceForPersonalCareAloneActivity,
				m_personalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity,
				m_personalCareAtThirdPlaceForPersonalCareWithFriendsActivity,
				m_householdAndFamilyCareAtHomeAloneActivity,
				m_householdAndFamilyCareAtHomeWithHousholdMembersActivty,
				m_householdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity,
				m_householdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembersActivity,
				m_travelActivity)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private void initWorkActivities() {
		m_workAtHomeAloneActivity = ActivityInitializer.initWorkAtHomeAloneActivity();
		m_workAtWorkPlaceAloneActivity = ActivityInitializer.initWorkAtWorkPlaceAloneActivity();
		m_workAtWorkPlaceWithCoworkersActivity = ActivityInitializer.initWorkAtWorkPlaceWithCoworkers();
		m_workAtThirdPlaceForWorkAloneActivity = ActivityInitializer.initWorkAtThirdPlaceForWorkAloneActivity();
		m_workAtThirdPlaceForWorkWithCoworkersActivity = ActivityInitializer.initWortAtThirdPlaceForWorkWithCoworkers();
		m_workDuringTravelAloneActivity = ActivityInitializer.initWorkDuringTravelAloneActivity();
		m_workDuringTravelWithCoworkersActivity = ActivityInitializer.initWorkDuringTravelWithCoworkers();
	}

	private void initLeisureActivities() {
		m_leisureAtHomeAloneActivity = ActivityInitializer.initLeisureAtHomeAloneActivity();
		m_leisureAtHomeWithHouseholdMembersActivity = ActivityInitializer.initLeisureAtHomeWithHouseholdMembersActivity();
		m_leisureAtHomeWithFriends = ActivityInitializer.initLeisureAtHomeWithFriendsActivity();
		m_leisureAtThirdPlaceForLeisureAlone = ActivityInitializer.initLeisureAtThirdPlaceForLeisureAloneActivity();
		m_leisureAtThirdPlaceForLeisureWithHouseholdMembersActivity = ActivityInitializer.initLeisureAtThirdPlaceForLeisureWithHouseholdMembersActivity();
		m_leisureAtThirdPlaceForLeisureWithFriends = ActivityInitializer.initLeisureAtThirdPlaceForLeisureWithFriendsActivity();
	}
	
	private void initPersonalCareActivities() {
		m_personalCareAtHomeAloneActivity = ActivityInitializer.initPersonalCareAtHomeAloneActivity();
		m_personalCareAtHomeWithHouseholdMembersActivity = ActivityInitializer.initPersonalCareAtHomeWithHouseholdMembersActivity();
		m_personalCareAtHomeWithFriendsActivity = ActivityInitializer.initPersonalCareAtHomeWithFriendsActivity();
		m_personalCareAtWorkPlaceAloneActivity = ActivityInitializer.initPersonalCareAtWorkPlaceAloneActivity();
		m_personalCareAtWorkPlaceWithCoworkersActivity = ActivityInitializer.initPersonalCareAtWorkPlaceWithCoworkersActivity();
		m_personalCareAtThirdPlaceForPersonalCareAloneActivity = ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareAloneActivity();
		m_personalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity = ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity();
		m_personalCareAtThirdPlaceForPersonalCareWithFriendsActivity = ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity();
	}
	
	private void initHouseholdCareActivities() {
		m_householdAndFamilyCareAtHomeAloneActivity = ActivityInitializer.initHouseholdAndFamilyCareAtHomeAloneActivity();
		m_householdAndFamilyCareAtHomeWithHousholdMembersActivty = ActivityInitializer.initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty();
		m_householdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity = ActivityInitializer.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity();
		m_householdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembersActivity = ActivityInitializer.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembers();
	}
	
	private void initTravelActivities() {
		m_travelActivity = ActivityInitializer.initTravelActivity();
	}
	
	private void initIndividuals() {
		m_individuals =  IndividualInitializer.initIndividuals(this);
	}
	
	private void initBuildings() {
		initBuildingToClosestPathMap();
	}
	
	private void initBuildingToClosestPathMap() {
		for (Object buildingObject: m_buildingsGeomVectorField.getGeometries()) {
			MasonGeometry building = (MasonGeometry) buildingObject;
			MasonGeometry pathClosestToBuilding = getClosestPath(building);
			BUILDING_TO_CLOSEST_PATH_MAP.put(building, pathClosestToBuilding);
		}
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
	
	public ArrayList<Integer> getIndividualsNotInitialized() {
		return m_individualsNotInitialized;
	}
	
	public SimulationTime getSimulationTime() {
		return m_simulationTime;
	}

	public Activity getActivityWorkAtHomeAlone() {
		return m_workAtHomeAloneActivity;
	}

	public ArrayList<Activity> getAllActivities() {
		return m_allActivities;
	}
}
