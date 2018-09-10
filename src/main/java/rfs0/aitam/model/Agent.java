package rfs0.aitam.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.planargraph.DirectedEdgeStar;
import com.vividsolutions.jts.planargraph.Node;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
import sim.util.geo.PointMoveTo;

public class Agent implements Steppable {

	private static final long serialVersionUID = 1L;

	private MasonGeometry m_currentLocation;
	private double m_basemoveRate = 10.0;
	private double m_moveRate = m_basemoveRate;
	private LengthIndexedLine m_segment = null;
	private double m_startIndex = 0.0;
	private double m_endIndex = 0.0;
	private double m_currentIndex = 0.0;
	private PointMoveTo m_pointMoveTo = new PointMoveTo();

	private static final GeometryFactory FACTORY = new GeometryFactory();

	public Agent(World state) {
		LineString line = null;
		m_currentLocation = new MasonGeometry(FACTORY.createPoint(new Coordinate(10, 10)));
		m_currentLocation.isMovable = true;
		
		while (line == null) {
			int path = state.random.nextInt(state.m_pedestrianPaths.getGeometries().numObjs);
			MasonGeometry mg = (MasonGeometry) state.m_pedestrianPaths.getGeometries().objs[path];
			line = extractLineString(mg);
		}
		setNewRoute(line, true);
		if (state.random.nextBoolean()) {
			m_currentLocation.addStringAttribute("TYPE", "STUDENT");

			int age = (int) (20.0 + 2.0 * state.random.nextGaussian());
			m_currentLocation.addIntegerAttribute("AGE", age);
		} else {
			m_currentLocation.addStringAttribute("TYPE", "FACULTY");
			int age = (int) (40.0 + 9.0 * state.random.nextGaussian());
			m_currentLocation.addIntegerAttribute("AGE", age);
		}
		m_basemoveRate *= Math.abs(state.random.nextGaussian());
		m_currentLocation.addDoubleAttribute("MOVE RATE", m_basemoveRate);
	}

	@Override
	public void step(SimState state) {
		World world = (World) state;
		move(world);
	}

	public void setNewRoute(LineString line, boolean isStart) {
		m_segment = new LengthIndexedLine(line);
		m_startIndex = m_segment.getStartIndex();
		m_endIndex = m_segment.getEndIndex();

		Coordinate coord = null;

		if (isStart) {
			coord = m_segment.extractPoint(m_startIndex);
			m_currentIndex = m_startIndex;
			m_moveRate = m_basemoveRate;
		} else {
			coord = m_segment.extractPoint(m_endIndex);
			m_currentIndex = m_endIndex;
			m_moveRate = -m_basemoveRate;
		}
		moveTo(coord);
	}

	public void moveTo(Coordinate c) {
		m_pointMoveTo.setCoordinate(c);
		m_currentLocation.getGeometry().apply(m_pointMoveTo);
		m_currentLocation.geometry.geometryChanged();
	}

	private LineString extractLineString(MasonGeometry masonGeometry) {
		for (int i = 0; i < masonGeometry.getGeometry().getNumGeometries(); i++) {
			Geometry geometry = masonGeometry.getGeometry().getGeometryN(i);
			if (geometry.getGeometryType().equals("LineString")) {
				return (LineString) geometry;
			}
		}
		return null;
	}

	private void moveAlongPath() {
		m_currentIndex += m_moveRate;

		if (m_moveRate < 0) {
			if (m_currentIndex < m_startIndex) {
				m_currentIndex = m_startIndex;
			}
		} else {
			if (m_currentIndex > m_endIndex) {
				m_currentIndex = m_endIndex;
			}
		}

		Coordinate currentPosition = m_segment.extractPoint(m_currentIndex);
		moveTo(currentPosition);
	}

	private void move(World world) {
		if (!hasArrived()) {
			moveAlongPath();
		} else {
			findNewPath(world);
		}
	}

	private boolean hasArrived() {
		if ((m_moveRate > 0 && m_currentIndex >= m_endIndex) || (m_moveRate < 0 && m_currentIndex <= m_startIndex)) {
			return true;
		}
		return false;
	}

	private void findNewPath(World world) {
		Node currentJunction = world.m_pathNetwork.findNode(m_currentLocation.getGeometry().getCoordinate());
		if (currentJunction != null) {
			DirectedEdgeStar directedEdgeStar = currentJunction.getOutEdges();
			Object[] edges = directedEdgeStar.getEdges().toArray();
			if (edges.length > 0) {
				int i = world.random.nextInt(edges.length);
				GeomPlanarGraphDirectedEdge directedEdge = (GeomPlanarGraphDirectedEdge) edges[i];
				GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) directedEdge.getEdge();
				LineString newRoute = edge.getLine();
				Point startPoint = newRoute.getStartPoint();
				Point endPoint = newRoute.getEndPoint();

				if (startPoint.equals(m_currentLocation.geometry)) {
					setNewRoute(newRoute, true);
				} else {
					if (endPoint.equals(m_currentLocation.geometry)) {
						setNewRoute(newRoute, false);
					} else {
						System.err.println("Where am I?");
					}
				}
			}
		} else {
			System.out.println("Found no junction for new path...");
		}
	}

	public MasonGeometry getLocation() {
		return m_currentLocation;
	}

	public String getType() {
		return m_currentLocation.getStringAttribute("TYPE");
	}
}
