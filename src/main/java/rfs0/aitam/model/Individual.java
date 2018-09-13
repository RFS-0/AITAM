package rfs0.aitam.model;

import java.util.ArrayList;
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

import rfs0.aitam.utilities.GraphUtility;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
import sim.util.geo.PointMoveTo;

public class Individual implements Steppable {

	private static final long serialVersionUID = 1L;

	Environment m_environment;

	// Needs
	private NeedTimeSplit m_needTimeSplit;

	// GIS
	private Node m_homeLocation;
	private Node m_workLocation;
	private MasonGeometry m_currentLocation;
	private double m_basemoveRate = 50.0;
	private double m_moveRate = m_basemoveRate;
	private LengthIndexedLine m_segment = null; // Used by individual to walk along line segment
	private double m_startIndex = 0.0;
	private double m_endIndex = 0.0;
	private double m_currentIndex = 0.0;
	private PointMoveTo m_pointMoveTo = new PointMoveTo();
	private ArrayList<GeomPlanarGraphDirectedEdge> m_pathToNextTarget = new ArrayList<GeomPlanarGraphDirectedEdge>();
	private GeomPlanarGraphEdge m_currentEdge = null;
	private boolean m_hasReachedDestination = false;
	private int m_linkDirection = 1; // used to indicate the direction the individual walks along the path
	private int m_pathDirection = 1; // used to indicate the direction of the path

	private Individual(Environment environment) {
		m_environment = environment;
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
				int path = state.random.nextInt(state.m_paths.getGeometries().numObjs);
				MasonGeometry mg = (MasonGeometry) state.m_paths.getGeometries().objs[path];
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
		
		private LineString extractLineString(MasonGeometry masonGeometry) {
			for (int i = 0; i < masonGeometry.getGeometry().getNumGeometries(); i++) {
				Geometry geometry = masonGeometry.getGeometry().getGeometryN(i);
				if (geometry.getGeometryType().equals("LineString")) {
					return (LineString) geometry;
				}
			}
			return null;
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
		
		public Builder withHomeLocation(Node homeLocation) {
			individualToBuild.m_homeLocation = homeLocation;
			return this;
		}
		
		public Builder withWorkLocation(Node workLocation) {
			individualToBuild.m_homeLocation = workLocation;
			return this;
		}
	}

	@Override
	public void step(SimState state) {
		Environment environment = (Environment) state;
		move(environment);
	}
	
	public void findPath(Environment environment, Node targetNode) {
		try {
			Node currentNode = getCurrentNode(environment);
			if (currentNode == null || targetNode == null) {
				throw new Exception("Invalid nodes. Can find path...");
			}
		} catch (Exception e) {
			Logger.getLogger(Individual.class.getName()).log(Level.WARNING, e.getMessage(), e);
		}
		ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget = GraphUtility.astarPath(targetNode, targetNode);
		if (pathToTarget != null && pathToTarget.size() > 0) {
			m_pathToNextTarget = pathToTarget;
			GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) pathToTarget.get(0).getEdge();
			setupEdge(edge);
			updatePosition(m_segment.extractPoint(m_currentIndex));
		}
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

	public MasonGeometry getCurrentLocation() {
		return m_currentLocation;
	}

	public Node getCurrentNode(Environment environment) {
		return environment.m_pathNetwork.findNode(m_currentLocation.getGeometry().getCoordinate());
	}

	public String getType() {
		return m_currentLocation.getStringAttribute("TYPE");
	}

	public NeedTimeSplit getNeedTimeSplit() {
		return m_needTimeSplit;
	}
	
	private void flipPath() {
        m_hasReachedDestination = false;
        m_pathDirection = -m_pathDirection;
        m_linkDirection = -m_linkDirection;
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

	private void move(Environment environment) {
		if (!hasArrived()) {
			moveAlongPath();
		} else {
			findNewPath(environment);
		}
	}

	private boolean hasArrived() {
		if ((m_moveRate > 0 && m_currentIndex >= m_endIndex) || (m_moveRate < 0 && m_currentIndex <= m_startIndex)) {
			return true;
		}
		return false;
	}

	private void findNewPath(Environment environment) {
		Node currentJunction = environment.m_pathNetwork.findNode(m_currentLocation.getGeometry().getCoordinate());
		if (currentJunction != null) {
			DirectedEdgeStar directedEdgeStar = currentJunction.getOutEdges();
			Object[] edges = directedEdgeStar.getEdges().toArray();
			if (edges.length > 0) {
				int i = environment.random.nextInt(edges.length);
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
			Logger.getLogger(Individual.class.getName())
					.log(Level.WARNING, "Could not find junction for new path! Current juction: " + currentJunction);
		}
	}

	/**
	 * Sets the Individual up to proceed along an Edge
	 * 
	 * @param edge the GeomPlanarGraphEdge to traverse next
	 */
	private void setupEdge(GeomPlanarGraphEdge edge) {
		removeFromOldEdge(edge);
		m_currentEdge = edge;
		updateEdgeTraffic();
		LineString line = setupNewSegment(edge);
		updateDirection(line);
	}

	private void updateDirection(LineString line) {
		double distanceToStart = line.getStartPoint().distance(m_currentLocation.geometry);
		double distanceToEnd = line.getEndPoint().distance(m_currentLocation.geometry);
		if (distanceToStart <= distanceToEnd) { // closer to start
			m_currentIndex = m_startIndex;
			m_linkDirection = 1;
		} else if (distanceToEnd < distanceToStart) { // closer to end
			m_currentIndex = m_endIndex;
			m_linkDirection = -1;
		}
	}

	private LineString setupNewSegment(GeomPlanarGraphEdge edge) {
		LineString line = edge.getLine();
		m_segment = new LengthIndexedLine(line);
		m_startIndex = m_segment.getStartIndex();
		m_endIndex = m_segment.getEndIndex();
		m_linkDirection = 1;
		return line;
	}

	private void updateEdgeTraffic() {
		if (m_environment.m_edgeTraffic.get(m_currentEdge) == null) {
			m_environment.m_edgeTraffic.put(m_currentEdge, new ArrayList<Individual>());
		}
		m_environment.m_edgeTraffic.get(m_currentEdge).add(this);
	}

	private void removeFromOldEdge(GeomPlanarGraphEdge edge) {
		// clean up on old edge
		if (m_currentEdge != null) {
			ArrayList<Individual> traffic = m_environment.m_edgeTraffic.get(m_currentEdge);
			traffic.remove(this);
		}
	}

	/** move the agent to the given coordinates */
	private void updatePosition(Coordinate c) {
		m_pointMoveTo.setCoordinate(c);
		// TODO: why is this commented out?
//        location.geometry.apply(pointMoveTo); 

		m_environment.m_individuals.setGeometryLocation(m_currentLocation, m_pointMoveTo);
	}

	}
