package rfs0.aitam.model;

import java.awt.Color;
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

import rfs0.aitam.utilities.GeometryUtility;
import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.field.grid.ObjectGrid2D;
import sim.io.geo.ShapeFileImporter;
import sim.portrayal.geo.GeomPortrayal;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;

public class Environment extends SimState {

	// DEV-Variables
	public ArrayList<MasonGeometry> DEV_BUILDINGS = new ArrayList<>();
	public HashMap<MasonGeometry, MasonGeometry> BUILDING_TO_CLOSEST_PATH_MAP = new HashMap<>();

	private static final long serialVersionUID = 1L;

	// TODO: use properties file to overwrite these settings
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	public static final int NUMBER_OF_AGENTS = 2;
	public static final String BUILDINGS_FILE = "\\data\\environment\\buildings\\buildings.shp";
	public static final String PATHS_FILE = "\\data\\environment\\paths\\paths.shp";

	public static final GeometryFactory GEO_FACTORY = new GeometryFactory();

	// Time
	// TODO probably replace this with corresponding class of joda time
	public static Calendar CALENDAR = new Calendar.Builder().setDate(0, 0, 0).setTimeOfDay(0, 0, 0).build();

	// GIS
	public GeomVectorField m_buildings = new GeomVectorField(WIDTH, HEIGHT); // holds GIS data of buildings
	public GeomVectorField m_paths = new GeomVectorField(WIDTH, HEIGHT); // holds GIS data of paths
	public GeomPlanarGraph m_pathNetwork = new GeomPlanarGraph(); // represents graph all paths
	public GeomVectorField m_pathIntersections = new GeomVectorField(WIDTH, HEIGHT); // represents all crossings
	public HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> m_edgeTraffic = new HashMap<>(); // used to capture the
																								// usage of edges
	public ObjectGrid2D m_landGrid; // holds parcels
	public Bag m_parcels; // used to capture information about the current state of the environment

	// Individuals
	public GeomVectorField m_individuals = new GeomVectorField(WIDTH, HEIGHT); // used to represent the individuals

	public Environment(long seed) {
		super(seed);
		initBasicEnvironment();

		setupEnvironment();
	}

	@Override
	public void start() {
		super.start();
		m_individuals.clear();
		initAgents();
		m_individuals.setMBR(m_buildings.getMBR());
		schedule.scheduleRepeating(m_individuals.scheduleSpatialIndexUpdater(), Integer.MAX_VALUE, 1.0);
	}

	private void setupEnvironment() {
		allocateActivitiesToBuildings();
	}

	@SuppressWarnings("unchecked")
	private void initBasicEnvironment() {
		Envelope globalMBR = m_buildings.getMBR();
		readShapeFiles(globalMBR);
		synchronizeMinimumBoundingRectangles(globalMBR);
		m_pathNetwork.createFromGeomField(m_paths);
		addIntersectionNodes(m_pathNetwork.nodeIterator());
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

	private void readShapeFiles(Envelope globalMBR) {
		try {
			System.out.println("Reading building layer...");
			Bag attributesOfBuildings = initAttributesOfBuildings();
			readShapeFile(BUILDINGS_FILE, m_buildings, globalMBR, attributesOfBuildings);

			System.out.println("Reading the path layer...");
			Bag attributesOfPaths = initializeAttributesOfPaths();
			readShapeFile(PATHS_FILE, m_paths, globalMBR, attributesOfPaths);
		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, "Failed to construct simulation", e);
		}
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
		m_buildings.setMBR(minimumBoundingRectangle);
		m_paths.setMBR(minimumBoundingRectangle);
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
	private void addIntersectionNodes(Iterator<Node> nodeIterator) {
		nodeIterator.forEachRemaining(node -> {
			Point point = GEO_FACTORY.createPoint(node.getCoordinate());
			m_pathIntersections.addGeometry(new MasonGeometry(point));
			;
		});
	}

	private void allocateActivitiesToBuildings() {
		// home
		MasonGeometry home = (MasonGeometry) m_buildings.getGeometries().get(0);
		DEV_BUILDINGS.add(home);
		home.setUserData(new GeomPortrayal(new Color(0, 255, 0), 10.0));
		MasonGeometry closestPathToHome = getClosestPath(home);
		closestPathToHome.setUserData(new GeomPortrayal(new Color(255, 20, 147), 10.0));
		BUILDING_TO_CLOSEST_PATH_MAP.put(home, closestPathToHome);

		// some target building
		MasonGeometry target = (MasonGeometry) m_buildings.getGeometries().get(100);
		DEV_BUILDINGS.add(target);
		target.setUserData(new GeomPortrayal(new Color(0, 255, 0), 10.0));
		MasonGeometry closestPathToTarget = getClosestPath(target);
		closestPathToTarget.setUserData(new GeomPortrayal(new Color(255, 20, 147), 10.0));
		BUILDING_TO_CLOSEST_PATH_MAP.put(target, closestPathToTarget);
	}

	private MasonGeometry getClosestPath(MasonGeometry home) {
		double searchDistance = 1.0;
		MasonGeometry closestPath = null;
		while (closestPath == null) {
			Bag withinDistance = m_paths.getObjectsWithinDistance(
					GEO_FACTORY.createPoint(home.getGeometry().getCoordinate()), searchDistance);
			closestPath = GeometryUtility.findClosestCoordinate(home, withinDistance);
			searchDistance += 1.0;
		}
		return closestPath;
	}

	protected void initAgents() {
		Individual.Builder builder = new Individual.Builder(this);
		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {

			Individual individual = builder.build();
			m_individuals.addGeometry(individual.getCurrentLocation());
			schedule.scheduleRepeating(individual);
		}
	}

	public static void main(String[] args) {
		doLoop(Environment.class, args);
		System.exit(0);
	}

}
