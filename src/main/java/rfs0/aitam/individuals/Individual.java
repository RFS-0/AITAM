package rfs0.aitam.individuals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.activities.Activity;
import rfs0.aitam.activities.ActivityAgenda;
import rfs0.aitam.activities.ActivityCategory;
import rfs0.aitam.activities.ActivityLocation;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import rfs0.aitam.model.needs.ActualNeedTimeSplit;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.utilities.CalculationUtility;
import rfs0.aitam.utilities.GeometryUtility;
import rfs0.aitam.utilities.GraphUtility;
import rfs0.aitam.utilities.TimeUtility;
import sim.field.geo.GeomVectorField;
import sim.field.network.Network;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
import sim.util.geo.PointMoveTo;

public class Individual {

	Environment m_environment;
	private int m_id;
	
	/**
	 * Networks related activities
	 */
	private Network m_householdMembersNetwork = new Network(false);
	private int m_householdMembersNetworkId = -1;
	private int m_numberOfHouseholdNetworkActivitiesPlanned = 0;
	private Network m_workColleguesNetwork = new Network(false);
	private int m_workColleguesNetworkId = -1;
	private int m_numberOfWorkColleguesNetworkActivitiesPlanned = 0;
	private Network m_friendsNetwork = new Network(false);
	private int m_friendsNetworkId = -1;
	private int m_numberOfFriendsNetworkActivitiesPlanned = 0;

	/**
	 * Needs related activities
	 */
	private NeedTimeSplit m_targetNeedTimeSplit;
	private ActualNeedTimeSplit m_actualNeedTimeSplit = new ActualNeedTimeSplit();
	
	/**
	 * Activity related variables
	 */
	private ActivityAgenda m_activityAgenda = new ActivityAgenda();
	private ActivityAgenda m_jointActivityAgenda = new ActivityAgenda();
	private ArrayList<ActivityAgenda> m_allDayPlans = new ArrayList<>();

	/**
	 * GIS related variables
	 */
	// buildings for activities
	private Node m_homeNode;
	private Node m_thirdPlaceForHouseholdAndFamilyCareNode;
	private Node m_workPlaceNode;
	private Node m_thirdPlaceForWorkNode;
	private Node m_leisureNode;
	private Node m_thirdPlaceForLeisureNode;
	
	// dynamic locations
	private MasonGeometry m_currentLocationPoint; // point that represents the agent
	private LengthIndexedLine m_segment = null; // Used by individual to walk along line segment
	private double m_endIndexOfCurrentEdge = 0.0;
	private double m_startIndexOfCurrentEdge = 0.0;
	private double m_currentIndexOnLineOfEdge = 0.0;
	private PointMoveTo m_pointMoveTo = new PointMoveTo();
	private ArrayList<GeomPlanarGraphDirectedEdge> m_pathToNextTarget = new ArrayList<GeomPlanarGraphDirectedEdge>();
	private GeomPlanarGraphEdge m_currentEdge = null;
	private int m_edgeDirection = 1;
	private int m_currentIndexOnPathToNextTarget = 0;
	private Node m_currentNode;

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
			individualToBuild.setId(id);
			return this;
		}

		public Builder withTargetNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			individualToBuild.setTargetNeedTimeSplit(needTimeSplit);
			return this;
		}

		/**
		 * Sets the {@link MasonGeometry} that represents the building in which this
		 * {@link Individual} lives in. <b>Note:</b> Use BUILDING_TO_CLOSEST_PATH_MAP in
		 * {@link Environment} to get the {@link MasonGeometry} that represents the path
		 * which is closest to the individuals home location.
		 * 
		 * @param homeBuilding - The {@link MasonGeometry} that represents the building
		 *                     this individual lives in
		 * @return {@link Builder}
		 */
		public Builder withHomeBuilding(MasonGeometry homeBuilding) {
			String warningMessage = "Home location is invalid. The built individual may be unusable!";
			validate(homeBuilding.getGeometry().getCoordinate(), warningMessage);
			Node homeNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(homeBuilding);
			individualToBuild.m_currentLocationPoint = new MasonGeometry(Environment.GEO_FACTORY.createPoint(homeNode.getCoordinate()));
			individualToBuild.m_currentLocationPoint.isMovable = true;
			validate(homeNode, warningMessage);
			individualToBuild.m_currentNode = homeNode;
			individualToBuild.m_homeNode = homeNode;
			return this;
		}
		
		public Builder withThirdPlaceForHouseholdAndFamilyCareBuilding(MasonGeometry thirdPlaceForHouseholdAndFamilyCareBuilding) {
			String warningMessage = "Third place for household and family care is invalid. The built individual may be unusable!";
			validate(thirdPlaceForHouseholdAndFamilyCareBuilding.getGeometry().getCoordinate(), warningMessage);
			Node thirdPlaceForHouseholdAndFamilyCareNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(thirdPlaceForHouseholdAndFamilyCareBuilding);
			validate(thirdPlaceForHouseholdAndFamilyCareNode, warningMessage);
			individualToBuild.setThirdPlaceForHouseholdAndFamilyCareNode(thirdPlaceForHouseholdAndFamilyCareNode);
			return this;
		}
		
		public Builder withWorkPlaceBuilding(MasonGeometry workPlaceBuilding) {
			String warningMessage = "The work place building is invalid. The built individual may be unusable!";
			validate(workPlaceBuilding.getGeometry().getCoordinate(), warningMessage);
			Node workPlaceNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(workPlaceBuilding);
			validate(workPlaceNode, warningMessage);
			individualToBuild.setWorkPlaceNode(workPlaceNode);
			return this;
		}
		
		public Builder withThirdPlaceForWorkBuilding(MasonGeometry thirdPlaceForWorkBuilding) {
			String warningMessage = "The third place for work is invalid. The built individual may be unusable!";
			validate(thirdPlaceForWorkBuilding.getGeometry().getCoordinate(), warningMessage);
			Node thirdPlaceForWorkNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(thirdPlaceForWorkBuilding);
			validate(thirdPlaceForWorkNode, warningMessage);
			individualToBuild.setThirdPlaceForWorkNode(thirdPlaceForWorkNode);
			return this;
		}
		
		public Builder withLeisureBuilding(MasonGeometry leisureBuilding) {
			String warningMessage = "The place for leisure is invalid. The built individual may be unusable!";
			validate(leisureBuilding.getGeometry().getCoordinate(), warningMessage);
			Node leisureNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(leisureBuilding);
			validate(leisureNode, warningMessage);
			individualToBuild.setLeisureNode(leisureNode);
			return this;
		}
		
		public Builder withThirdPlaceForLeisureBuilding(MasonGeometry thirdPlaceForLeisureBuilding) {
			String warningMessage = "The third place for leisure is invalid. The built individual may be unusable!";
			validate(thirdPlaceForLeisureBuilding.getGeometry().getCoordinate(), warningMessage);
			Node thirdPlaceForLeisureNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(thirdPlaceForLeisureBuilding);
			validate(thirdPlaceForLeisureNode, warningMessage);
			individualToBuild.setThirdPlaceForLeisureNode(thirdPlaceForLeisureNode);
			return this;
		}
		
		private void validate(Object objToValidate, String message) {
			if (objToValidate == null) {
				Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, message);
			}
		}
		
		public Builder withHousholdMembersNetworkId(int householdMembersNetworkId) {
			individualToBuild.setHouseholdMembersNetworkId(householdMembersNetworkId);
			return this;
		}
		
		public Builder withHousholdMembersNetwork(Network householdMembersNetwork) {
			individualToBuild.setHouseholdMembersNetwork(householdMembersNetwork);
			return this;
		}
		
		public Builder addHouseholdMember(Individual householdMember) {
			individualToBuild.getHouseholdMembersNetwork().addNode(householdMember);
			return this;
		}
		
		public Builder withWorkColleguesNetworkId(int workColleguesNetworkId) {
			individualToBuild.setWorkColleguesNetworkId(workColleguesNetworkId);
			return this;
		}
		
		public Builder withWorkColleguesNetwork(Network workColleguesNetwork) {
			individualToBuild.setWorkColleguesNetwork(workColleguesNetwork);
			return this;
		}
		
		public Builder addWorkCollegue(Individual workCollegue) {
			individualToBuild.getWorkColleguesNetwork().addNode(workCollegue);
			return this;
		}
		
		public Builder withFriendsNetworkId(int friendsNetworkId) {
			individualToBuild.setFriendsNetworkId(friendsNetworkId);
			return this;
		}
		
		public Builder withFriendsNetwork(Network friendsNetwork) {
			individualToBuild.setFriendsNetwork(friendsNetwork);
			return this;
		}
		
		public Builder addFriend(Individual friend) {
			individualToBuild.getFriendsNetwork().addNode(friend);
			return this;
		}
		
		public Builder adjust(Individual individualToAdjust) {
			individualToBuild = individualToAdjust;
			return this;
		}
		
		public Individual build() {
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual();
			return builtIndividual;
		}
	}
	
	// PLAN JOINT ACTIVITIES
	
	public void planJointActivities(Environment environment) {
		if (isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY)) {
			planActivityForNetwork(environment, getHouseholdMembersNetwork(), NetworkType.HOUSEHOLD_NETWORK , ISimulationSettings.AVAILABLE_START_TIMES_FOR_HOUSEHOLD_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY)) {
			planActivityForNetwork(environment, getWorkColleguesNetwork(), NetworkType.WORK_COLLEGUES_NETWORK, ISimulationSettings.AVAILABLE_START_TIMES_FOR_WORK_COLLEGUES_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY)) {
			planActivityForNetwork(environment, getFriendsNetwork(), NetworkType.FRIENDS_NETWORK, ISimulationSettings.AVAILABLE_START_TIMES_FOR_FRIENDS_NETWORK_ACTIVITIES);
		}
	}
	
	public boolean isOpenForNetworkActivities(Environment environment, int maxNumberOfActivitiesForNetworkType, double probabilityOfPlaningActivityForNetworkType) {
		if (!TimeUtility.isDayFullyPlanned(environment, getJointActivityAgenda())
				&& getNumberOfHouseholdNetworkActivitiesPlanned() < maxNumberOfActivitiesForNetworkType
				&& environment.random.nextDouble(true, true) <= probabilityOfPlaningActivityForNetworkType) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void planActivityForNetwork(Environment environment, Network network, NetworkType type, ArrayList<DateTime> availableStartTimes) {
		ArrayList<Individual> networkMemberParticipating = determineParticipatingNetworkMembers(environment, network, type);
		if (networkMemberParticipating.size() > 1) {
			
		}
		Interval intervalOfJointActivity = determineIntervalOfJointActivity(environment, networkMemberParticipating, availableStartTimes);
		if (intervalOfJointActivity != null) {
			ArrayList<Activity> availableActivities = environment.getAllActivities().stream()
					.filter(activity -> activity.isJointActivity())
					.filter(activity -> activity.getActivityCategory() == ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE)
					.filter(activity -> activity.isAvailableAt(environment.getSimulationTime().getCurrentWeekDay(), intervalOfJointActivity))
					.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
					.collect(Collectors.toCollection(ArrayList::new));
			if (availableActivities.size() == 0) {
				Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("No activity availabe for interval interval: %s. Make sure there is always at least one activity available!", String.valueOf(intervalOfJointActivity)));	
			}
			// setup activity for all participating network members
			Activity jointActivity = availableActivities.get(environment.random.nextInt(availableActivities.size()));
			Node jointActivityNode = getActivityNode(jointActivity);
			for (Individual individual: networkMemberParticipating) {
				individual.getJointActivityAgenda().addActivityForInterval(intervalOfJointActivity, jointActivity);
				individual.getJointActivityAgenda().addNodeForInterval(intervalOfJointActivity, jointActivityNode);
				switch (type) {
				case FRIENDS_NETWORK:
					individual.incrementNumberOfFriendsNetworkActivitiesPlanned();
					break;
				case HOUSEHOLD_NETWORK:
					individual.incrementNumberOfHouseholdNetworkActivitiesPlanned();
					break;
				case WORK_COLLEGUES_NETWORK:
					individual.incrementNumberOfWorkColleguesNetworkActivitiesPlanned();
					break;
				default:
					Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("%s is an invalid NetworkType! Can not increment number of activities for this type!", String.valueOf(type)));
					break;
				}
			}
		}
	}
	
	private ArrayList<Individual> determineParticipatingNetworkMembers(Environment environment, Network network, NetworkType type) {
		ArrayList<Individual> networkMemberParticipating = new ArrayList<>();
		networkMemberParticipating.add(this);
		for (Object individualObj: network.getAllNodes()) {
			Individual individual = (Individual) individualObj;
			switch (type) {
			case HOUSEHOLD_NETWORK:
				if (individual.isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY)) {
					networkMemberParticipating.add(individual);
				}
				break;
			case WORK_COLLEGUES_NETWORK:
				if (individual.isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY)) {
					networkMemberParticipating.add(individual);
				}
				break;
			case FRIENDS_NETWORK:
				if (individual.isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY)) {
					networkMemberParticipating.add(individual);
				}
				break;
			default:
				Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("%s is an invalid NetworkType! Can not plan activity for this type!", String.valueOf(type)));
				break;
			}
		}
		return networkMemberParticipating;
	}
	
	
	private Interval determineIntervalOfJointActivity(Environment environment, ArrayList<Individual> networkMemberParticipating, ArrayList<DateTime> availableStartTimes) {
		Interval intervalOfInterest;
		int numberOfTrials = 0;
		do {
			DateTime startOfJointActivity = availableStartTimes.get(environment.random.nextInt(availableStartTimes.size()));
			BigDecimal duration = ISimulationSettings.ACTIVITY_DURATIONS_IN_MINUTES.get(environment.random.nextInt(ISimulationSettings.ACTIVITY_DURATIONS_IN_MINUTES.size()));
			DateTime endOfJointActivity = startOfJointActivity.plusMinutes(duration.intValue());
			intervalOfInterest = new Interval(startOfJointActivity, endOfJointActivity);
			numberOfTrials++;
		} 
		while (TimeUtility.isIntervalOverlappingAnyAgenda(networkMemberParticipating, intervalOfInterest) && numberOfTrials < ISimulationSettings.MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY);
		if (numberOfTrials < ISimulationSettings.MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY) {
			return intervalOfInterest;
		} 
		else {
			return null;
		}
	}
	
	private Node getActivityNode(Activity activity) {
		switch (activity.getActivityLocation()) {
		case HOME:
			return getHomeNode();
		case THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE:
			return getThirdPlaceForHouseholdAndFamilyCareNode();
		case LEISURE:
			return getLeisureNode();
		case THIRD_PLACE_FOR_LEISURE:
			return getThirdPlaceForLeisureNode();
		case THIRD_PLACE_FOR_WORK:
			return getThirdPlaceForWorkNode();
		case WORK:
			return getWorkPlaceNode();
		default:
			Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, "Could not choose activty location!");
			return null;
		}
	}
	
	// PLAN INDIVIDUAL ACTIVITIES
	
	public void planIndividualActivities(Environment environment) {
		getAllDayPlans().clear();
		DateTime endOfCurrentDay = TimeUtility.getStartOfNextDay(environment.getSimulationTime().getCurrentDateTime()).minusMinutes(1);
		for (int i = 0; i < ISimulationSettings.NUMBER_OF_PLANS_TO_GENERATE; i++) {
			ActivityAgenda randomPlan = ActivityAgenda.newInstance(getJointActivityAgenda());
			while (!TimeUtility.isDayFullyPlanned(environment, randomPlan)) {
				Interval availableInterval = TimeUtility.getFirstAvailableInterval(environment, randomPlan);
				int maxDurationInMinutes = (int) availableInterval.toDuration().getStandardMinutes();
				BigDecimal duration = determineDuration(environment, maxDurationInMinutes);
				Interval activityInterval = determineActivityInterval(availableInterval, duration.intValue(), endOfCurrentDay);
				Activity activity = determineActivity(environment, randomPlan, activityInterval);
				Node activityNode = getActivityNode(activity);
				randomPlan.addActivityForInterval(activityInterval, activity);
				randomPlan.addNodeForInterval(activityInterval, activityNode);
				for (Need needSatisfiedByRandomActivity: activity.getNeedTimeSplit().getNeedTimeSplit().keySet()) {
					BigDecimal fractionForNeed = activity.getNeedTimeSplit().getFractionForNeed(needSatisfiedByRandomActivity);
					BigDecimal timeSpentSatisfyingNeed = fractionForNeed.multiply(duration);
					randomPlan.getActualNeedTimeSplit().updateNeedTimeSplit(needSatisfiedByRandomActivity, timeSpentSatisfyingNeed);
				}
			}
			getAllDayPlans().add(randomPlan);
		}
	}
	
	private BigDecimal determineDuration(Environment environment, int maxDurationInMinutes) {
		List<BigDecimal> availableDurations = ISimulationSettings.ACTIVITY_DURATIONS_IN_MINUTES.stream()
				.filter(minutes -> minutes.compareTo(CalculationUtility.createBigDecimal(maxDurationInMinutes)) <= 0)
				.collect(Collectors.toList());
		if (availableDurations.size() == 0) {
			return CalculationUtility.createBigDecimal(ISimulationSettings.MIN_DURATION);
		}
		else {
			return availableDurations.get(environment.random.nextInt(availableDurations.size()));
		}
	}
	
	private Interval determineActivityInterval(Interval availableInterval, int duration, DateTime endOfCurrentDay) {
		DateTime end = availableInterval.getStart().plusMinutes(duration);
		if (end.isAfter(endOfCurrentDay)) { // make sure plan ends at 23:59 of current day
			end = endOfCurrentDay;
		}
		return new Interval(availableInterval.getStart(), end);
	}
	
	private Activity determineActivity(Environment environment, ActivityAgenda randomAgenda, Interval activityInterval) {
		ArrayList<Activity> availableActivities;
		if ((int) activityInterval.toDuration().getStandardMinutes() == ISimulationSettings.MIN_DURATION) {
			ActivityLocation currentLocation = randomAgenda.getActivityForDateTime(activityInterval.getStart().minusMinutes(1)).getActivityLocation();
			availableActivities = environment.getAllActivities().stream()
				.filter(activity -> activity.isAvailableAt(environment.getSimulationTime().getCurrentWeekDay(), activityInterval))
				.filter(activity -> activity.getActivityLocation() == currentLocation)
				.filter(activity -> !activity.isJointActivity())
				.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
				.collect(Collectors.toCollection(ArrayList::new));
		}
		else {
			availableActivities = environment.getAllActivities().stream()
				.filter(activity -> activity.isAvailableAt(environment.getSimulationTime().getCurrentWeekDay(), activityInterval))
				.filter(activity -> !activity.isJointActivity())
				.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
				.collect(Collectors.toCollection(ArrayList::new));
		}
		if (availableActivities.size() == 0) {
			Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("No activity availabe for interval interval: %s. Location is: %s. Make sure there is always at least one activity available!", String.valueOf(activityInterval), String.valueOf(randomAgenda.getActivityForDateTime(activityInterval.getStart().minusMinutes(1)).getActivityLocation())));	
		}
		return availableActivities.get(environment.random.nextInt(availableActivities.size()));
	}

	
	/**
	 * Use MSE for evaluation of best plan
	 */
	public void chooseBestAgenda() {
		ActivityAgenda bestAgenda = null;
		BigDecimal minimumSquaredMeanError = new BigDecimal(Integer.MAX_VALUE);
		for (int i = 0; i < getAllDayPlans().size(); i++) {
			BigDecimal meanSquaredError = CalculationUtility.calculateMeanSquaredError(getAllDayPlans().get(i), getTargetNeedTimeSplit());
			if (meanSquaredError.compareTo(minimumSquaredMeanError) < 0) {
				minimumSquaredMeanError = meanSquaredError;
				bestAgenda = getAllDayPlans().get(i);
			}
		}
		setActivityAgenda(bestAgenda);
	}
	
	// EXECUTE ACTIVITIES

	public void executeActivity(Environment environment) {
		Activity currentActivity = getActivityAgenda().getActivityForDateTime(environment.getSimulationTime().getCurrentTime());
		Node currentNode = getCurrentNode();
		Node targetNode = getActivityNode(currentActivity);
		if (!currentNode.equals(targetNode)) {
			if (getPathToNextTarget().isEmpty()) {
				initPathToTarget(targetNode);
			}
			if (!hasReachedTarget()) {
				moveTowardsTarget();
			}
		}
		if (hasReachedTarget()) {
			if (!currentActivity.isJointActivity()) {
				updateActualNeedTimeSplit(currentActivity);
			}
			else { // joint activity
				// TODO: how to handle case where individual is the only one currently at target location
				updateActualNeedTimeSplit(currentActivity);
			}
		}
	}
	
	private void initPathToTarget(Node targetNode) {
		Node currentNode = getCurrentNode();
		if (currentNode == null || targetNode == null) {
			Logger.getLogger(Individual.class.getName()).log(Level.WARNING, String.format("Can not initialize path to target building. Got values currentNode=%s; targetNode=%s.", String.valueOf(currentNode), String.valueOf(targetNode)));
		}
		ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget = GraphUtility.astarPath(currentNode, targetNode);
		if (!pathToTarget.isEmpty()) {
			setPathToNextTarget(pathToTarget);
			setCurrentEdge((GeomPlanarGraphEdge) pathToTarget.get(0).getEdge());
			setupNextEdge();
			updatePosition(getSegment().extractPoint(getCurrentIndexOnLineOfEdge()));
//			colorPathToTarget();
		} 
		else { // already at the target location
		}
	}
	
	
	/**
	 * Sets up the next edge on which the individual continues to its target
	 * location
	 * 
	 * @param nextEdge - the GeomPlanarGraphEdge to traverse next
	 */
	private void setupNextEdge() {
		GeomPlanarGraphEdge nextEdge = (GeomPlanarGraphEdge) getPathToNextTarget().get(getCurrentIndexOnPathToNextTarget()).getEdge();
		updateEdgeTraffic(nextEdge);
		setCurrentEdge(nextEdge);
		LineString lineOfNextEdge = nextEdge.getLine();
		setSegment(new LengthIndexedLine(nextEdge.getLine()));
		setStartIndexOfCurrentEdge(getSegment().getStartIndex());
		setEndIndexOfCurrentEdge(getSegment().getEndIndex());
		setEdgeDirection(1);
		setCurrentIndexOnLineOfEdge(0);
		double distanceToStart = lineOfNextEdge.getStartPoint().distance(getCurrentLocationPoint().geometry);
		double distanceToEnd = lineOfNextEdge.getEndPoint().distance(getCurrentLocationPoint().geometry);
		if (distanceToStart <= distanceToEnd) {
			setCurrentIndexOnLineOfEdge(getSegment().getStartIndex());
			setEdgeDirection(1);
		} else {
			setCurrentIndexOnLineOfEdge(getSegment().getEndIndex());
			setEdgeDirection(-1);
		}
	}
	
	private void updateEdgeTraffic(GeomPlanarGraphEdge nextEdge) {
		if (m_environment.getEdgeTraffic().get(getCurrentEdge()) != null) {
			m_environment.getEdgeTraffic().get(getCurrentEdge()).remove(this); // current edge is actually the old edge here
		}
		if (m_environment.getEdgeTraffic().get(nextEdge) == null) {
			m_environment.getEdgeTraffic().put(nextEdge, new ArrayList<Individual>());
		}
		m_environment.getEdgeTraffic().get(nextEdge).add(this);
	}
	
	/**
	 * Update the position of this individual by moving it to to the provided
	 * {@link Coordinate} <code>c</code>.
	 * 
	 * @param c - The coordinate to which the individual is moved to
	 */
	private void updatePosition(Coordinate c) {
		getPointMoveTo().setCoordinate(c);
		m_environment.getIndividualsField().setGeometryLocation(getCurrentLocationPoint(), getPointMoveTo());
	}
	
	private boolean hasReachedTarget() {
		if (getPathToNextTarget().isEmpty()) { // current location is target location
			return true;
		}
		else {
			return getCurrentIndexOnPathToNextTarget() >= getPathToNextTarget().size();
		}
	}
	
	public void colorPathToTarget() {
		GeomVectorField pathField = m_environment.getPathField();
		List<Coordinate> coordinatesOfPath = getPathToNextTarget()
				.stream()
				.map(path -> path.getCoordinate())
				.collect(Collectors.toList());
		for (Coordinate coordinate : coordinatesOfPath) {
			ArrayList<MasonGeometry> coveringObjects = GeometryUtility
					.getCoveringObjects(new MasonGeometry(Environment.GEO_FACTORY.createPoint(coordinate)), pathField);
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
	
	private void updateActualNeedTimeSplit(Activity activity) {
		for (Need needSatisfiedByRandomActivity: activity.getNeedTimeSplit().getNeedTimeSplit().keySet()) {
			getActualNeedTimeSplit().updateNeedTimeSplit(needSatisfiedByRandomActivity, activity.getNeedTimeSplit().getFractionForNeed(needSatisfiedByRandomActivity));
		}
	}
	
	private void moveTowardsTarget() {
		setCurrentIndexOnLineOfEdge(getCurrentIndexOnLineOfEdge() + calculateTravellingDistance());
		if (getEdgeDirection() == 1 && getCurrentIndexOnLineOfEdge() >= getEndIndexOfCurrentEdge()) {
			// positive movement
			moveRemainingDistanceOnNextEdge(getCurrentIndexOnLineOfEdge() - getEndIndexOfCurrentEdge());

		} else if (getEdgeDirection() == -1 && getCurrentIndexOnLineOfEdge() <= getStartIndexOfCurrentEdge()) {
			// negative movement
			moveRemainingDistanceOnNextEdge(getStartIndexOfCurrentEdge() - getCurrentIndexOnLineOfEdge());
		}
		updatePosition(getSegment().extractPoint(getCurrentIndexOnLineOfEdge()));
	}
	
	public boolean isPlanningPossible(Environment environment, ArrayList<DateTime> availableTimePoints) {
		return availableTimePoints.stream().anyMatch(timePoint -> timePoint.equals(environment.getSimulationTime().getCurrentTime()));
	}
	
	public void carryOverJointActivities(Environment environment) {
		getActivityAgenda().clearAgenda();
		for (Interval interval: getJointActivityAgenda().getIntervals()) {
			getActivityAgenda().addActivityForInterval(interval, getJointActivityAgenda().getActivityForInterval(interval));
			getActivityAgenda().addNodeForInterval(interval, getJointActivityAgenda().getNodeForInterval(interval));
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
		return getEdgeDirection() * ISimulationSettings.MAX_VELOCITY;
	}
	
	/**
	 * 
	 * 
	 * @param remainingDistance the distance the agent can still travel this turn
	 */
	private void moveRemainingDistanceOnNextEdge(double remainingDistance) {
		setCurrentIndexOnPathToNextTarget(getCurrentIndexOnPathToNextTarget() + 1);
		if (hasReachedTarget()) {
			setCurrentIndexOnLineOfEdge(getSegment().getEndIndex());
			setCurrentIndexOnPathToNextTarget(0);
			getPathToNextTarget().clear();
			return;
		}
		setupNextEdge();
		setCurrentIndexOnLineOfEdge(getCurrentIndexOnLineOfEdge() + remainingDistance);
		if (getEdgeDirection() == 1 && getCurrentIndexOnLineOfEdge() >= getEndIndexOfCurrentEdge()) {
			// positive movement
			moveRemainingDistanceOnNextEdge(getCurrentIndexOnLineOfEdge() - getEndIndexOfCurrentEdge());

		} else if (getEdgeDirection() == -1 && getCurrentIndexOnLineOfEdge() <= getStartIndexOfCurrentEdge()) {
			// negative movement
			moveRemainingDistanceOnNextEdge(getStartIndexOfCurrentEdge() - getCurrentIndexOnLineOfEdge());
		}
	}
	
	// HELPER FUNCTIONS
	
	public void updateActualNeedTimeSplit(Need need, BigDecimal timeSpentSatisfyingNeed) {
		getActualNeedTimeSplit().updateNeedTimeSplit(need, timeSpentSatisfyingNeed);
	}
	
	public void initNewDay() {
		getActivityAgenda().clearAgenda();
		getActivityAgenda().clearLocations();
		getJointActivityAgenda().clearAgenda();
		getJointActivityAgenda().clearLocations();
		setNumberOfFriendsNetworkActivitiesPlanned(0);
		setNumberOfHouseholdNetworkActivitiesPlanned(0);
		setNumberOfWorkColleguesNetworkActivitiesPlanned(0);
	}

	// GETTER & SETTER
	
	public int getId() {
		return m_id;
	}

	public void setId(int id) {
		m_id = id;
	}

	public Network getHouseholdMembersNetwork() {
		return m_householdMembersNetwork;
	}

	public void setHouseholdMembersNetwork(Network householdMembersNetwork) {
		m_householdMembersNetwork = householdMembersNetwork;
	}

	public int getHouseholdMembersNetworkId() {
		return m_householdMembersNetworkId;
	}

	public void setHouseholdMembersNetworkId(int householdMembersNetworkId) {
		m_householdMembersNetworkId = householdMembersNetworkId;
	}

	public int getNumberOfHouseholdNetworkActivitiesPlanned() {
		return m_numberOfHouseholdNetworkActivitiesPlanned;
	}

	public void setNumberOfHouseholdNetworkActivitiesPlanned(int numberOfHouseholdNetworkActivitiesPlanned) {
		m_numberOfHouseholdNetworkActivitiesPlanned = numberOfHouseholdNetworkActivitiesPlanned;
	}

	public void incrementNumberOfHouseholdNetworkActivitiesPlanned() {
		m_numberOfHouseholdNetworkActivitiesPlanned++;
	}

	public Network getWorkColleguesNetwork() {
		return m_workColleguesNetwork;
	}

	public void setWorkColleguesNetwork(Network workColleguesNetwork) {
		m_workColleguesNetwork = workColleguesNetwork;
	}

	public int getWorkColleguesNetworkId() {
		return m_workColleguesNetworkId;
	}

	public void setWorkColleguesNetworkId(int workColleguesNetworkId) {
		m_workColleguesNetworkId = workColleguesNetworkId;
	}

	public int getNumberOfWorkColleguesNetworkActivitiesPlanned() {
		return m_numberOfWorkColleguesNetworkActivitiesPlanned;
	}

	public void setNumberOfWorkColleguesNetworkActivitiesPlanned(int numberOfWorkColleguesNetworkActivitiesPlanned) {
		m_numberOfWorkColleguesNetworkActivitiesPlanned = numberOfWorkColleguesNetworkActivitiesPlanned;
	}
	
	public void incrementNumberOfWorkColleguesNetworkActivitiesPlanned() {
		m_numberOfWorkColleguesNetworkActivitiesPlanned++;
	}

	public Network getFriendsNetwork() {
		return m_friendsNetwork;
	}

	public void setFriendsNetwork(Network friendsNetwork) {
		m_friendsNetwork = friendsNetwork;
	}

	public int getFriendsNetworkId() {
		return m_friendsNetworkId;
	}

	public void setFriendsNetworkId(int friendsNetworkId) {
		m_friendsNetworkId = friendsNetworkId;
	}

	public int getNumberOfFriendsNetworkActivitiesPlanned() {
		return m_numberOfFriendsNetworkActivitiesPlanned;
	}

	public void setNumberOfFriendsNetworkActivitiesPlanned(int numberOfFriendsNetworkActivitiesPlanned) {
		m_numberOfFriendsNetworkActivitiesPlanned = numberOfFriendsNetworkActivitiesPlanned;
	}
	
	public void incrementNumberOfFriendsNetworkActivitiesPlanned() {
		m_numberOfFriendsNetworkActivitiesPlanned++;
	}

	public NeedTimeSplit getTargetNeedTimeSplit() {
		return m_targetNeedTimeSplit;
	}

	public void setTargetNeedTimeSplit(NeedTimeSplit targetNeedTimeSplit) {
		m_targetNeedTimeSplit = targetNeedTimeSplit;
	}

	public ActualNeedTimeSplit getActualNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}

	public void setActualNeedTimeSplit(ActualNeedTimeSplit actualNeedTimeSplit) {
		m_actualNeedTimeSplit = actualNeedTimeSplit;
	}

	public ActivityAgenda getActivityAgenda() {
		return m_activityAgenda;
	}

	public void setActivityAgenda(ActivityAgenda activityAgenda) {
		m_activityAgenda = activityAgenda;
	}

	public ActivityAgenda getJointActivityAgenda() {
		return m_jointActivityAgenda;
	}

	public void setJointActivityAgenda(ActivityAgenda jointActivityAgenda) {
		m_jointActivityAgenda = jointActivityAgenda;
	}

	public ArrayList<ActivityAgenda> getAllDayPlans() {
		return m_allDayPlans;
	}

	public void setAllDayPlans(ArrayList<ActivityAgenda> allDayPlans) {
		m_allDayPlans = allDayPlans;
	}

	public Node getHomeNode() {
		return m_homeNode;
	}

	public void setHomeNode(Node homeNode) {
		m_homeNode = homeNode;
	}

	public Node getThirdPlaceForHouseholdAndFamilyCareNode() {
		return m_thirdPlaceForHouseholdAndFamilyCareNode;
	}

	public void setThirdPlaceForHouseholdAndFamilyCareNode(Node thirdPlaceForHouseholdAndFamilyCareNode) {
		m_thirdPlaceForHouseholdAndFamilyCareNode = thirdPlaceForHouseholdAndFamilyCareNode;
	}

	public Node getWorkPlaceNode() {
		return m_workPlaceNode;
	}

	public void setWorkPlaceNode(Node workPlaceNode) {
		m_workPlaceNode = workPlaceNode;
	}

	public Node getThirdPlaceForWorkNode() {
		return m_thirdPlaceForWorkNode;
	}

	public void setThirdPlaceForWorkNode(Node thirdPlaceForWorkNode) {
		m_thirdPlaceForWorkNode = thirdPlaceForWorkNode;
	}

	public Node getLeisureNode() {
		return m_leisureNode;
	}

	public void setLeisureNode(Node leisureNode) {
		m_leisureNode = leisureNode;
	}

	public Node getThirdPlaceForLeisureNode() {
		return m_thirdPlaceForLeisureNode;
	}

	public void setThirdPlaceForLeisureNode(Node thirdPlaceForLeisureNode) {
		m_thirdPlaceForLeisureNode = thirdPlaceForLeisureNode;
	}

	public MasonGeometry getCurrentLocationPoint() {
		return m_currentLocationPoint;
	}

	public void setCurrentLocationPoint(MasonGeometry currentLocationPoint) {
		m_currentLocationPoint = currentLocationPoint;
	}

	public LengthIndexedLine getSegment() {
		return m_segment;
	}

	public void setSegment(LengthIndexedLine segment) {
		m_segment = segment;
	}

	public double getEndIndexOfCurrentEdge() {
		return m_endIndexOfCurrentEdge;
	}

	public void setEndIndexOfCurrentEdge(double endIndexOfCurrentEdge) {
		m_endIndexOfCurrentEdge = endIndexOfCurrentEdge;
	}

	public double getStartIndexOfCurrentEdge() {
		return m_startIndexOfCurrentEdge;
	}

	public void setStartIndexOfCurrentEdge(double startIndexOfCurrentEdge) {
		m_startIndexOfCurrentEdge = startIndexOfCurrentEdge;
	}

	public double getCurrentIndexOnLineOfEdge() {
		return m_currentIndexOnLineOfEdge;
	}

	public void setCurrentIndexOnLineOfEdge(double currentIndexOnLineOfEdge) {
		m_currentIndexOnLineOfEdge = currentIndexOnLineOfEdge;
	}

	public PointMoveTo getPointMoveTo() {
		return m_pointMoveTo;
	}

	public void setPointMoveTo(PointMoveTo pointMoveTo) {
		m_pointMoveTo = pointMoveTo;
	}

	public ArrayList<GeomPlanarGraphDirectedEdge> getPathToNextTarget() {
		return m_pathToNextTarget;
	}

	public void setPathToNextTarget(ArrayList<GeomPlanarGraphDirectedEdge> pathToNextTarget) {
		m_pathToNextTarget = pathToNextTarget;
	}

	public GeomPlanarGraphEdge getCurrentEdge() {
		return m_currentEdge;
	}

	public void setCurrentEdge(GeomPlanarGraphEdge currentEdge) {
		m_currentEdge = currentEdge;
	}

	public int getEdgeDirection() {
		return m_edgeDirection;
	}

	public void setEdgeDirection(int edgeDirection) {
		m_edgeDirection = edgeDirection;
	}

	public int getCurrentIndexOnPathToNextTarget() {
		return m_currentIndexOnPathToNextTarget;
	}

	public void setCurrentIndexOnPathToNextTarget(int currentIndexOnPathToNextTarget) {
		m_currentIndexOnPathToNextTarget = currentIndexOnPathToNextTarget;
	}
	
	public Node getCurrentNode() {
		return m_currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		m_currentNode = currentNode;
	}
}
