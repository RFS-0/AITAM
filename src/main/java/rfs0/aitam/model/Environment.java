package rfs0.aitam.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.activities.Activity;
import rfs0.aitam.activities.ActivityInitializer;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.individuals.Individual;
import rfs0.aitam.individuals.IndividualInitializer;
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
	private SimulationTime m_simulationTime = new SimulationTime();

	// GIS
	private GeomVectorField m_buildingsField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of buildings
	private GeomVectorField m_pathField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of paths
	private GeomPlanarGraph m_pathGraph = new GeomPlanarGraph();
	private HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> m_edgeTraffic = new HashMap<>(); // used to capture the
	
	// Individuals
	private GeomVectorField m_individualsField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // used to represent the individuals
	private ArrayList<Individual> m_individuals = new ArrayList<>();
	
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
		// schedule the individual via anonymus classes
		for (Individual individual: getIndividuals()) {
			schedule.scheduleRepeating(0.0, 0, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					Environment environment = (Environment) state;
					if (individual.isPlanningPossible(environment, ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_OF_JOINT_ACTIVITIES)) {
						individual.planJointActivities(environment);
					}
				}
			});
			schedule.scheduleRepeating(0.0, 1, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					individual.carryOverJointActivities((Environment) state);
				}
			});
			schedule.scheduleRepeating(0.0, 2, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					Environment environment = (Environment) state;
					if (individual.isPlanningPossible(environment, ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_OF_INDIVIDUAL_ACTIVITIES)) {
						individual.planIndividualActivities(environment);
					}
				}
			});
			schedule.scheduleRepeating(0.0, 3, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					individual.chooseBestAgenda();
				}
			});
			schedule.scheduleRepeating(0.0, 4, new Steppable() {			
				private static final long serialVersionUID = 1L;
				@Override
				public void step(SimState state) {
					individual.executeActivity((Environment) state);
				}
			});
		}
		schedule.scheduleRepeating(0.0, 5, m_individualsField.scheduleSpatialIndexUpdater());
		schedule.scheduleRepeating(0.0, 6, getSimulationTime());
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
		m_pathGraph.createFromGeomField(getPathField());
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
	}
	
	private void initActivities() {
		System.out.println("Initializing activities...");
		long start = System.nanoTime();
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
		System.out.println(String.format("Initialized activities in %d ms", (System.nanoTime() - start) / 1000000));
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
		System.out.println("Initializing individuals...");
		m_individualsField.clear();
		long start = System.nanoTime();
		m_individuals = IndividualInitializer.initIndividuals(this);
		for (Individual individual: m_individuals) {
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
			Node closestNode = null;
			Bag alreadyChecked = new Bag();
			double searchDistance = 0.0;
			while (closestNode == null) {
				Bag withinDistance = m_pathField.getObjectsWithinDistance(GEO_FACTORY.createPoint(building.getGeometry().getCoordinate()), searchDistance);
				withinDistance.removeAll(alreadyChecked);
				while (!withinDistance.isEmpty() && closestNode == null) {
					closestNode = m_pathGraph.findNode(((MasonGeometry) withinDistance.get(0)).getGeometry().getCoordinate());
				}
				if (withinDistance.size() > 0) {
					alreadyChecked.add(withinDistance.remove(0));
				}
				searchDistance += 1.0;
			}
			BUILDING_TO_CLOSEST_NODE_MAP.put(building, closestNode);
		}
	}

	public ArrayList<Activity> getAllActivities() {
		return m_allActivities;
	}

	public void setAllActivities(ArrayList<Activity> allActivities) {
		m_allActivities = allActivities;
	}

	public SimulationTime getSimulationTime() {
		return m_simulationTime;
	}

	public void setSimulationTime(SimulationTime simulationTime) {
		m_simulationTime = simulationTime;
	}

	public GeomVectorField getBuildingsField() {
		return m_buildingsField;
	}

	public void setBuildingsField(GeomVectorField buildingsField) {
		m_buildingsField = buildingsField;
	}

	public GeomVectorField getPathField() {
		return m_pathField;
	}

	public void setPathField(GeomVectorField pathField) {
		m_pathField = pathField;
	}

	public GeomPlanarGraph getPathGraph() {
		return m_pathGraph;
	}

	public void setPathGraph(GeomPlanarGraph pathGraph) {
		m_pathGraph = pathGraph;
	}

	public HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> getEdgeTraffic() {
		return m_edgeTraffic;
	}

	public void setEdgeTraffic(HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> edgeTraffic) {
		m_edgeTraffic = edgeTraffic;
	}

	public GeomVectorField getIndividualsField() {
		return m_individualsField;
	}

	public void setIndividualsField(GeomVectorField individualsField) {
		m_individualsField = individualsField;
	}

	public ArrayList<Individual> getIndividuals() {
		return m_individuals;
	}

	public void setIndividuals(ArrayList<Individual> individuals) {
		m_individuals = individuals;
	}
}
