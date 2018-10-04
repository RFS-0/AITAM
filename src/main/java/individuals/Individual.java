package individuals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.planargraph.Node;

import activities.Activity;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import rfs0.aitam.model.needs.ActualNeedTimeSplit;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.utilities.GeometryUtility;
import rfs0.aitam.utilities.GraphUtility;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.geo.GeomVectorField;
import sim.field.network.Network;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
import sim.util.geo.PointMoveTo;

public class Individual implements Steppable {

	private static final long serialVersionUID = 1L;

	Environment m_environment;
	private int m_id;
	
	/**
	 * Networks related activities
	 */
	private Network m_householdMembersNetwork = new Network(false);
	private int m_householdMembersNetworkId;
	private Network m_workColleguesNetwork = new Network(false);
	private int m_workColleguesNetworkId;
	private Network m_friendsNetwork = new Network(false);
	private int m_friendsNetworkId;

	/**
	 * Needs related activities
	 */
	private NeedTimeSplit m_targetNeedTimeSplit;
	private ActualNeedTimeSplit m_actualNeedTimeSplit = new ActualNeedTimeSplit();
	
	/**
	 * Activity related variables
	 */
	private ArrayList<Activity> m_individualActivities = new ArrayList<>();

	/**
	 * GIS related variables
	 */
	// buildings for activities
	private MasonGeometry m_homeBuilding;
	private MasonGeometry m_thirdPlaceForHouseholdAndFamilyCareBuilding;
	private MasonGeometry m_thirdPlaceForHouseholdAndFamilyCarePoint;
	private MasonGeometry m_workPlaceBuilding;
	private MasonGeometry m_workPlacePoint;
	private MasonGeometry m_thirdPlaceForWorkBuilding;
	private MasonGeometry m_thirdPlaceForWorkPoint;
	private MasonGeometry m_leisureBuilding;
	private MasonGeometry m_leisurePoint;
	private MasonGeometry m_thirdPlaceForLeisureBuilding;
	private MasonGeometry m_thirdPlaceForLeisurePoint;
	
	// dynamic locations
	private MasonGeometry m_currentLocationPoint;
	private MasonGeometry m_targetLocationGeometry; // actual target building
	private MasonGeometry m_targetLocationPoint;
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

	private Individual() {}

	public static class Builder {

		private Individual individualToBuild;

		public Builder() {
			individualToBuild = new Individual();
		}
		
		public Builder withEnvironment(Environment environment) {
			individualToBuild.m_environment = environment;
			return this;
		}
		
		public Builder withId(int id) {
			individualToBuild.m_id = id;
			return this;
		}

		public Builder withTargetNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			individualToBuild.m_targetNeedTimeSplit = needTimeSplit;
			return this;
		}

		/**
		 * Sets the {@link MasonGeometry} that represents the building in which this
		 * {@link Individual} lives in. <b>Note:<b> Use BUILDING_TO_CLOSEST_PATH_MAP in
		 * {@link Environment} to get the {@link MasonGeometry} that represents the path
		 * which is closest to the individuals home location.
		 * 
		 * @param homeBuilding - The {@link MasonGeometry} that represents the building
		 *                     this individual lives in
		 * @return {@link Builder}
		 */
		public Builder withHomeBuilding(MasonGeometry homeBuilding) {
			String warningMessage = "Home location is invalid. The built individual may be unusable!";
			validate(homeBuilding, warningMessage);
			individualToBuild.m_homeBuilding = homeBuilding;
			Point pointOfClosestPath = getPointForPath(homeBuilding);
			validate(pointOfClosestPath, warningMessage);
			individualToBuild.m_currentLocationPoint = new MasonGeometry(pointOfClosestPath);
			individualToBuild.m_currentLocationPoint.isMovable = true;
			return this;
		}
		
		public Builder withThirdPlaceForHouseholdAndFamilyCareBuilding(MasonGeometry thirdPlaceForHouseholdAndFamilyCareBuilding) {
			String warningMessage = "Third place for household and family care is invalid. The built individual may be unusable!";
			validate(thirdPlaceForHouseholdAndFamilyCareBuilding, warningMessage);
			individualToBuild.m_thirdPlaceForHouseholdAndFamilyCareBuilding = thirdPlaceForHouseholdAndFamilyCareBuilding;
			Point pointOfClosestPath = getPointForPath(thirdPlaceForHouseholdAndFamilyCareBuilding);
			validate(pointOfClosestPath, warningMessage);
			individualToBuild.m_thirdPlaceForHouseholdAndFamilyCarePoint = new MasonGeometry(pointOfClosestPath);
			individualToBuild.m_thirdPlaceForHouseholdAndFamilyCarePoint.isMovable = false;
			return this;
		}
		
		public Builder withWorkPlaceBuilding(MasonGeometry workPlaceBuilding) {
			String warningMessage = "The work place building is invalid. The built individual may be unusable!";
			validate(workPlaceBuilding, warningMessage);
			individualToBuild.m_workPlaceBuilding = workPlaceBuilding;
			Point pointOfClosestPath = getPointForPath(workPlaceBuilding);
			validate(pointOfClosestPath, warningMessage);
			individualToBuild.m_workPlacePoint = new MasonGeometry(pointOfClosestPath);
			individualToBuild.m_workPlacePoint.isMovable = false;
			return this;
		}
		
		public Builder withThirdPlaceForWorkBuilding(MasonGeometry thirdPlaceForWorkBuilding) {
			String warningMessage = "The third place for work is invalid. The built individual may be unusable!";
			validate(thirdPlaceForWorkBuilding, warningMessage);
			individualToBuild.m_thirdPlaceForWorkBuilding = thirdPlaceForWorkBuilding;
			Point pointOfClosestPath = getPointForPath(thirdPlaceForWorkBuilding);
			validate(pointOfClosestPath, warningMessage);
			individualToBuild.m_thirdPlaceForWorkPoint = new MasonGeometry(pointOfClosestPath);
			individualToBuild.m_thirdPlaceForWorkPoint.isMovable = false;
			return this;
		}
		
		public Builder withLeisureBuilding(MasonGeometry leisureBuilding) {
			String warningMessage = "The place for leisure is invalid. The built individual may be unusable!";
			validate(leisureBuilding, warningMessage);
			individualToBuild.m_leisureBuilding = leisureBuilding;
			Point pointOfClosestPath = getPointForPath(leisureBuilding);
			validate(pointOfClosestPath, warningMessage);
			individualToBuild.m_leisurePoint = new MasonGeometry(pointOfClosestPath);
			individualToBuild.m_leisurePoint.isMovable = false;
			return this;
		}
		
		public Builder withThirdPlaceForLeisureBuilding(MasonGeometry thirdPlaceForLeisureBuilding) {
			String warningMessage = "The third place for leisure is invalid. The built individual may be unusable!";
			validate(thirdPlaceForLeisureBuilding, warningMessage);
			individualToBuild.m_thirdPlaceForLeisureBuilding = thirdPlaceForLeisureBuilding;
			Point pointOfClosestPath = getPointForPath(thirdPlaceForLeisureBuilding);
			validate(pointOfClosestPath, warningMessage);
			individualToBuild.m_thirdPlaceForLeisurePoint = new MasonGeometry(pointOfClosestPath);
			individualToBuild.m_thirdPlaceForLeisurePoint.isMovable = false;
			return this;
		}
		
		private void validate(Object objToValidate, String message) {
			if (objToValidate == null) {
				Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, message);
			}
		}
		
		private Point getPointForPath(MasonGeometry building) {
			Coordinate coordinateOfClosestPath = Environment.BUILDING_TO_CLOSEST_PATH_MAP.get(building).geometry.getCoordinate();
			return Environment.GEO_FACTORY.createPoint(coordinateOfClosestPath);
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
			Coordinate targetCoordinate = Environment.BUILDING_TO_CLOSEST_PATH_MAP.get(individualToBuild.m_targetLocationGeometry).geometry.getCoordinate();
			Point targetPoint = Environment.GEO_FACTORY.createPoint(targetCoordinate);
			if (targetPoint == null) {
				Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, "Target point is invalid. The built individual may be unusable!");
			}
			individualToBuild.m_targetLocationPoint = new MasonGeometry(targetPoint);
			individualToBuild.m_targetLocationPoint.isMovable = true;
			Node targetNode = individualToBuild.getNode(individualToBuild.m_targetLocationPoint);
			if (targetNode == null) {
				Logger.getLogger(Individual.class.getName())
						.log(Level.SEVERE, "Target node is invalid. The built individual may be unusable!");
			}
			return this;
		}
		
		public Builder withIndividualActivities(ArrayList<Activity> individualActivities) {
			individualToBuild.m_individualActivities = individualActivities;
			return this;
		}
		
		public Builder withIndividualActivity(Activity individualActivity) {
			individualToBuild.m_individualActivities.add(individualActivity);
			return this;
		}
		
		public Builder withHousholdMembersNetworkId(int householdMembersNetworkId) {
			individualToBuild.m_householdMembersNetworkId = householdMembersNetworkId;
			return this;
		}
		
		public Builder withHousholdMembersNetwork(Network householdMembersNetwork) {
			individualToBuild.m_householdMembersNetwork = householdMembersNetwork;
			return this;
		}
		
		public Builder addHouseholdMember(Individual householdMember) {
			individualToBuild.m_householdMembersNetwork.addNode(householdMember);
			return this;
		}
		
		public Builder withWorkColleguesNetworkId(int workColleguesNetworkId) {
			individualToBuild.m_workColleguesNetworkId = workColleguesNetworkId;
			return this;
		}
		
		public Builder withWorkColleguesNetwork(Network workColleguesNetwork) {
			individualToBuild.m_workColleguesNetwork = workColleguesNetwork;
			return this;
		}
		
		public Builder addWorkCollegue(Individual workCollegue) {
			individualToBuild.m_workColleguesNetwork.addNode(workCollegue);
			return this;
		}
		
		public Builder withFriendsNetworkId(int friendsNetworkId) {
			individualToBuild.m_friendsNetworkId = friendsNetworkId;
			return this;
		}
		
		public Builder withFriendsNetwork(Network friendsNetwork) {
			individualToBuild.m_friendsNetwork = friendsNetwork;
			return this;
		}
		
		public Builder addFriend(Individual friend) {
			individualToBuild.m_friendsNetwork.addNode(friend);
			return this;
		}
		
		public Builder adjust(Individual individualToAdjust) {
			individualToBuild = individualToAdjust;
			return this;
		}
		
		public Individual build() {
			individualToBuild.initPathToBuilding(individualToBuild.m_targetLocationGeometry);
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual();
			return builtIndividual;
		}
	}

	@Override
	public void step(SimState state) {
		updateKnowledge();
		planActivities();
		executeActivitiy();
		updateMemory();
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
	
	private void updateKnowledge() {
		// gather information about networks
	}
	
	private void planActivities() {
		// get available activities
		// determine which activitiy should be scheduled next
		// determine where it should be executed
		// determine how long the activity shall be executed
	}
	
	private void executeActivitiy() {
		
	}
	
	private void updateMemory() {
		// TODO: update actual need time split
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
			m_targetLocationGeometry = null;
			m_targetLocationPoint = null;
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

	public void initPathToBuilding(MasonGeometry targetBuilding) {
		MasonGeometry closestPathToBuilding = Environment.BUILDING_TO_CLOSEST_PATH_MAP.get(targetBuilding);
		Node currentNode = getCurrentNode();
		Node targetNode = getNode(closestPathToBuilding);
		initPath(currentNode, targetNode);
		colorPathToTarget();
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
	
	public void colorPathToTarget() {
		GeomVectorField field = m_environment.getPathsGeomVectorField();
		List<Coordinate> coordinatesOfPath = getPathToNextTarget()
				.stream()
				.map(path -> path.getCoordinate())
				.collect(Collectors.toList());
		for (Coordinate coordinate : coordinatesOfPath) {
			ArrayList<MasonGeometry> coveringObjects = GeometryUtility
					.getCoveringObjects(new MasonGeometry(Environment.GEO_FACTORY.createPoint(coordinate)), field);
			coveringObjects.forEach(mg -> {
				mg.setUserData(
						new CircledPortrayal2D(
								new GeomPortrayal(
										ISimulationSettings.COLOR_OF_PATH_SELECTED,
										ISimulationSettings.SIZE_OF_PATH
										),
								ISimulationSettings.COLOR_OF_PATH_SELECTED,
								true
								));
			});
		}
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
	public Node getCurrentNode() {
		return m_environment.m_pathNetworkGeomVectorField.findNode(m_currentLocationPoint.getGeometry().getCoordinate());
	}

	/**
	 * 
	 * @param environment         - the current state of the simulation
	 * @param masonGeometryOfPath - some {@link MasonGeometry} that is part of the
	 *                            path network
	 * @return {@link Node} of the path network
	 */
	public Node getNode(MasonGeometry masonGeometryOfPath) {
		return m_environment.m_pathNetworkGeomVectorField.findNode(masonGeometryOfPath.getGeometry().getCoordinate());
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
	
	public NeedTimeSplit getTargetNeedTimeSplit() {
		return m_targetNeedTimeSplit;
	}
	
	public ArrayList<GeomPlanarGraphDirectedEdge> getPathToNextTarget() {
		return m_pathToNextTarget;
	}
	
	public void updateActualNeedTimeSplit(Need need, BigDecimal timeSpentSatisfyingNeed) {
		m_actualNeedTimeSplit.updateNeedTimeSplit(need, timeSpentSatisfyingNeed);
	}
	
	public ActualNeedTimeSplit getActualNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}
	
	public Network getHouseholdMembersNetwork() {
		return m_householdMembersNetwork;
	}
	
	public Network getWorkColleguesNetwork() {
		return m_workColleguesNetwork;
	}
	
	public Network getFriendsNetwork() {
		return m_friendsNetwork;
	}
	
	public int getId() {
		return m_id;
	}
	
	public int getHouseholdMembersNetworkId() {
		return m_householdMembersNetworkId;
	}

	public int getWorkColleguesNetworkId() {
		return m_workColleguesNetworkId;
	}
	
	public int getFriendsNetworkId() {
		return m_friendsNetworkId;
	}
	
	public MasonGeometry geHomeBuilding() {
		return m_homeBuilding;
	}
	
	public MasonGeometry getThirdPlaceForHouseholdAndFamilyCareBuilding() {
		return m_thirdPlaceForHouseholdAndFamilyCareBuilding;
	}
	
	public MasonGeometry getThirdPlaceForHouseholdAndFamilyCarePoint() {
		return m_thirdPlaceForHouseholdAndFamilyCarePoint;
	}
	
	public MasonGeometry getWorkPlaceBuilding() {
		return m_workPlaceBuilding;
	}
	
	public MasonGeometry getWorkPlacePoint() {
		return m_workPlacePoint;
	}
	
	public MasonGeometry getThirdPlaceForWorkBuilding() {
		return m_thirdPlaceForWorkBuilding;
	}
	
	public MasonGeometry getThirdPlaceForWorkPoint() {
		return m_thirdPlaceForWorkPoint;
	}
	
	public MasonGeometry getLeisureBuilding() {
		return m_leisureBuilding;
	}
	
	public MasonGeometry getLeisurePoint() {
		return m_leisurePoint;
	}
	
	public MasonGeometry getThirdPlaceForLeisureBuilding() {
		return m_thirdPlaceForLeisureBuilding;
	}
	
	public MasonGeometry getThirdPlaceForLeisurePoint() {
		return m_thirdPlaceForLeisurePoint;
	}
}
