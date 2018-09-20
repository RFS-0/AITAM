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
	private MasonGeometry m_homeLocation;
	private MasonGeometry m_targetLocation;
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

		// TODO: remove random initialization of paths
		private void init() {
			individualToBuild = new Individual(state);
			individualToBuild.m_currentLocation = new MasonGeometry(FACTORY.createPoint(new Coordinate(10, 10)));
			individualToBuild.m_currentLocation.isMovable = true;
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

		public Individual build() {
			individualToBuild.m_currentLocation = state.getBuildingToClosestPathMap().get(individualToBuild.m_homeLocation); // set current location to the path which is closes to the individual's home
			individualToBuild.initPathToBuilding(state, individualToBuild.m_targetLocation);
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual(state);
			init();
			return builtIndividual;
		}

		public Builder withNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			individualToBuild.m_needTimeSplit = needTimeSplit;
			return this;
		}
		
		/**
		 * Sets the {@link MasonGeometry} that represents the building in which this {@link Individual} lives in.
		 * <b>Note:<b> Use BUILDING_TO_CLOSEST_PATH_MAP in {@link Environment} to get the {@link MasonGeometry} that represents the path which is closest to the individuals home location.
		 * 
		 * @param homeLocation - The {@link MasonGeometry} that represents the building this individual lives in 
		 * @return {@link Builder}
		 */
		public Builder withHomeLocation(MasonGeometry homeLocation) {
			individualToBuild.m_homeLocation = homeLocation;
			return this;
		}
		
		/**
		 * Sets the {@link MasonGeometry} that represents the building where this {@link Individual} wants to go to.
		 * <b>Note:<b> Use BUILDING_TO_CLOSEST_PATH_MAP in {@link Environment} to get the {@link MasonGeometry} that represents the path which is closest to the individuals home location.
		 * 
		 * @param homeLocation - The {@link MasonGeometry} that represents the building this individual wants to go to
		 * @return {@link Builder}
		 */
		public Builder withTargetLocation(MasonGeometry targetLocation) {
			individualToBuild.m_targetLocation = targetLocation;
			return this;
		}
	}

	@Override
	public void step(SimState state) {
		Environment environment = (Environment) state;
		move(environment);
	}
	
	private void initPathToBuilding(Environment environment, MasonGeometry targetBuilding) {
		MasonGeometry closestPathToBuilding = environment.getBuildingToClosestPathMap().get(targetBuilding);
		Node currentNode = getCurrentNode(environment);
		Node targetNode = getNode(environment, closestPathToBuilding);
		initPath(currentNode, targetNode);
	}

	private void initPathToPath(Environment environment, MasonGeometry targetPath) {
		Node currentNode = getCurrentNode(environment);
		Node targetNode = getNode(environment, targetPath);
		initPath(currentNode, targetNode);
	}
	
	private void initPath(Node startNode, Node targetNode) {
		try {
			if (startNode == null || targetNode == null) {
				throw new Exception("Invalid nodes. Can not find path...");
			}
		} catch (Exception e) {
			Logger.getLogger(Individual.class.getName()).log(Level.WARNING, e.getMessage(), e);
		}
		ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget = GraphUtility.astarPath(startNode, targetNode);
		if (pathToTarget != null && pathToTarget.size() > 0) {
			savePathToNextTarget(pathToTarget);
			initEdgeTraversal(); // TODO: maybe move this method out; or rename this one
			updatePosition(m_segment.extractPoint(m_currentIndex)); // TODO: maybe move this method out or rename this one
		}
		else {
			Logger.getLogger(Individual.class.getName()).log(Level.WARNING, String.format("AStar can not find path between the following nodes: %s and %s", startNode.toString(), targetNode.toString()));
		}
	}

	private void initEdgeTraversal() {
		GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) m_pathToNextTarget.get(0).getEdge();
		setupEdge(edge);
	}

	private void savePathToNextTarget(ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget) {
		m_pathToNextTarget = pathToTarget;
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

	/**
	 * 
	 * @return The {@link MasonGeometry} where this individual currently is located on
	 */
	public MasonGeometry getCurrentLocation() {
		return m_currentLocation;
	}

	/**
	 * @param environment - the current state of the environment
	 * @return the {@link Node} where the agent is currently located
	 */
	public Node getCurrentNode(Environment environment) {
		return environment.m_pathNetwork.findNode(m_currentLocation.getGeometry().getCoordinate());
	}
	
	/**
	 * 
	 * @param environment - the current state of the simulation
	 * @param masonGeometryOfPath - some {@link MasonGeometry} that is part of the path network
	 * @return {@link Node} of the path network
	 */
	public Node getNode(Environment environment, MasonGeometry masonGeometryOfPath) {
		return environment.m_pathNetwork.findNode(masonGeometryOfPath.getGeometry().getCoordinate());
	}
	
	/**
	 * 
	 * @param environment - the current state of the simulation
	 * @param coordinateOfPath - some {@link Coordinate} that is part of the path network
	 * @return {@link Node} of the path network
	 */
	public Node getNode(Environment environment, Coordinate coordinateOfPath) {
		return environment.m_pathNetwork.findNode(coordinateOfPath);
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
