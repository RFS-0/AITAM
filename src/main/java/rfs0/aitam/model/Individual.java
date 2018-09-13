package rfs0.aitam.model;

import java.util.logging.Level;
import java.util.logging.Logger;

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

public class Individual implements Steppable {

	private static final long serialVersionUID = 1L;

	// Needs
	private NeedTimeSplit m_needTimeSplit;

	// GIS
	private MasonGeometry m_currentLocation;
	private double m_basemoveRate = 50.0;
	private double m_moveRate = m_basemoveRate;
	private LengthIndexedLine m_segment = null;
	private double m_startIndex = 0.0;
	private double m_endIndex = 0.0;
	private double m_currentIndex = 0.0;
	private PointMoveTo m_pointMoveTo = new PointMoveTo();

	private Individual(Environment state) {
	}

	public static class Builder {

		private static final GeometryFactory FACTORY = new GeometryFactory();

		private Environment state;
		private Individual individualToBuild;

		public Builder(Environment state) {
			this.state = state;
			init();
		}

		private void init() {
			individualToBuild = new Individual(state);
			individualToBuild.m_currentLocation = new MasonGeometry(FACTORY.createPoint(new Coordinate(10, 10)));
			individualToBuild.m_currentLocation.isMovable = true;
			LineString line = null;
			while (line == null) {
				int path = state.random.nextInt(state.m_pedestrianPaths.getGeometries().numObjs);
				MasonGeometry mg = (MasonGeometry) state.m_pedestrianPaths.getGeometries().objs[path];
				line = extractLineString(mg);
			}
			initRoute(line);
			if (state.random.nextBoolean()) {
				individualToBuild.m_currentLocation.addStringAttribute("TYPE", "STUDENT");

				int age = (int) (20.0 + 2.0 * state.random.nextGaussian());
				individualToBuild.m_currentLocation.addIntegerAttribute("AGE", age);
			} else {
				individualToBuild.m_currentLocation.addStringAttribute("TYPE", "FACULTY");
				int age = (int) (40.0 + 9.0 * state.random.nextGaussian());
				individualToBuild.m_currentLocation.addIntegerAttribute("AGE", age);
			}
			individualToBuild.m_basemoveRate *= Math.abs(state.random.nextGaussian());
			individualToBuild.m_currentLocation.addDoubleAttribute("MOVE RATE", individualToBuild.m_basemoveRate);
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

		private void initRoute(LineString line) {
			individualToBuild.m_segment = new LengthIndexedLine(line);
			individualToBuild.m_startIndex = individualToBuild.m_segment.getStartIndex();
			individualToBuild.m_endIndex = individualToBuild.m_segment.getEndIndex();
			Coordinate coord = null;
			coord = individualToBuild.m_segment.extractPoint(individualToBuild.m_startIndex);
			individualToBuild.m_currentIndex = individualToBuild.m_startIndex;
			individualToBuild.m_moveRate = individualToBuild.m_basemoveRate;
			individualToBuild.m_pointMoveTo.setCoordinate(coord);
			individualToBuild.m_currentLocation.getGeometry().apply(individualToBuild.m_pointMoveTo);
			individualToBuild.m_currentLocation.geometry.geometryChanged();
		}

		public Individual build() {
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual(state);
			init();
			return builtIndividual;
		}

		public Builder withNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			individualToBuild.m_needTimeSplit = needTimeSplit;
			return this;
		}
	}

	@Override
	public void step(SimState state) {
		Environment world = (Environment) state;
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

	private void move(Environment world) {
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

	private void findNewPath(Environment world) {
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
			Logger.getLogger(Individual.class.getName()).log(Level.WARNING,
					"Could not find junction for new path! Current juction: " + currentJunction);
		}
	}

	public MasonGeometry getLocation() {
		return m_currentLocation;
	}

	public String getType() {
		return m_currentLocation.getStringAttribute("TYPE");
	}

	public NeedTimeSplit getNeedTimeSplit() {
		return m_needTimeSplit;
	}
}
