package rfs0.aitam.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.planargraph.Node;

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

	// Time
	// TODO probably replace this with corresponding class of joda time
	public static Calendar CALENDAR = new Calendar.Builder().setDate(0, 0, 0).setTimeOfDay(0, 0, 0).build();

	// GIS
	public GeomVectorField m_buildingsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of buildings
	public GeomVectorField m_pathsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // holds GIS data of paths
	public GeomPlanarGraph m_pathNetworkGeomVectorField = new GeomPlanarGraph(); // represents graph all paths
	public GeomVectorField m_pathIntersectionsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // TODO: check if necessary at all (represents all crossings)
	public HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> m_edgeTrafficMap = new HashMap<>(); // used to capture the

	// Individuals
	public GeomVectorField m_individualsGeomVectorField = new GeomVectorField(ISimulationSettings.ENVIRONMENT_WIDTH, ISimulationSettings.ENVIRONMENT_HEIGHT); // used to represent the individuals
	public ArrayList<Individual> m_individuals = new ArrayList<>();
	
	public Environment(long seed) {
		super(seed);
		initEnvironment();
		setupEnvironment();
	}

	@Override
	public void start() {
		super.start();
		m_individualsGeomVectorField.clear();
		initAgents();
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
	
	/**
	 * adds nodes corresponding to road intersections to GeomVectorField
	 *
	 * @param nodeIterator  Points to first node
	 * @param intersections GeomVectorField containing intersection geometry
	 *
	 *                      Nodes will belong to a planar graph populated from
	 *                      LineString network.
	 */
	// TODO: check if necessary at all
	private void addIntersectionNodes(Iterator<Node> nodeIterator) {
		nodeIterator.forEachRemaining(node -> {
			Point point = GEO_FACTORY.createPoint(node.getCoordinate());
			m_pathIntersectionsGeomVectorField.addGeometry(new MasonGeometry(point));
		});
	}
	
	private void setupEnvironment() {
		allocateActivitiesToBuildings();
	}
	
	private void allocateActivitiesToBuildings() {
		// home
		MasonGeometry home = (MasonGeometry) m_buildingsGeomVectorField.getGeometries().get(IDevSettings.START_BUILDING);
		IDevSettings.DEV_BUILDINGS.add(home);
		home.setUserData(new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING_SELECTED, ISimulationSettings.SIZE_OF_BUILDING));
		MasonGeometry closestPathToHome = getClosestPath(home);
		closestPathToHome.setUserData(new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH_SELECTED, ISimulationSettings.SIZE_OF_PATH));
		BUILDING_TO_CLOSEST_PATH_MAP.put(home, closestPathToHome);
		home.addAttribute(MASON_GEOMETRY_OF_CLOSEST_PATH, closestPathToHome);

		// some target building
		MasonGeometry target = (MasonGeometry) m_buildingsGeomVectorField.getGeometries().get(IDevSettings.TARGET_BUILDING);
		IDevSettings.DEV_BUILDINGS.add(target);
		target.setUserData(new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING_SELECTED, ISimulationSettings.SIZE_OF_BUILDING));
		MasonGeometry closestPathToTarget = getClosestPath(target);
		closestPathToTarget.setUserData(new GeomPortrayal(ISimulationSettings.COLOR_OF_PATH_SELECTED, ISimulationSettings.SIZE_OF_PATH));
		BUILDING_TO_CLOSEST_PATH_MAP.put(target, closestPathToTarget);
		target.addAttribute(MASON_GEOMETRY_OF_CLOSEST_PATH, closestPathToTarget);
	}
	
	private void initAgents() {
		Individual.Builder builder = new Individual.Builder(this);
		for (int i = 0; i < ISimulationSettings.NUMBER_OF_AGENTS; i++) {
			Individual individual = builder
					.withHomeLocation(IDevSettings.DEV_BUILDINGS.get(i))
					.withTargetLocation(IDevSettings.DEV_BUILDINGS.get((i+1) % 2))
					.build();
			m_individuals.add(individual);
			m_individualsGeomVectorField.addGeometry(individual.getCurrentLocation());
			schedule.scheduleRepeating(individual);
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
}
