package rfs0.aitam.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.planargraph.Node;

import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;

public class Environment extends SimState {

	private static final long serialVersionUID = 1L;

	// TODO: use properties file to overwrite these settings
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	public static final int NUMBER_OF_AGENTS = 1000;

	// Time
	// TODO probably replace this with corresponding class of joda time
	public static Calendar CALENDAR = new Calendar.Builder().setDate(0, 0, 0).setTimeOfDay(0, 0, 0).build(); // represents the current time of the simulation; each tick represents one second

	// GIS
	public GeomVectorField m_buildings = new GeomVectorField(WIDTH, HEIGHT); // holds GIS data of buildings
	public GeomVectorField m_paths = new GeomVectorField(WIDTH, HEIGHT); // holds GIS data of paths
	public GeomVectorField m_home = new GeomVectorField(WIDTH, HEIGHT); // TODO: only for dev; remove later
	public GeomVectorField m_work = new GeomVectorField(WIDTH, HEIGHT); // TODO: only for dev; remove later
	public GeomPlanarGraph m_pathNetwork = new GeomPlanarGraph(); // represents graph all paths
	public GeomVectorField m_pathJunctions = new GeomVectorField(WIDTH, HEIGHT); // represents all crossings
	public HashMap<GeomPlanarGraphEdge, ArrayList<Individual>> m_edgeTraffic = new HashMap<>(); // used to capture the usage of edges

	// Individuals
	public GeomVectorField m_individuals = new GeomVectorField(WIDTH, HEIGHT); // used to represent the individuals

	@SuppressWarnings("unchecked")
	public Environment(long seed) {
		super(seed);
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
		try {
			Envelope globalMBR = m_buildings.getMBR();
			
			System.out.println("Reading building layer...");
			readGisFile("\\data\\environment\\buildings\\buildings.shp", m_buildings, globalMBR);
			
			System.out.println("Reading the path layer...");
			readGisFile("\\data\\environment\\paths\\paths.shp", m_paths, globalMBR);
			
			synchronizeMinimumBoundingRectangles(globalMBR);
			m_pathNetwork.createFromGeomField(m_paths);
			addIntersectionNodes(m_pathNetwork.nodeIterator(), m_pathJunctions);
		} catch (Exception e) {
			Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, "Failed to construct simulation", e);
		}
	}

	@Override
	public void start() {
		super.start();
		m_individuals.clear();
		m_home.clear();
		m_work.clear();
		addAgents();
		m_individuals.setMBR(m_buildings.getMBR());
		m_home.setMBR(m_buildings.getMBR());
		m_work.setMBR(m_buildings.getMBR());
		schedule.scheduleRepeating(m_individuals.scheduleSpatialIndexUpdater(), Integer.MAX_VALUE, 1.0);
	}

	private void readGisFile(String relativePathToFile, GeomVectorField geometry, Envelope minimumBoundingRectangle) {
		try {
			URL url = new File(System.getProperty("user.dir") + relativePathToFile).toURI().toURL();
			ShapeFileImporter.read(url, geometry);
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
	private void addIntersectionNodes(Iterator<Node> nodeIterator, GeomVectorField intersections) {
		GeometryFactory factory = new GeometryFactory();
		nodeIterator.forEachRemaining(node -> {
			Point point = factory.createPoint(node.getCoordinate());
			m_pathJunctions.addGeometry(new MasonGeometry(point));
			;
		});
	}
	
	protected void addAgents() {
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
