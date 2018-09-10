package rfs0.aitam.model;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.planargraph.Node;

import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.GeomPlanarGraph;
import sim.util.geo.MasonGeometry;

public class World extends SimState {

	private static final long serialVersionUID = 1L;

	// TODO: use properties file to overwrite these settings
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	public static final int NUMBER_OF_AGENTS = 1000;

	// GIS
	public GeomVectorField m_buildings = new GeomVectorField(WIDTH, HEIGHT);
	public GeomVectorField m_roads = new GeomVectorField(WIDTH, HEIGHT);
	public GeomVectorField m_pedestrianPaths = new GeomVectorField(WIDTH, HEIGHT);
	public GeomVectorField m_bicyclePaths = new GeomVectorField(WIDTH, HEIGHT);

	// Agents
	public GeomVectorField m_agents = new GeomVectorField(WIDTH, HEIGHT);

	// Networks
	public GeomPlanarGraph m_pathNetwork = new GeomPlanarGraph();
	public GeomVectorField m_pathJunctions = new GeomVectorField(WIDTH, HEIGHT);

	public World(long seed) {
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
			Envelope mbr = m_buildings.getMBR();
			System.out.println("Reading building layer...");
			readGisFile("\\data\\environment\\buildings\\buildings.shp", m_buildings, mbr);
			System.out.println("Reading the path layer...");
			readGisFile("\\data\\environment\\paths\\paths.shp", m_pedestrianPaths, mbr);
			synchronizeMinimumBoundingRectangles(mbr);
			m_pathNetwork.createFromGeomField(m_pedestrianPaths);
			addIntersectionNodes(m_pathNetwork.nodeIterator(), m_pathJunctions);
		} catch (Exception e) {
			Logger.getLogger(World.class.getName()).log(Level.SEVERE, "Failed to construct simulation", e);
		}
	}

	@Override
	public void start() {
		super.start();
		m_agents.clear();
		addAgents();
		m_agents.setMBR(m_buildings.getMBR());
		schedule.scheduleRepeating(m_agents.scheduleSpatialIndexUpdater(), Integer.MAX_VALUE, 1.0);
	}

	private void readGisFile(String relativePathToFile, GeomVectorField geometry, Envelope minimumBoundingRectangle) {
		try {
			URL url = new File(System.getProperty("user.dir") + relativePathToFile).toURI().toURL();
			ShapeFileImporter.read(url, geometry);
			minimumBoundingRectangle.expandToInclude(geometry.getMBR());

		} catch (Exception e) {
			Logger.getLogger(World.class.getName()).log(Level.SEVERE,
					String.format("Failed to read GIS file with path: %s", relativePathToFile), e);
		}
	}

	private void synchronizeMinimumBoundingRectangles(Envelope minimumBoundingRectangle) {
		m_buildings.setMBR(minimumBoundingRectangle);
	}

	private void addIntersectionNodes(Iterator<Node> nodeIterator, GeomVectorField intersections) {
		GeometryFactory factory = new GeometryFactory();
		nodeIterator.forEachRemaining(node -> {
			Point point = factory.createPoint(node.getCoordinate());
			m_pathJunctions.addGeometry(new MasonGeometry(point));
			;
		});
	}

	protected void addAgents() {
		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {
			Agent a = new Agent(this);
			m_agents.addGeometry(a.getLocation());
			schedule.scheduleRepeating(a);
		}
	}

	public static void main(String[] args) {
		doLoop(World.class, args);
		System.exit(0);
	}

}
