package individuals;

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
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.planargraph.Node;

import activities.Activity;
import activities.ActivityAgenda;
import activities.ActivityCategory;
import activities.ActivityLocation;
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
	private int m_currentIndexOnPathToNextTarget = 0;
	private Node m_currentNode;
	private Node m_targetNode;

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
			validate(homeBuilding, warningMessage);
			individualToBuild.m_homeBuilding = homeBuilding;
			Point pointOfClosestPath = getPointForPath(homeBuilding);
			validate(pointOfClosestPath, warningMessage);
			individualToBuild.m_currentLocationPoint = new MasonGeometry(pointOfClosestPath);
			individualToBuild.m_currentLocationPoint.isMovable = true;
			individualToBuild.m_currentNode = individualToBuild.getCurrentNode();
			validate(individualToBuild.m_currentNode, warningMessage);
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
				Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, "Target node is invalid. The built individual may be unusable!");
			}
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
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual();
			return builtIndividual;
		}
	}

	public void executeActivity(Environment environment) {
		Activity currentActivity = getActivityAgenda().getActivityForDateTime(environment.getSimulationTime().getCurrentTime());
		MasonGeometry currentLocation = getCurrentLocation();
		MasonGeometry activityLocation = getActivityLocation(currentActivity);
		if (!currentLocation.equals(activityLocation)) {
			if (m_pathToNextTarget.isEmpty()) {
				initPathToTarget(activityLocation);
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
	
	private void updateActualNeedTimeSplit(Activity activity) {
		for (Need needSatisfiedByRandomActivity: activity.getNeedTimeSplit().getNeedTimeSplit().keySet()) {
			m_actualNeedTimeSplit.updateNeedTimeSplit(needSatisfiedByRandomActivity, activity.getNeedTimeSplit().getFractionForNeed(needSatisfiedByRandomActivity));
		}
	}
	
	private void moveTowardsTarget() {
		m_currentIndexOnLineOfEdge += calculateTravellingDistance();
		if (m_edgeDirection == 1 && m_currentIndexOnLineOfEdge >= m_endIndexOfCurrentEdge) {
			// positive movement
			moveRemainingDistanceOnNextEdge(m_currentIndexOnLineOfEdge - m_endIndexOfCurrentEdge);

		} else if (m_edgeDirection == -1 && m_currentIndexOnLineOfEdge <= m_startIndexOfCurrentEdge) {
			// negative movement
			moveRemainingDistanceOnNextEdge(m_startIndexOfCurrentEdge - m_currentIndexOnLineOfEdge);
		}
		updatePosition(m_segment.extractPoint(m_currentIndexOnLineOfEdge));
	}
	
	public boolean isPlanningPossible(Environment environment, ArrayList<DateTime> availableTimePoints) {
		return availableTimePoints.stream().anyMatch(timePoint -> timePoint.equals(environment.getSimulationTime().getCurrentTime()));
	}
	
	public void carryOverJointActivities(Environment environment) {
		m_activityAgenda.clearAgenda();
		for (Interval interval: m_jointActivityAgenda.getIntervals()) {
			m_activityAgenda.addActivityForInterval(interval, m_jointActivityAgenda.getActivityForInterval(interval));
			m_activityAgenda.addLocationForInterval(interval, m_jointActivityAgenda.getLocationForInterval(interval));
		}
	}
	
	public void planJointActivities(Environment environment) {
		if (isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY)) {
			planActivityForNetwork(environment, m_householdMembersNetwork, NetworkType.HOUSEHOLD_NETWORK , ISimulationSettings.AVAILABLE_START_TIMES_FOR_HOUSEHOLD_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY)) {
			planActivityForNetwork(environment, m_workColleguesNetwork, NetworkType.WORK_COLLEGUES_NETWORK, ISimulationSettings.AVAILABLE_START_TIMES_FOR_WORK_COLLEGUES_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(environment, ISimulationSettings.MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY, ISimulationSettings.PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY)) {
			planActivityForNetwork(environment, m_friendsNetwork, NetworkType.FRIENDS_NETWORK, ISimulationSettings.AVAILABLE_START_TIMES_FOR_FRIENDS_NETWORK_ACTIVITIES);
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
			MasonGeometry jointActivityLocation = getActivityLocation(jointActivity);
			for (Individual individual: networkMemberParticipating) {
				individual.getJointActivityAgenda().addActivityForInterval(intervalOfJointActivity, jointActivity);
				individual.getJointActivityAgenda().addLocationForInterval(intervalOfJointActivity, jointActivityLocation);
				individual.incrementNumberOfHouseholdNetworkActivitiesPlanned();	
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
	
	public void planIndividualActivities(Environment environment) {
		m_allDayPlans.clear();
		DateTime endOfCurrentDay = TimeUtility.getStartOfNextDay(environment.getSimulationTime().getCurrentDateTime()).minusMinutes(1);
		for (int i = 0; i < ISimulationSettings.NUMBER_OF_PLANS_TO_GENERATE; i++) {
			ActivityAgenda randomPlan = ActivityAgenda.newInstance(m_jointActivityAgenda);
			while (!TimeUtility.isDayFullyPlanned(environment, randomPlan)) {
				Interval availableInterval = TimeUtility.getFirstAvailableInterval(environment, randomPlan);
				int maxDurationInMinutes = (int) availableInterval.toDuration().getStandardMinutes();
				BigDecimal duration = determineDuration(environment, maxDurationInMinutes);
				Interval activityInterval = determineActivityInterval(availableInterval, duration.intValue(), endOfCurrentDay);
				Activity activity = determineActivity(environment, randomPlan, activityInterval);
				MasonGeometry activityLocation = getActivityLocation(activity);
				randomPlan.addActivityForInterval(activityInterval, activity);
				randomPlan.addLocationForInterval(activityInterval, activityLocation);
				for (Need needSatisfiedByRandomActivity: activity.getNeedTimeSplit().getNeedTimeSplit().keySet()) {
					BigDecimal fractionForNeed = activity.getNeedTimeSplit().getFractionForNeed(needSatisfiedByRandomActivity);
					BigDecimal timeSpentSatisfyingNeed = fractionForNeed.multiply(duration);
					randomPlan.getActualNeedTimeSplit().updateNeedTimeSplit(needSatisfiedByRandomActivity, timeSpentSatisfyingNeed);
				}
			}
			m_allDayPlans.add(randomPlan);
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
			Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("No activity availabe for interval interval: %s. Make sure there is always at least one activity available!", String.valueOf(activityInterval)));	
		}
		return availableActivities.get(environment.random.nextInt(availableActivities.size()));
	}

	
	/**
	 * Use MSE for evaluation of best plan
	 */
	public void chooseBestAgenda() {
		ActivityAgenda bestAgenda = null;
		BigDecimal minimumSquaredMeanError = new BigDecimal(Integer.MAX_VALUE);
		for (int i = 0; i < m_allDayPlans.size(); i++) {
			BigDecimal meanSquaredError = CalculationUtility.calculateMeanSquaredError(m_allDayPlans.get(i), m_targetNeedTimeSplit);
			if (meanSquaredError.compareTo(minimumSquaredMeanError) < 0) {
				minimumSquaredMeanError = meanSquaredError;
				bestAgenda = m_allDayPlans.get(i);
			}
		}
		m_activityAgenda = bestAgenda;
	}
	
	private MasonGeometry getActivityLocation(Activity activity) {
		switch (activity.getActivityLocation()) {
		case HOME:
			return m_homeBuilding;
		case THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE:
			return m_thirdPlaceForHouseholdAndFamilyCareBuilding;
		case LEISURE:
			return m_leisureBuilding;
		case THIRD_PLACE_FOR_LEISURE:
			return m_thirdPlaceForLeisureBuilding;
		case THIRD_PLACE_FOR_WORK:
			return m_thirdPlaceForWorkBuilding;
		case WORK:
			return m_thirdPlaceForWorkBuilding;
		default:
			Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, "Could not choose activty location!");
			return null;
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
			m_pathToNextTarget.clear();
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
		m_environment.getIndividualsGeomVectorField().setGeometryLocation(m_currentLocationPoint, m_pointMoveTo);
	}
	
	private boolean hasReachedTarget() {
		if (m_pathToNextTarget.isEmpty()) { // current location is target location
			return true;
		}
		else {
			return m_currentIndexOnPathToNextTarget >= m_pathToNextTarget.size();
		}
	}
	
	/**
	 * Sets up the next edge on which the individual continues to its target
	 * location
	 * 
	 * @param nextEdge - the GeomPlanarGraphEdge to traverse next
	 */
	private void setupNextEdge() {
		GeomPlanarGraphEdge nextEdge = (GeomPlanarGraphEdge) m_pathToNextTarget.get(m_currentIndexOnPathToNextTarget).getEdge();
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

	private void initPathToTarget(MasonGeometry targetBuilding) {
		MasonGeometry closestPathToBuilding = Environment.BUILDING_TO_CLOSEST_PATH_MAP.get(targetBuilding);
		Node currentNode = m_currentNode;
		Node targetNode = getNode(closestPathToBuilding);
		if (currentNode == null || targetNode == null) {
			Logger.getLogger(Individual.class.getName()).log(Level.WARNING, String.format("Can not initialize path to target building. Got values currentNode=%s; targetNode=%s.", String.valueOf(currentNode), String.valueOf(targetNode)));
		}
		ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget = GraphUtility.astarPath(currentNode, targetNode);
		if (!pathToTarget.isEmpty()) {
			m_pathToNextTarget = pathToTarget;
			m_currentEdge = (GeomPlanarGraphEdge) pathToTarget.get(0).getEdge();
			setupNextEdge();
			updatePosition(m_segment.extractPoint(m_currentIndexOnLineOfEdge));
			colorPathToTarget();
		} 
		else { // already at the target location
		}
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
	
	public MasonGeometry getHomeBuilding() {
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
	
	public ActivityAgenda getActivityAgenda() {
		return m_activityAgenda;
	}
	
	public ActivityAgenda getJointActivityAgenda() {
		return m_jointActivityAgenda;
	}
	
	public boolean isOpenForNetworkActivities(Environment environment, int maxNumberOfActivitiesForNetworkType, double probabilityOfPlaningActivityForNetworkType) {
		if (!TimeUtility.isDayFullyPlanned(environment, m_jointActivityAgenda)
				&& m_numberOfHouseholdNetworkActivitiesPlanned < maxNumberOfActivitiesForNetworkType
				&& environment.random.nextDouble(true, true) <= probabilityOfPlaningActivityForNetworkType) {
			return true;
		}
		else {
			return false;
		}
	}

	public int getNumberOfHouseholdNetworkActivitiesPlanned() {
		return m_numberOfHouseholdNetworkActivitiesPlanned;
	}
	
	public void incrementNumberOfHouseholdNetworkActivitiesPlanned() {
		m_numberOfHouseholdNetworkActivitiesPlanned++;
	}
	
	public void setNumberOfHouseholdNetworkActivitiesPlanned(int numberOfHouseholdNetworkActivitiesPlanned) {
		m_numberOfHouseholdNetworkActivitiesPlanned = numberOfHouseholdNetworkActivitiesPlanned;
	}
	
	public int getNumberOfWorkColleguesNetworkActivitiesPlanned() {
		return m_numberOfWorkColleguesNetworkActivitiesPlanned;
	}
	
	public void incrementNumberOfWorkColleguesNetworkActivitiesPlanned() {
		m_numberOfWorkColleguesNetworkActivitiesPlanned++;
	}
	
	public void setNumberOfWorkColleguesNetworkActivitiesPlanned(int numberOfWorkColleguesNetworkActivitiesPlanned) {
		m_numberOfWorkColleguesNetworkActivitiesPlanned = numberOfWorkColleguesNetworkActivitiesPlanned;
	}
	
	public int getNumberOfFriendsNetworkActivitiesPlanned() {
		return m_numberOfFriendsNetworkActivitiesPlanned;
	}
	
	public void incrementNumberOfFriendsNetworkActivitiesPlanned() {
		m_numberOfFriendsNetworkActivitiesPlanned++;
	}
	
	public void setNumberOfFriendsNetworkActivitiesPlanned(int numberOfFriendsNetworkActivitiesPlanned) {
		m_numberOfFriendsNetworkActivitiesPlanned = numberOfFriendsNetworkActivitiesPlanned;
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
}
