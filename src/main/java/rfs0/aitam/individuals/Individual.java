package rfs0.aitam.individuals;

import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
import sim.util.geo.PointMoveTo;

public class Individual {

	private Environment m_environment;
	private int m_id;
	
	/**
	 * Networks
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
	 * Needs
	 */
	private NeedTimeSplit m_targetNeedTimeSplit;
	private ActualNeedTimeSplit m_actualNeedTimeSplit = new ActualNeedTimeSplit();
	
	/**
	 * Agendas
	 */
	private ActivityAgenda m_activityAgenda = new ActivityAgenda();
	private ActivityAgenda m_jointActivityAgenda = new ActivityAgenda();
	private HashMap<ActivityAgenda, ActivityAgenda> m_allDayPlans = new HashMap<>();

	/**
	 * Static locations
	 */
	private Node m_homeNode;
	private ArrayList<Node> m_otherPlaceForHouseholdAndFamilyCareNodes;
	private Node m_workPlaceNode;
	private ArrayList<Node> m_otherPlaceForWorkNodes;
	private Node m_leisureNode;
	private ArrayList<Node> m_otherPlaceForLeisureNodes;
	
	/**
	 * Dynamic locations
	 */
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
			Coordinate currentLocationPoint = new Coordinate(homeNode.getCoordinate());
			individualToBuild.m_currentLocationPoint = new MasonGeometry(Environment.GEO_FACTORY.createPoint(currentLocationPoint));
			individualToBuild.m_currentLocationPoint.isMovable = true;
			validate(homeNode, warningMessage);
			individualToBuild.m_currentNode = new Node(homeNode.getCoordinate(), homeNode.getOutEdges());
			individualToBuild.m_homeNode = homeNode;
			return this;
		}
		
		public Builder withOtherPlaceForHouseholdAndFamilyCareBuildings(ArrayList<MasonGeometry> otherPlaceForHouseholdAndFamilyCareBuildings) {
			String warningMessage = "Third place for household and family care is invalid. The built individual may be unusable!";
			ArrayList<Node> otherPlaceForHouseholdAndFamilyCareNodes = new ArrayList<>();
			for (MasonGeometry otherPlaceForHouseholdAndFamilyCareBuilding: otherPlaceForHouseholdAndFamilyCareBuildings) {
				validate(otherPlaceForHouseholdAndFamilyCareBuilding.getGeometry().getCoordinate(), warningMessage);
				Node thirdPlaceForHouseholdAndFamilyCareNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(otherPlaceForHouseholdAndFamilyCareBuilding);
				otherPlaceForHouseholdAndFamilyCareNodes.add(thirdPlaceForHouseholdAndFamilyCareNode);
			}
			individualToBuild.m_otherPlaceForHouseholdAndFamilyCareNodes = otherPlaceForHouseholdAndFamilyCareNodes;
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
		
		public Builder withOtherPlaceForWorkBuildings(ArrayList<MasonGeometry> otherPlaceForWorkBuildings) {
			String warningMessage = "The third place for work is invalid. The built individual may be unusable!";
			ArrayList<Node> otherPlaceForWorkNodes = new ArrayList<>();
			for (MasonGeometry otherPlaceForWorkBuilding: otherPlaceForWorkBuildings) {
				validate(otherPlaceForWorkBuilding.getGeometry().getCoordinate(), warningMessage);
				Node otherPlaceForWorkNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(otherPlaceForWorkBuilding);
				otherPlaceForWorkNodes.add(otherPlaceForWorkNode);
			}
			individualToBuild.m_otherPlaceForWorkNodes = otherPlaceForWorkNodes;
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
		
		public Builder withOtherPlaceForLeisureBuildings(ArrayList<MasonGeometry> otherPlaceForLeisureBuildings) {
			String warningMessage = "The third place for leisure is invalid. The built individual may be unusable!";
			ArrayList<Node> otherPlaceForLeisureNodes = new ArrayList<>();
			for (MasonGeometry otherPlaceForLeisureBuilding: otherPlaceForLeisureBuildings) {
				validate(otherPlaceForLeisureBuilding.getGeometry().getCoordinate(), warningMessage);
				Node thirdPlaceForLeisureNode = Environment.BUILDING_TO_CLOSEST_NODE_MAP.get(otherPlaceForLeisureBuilding);
				otherPlaceForLeisureNodes.add(thirdPlaceForLeisureNode);
			}
			individualToBuild.m_otherPlaceForLeisureNodes = otherPlaceForLeisureNodes;
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
	
	@Override
	public String toString() {
		return "Id = " + m_id + " | Household Network = " + m_householdMembersNetworkId + " | Work network = " + m_workColleguesNetworkId + " | Friends network = " + m_friendsNetworkId;
	}
	
	// PLAN JOINT ACTIVITIES
	
	public void planJointActivities() {
		removeFutureJointActivitiesOfNetwork(m_householdMembersNetwork, NetworkType.HOUSEHOLD_NETWORK);
		removeFutureJointActivitiesOfNetwork(m_workColleguesNetwork, NetworkType.WORK_COLLEGUES_NETWORK);
		removeFutureJointActivitiesOfNetwork(m_friendsNetwork, NetworkType.FRIENDS_NETWORK);
		if (isOpenForNetworkActivities(NetworkType.HOUSEHOLD_NETWORK, ISimulationSettings.PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY)) {
			// TODO: define mapping from network to activity categories and use this instead of just one category!
			planActivityForNetwork(m_householdMembersNetwork, NetworkType.HOUSEHOLD_NETWORK , ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, ISimulationSettings.AVAILABLE_START_TIMES_FOR_HOUSEHOLD_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(NetworkType.WORK_COLLEGUES_NETWORK, ISimulationSettings.PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY)) {
			// TODO: define mapping from network to activity categories and use this instead of just one category!
			planActivityForNetwork(m_workColleguesNetwork, NetworkType.WORK_COLLEGUES_NETWORK, ActivityCategory.WORK, ISimulationSettings.AVAILABLE_START_TIMES_FOR_WORK_COLLEGUES_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(NetworkType.FRIENDS_NETWORK, ISimulationSettings.PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY)) {
			// TODO: define mapping from network to activity categories and use this instead of just one category!
			planActivityForNetwork(m_friendsNetwork, NetworkType.FRIENDS_NETWORK, ActivityCategory.LEISURE, ISimulationSettings.AVAILABLE_START_TIMES_FOR_FRIENDS_NETWORK_ACTIVITIES);
		}
	}
	
	private void removeFutureJointActivitiesOfNetwork(Network network, NetworkType networkType) {
		DateTime currentDateTime = m_environment.getSimulationTime().getCurrentDateTime();
		for (Object individualObj: network.getAllNodes()) {
			Individual individual = (Individual) individualObj;
			List<Interval> futureIntervals = individual.getJointActivityAgenda().getAgenda().keySet().stream()
				.filter(interval -> (interval.getStart().isAfter(currentDateTime))
						|| interval.getStart().equals(currentDateTime)
						|| interval.getEnd().isAfter(currentDateTime))
				.collect(Collectors.toList());
			for (Interval futureInterval: futureIntervals) {
				if (individual.getJointActivityAgenda().getActivityForInterval(futureInterval).getNetworkType() == networkType) {
					individual.getJointActivityAgenda().getAgenda().remove(futureInterval);
					individual.getJointActivityAgenda().getNodes().remove(futureInterval);
					switch (networkType) {
						case HOUSEHOLD_NETWORK:
							individual.decrementNumberOfHouseholdNetworkActivitiesPlanned();
							break;
						case WORK_COLLEGUES_NETWORK:
							individual.decrementNumberOfWorkColleguesNetworkActivitiesPlanned();
							break;
						case FRIENDS_NETWORK:
							individual.decrementNumberOfFriendsNetworkActivitiesPlanned();
							break;
						default:
							Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("Can not apply method to the following network type: %s", String.valueOf(networkType)));
					}
				}
			}
		}
	}
	
	public boolean isOpenForNetworkActivities(NetworkType networkType, double probabilityOfPlaningActivityForNetworkType) {
		boolean hasReachedMaxNumberOfActivitiesForNetworkType = false;
		switch (networkType) {
			case FRIENDS_NETWORK:
				hasReachedMaxNumberOfActivitiesForNetworkType = m_numberOfFriendsNetworkActivitiesPlanned >= ISimulationSettings.MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY;
				break;
			case HOUSEHOLD_NETWORK:
				hasReachedMaxNumberOfActivitiesForNetworkType = m_numberOfHouseholdNetworkActivitiesPlanned >= ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY;
				break;
			case WORK_COLLEGUES_NETWORK:
				hasReachedMaxNumberOfActivitiesForNetworkType = m_numberOfWorkColleguesNetworkActivitiesPlanned >= ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY;
				break;
			default:
				Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("Can not apply method to the following network type: %s", String.valueOf(networkType)));
				break;
		}
		if (!TimeUtility.isDayFullyPlanned(m_environment, m_jointActivityAgenda)
				&& !hasReachedMaxNumberOfActivitiesForNetworkType
				&& m_environment.random.nextDouble(true, true) <= probabilityOfPlaningActivityForNetworkType) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void planActivityForNetwork(Network network, NetworkType type, ActivityCategory activityCategory, ArrayList<DateTime> availableStartTimes) {
		// check if any activity is available for this category at this day
		int currentWeekDay = m_environment.getSimulationTime().getCurrentWeekDay();
		long numberOfActivitiesAvailableAtWeekDay = m_environment.getAllActivities().values().stream()
				.filter(activity -> activity.getActivityCategory() == activityCategory)
				.filter(activity -> activity.isAvailableAt(currentWeekDay))
				.count();
		// no activity available at this day of week for given category
		if (numberOfActivitiesAvailableAtWeekDay < 1) {
			return;
		}
		ArrayList<Individual> networkMemberParticipating = determineParticipatingNetworkMembers(network, type);
		// nobody wants to participate in joint activity
		if (networkMemberParticipating.size() < 2) { // TODO: make this dependent on network type?
			return;
		}
		Interval baseIntervalOfJointActivity = determineIntervalOfJointActivity(networkMemberParticipating, availableStartTimes);
		// no agreement on interval established
		if (baseIntervalOfJointActivity == null) {
			return;
		}
		// at this point we should have ensured that some activity is available and at least two network members agreed on some interval for conducting it
		ArrayList<Activity> availableActivities = m_environment.getAllActivities().values().stream()
				.filter(activity -> activity.isJointActivity())
				.filter(activity -> activity.getActivityCategory() == activityCategory)
				.filter(activity -> activity.isAvailableAt(m_environment.getSimulationTime().getCurrentWeekDay(), baseIntervalOfJointActivity))
				.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
				.collect(Collectors.toCollection(ArrayList::new));
		if (availableActivities.size() == 0) {
			Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("No activity availabe for interval interval: %s. Make sure there is always at least one activity available!", String.valueOf(baseIntervalOfJointActivity)));	
		}
		// setup activity for all participating network members
		DateTime currentDateTime = m_environment.getSimulationTime().getCurrentDateTime();
		Interval realIntervalOfJointActivity = TimeUtility.convertToRealInterval(currentDateTime, baseIntervalOfJointActivity);
		Activity jointActivity = availableActivities.get(m_environment.random.nextInt(availableActivities.size()));
		Node jointActivityNode = determineActivityNode(jointActivity);
		for (Individual individual: networkMemberParticipating) {
			individual.getJointActivityAgenda().addActivityForInterval(realIntervalOfJointActivity, jointActivity);
			individual.getJointActivityAgenda().addNodeForInterval(realIntervalOfJointActivity, jointActivityNode);
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
	
	private ArrayList<Individual> determineParticipatingNetworkMembers(Network network, NetworkType type) {
		ArrayList<Individual> networkMemberParticipating = new ArrayList<>();
		networkMemberParticipating.add(this);
		for (Object individualObj: network.getAllNodes()) {
			Individual individual = (Individual) individualObj;
			if (!individual.equals(this)) {
				switch (type) {
					case HOUSEHOLD_NETWORK:
						if (individual.isOpenForNetworkActivities(type, ISimulationSettings.PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY)) {
							networkMemberParticipating.add(individual);
						}
						break;
					case WORK_COLLEGUES_NETWORK:
						if (individual.isOpenForNetworkActivities(type, ISimulationSettings.PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY)) {
							networkMemberParticipating.add(individual);
						}
						break;
					case FRIENDS_NETWORK:
						if (individual.isOpenForNetworkActivities(type, ISimulationSettings.PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY)) {
							networkMemberParticipating.add(individual);
						}
						break;
					default:
						Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("%s is an invalid NetworkType! Can not plan activity for this type!", String.valueOf(type)));
						break;
				}
			}
		}
		return networkMemberParticipating;
	}
	
	
	private Interval determineIntervalOfJointActivity(ArrayList<Individual> networkMemberParticipating, ArrayList<DateTime> startTimes) {
		Interval baseIntervalOfInterest;
		Interval realIntervalOfInterest;
		DateTime currentDateTime = m_environment.getSimulationTime().getCurrentDateTime();
		int numberOfTrials = 0;
		List<DateTime> availableStartTimes = startTimes.stream()
				.filter(startTime -> startTime.isAfter(m_environment.getSimulationTime().getCurrentTime()))
				.collect(Collectors.toList());
		// TODO: mention in javadoc that this is configurable in settings
		// no start times available anymore for network
		if (availableStartTimes.size() == 0) {
			return null;
		}
		do {
			DateTime startOfJointActivity = availableStartTimes.get(m_environment.random.nextInt(availableStartTimes.size()));
			BigDecimal duration = ISimulationSettings.ACTIVITY_DURATIONS_IN_MINUTES.get(m_environment.random.nextInt(ISimulationSettings.ACTIVITY_DURATIONS_IN_MINUTES.size()));
			DateTime endOfJointActivity = startOfJointActivity.plusMinutes(duration.intValue());
			baseIntervalOfInterest = new Interval(startOfJointActivity, endOfJointActivity);
			realIntervalOfInterest = TimeUtility.convertToRealInterval(currentDateTime, baseIntervalOfInterest);
			numberOfTrials++;
		} 
		while (TimeUtility.isIntervalOverlappingAnyAgenda(networkMemberParticipating, realIntervalOfInterest) && numberOfTrials < ISimulationSettings.MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY);
		if (numberOfTrials < ISimulationSettings.MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY) {
			return baseIntervalOfInterest;
		} 
		else {
			return null;
		}
	}
	
	private Node determineActivityNode(Activity activity) {
		// TODO: decide wheter random choice is ok or not -> alternatively we can retrieve the closest node to current location
		switch (activity.getActivityLocation()) {
		case HOME:
			return m_homeNode;
		case THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE:
			return m_otherPlaceForHouseholdAndFamilyCareNodes.get(m_environment.random.nextInt(m_otherPlaceForHouseholdAndFamilyCareNodes.size()));
		case LEISURE:
			return m_leisureNode;
		case THIRD_PLACE_FOR_LEISURE:
			return m_otherPlaceForLeisureNodes.get(m_environment.random.nextInt(m_otherPlaceForLeisureNodes.size()));
		case THIRD_PLACE_FOR_WORK:
			return m_otherPlaceForWorkNodes.get(m_environment.random.nextInt(m_otherPlaceForWorkNodes.size()));
		case WORK:
			return m_workPlaceNode;
		default:
			Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, "Could not choose activty location!");
			return null;
		}
	}
	
	// PLAN INDIVIDUAL ACTIVITIES
	
	public void planIndividualActivities() {
		getAllDayPlans().clear();
		DateTime endOfCurrentDay = TimeUtility.getEndOfCurrentDay(m_environment.getSimulationTime().getCurrentDateTime());
		for (int i = 0; i < ISimulationSettings.NUMBER_OF_PLANS_TO_GENERATE; i++) {
			ActivityAgenda randomAgenda = ActivityAgenda.newInstance(m_activityAgenda);
			while (!TimeUtility.isDayFullyPlanned(m_environment, randomAgenda)) {
				Interval availableInterval = TimeUtility.getFirstAvailableInterval(m_environment, randomAgenda);
				int maxDurationInMinutes = (int) availableInterval.toDuration().getStandardMinutes();
				BigDecimal duration = determineDuration(maxDurationInMinutes);
				Interval realActivityInterval = determineActivityInterval(availableInterval, duration.intValue(), endOfCurrentDay);
				Interval baseActivityInterval = TimeUtility.convertToBaseInterval(realActivityInterval);
				Activity activity = determineActivity(randomAgenda, realActivityInterval, baseActivityInterval);
				Node activityNode = determineActivityNode(activity);
				randomAgenda.addActivityForInterval(realActivityInterval, activity);
				randomAgenda.addNodeForInterval(realActivityInterval, activityNode);
				for (Need needSatisfiedByRandomActivity: activity.getNeedTimeSplit().getNeedTimeSplit().keySet()) {
					BigDecimal fractionForNeed = activity.getNeedTimeSplit().getFractionForNeed(needSatisfiedByRandomActivity);
					BigDecimal timeSpentSatisfyingNeed = fractionForNeed.multiply(duration);
					randomAgenda.getActualNeedTimeSplit().updateNeedTimeSplit(needSatisfiedByRandomActivity, timeSpentSatisfyingNeed);
				}
			}
			ActivityAgenda randomAgendaWithTravelTime = createAgendaWithTravelTime(randomAgenda);
			m_allDayPlans.put(randomAgendaWithTravelTime, randomAgenda);
		}
	}
	
	private BigDecimal determineDuration(int maxDurationInMinutes) {
		m_environment.random.nextGaussian();
		List<BigDecimal> availableDurations = ISimulationSettings.ACTIVITY_DURATIONS_IN_MINUTES.stream()
				.filter(minutes -> minutes.compareTo(CalculationUtility.createBigDecimal(maxDurationInMinutes)) < 0)
				.collect(Collectors.toList());
		if (availableDurations.size() == 0) {
			return CalculationUtility.createBigDecimal(ISimulationSettings.MIN_DURATION);
		}
		else {
			return availableDurations.get(m_environment.random.nextInt(availableDurations.size()));
		}
	}
	
	private Interval determineActivityInterval(Interval availableInterval, int duration, DateTime endOfCurrentDay) {
		DateTime end = availableInterval.getStart().plusMinutes(duration);
		if (end.isAfter(endOfCurrentDay)) { // make sure plan ends at 23:59 of current day
			end = endOfCurrentDay;
		}
		return new Interval(availableInterval.getStart(), end);
	}
	
	private Activity determineActivity(ActivityAgenda randomAgenda, Interval realActivityInterval, Interval baseActivityInterval) {
		ArrayList<Activity> availableActivities;
		if ((int) baseActivityInterval.toDuration().getStandardMinutes() <= ISimulationSettings.MIN_DURATION) {
			ActivityLocation currentLocation = randomAgenda.getActivityForDateTime(realActivityInterval.getStart().minusMinutes(1)).getActivityLocation();
			availableActivities = m_environment.getAllActivities().values().stream()
				.filter(activity -> activity.isAvailableAt(m_environment.getSimulationTime().getCurrentWeekDay(), baseActivityInterval))
				.filter(activity -> activity.getActivityLocation() == currentLocation)
				.filter(activity -> !activity.isJointActivity())
				.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
				.collect(Collectors.toCollection(ArrayList::new));
		}
		else {
			availableActivities = m_environment.getAllActivities().values().stream()
				.filter(activity -> activity.isAvailableAt(m_environment.getSimulationTime().getCurrentWeekDay(), baseActivityInterval))
				.filter(activity -> !activity.isJointActivity())
				.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
				.collect(Collectors.toCollection(ArrayList::new));
		}
		if (availableActivities.size() == 0) {
			Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, String.format("No activity availabe for interval interval: %s. Location is: %s. Make sure there is always at least one activity available!", String.valueOf(realActivityInterval), String.valueOf(randomAgenda.getActivityForDateTime(realActivityInterval.getStart().minusMinutes(1)).getActivityLocation())));	
		}
		return availableActivities.get(m_environment.random.nextInt(availableActivities.size()));
	}
	
	private ActivityAgenda createAgendaWithTravelTime(ActivityAgenda agenda) {
		ActivityAgenda activityAgendaWithTravelTimes = ActivityAgenda.newInstance(agenda);
		Activity travelActivity = m_environment.getAllActivities().get(ISimulationSettings.TRAVEL);
		for (Interval interval: agenda.getIntervals()) {
			Activity currentActivity = agenda.getActivityForInterval(interval);
			Node activityNode = agenda.getNodeForInterval(interval);
			Node nextActivityNode = agenda.getNodeForDateTime(interval.getEnd().plusMinutes(1));
			// there is a next activity (last entry has no next node) and it is conducted at a different location
			if (nextActivityNode != null && !activityNode.getCoordinate().equals(nextActivityNode.getCoordinate())) {
				ArrayList<GeomPlanarGraphDirectedEdge> pathToNextActivity = GraphUtility.astarPath(activityNode, nextActivityNode);
				double lengthOfPathToNextActivity = 0;
				for (int i = 0; i<pathToNextActivity.size(); i++) {
					GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) pathToNextActivity.get(i).getEdge();
					lengthOfPathToNextActivity += edge.getLine().getLength();
				}
				int travelDurationInMinutes = Math.toIntExact(Math.round(lengthOfPathToNextActivity / ISimulationSettings.MAX_VELOCITY));
				DateTime startOfTravel = interval.getStart();
				DateTime endOfTravel = startOfTravel.plusMinutes(travelDurationInMinutes);
				DateTime endOfActivity = interval.getEnd();
				Interval travelInterval = new Interval(startOfTravel, endOfTravel);
				Interval effectiveActivityInterval = new Interval(endOfTravel, endOfActivity);
				activityAgendaWithTravelTimes.getAgenda().remove(interval);
				activityAgendaWithTravelTimes.getAgenda().put(travelInterval, travelActivity);
				activityAgendaWithTravelTimes.getAgenda().put(effectiveActivityInterval, currentActivity);
			}
		}
		return activityAgendaWithTravelTimes;
	}

	
	/**
	 * Use MSE for evaluation of best plan
	 */
	public void chooseBestAgenda() {
		ActivityAgenda bestAgenda = null;
		BigDecimal minimumSquaredMeanError = new BigDecimal(Integer.MAX_VALUE);
		for (ActivityAgenda randomAgendaWithTravelTime: m_allDayPlans.keySet()) {
			BigDecimal meanSquaredError = CalculationUtility.calculateMeanSquaredError(randomAgendaWithTravelTime, getTargetNeedTimeSplit());
			if (meanSquaredError.compareTo(minimumSquaredMeanError) < 0) {
				minimumSquaredMeanError = meanSquaredError;
				bestAgenda = randomAgendaWithTravelTime;
			}
		}
		m_activityAgenda = m_allDayPlans.get(bestAgenda); // this gives the agenda without travel times
		m_allDayPlans.clear();
	}
	
	public boolean isPlanningPossible(ArrayList<DateTime> availableTimePoints) {
		return availableTimePoints.stream().anyMatch(timePoint -> timePoint.equals(m_environment.getSimulationTime().getCurrentTime()));
	}
	
	public void carryOverJointActivities() {
		// remvoe all future activities
		DateTime currentDateTime = m_environment.getSimulationTime().getCurrentDateTime();
		List<Interval> futureIntervals = m_activityAgenda.getAgenda().keySet().stream()
			.filter(interval -> (interval.getStart().isAfter(currentDateTime) 
					|| interval.getStart().equals(currentDateTime)
					|| interval.getEnd().isAfter(currentDateTime)))
			.collect(Collectors.toList());
		for (Interval futureInterval: futureIntervals) {
			m_activityAgenda.getAgenda().remove(futureInterval);
			m_activityAgenda.getNodes().remove(futureInterval);
		}
		// fill future with joint activities
		for (Interval interval: m_jointActivityAgenda.getIntervals()) {
			if (interval.isAfter(m_environment.getSimulationTime().getCurrentDateTime())) {
				m_activityAgenda.addActivityForInterval(interval, m_jointActivityAgenda.getActivityForInterval(interval));
				m_activityAgenda.addNodeForInterval(interval, m_jointActivityAgenda.getNodeForInterval(interval));
			}
		}
	}
	
	// EXECUTE ACTIVITIES

	public void executeActivity() {
		Activity currentActivity = m_activityAgenda.getActivityForDateTime(m_environment.getSimulationTime().getCurrentDateTime());
		Node targetNode = determineActivityNode(currentActivity);
			Environment.NODE_TO_CLOSEST_BUILDING_MAP.get(targetNode).getGeometry().setUserData(
					new LabelledPortrayal2D(
							new GeomPortrayal(ISimulationSettings.COLOR_OF_TARGET_BUILDING, ISimulationSettings.SIZE_OF_BUILDING_SELCTED, true), 
							10,
							5,
							0.5,
							0.5,
							new Font("SansSerif",Font.BOLD, 15),
							LabelledPortrayal2D.ALIGN_LEFT,
							null, 
							ISimulationSettings.COLOR_OF_TARGET_BUILDING, 
							false) {
						private static final long serialVersionUID = 1L;
						@Override
						public String getLabel(Object object, DrawInfo2D info) {
							return String.format("Target of %s for activity %s", String.valueOf(m_id), currentActivity.getActivityDescription());
						}
					});
		if (!m_currentNode.getCoordinate().equals(targetNode.getCoordinate())) {
			if (m_pathToNextTarget.isEmpty()) {
				initPathToTarget(m_currentNode, targetNode);
			}
			if (!hasReachedTarget()) {
				// TODO: decide on mode of transport
				moveTowardsTarget();
			}
		}
		if (hasReachedTarget()) {
			if (!m_currentNode.getCoordinate().equals(targetNode.getCoordinate())) {
				updatePosition(targetNode.getCoordinate());
				m_currentNode = new Node(targetNode.getCoordinate(), targetNode.getOutEdges());
			}
			if (!currentActivity.isJointActivity()) {
				updateActualNeedTimeSplit(currentActivity);
			}
			else { // joint activity
				// TODO: how to handle case where individual is the only one currently at target location
				updateActualNeedTimeSplit(currentActivity);
			}
			m_environment.incrementIntegerValueOfOutputHolder(currentActivity.getActivityCategory().toString());
			m_environment.incrementIntegerValueOfOutputHolder(currentActivity.getActivityDescription());
		}
		else { // traveling towards target
			m_environment.incrementIntegerValueOfOutputHolder(ActivityCategory.TRAVEL.toString());
			// this has to be adjusted once more modes of transport are implemented
			m_environment.incrementIntegerValueOfOutputHolder(m_environment.getCategoryToActivities().get(ActivityCategory.TRAVEL).get(0).getActivityDescription());
		}
		m_environment.incrementIntegerValueOfOutputHolder(ISimulationSettings.TOTAL_NUMBER_OF_AGENTS);
	}
	
	private void initPathToTarget(Node currentNode, Node targetNode) {
		m_pathToNextTarget.clear();
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
			// nop
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
		double distanceToStart = lineOfNextEdge.getStartPoint().distance(m_currentLocationPoint.getGeometry());
		double distanceToEnd = lineOfNextEdge.getEndPoint().distance(m_currentLocationPoint.getGeometry());
		if (distanceToStart <= distanceToEnd) {
			m_currentIndexOnLineOfEdge = m_segment.getStartIndex();
			m_edgeDirection = 1;
		} else {
			m_currentIndexOnLineOfEdge = m_segment.getEndIndex();
			m_edgeDirection = -1;
		}
	}
	
	private void updateEdgeTraffic(GeomPlanarGraphEdge nextEdge) {
		if (m_environment.getEdgeTraffic().get(m_currentEdge) != null) {
			m_environment.getEdgeTraffic().get(m_currentEdge).remove(this); // current edge is actually the old edge here
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
	 * @param targetCoordinate - The coordinate to which the individual is moved to
	 */
	private void updatePosition(Coordinate targetCoordinate) {
		Coordinate copy = new Coordinate(targetCoordinate);
		m_pointMoveTo.setCoordinate(copy);
		m_environment.getIndividualsField().setGeometryLocation(m_currentLocationPoint, m_pointMoveTo);
		m_environment.getIndividualsField().updateSpatialIndex();
	}
	
	private boolean hasReachedTarget() {
		if (m_pathToNextTarget.isEmpty()) { // current location is target location
			return true;
		}
		else {
			return m_currentIndexOnPathToNextTarget >= m_pathToNextTarget.size();
		}
	}
	
	public void colorPathToTarget() {
		GeomVectorField pathField = m_environment.getPathField();
		List<Coordinate> coordinatesOfPath = getPathToNextTarget()
				.stream()
				.map(path -> path.getCoordinate())
				.collect(Collectors.toList());
		for (Coordinate coordinate : coordinatesOfPath) {
			ArrayList<MasonGeometry> coveringObjects = GeometryUtility.getCoveringObjects(new MasonGeometry(Environment.GEO_FACTORY.createPoint(coordinate)), pathField);
			coveringObjects.forEach(mg -> {
				mg.setUserData(
						new CircledPortrayal2D(
								new GeomPortrayal(
										ISimulationSettings.COLOR_OF_SELECTED_ENTITY,
										ISimulationSettings.SIZE_OF_PATH
										),
								ISimulationSettings.COLOR_OF_SELECTED_ENTITY,
								true
								));
			});
		}
	}
	
	private void updateActualNeedTimeSplit(Activity activity) {
		for (Need needSatisfiedByRandomActivity: activity.getNeedTimeSplit().getNeedTimeSplit().keySet()) {
			m_actualNeedTimeSplit.updateNeedTimeSplit(needSatisfiedByRandomActivity, activity.getNeedTimeSplit().getFractionForNeed(needSatisfiedByRandomActivity));
		}
	}
	
	private void moveTowardsTarget() {
		double travellingDistance = calculateTravellingDistance();
		m_currentIndexOnLineOfEdge += travellingDistance;
		if (m_edgeDirection == 1 && m_currentIndexOnLineOfEdge >= m_endIndexOfCurrentEdge) {
			// positive movement
			moveRemainingDistanceOnNextEdge(m_currentIndexOnLineOfEdge - m_endIndexOfCurrentEdge);
		} 
		else if (m_edgeDirection == -1 && m_currentIndexOnLineOfEdge <= m_startIndexOfCurrentEdge) {
			// negative movement
			moveRemainingDistanceOnNextEdge(m_startIndexOfCurrentEdge - m_currentIndexOnLineOfEdge);
		}
		updatePosition(new Coordinate(m_segment.extractPoint(m_currentIndexOnLineOfEdge)));
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
		m_currentIndexOnPathToNextTarget += 1;
		if (hasReachedTarget()) {
			if (m_edgeDirection == -1) {
				m_currentIndexOnLineOfEdge = m_segment.getStartIndex();
			}
			else {
				m_currentIndexOnLineOfEdge = m_segment.getEndIndex();
			}
			m_currentIndexOnPathToNextTarget = 0;
			m_pathToNextTarget.clear();
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
	
	// HELPER FUNCTIONS
	
	public void updateActualNeedTimeSplit(Need need, BigDecimal timeSpentSatisfyingNeed) {
		m_actualNeedTimeSplit.updateNeedTimeSplit(need, timeSpentSatisfyingNeed);
	}
	
	public void initNewDay() {
		m_numberOfHouseholdNetworkActivitiesPlanned = 0;
		m_numberOfWorkColleguesNetworkActivitiesPlanned = 0;
		m_numberOfFriendsNetworkActivitiesPlanned = 0;
		m_actualNeedTimeSplit.clear();
		m_activityAgenda.clear();
		m_jointActivityAgenda.clear();
		m_allDayPlans.clear();
	}

	// GETTER & SETTER
	
	public Environment getEnvironment() {
		return m_environment;
	}
	
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
	
	public void decrementNumberOfHouseholdNetworkActivitiesPlanned() {
		m_numberOfHouseholdNetworkActivitiesPlanned--;
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
	
	public void decrementNumberOfWorkColleguesNetworkActivitiesPlanned() {
		m_numberOfWorkColleguesNetworkActivitiesPlanned--;
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
	
	public void decrementNumberOfFriendsNetworkActivitiesPlanned() {
		m_numberOfFriendsNetworkActivitiesPlanned--;
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

	public HashMap<ActivityAgenda, ActivityAgenda> getAllDayPlans() {
		return m_allDayPlans;
	}

	public void setAllDayPlans(HashMap<ActivityAgenda, ActivityAgenda> allDayPlans) {
		m_allDayPlans = allDayPlans;
	}

	public Node getHomeNode() {
		return m_homeNode;
	}

	public void setHomeNode(Node homeNode) {
		m_homeNode = homeNode;
	}

	public ArrayList<Node> getOtherPlaceForHouseholdAndFamilyCareNodes() {
		return m_otherPlaceForHouseholdAndFamilyCareNodes;
	}

	public void setOtherPlaceForHouseholdAndFamilyCareNodes(ArrayList<Node> otherPlaceForHouseholdAndFamilyCareNodes) {
		m_otherPlaceForHouseholdAndFamilyCareNodes = otherPlaceForHouseholdAndFamilyCareNodes;
	}

	public Node getWorkPlaceNode() {
		return m_workPlaceNode;
	}

	public void setWorkPlaceNode(Node workPlaceNode) {
		m_workPlaceNode = workPlaceNode;
	}

	public ArrayList<Node> getOtherPlaceForWorkNodes() {
		return m_otherPlaceForWorkNodes;
	}

	public void setOtherPlaceForWorkNodes(ArrayList<Node> otherPlaceForWorkNodes) {
		m_otherPlaceForWorkNodes = otherPlaceForWorkNodes;
	}

	public Node getLeisureNode() {
		return m_leisureNode;
	}

	public void setLeisureNode(Node leisureNode) {
		m_leisureNode = leisureNode;
	}

	public ArrayList<Node> getOtherPlaceForLeisureNodes() {
		return m_otherPlaceForLeisureNodes;
	}

	public void setOtherPlaceForLeisureNodes(ArrayList<Node> otherPlaceForLeisureNodes) {
		m_otherPlaceForLeisureNodes = otherPlaceForLeisureNodes;
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
