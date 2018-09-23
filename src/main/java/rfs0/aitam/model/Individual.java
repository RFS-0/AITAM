package rfs0.aitam.model;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.commons.ISimulationSettings;
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
	private MasonGeometry m_homeLocationGeometry; // actual home building
	private Coordinate m_homeCoordinate; // on path network
	private Node m_homeNode; // on path network
	private MasonGeometry m_targetLocationGeometry; // actual target building
	private Coordinate m_targetCoordinate; // on path network
	private Node m_targetNode; // on path network
	private MasonGeometry m_targetLocationPoint;
	private MasonGeometry m_currentLocationPoint;
	private LengthIndexedLine m_segment = null; // Used by individual to walk along line segment
	private double m_endIndexOfCurrentEdge = 0.0;
	private double m_startIndexOfCurrentEdge = 0.0;
	private double m_currentIndexOnLineOfEdge = 0.0;
	private PointMoveTo m_pointMoveTo = new PointMoveTo();
	private ArrayList<GeomPlanarGraphDirectedEdge> m_pathToNextTarget = new ArrayList<GeomPlanarGraphDirectedEdge>();
	private GeomPlanarGraphEdge m_currentEdge = null;
	private int m_edgeDirection = 1;
	private boolean m_hasReachedTarget = false;
	private int m_currentIndexOnPathToNextTarget = 0;
	private boolean m_hasFinishedActivity = false;

	private Individual(Environment environment) {
		m_environment = environment;
	}

	public static class Builder {

		private Environment state;
		private Individual individualToBuild;

		public Builder(Environment state) {
			this.state = state;
			init();
		}

		private void init() {
			individualToBuild = new Individual(state);
		}

		public Builder withNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			individualToBuild.m_needTimeSplit = needTimeSplit;
			return this;
		}

		/**
		 * Sets the {@link MasonGeometry} that represents the building in which this
		 * {@link Individual} lives in. <b>Note:<b> Use BUILDING_TO_CLOSEST_PATH_MAP in
		 * {@link Environment} to get the {@link MasonGeometry} that represents the path
		 * which is closest to the individuals home location.
		 * 
		 * @param homeLocation - The {@link MasonGeometry} that represents the building
		 *                     this individual lives in
		 * @return {@link Builder}
		 */
		// TODO: it is unlikely that 3 members are necessary to store this information
		// -> clean up once it is clear what actually is necessary (or create data type)
		public Builder withHomeLocation(MasonGeometry homeLocation) {
			if (homeLocation == null) {
				Logger.getLogger(Individual.class.getName())
						.log(Level.SEVERE, "Home location is invalid. The built individual may be unusable!");
			}
			individualToBuild.m_homeLocationGeometry = homeLocation;
			individualToBuild.m_homeCoordinate = Environment.BUILDING_TO_CLOSEST_PATH_MAP
					.get(individualToBuild.m_homeLocationGeometry).geometry.getCoordinate();
			Point homePoint = Environment.GEO_FACTORY.createPoint(individualToBuild.m_homeCoordinate);
			if (homePoint == null) {
				Logger.getLogger(Individual.class.getName())
						.log(Level.SEVERE, "Home point is invalid. The built individual may be unusable!");
			}
			individualToBuild.m_currentLocationPoint = new MasonGeometry(homePoint); // set current location to the path
																						// which is closest to the
			individualToBuild.m_currentLocationPoint.isMovable = true;
			individualToBuild.m_homeNode = individualToBuild.getCurrentNode(state);
			if (individualToBuild.m_homeNode == null) {
				Logger.getLogger(Individual.class.getName())
						.log(Level.SEVERE, "Home node is invalid. The built individual may be unusable!");
			}
			return this;
		}

		/**
		 * Sets the {@link MasonGeometry} that represents the building where this
		 * {@link Individual} wants to go to. <b>Note:<b> Use
		 * BUILDING_TO_CLOSEST_PATH_MAP in {@link Environment} to get the
		 * {@link MasonGeometry} that represents the path which is closest to the
		 * individuals home location.
		 * 
		 * @param homeLocation - The {@link MasonGeometry} that represents the building
		 *                     this individual wants to go to
		 * @return {@link Builder}
		 */
		// TODO: it is unlikely that 3 members are necessary to store this information
		// -> clean up once it is clear what actually is necessary (or create data type)
		public Builder withTargetLocation(MasonGeometry targetLocation) {
			if (targetLocation == null || !(targetLocation instanceof MasonGeometry)) {
				Logger.getLogger(Individual.class.getName())
						.log(Level.SEVERE, "Target location is invalid. The built individual may be unusable!");
			}
			individualToBuild.m_targetLocationGeometry = targetLocation;
			individualToBuild.m_targetCoordinate = Environment.BUILDING_TO_CLOSEST_PATH_MAP
					.get(individualToBuild.m_targetLocationGeometry).geometry.getCoordinate();
			Point targetPoint = Environment.GEO_FACTORY.createPoint(individualToBuild.m_targetCoordinate);
			if (targetPoint == null) {
				Logger.getLogger(Individual.class.getName())
						.log(Level.SEVERE, "Target point is invalid. The built individual may be unusable!");
			}
			individualToBuild.m_targetLocationPoint = new MasonGeometry(targetPoint);
			individualToBuild.m_targetLocationPoint.isMovable = true;
			individualToBuild.m_targetNode = individualToBuild.getNode(state, individualToBuild.m_targetLocationPoint);
			if (individualToBuild.m_targetNode == null) {
				Logger.getLogger(Individual.class.getName())
						.log(Level.SEVERE, "Target node is invalid. The built individual may be unusable!");
			}
			return this;
		}
		
		public Individual build() {
			individualToBuild.initPathToBuilding(state, individualToBuild.m_targetLocationGeometry);
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual(state);
			init();
			return builtIndividual;
		}
	}

	@Override
	public void step(SimState state) {
		if (m_segment == null) {
			// TODO: make sure we are on some edge of the path network & handle case where
			// we are not (maybe reset to last building visited or home location)
			System.out.println("Agent is not on any edge anymore. This should never occur. Handle this case!");
		}
		if (m_hasFinishedActivity) {
			// TODO: if it finished its activity it replans the rest of the day
		}
		if (!m_hasReachedTarget) {
			double travellingDistance = calculateTravellingDistance();
			m_currentIndexOnLineOfEdge += travellingDistance;
			if (m_edgeDirection == 1 && m_currentIndexOnLineOfEdge >= m_endIndexOfCurrentEdge) {
				// positive movement
				moveRemainingDistanceOnNextEdge(m_currentIndexOnLineOfEdge - m_endIndexOfCurrentEdge);

			} else if (m_edgeDirection == -1 && m_currentIndexOnLineOfEdge <= m_startIndexOfCurrentEdge) {
				// negative movement
				moveRemainingDistanceOnNextEdge(m_startIndexOfCurrentEdge - m_currentIndexOnLineOfEdge);
			}
			updatePosition(m_segment.extractPoint(m_currentIndexOnLineOfEdge));
		}
	}
	
	/**
	 * 
	 * 
	 * @return the travelling distance for this step
	 */
	private double calculateTravellingDistance() {
		// TODO: make this more realistic
		//		double maxTrafficCapacity = m_currentEdge.getLine().getLength() * Environment.MAX_TRAFFIC_CAPACITY_PER_UNIT_LENGHT;
		//		// edge can be occupied by at least 1 individual
		//		if (maxTrafficCapacity < 1.0) {
		//			maxTrafficCapacity = 1.0;
		//		}
		//		double traffic = m_environment.m_edgeTraffic.get(m_currentEdge).size();
		//		double trafficFactor = 1.0 - (traffic / maxTrafficCapacity); // TODO: not realistic -> velocity does not depend linearly
		//																// on traffic...
		//		trafficFactor = Math.max(trafficFactor, Environment.MAX_SLOW_DOWN_FACTOR);
		return m_edgeDirection * ISimulationSettings.MAX_VELOCITY;
	}
	
	/**
	 * 
	 * 
	 * @param remainingDistance the distance the agent can still travel this turn
	 */
	private void moveRemainingDistanceOnNextEdge(double remainingDistance) {
		m_currentIndexOnPathToNextTarget += 1;
		if (hasReachedTarget()) {
			m_currentIndexOnLineOfEdge = m_segment.getEndIndex();
			m_currentIndexOnPathToNextTarget = 0;
			m_pathToNextTarget = null;
			m_targetCoordinate = null;
			m_targetLocationGeometry = null;
			m_targetLocationPoint = null;
			m_targetNode = null;
			return;
		}
		setupNextEdge();
		m_currentIndexOnLineOfEdge += remainingDistance;
		if (m_edgeDirection == 1 && m_currentIndexOnLineOfEdge >= m_endIndexOfCurrentEdge) {
			// positive movement
			moveRemainingDistanceOnNextEdge(m_currentIndexOnLineOfEdge - m_endIndexOfCurrentEdge);

		} else if (m_edgeDirection == -1 && m_currentIndexOnLineOfEdge <= m_startIndexOfCurrentEdge) {
			// negative movement
			moveRemainingDistanceOnNextEdge(m_startIndexOfCurrentEdge - m_currentIndexOnLineOfEdge);
		}
	}
	
	/**
	 * Update the position of this individual by moving it to to the provided
	 * {@link Coordinate} <code>c</code>.
	 * 
	 * @param c - The coordinate to which the individual is moved to
	 */
	private void updatePosition(Coordinate c) {
		m_pointMoveTo.setCoordinate(c);
		m_environment.m_individualsGeomVectorField.setGeometryLocation(m_currentLocationPoint, m_pointMoveTo);
	}
	
	private boolean hasReachedTarget() {
		if (m_pathToNextTarget == null) {
			return false;
		}
		return m_currentIndexOnPathToNextTarget >= m_pathToNextTarget.size();
	}
	
	/**
	 * Sets up the next edge on which the individual continues to its target
	 * location
	 * 
	 * @param nextEdge - the GeomPlanarGraphEdge to traverse next
	 */
	private void setupNextEdge() {
		GeomPlanarGraphEdge nextEdge = (GeomPlanarGraphEdge) m_pathToNextTarget.get(m_currentIndexOnPathToNextTarget)
				.getEdge();
		updateEdgeTraffic(nextEdge);
		m_currentEdge = nextEdge;
		LineString lineOfNextEdge = nextEdge.getLine();
		m_segment = new LengthIndexedLine(nextEdge.getLine());
		m_startIndexOfCurrentEdge = m_segment.getStartIndex();
		m_endIndexOfCurrentEdge = m_segment.getEndIndex();
		m_edgeDirection = 1;
		m_currentIndexOnLineOfEdge = 0;
		double distanceToStart = lineOfNextEdge.getStartPoint().distance(m_currentLocationPoint.geometry);
		double distanceToEnd = lineOfNextEdge.getEndPoint().distance(m_currentLocationPoint.geometry);
		if (distanceToStart <= distanceToEnd) {
			m_currentIndexOnLineOfEdge = m_segment.getStartIndex();
			m_edgeDirection = 1;
		} else {
			m_currentIndexOnLineOfEdge = m_segment.getEndIndex();
			m_edgeDirection = -1;
		}
	}
	
	private void updateEdgeTraffic(GeomPlanarGraphEdge nextEdge) {
		if (m_environment.m_edgeTrafficMap.get(m_currentEdge) != null) {
			m_environment.m_edgeTrafficMap.get(m_currentEdge).remove(this); // current edge is actually the old edge here
		}
		if (m_environment.m_edgeTrafficMap.get(nextEdge) == null) {
			m_environment.m_edgeTrafficMap.put(nextEdge, new ArrayList<Individual>());
		}
		m_environment.m_edgeTrafficMap.get(nextEdge).add(this);
	}

	public void initPathToBuilding(Environment environment, MasonGeometry targetBuilding) {
		MasonGeometry closestPathToBuilding = Environment.BUILDING_TO_CLOSEST_PATH_MAP.get(targetBuilding);
		Node currentNode = getCurrentNode(environment);
		Node targetNode = getNode(environment, closestPathToBuilding);
		initPath(currentNode, targetNode);
	}

//	private void initPathToPath(Environment environment, MasonGeometry targetPath) {
//		Node currentNode = getCurrentNode(environment);
//		Node targetNode = getNode(environment, targetPath);
//		initPath(currentNode, targetNode);
//	}

	public void initPath(Node startNode, Node targetNode) {
		try {
			if (startNode == null || targetNode == null) {
				throw new Exception("Invalid nodes. Can not find path...");
			}
		} catch (Exception e) {
			Logger.getLogger(Individual.class.getName()).log(Level.WARNING, e.getMessage(), e);
		}
		ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget = findPath(startNode, targetNode);
		if (pathToTarget != null && pathToTarget.size() > 0) {
			m_pathToNextTarget = pathToTarget;
			m_currentEdge = (GeomPlanarGraphEdge) pathToTarget.get(0).getEdge();
			setupNextEdge();
			updatePosition(m_segment.extractPoint(m_currentIndexOnLineOfEdge));
		} else {
			Logger.getLogger(Individual.class.getName())
					.log(Level.WARNING, String.format("AStar can not find path between the following nodes: %s and %s",
							startNode.toString(), targetNode.toString()));
		}
	}
	
	public ArrayList<GeomPlanarGraphDirectedEdge> findPath(Node startNode, Node targetNode) {
		return GraphUtility.astarPath(startNode, targetNode);
	}

	/**
	 * 
	 * @return The {@link MasonGeometry} where this individual currently is located
	 *         on
	 */
	public MasonGeometry getCurrentLocation() {
		return m_currentLocationPoint;
	}

	/**
	 * @param environment - the current state of the environment
	 * @return the {@link Node} where the agent is currently located
	 */
	public Node getCurrentNode(Environment environment) {
		return environment.m_pathNetworkGeomVectorField.findNode(m_currentLocationPoint.getGeometry().getCoordinate());
	}

	/**
	 * 
	 * @param environment         - the current state of the simulation
	 * @param masonGeometryOfPath - some {@link MasonGeometry} that is part of the
	 *                            path network
	 * @return {@link Node} of the path network
	 */
	public Node getNode(Environment environment, MasonGeometry masonGeometryOfPath) {
		return environment.m_pathNetworkGeomVectorField.findNode(masonGeometryOfPath.getGeometry().getCoordinate());
	}

	/**
	 * 
	 * @param environment      - the current state of the simulation
	 * @param coordinateOfPath - some {@link Coordinate} that is part of the path
	 *                         network
	 * @return {@link Node} of the path network
	 */
	public Node getNode(Environment environment, Coordinate coordinateOfPath) {
		return environment.m_pathNetworkGeomVectorField.findNode(coordinateOfPath);
	}

	public NeedTimeSplit getNeedTimeSplit() {
		return m_needTimeSplit;
	}
	
	public ArrayList<GeomPlanarGraphDirectedEdge> getPathToNextTarget() {
		return m_pathToNextTarget;
	}
}
