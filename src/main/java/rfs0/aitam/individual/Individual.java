package rfs0.aitam.individual;

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
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.activity.Activity;
import rfs0.aitam.activity.ActivityAgenda;
import rfs0.aitam.activity.ActivityCategory;
import rfs0.aitam.activity.ActivityLocation;
import rfs0.aitam.environment.Environment;
import rfs0.aitam.need.AbsoluteNeedTimeSplit;
import rfs0.aitam.need.Need;
import rfs0.aitam.need.NeedTimeSplit;
import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;
import rfs0.aitam.utilities.DebugUtility;
import rfs0.aitam.utilities.GeometryUtility;
import rfs0.aitam.utilities.GraphUtility;
import rfs0.aitam.utilities.TimeUtility;
import rfs0.aitam.utilities.Tuple;
import sim.field.geo.GeomVectorField;
import sim.field.network.Network;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.GeomPlanarGraphEdge;
import sim.util.geo.MasonGeometry;
import sim.util.geo.PointMoveTo;

/**
 * <p>This class is used to model an abstraction of real-world individuals and their attributes relevant for planning and executing their daily activities.
 * They do so based on a set of needs.
 * This set of needs was introduced by Manfred Max-Neef (see <a href="https://en.wikipedia.org/wiki/Fundamental_human_needs">fundamental human needs</a> for more information). 
 * In order to do so, the following attributes are required:</p>
 *
 * <p><b>Environment</b></p>
 * 
 * <p>{@link Individual#m_environment}: A reference to the simulation's environment. 
 * It is used to retrieve information about the current state of the environment such as the current time, the state of other individuals etc.</p>
 * 
 * <p><b>Individual</b></p>
 * 
 * <p>{@link Individual#m_id}: The individual's id is a simple unique identifier which also reflects the order in which they are being created. 
 * It start at zero and goes up to {@link ISimulationSettings#NUMBER_OF_INDIVIDUALS} - 1.</p> 
 * 
 * <p><b>Networks</b></p>
 * 
 * <p>{@link Individual#m_householdMembersNetwork}: The individuals household network (or family). 
 * It contains references to all other individuals which are part of the household. 
 * The number of household members can be configured via {@link ISimulationSettings#MIN_NUMBER_OF_HOUSEHOLD_MEMBERS} and {@link ISimulationSettings#MAX_NUMBER_OF_HOUSEHOLD_MEMBERS}. 
 * The main purpose of a network is to enable the coordination of joint activities within the network.</p>
 * <p>{@link Individual#m_householdMembersNetworkId}: The id of the household members network. 
 * This is a unique identifier for each network. 
 * It defaults to -1 if an individual is not part of a household network.</p>
 * <p>{@link Individual#m_numberOfHouseholdNetworkActivitiesPlanned}: The number of household activities planned for the current day. 
 * It is reset to 0 at the beginning of each day.
 * It allows together with {@link ISimulationSettings#MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY} to limit the number of household activities.</p>
 * <p>{@link Individual#m_workColleaguesNetwork}: The individuals work colleagues network. 
 * It contains references to all the individual's work colleagues. 
 * The number of work colleagues can be configured via {@link ISimulationSettings#MIN_NUMBER_OF_WORK_COLLEGUES} and {@link ISimulationSettings#MAX_NUMBER_OF_WORK_COLLEGUES}. 
 * The main purpose of a network is to enable the coordination of joint activities within the network.</p>
 * <p>{@link Individual#m_workColleaguesNetworkId}: The id of the work colleagues members network. 
 * This is a unique identifier for each network. 
 * It defaults to -1 if an individual is not part of a work colleagues network.</p>
 * <p>{@link Individual#m_numberOfWorkColleguesNetworkActivitiesPlanned}: The number of activities together with work colleagues planned for the current day. 
 * It is reset to 0 at the beginning of each day.
 * It allows together with {@link ISimulationSettings#MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY} to limit the number of activities executed with work colleagues.</p>
 * <p>{@link Individual#m_friendsNetwork}: The individuals friends network. 
 * It contains references to all the individual's friends. 
 * The number of friends can be configured via {@link ISimulationSettings#MIN_NUMBER_OF_FRIENDS} and {@link ISimulationSettings#MAX_NUMBER_OF_FRIENDS}. 
 * The main purpose of a network is to enable the coordination of joint activities within the network.</p>
 * <p>{@link Individual#m_friendsNetworkId}: The id of the friends members network. 
 * This is a unique identifier for each network. It defaults to -1 if an individual is not part of a work colleagues network.</p>
 * <p>{@link Individual#m_numberOfFriendsNetworkActivitiesPlanned}: The number of activities together with friends planned for the current day. 
 * It is reset to 0 at the beginning of each day.
 * It allows together with {@link ISimulationSettings#MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY} to limit the number of activities executed with friends.</p>
 *
 * <p><b>Needs</b></p>
 * 
 * <p>{@link Individual#m_targetNeedTimeSplit}: The target need time split defines the individual's ideal relative distribution of time in regards to it's different needs. 
 * Thus, it is used as a benchmark to evaluate activity plans. 
 * The closer a plan is to the target need time split, the better it is at satisfying the individuals needs and thus the more an individual prefers it.<p>
 * <p>{@link Individual#m_actualNeedTimeSplit}: The actual need time split is used to record the time the individual spends on satisfying each of its needs. 
 * Thus, it can be used to compare the individuals actual need satisfaction to it's ideal (i.e. target) need satisfaction at any given point in time.</p>
 * 
 * <p><b>Agendas</b></p>
 * 
 * <p>{@link Individual#m_activityAgenda}: The agenda with all activities planned for the current day. 
 * It contains individual as well as joint activities.</p>
 * <p>{@link Individual#m_jointActivityAgenda}: The agenda with all joint activities planned for the current day. 
 * It contains only joint activities.</p>
 * <p>{@link Individual#m_allDayPlans}: This variable is used to create a configurable number of randomly generated plans and to choose from it. 
 * Use {@link ISimulationSettings#NUMBER_OF_PLANS_TO_GENERATE} to configure it.</p>
 * <p>{@link Individual#m_currentActivity}: The activity the individual has planned executing at the current point in time.</p>
 * 
 * <p><b>Static locations</b></p>
 * 
 * <p><b>Note:</b> Individuals can only travel along paths of the path network (i.e. they never leave the path network).
 * Thus, instead of using the actual building as target location the simulation uses the node on the path network which is closest to the building as a proxy for the building. 
 * This makes handling travel much easier. 
 * You can use {@link Environment#m_buildingToClosestNodeMap} to retrieve the node which is closest to some building you are interested in. 
 * Furthermore, you can use {@link Environment#m_nodeToClosestBuildingMap} to retrieve the building which is closest to some node you are interested in.</p>
 * 
 * <p>{@link Individual#m_homeNode}: The node on the path network which is closest to the individuals home building. 
 * It serves as a proxy for the individuals home.</p>
 * <p>{@link Individual#m_otherPlacesForHouseholdAndFamilyCareNodes}: A list of all other places (in addition to the home) where activities of {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE} can be executed.</p>
 * <p>{@link Individual#m_workPlaceNode}: The node on the path network which is closest to the individuals work place building. 
 * It serves as a proxy for the individuals work place.</p>
 * <p>{@link Individual#m_otherPlacesForWorkNodes}: A list of all other places (in addition to the work place) where activities of {@link ActivityCategory#WORK} can be executed.</p>
 * <p>{@link Individual#m_leisureNode}: The node on the path network which is closest to the individuals preferred building for executing leisure activities. 
 * It serves as a proxy for this building.</p>
 * <p>{@link Individual#m_otherPlacesForLeisureNodes}: A list of all other places (in addition to the preferred place for leisure) where activities of {@link ActivityCategory#LEISURE} can be executed.</p>
 * 
 * <p><b>Dynamic locations</b></p>
 * 
 * <p>{@link Individual#m_currentLocationPoint}: The current location of the individual.</p>
 * <p>{@link Individual#m_lengthIndexedLineOfEdge}: The line of the edge the individual is currently traveling on.</p>
 * <p>{@link Individual#m_endIndexOfCurrentEdge}: The end index of the current edge.</p>
 * <p>{@link Individual#m_startIndexOfCurrentEdge}: The start index of the current edge.</p>
 * <p>{@link Individual#m_currentIndexOnLineOfEdge}: The current index i.e. the position of the individual on the current edge.</p>
 * <p>{@link Individual#m_pointMoveTo}: A helper class to move a point to a new Coordinate.</p>
 * <p>{@link Individual#m_pathToNextTarget}: The path to the next target.</p>
 * <p>{@link Individual#m_currentEdge}: The edge on which the individual currently is traveling on.</p>
 * <p>{@link Individual#m_edgeDirection}: The direction which the individual is traveling on the current edge. 
 * It can either be positive or negative and indicates how the edge is traversed by the individual.</p>
 * <p>{@link Individual#m_currentIndexOnPathToNextTarget}: The index of the edge the individual is currently traveling on. 
 * If this value is equal to the size of {@link Individual#m_pathToNextTarget}, then the individual has reached its target.</p>
 * <p>{@link Individual#m_currentNode}: The node of the path network the individual is currently on.</p>
 * <p>{@link Individual#m_currentTargetNode}: The node of the path network that represents the next target.</p>
 */
public class Individual {
	
	private static final Logger LOG = Logger.getLogger(Individual.class.getName());

	/**
	 * @category Environment
	 */
	
	/**
	 * <p>A reference to the simulation's environment. 
	 * It is used to retrieve information about the current state of the environment such as the current time, the state of other individuals etc.</p>
	 */
	private Environment m_environment;
	
	/**
	 * @category Individual
	 */
	
	/**
	 * The individual's id is a simple unique identifier which also reflects the order in which they are being created. 
	 * It start at zero and goes up to {@link ISimulationSettings#NUMBER_OF_INDIVIDUALS} - 1.</p>
	 */
	private int m_id = -1;
	
	/**
	 * @category Networks
	 */
	
	/**
	 * <p>The individuals household network (or family). 
	 * It contains references to all other individuals which are part of the household. 
	 * The number of household members can be configured via {@link ISimulationSettings#MIN_NUMBER_OF_HOUSEHOLD_MEMBERS} and {@link ISimulationSettings#MAX_NUMBER_OF_HOUSEHOLD_MEMBERS}. 
	 * The main purpose of a network is to enable the coordination of joint activities within the network.</p>
	 */
	private Network m_householdMembersNetwork = new Network(false);
	/**
	 * <p>The id of the household members network. This is a unique identifier for each network. 
	 * It defaults to -1 if an individual is not part of a household network.</p>
	 */
	private int m_householdMembersNetworkId = -1;
	/**
	 * <p>The number of household activities planned for the current day. 
	 * It is reset to 0 at the beginning of each day and allows together with {@link ISimulationSettings#MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY} to limit the number of household activities.</p>
	 */
	private int m_numberOfHouseholdNetworkActivitiesPlanned = 0;
	/**
	 * The individuals work colleagues network. 
	 * It contains references to all the individual's work colleagues. 
	 * The number of work colleagues can be configured via {@link ISimulationSettings#MIN_NUMBER_OF_WORK_COLLEGUES} and {@link ISimulationSettings#MAX_NUMBER_OF_WORK_COLLEGUES}. 
	 * The main purpose of a network is to enable the coordination of joint activities within the network.</p>
	 */
	private Network m_workColleaguesNetwork = new Network(false);
	/**
	 * <p>The id of the work colleagues members network. 
	 * This is a unique identifier for each network. 
	 * It defaults to -1 if an individual is not part of a work colleagues network.</p>
	 */
	private int m_workColleaguesNetworkId = -1;
	/**
	 * <p>The number of activities together with work colleagues planned for the current day. 
	 * It is reset to 0 at the beginning of each day and allows together with {@link ISimulationSettings#MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY} to limit the number of activities executed with work colleagues.</p>
	 */
	private int m_numberOfWorkColleguesNetworkActivitiesPlanned = 0;
	/**
	 * The individuals friends network. 
	 * It contains references to all the individual's friends. 
	 * The number of friends can be configured via {@link ISimulationSettings#MIN_NUMBER_OF_FRIENDS} and {@link ISimulationSettings#MAX_NUMBER_OF_FRIENDS}. 
	 * The main purpose of a network is to enable the coordination of joint activities within the network.</p>
	 */
	private Network m_friendsNetwork = new Network(false);
	/**
	 * <p>The id of the friends members network. 
	 * This is a unique identifier for each network. 
	 * It defaults to -1 if an individual is not part of a work colleagues network.</p>
	 */
	private int m_friendsNetworkId = -1;
	/**
	 * <p>The number of activities together with friends planned for the current day. 
	 * It is reset to 0 at the beginning of each day and allows together with {@link ISimulationSettings#MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY} to limit the number of activities executed with friends.</p>
	 */
	private int m_numberOfFriendsNetworkActivitiesPlanned = 0;

	/**
	 * @category Needs
	 */
	
	/**
	 * The target need time split defines the individual's ideal relative distribution of time in regards to it's different needs. 
	 * Thus, it is used as a benchmark to evaluate activity plans. 
	 * The closer a plan is to the target need time split, the better it is at satisfying the individuals needs and thus the more an individual prefers it.<p>
	 */
	private NeedTimeSplit m_targetNeedTimeSplit;
	/**
	 * The actual need time split is used to record the time the individual spends on satisfying each of its needs. 
	 * Thus, it can be used to compare the individuals actual need satisfaction to it's ideal (i.e. target) need satisfaction at any given point in time.</p>
	 */
	private AbsoluteNeedTimeSplit m_actualNeedTimeSplit = new AbsoluteNeedTimeSplit();
	
	/**
	 * @category Agendas
	 */
	
	/**
	 * <p>The agenda with all activities planned for the current day. 
	 * It contains individual as well as joint activities.</p>
	 */
	private ActivityAgenda m_activityAgenda = new ActivityAgenda();
	/**
	 * <p>The agenda with all joint activities planned for the current day. 
	 * It contains only joint activities.</p>
	 */
	private ActivityAgenda m_jointActivityAgenda = new ActivityAgenda();
	/**
	 * <p>This variable is used to create a configurable number of randomly generated plans and to choose from it. 
	 * Use {@link ISimulationSettings#NUMBER_OF_PLANS_TO_GENERATE} to configure it.</p>
	 */
	private HashMap<ActivityAgenda, ActivityAgenda> m_allDayPlans = new HashMap<>();
	/**
	 * <p>The activity the individual has planned executing at the current point in time.</p>
	 */
	private Activity m_currentActivity = null;

	/**
	 * @category Static locations
	 */
	
	/**
	 * <p>The node on the path network which is closest to the individuals home building. 
	 * It serves as a proxy for the individuals home.</p>
	 */
	private Node m_homeNode;
	/**
	 * <p>A list of all other places (in addition to the home) where activities of {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE} can be executed.</p>
	 */
	private ArrayList<Node> m_otherPlacesForHouseholdAndFamilyCareNodes;
	/**
	 * The node on the path network which is closest to the individuals work place building. 
	 * It serves as a proxy for the individuals work place.</p>
	 */
	private Node m_workPlaceNode;
	/**
	 * <p>A list of all other places (in addition to the work place) where activities of {@link ActivityCategory#WORK} can be executed.</p>
	 */
	private ArrayList<Node> m_otherPlacesForWorkNodes;
	/**
	 * <p>The node on the path network which is closest to the individuals preferred building for executing leisure activities. 
	 * It serves as a proxy for this building.</p>
	 */
	private Node m_leisureNode;
	/**
	 * <p>A list of all other places (in addition to the preferred place for leisure) where activities of {@link ActivityCategory#LEISURE} can be executed.</p>
	 */
	private ArrayList<Node> m_otherPlacesForLeisureNodes;
	
	/**
	 * @category Dynamic locations
	 */
	
	/**
	 * <p>The current location of the individual.</p>
	 */
	private MasonGeometry m_currentLocationPoint;
	/**
	 * The line of the edge the individual is currently traveling on.</p>
	 */
	private LengthIndexedLine m_lengthIndexedLineOfEdge = null; // Used by individual to walk along line segment
	/**
	 * <p>The end index of the current edge.</p>
	 */
	private double m_endIndexOfCurrentEdge = 0.0;
	/**
	 * <p>The start index of the current edge.</p>
	 */
	private double m_startIndexOfCurrentEdge = 0.0;
	/**
	 * <p>The current index i.e. the position of the individual on the current edge.</p>
	 */
	private double m_currentIndexOnLineOfEdge = 0.0;
	/**
	 * <p>A helper class to move a point to a new Coordinate.</p>
	 */
	private PointMoveTo m_pointMoveTo = new PointMoveTo();
	/**
	 * <p>The path to the next target.</p>
	 */
	private ArrayList<GeomPlanarGraphDirectedEdge> m_pathToNextTarget = new ArrayList<GeomPlanarGraphDirectedEdge>();
	/**
	 * <p>The edge on which the individual currently is traveling on.</p>
	 */
	private GeomPlanarGraphEdge m_currentEdge = null;
	/**
	 * <p>The direction which the individual is traveling on the current edge. 
	 * It can either be positive or negative and indicates how the edge is traversed by the individual.</p>
	 */
	private static final int POSITIVE_MOVEMENT = 1;
	private static final int NEGATIVE_MOVEMENT = -1;
	private int m_edgeDirection;
	/**
	 * <p>The index of the edge the individual is currently traveling on. 
	 * If this value is equal to the size of {@link Individual#m_pathToNextTarget}, then the individual has reached its target.</p>
	 */
	private int m_currentIndexOnPathToNextTarget = 0;
	/**
	 * <p>The node of the path network the individual is currently on.</p>
	 */
	private Node m_currentNode;
	/**
	 * <p>The node of the path network that represents the next target.</p>
	 */
	private Node m_currentTargetNode = null;

	/**
	 * <p>Please, use the {@link Builder} to instantiate this class.</p>
	 */
	private Individual() {}
	
	@Override
	public String toString() {
		return "Id = " + m_id 
				+ " | Household Network = " + m_householdMembersNetworkId 
				+ " | Work network = " + m_workColleaguesNetworkId 
				+ " | Friends network = " + m_friendsNetworkId;
	}

	/**
	 * @category Builder
	 */
	
	/**
	 * <p>This builder must be used to instantiate {@link Individual}s. 
	 * In addition to making creation of instances easier it validates some of the most important attributes.</p>
	 */
	public static class Builder {

		private Individual individualToBuild;

		public Builder() {
			individualToBuild = new Individual();
		}
		
		/**
		 * <p>Each {@link Individual} must have a reference to its {@link Environment} and this method sets it for {@link Builder#individualToBuild}. 
		 * The reference is used to retrieve information about the current state of the environment such as the current time, the state of other individuals etc.</p>
		 * 
		 * @param environment - where the individual is placed in.
		 * @return {@link Builder} - builder with a reference to {@link Builder#individualToBuild}'s environment.
		 */
		public Builder withEnvironment(Environment environment) {
			String warningMessage = "Environment is invalid. The built individual may be unusable!";
			validate(environment, warningMessage);
			individualToBuild.m_environment = environment;
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have an id and this method sets it for {@link Builder#individualToBuild}.
		 * The individual's id is a simple unique identifier which also reflects the order in which they are being created. 
		 * It starts at zero and goes up to {@link ISimulationSettings#NUMBER_OF_INDIVIDUALS} - 1.</p>
		 * 
		 * @param id - id of the individual.
		 * @return {@link Builder} - builder with the id set for {@link Builder#individualToBuild}.
		 */
		public Builder withId(int id) {
			String warningMessage = "Id is invalid. The built individual may be unusable!";
			validate(id, warningMessage);
			individualToBuild.m_id = id;
			return this;
		}

		/**
		 * <p>Each {@link Individual} must have a target need time split and this method sets it for {@link Builder#individualToBuild}. 
		 * The target need time split defines the individual's ideal relative distribution of time in regards to its different needs. 
		 * Thus, it is used as a benchmark to evaluate activity plans. 
		 * The closer a plan is to the target need time split, the better it is at satisfying the individuals needs and thus the more an individual prefers it.</p>
		 * 
		 * @param targetNeedTimeSplit - the target need time split.
		 * @return {@link Builder} - builder with the target need time split set for {@link Builder#individualToBuild}.
		 */
		public Builder withTargetNeedTimeSplit(NeedTimeSplit targetNeedTimeSplit) {
			String warningMessage = "Target need  time split is invalid. The built individual may be unusable!";
			validate(targetNeedTimeSplit, warningMessage);
			individualToBuild.m_targetNeedTimeSplit = targetNeedTimeSplit;
			return this;
		}

		/**
		 * <p>Each {@link Individual} must have a home and this method sets it for {@link Builder#individualToBuild}.
		 * More specifically, the node on the path network which is closest to the individuals home building will be used as home. 
		 * Thus, it serves as a proxy for the individuals home.
		 * Since the home is also the starting point of all individuals, all variables referring to the individuals location will be initialized with the home location as well.</p>
		 * 
		 * @param homeBuilding - The {@link MasonGeometry} that represents the building where the individual lives in
		 * @return {@link Builder} - builder with the home building and the initial location set for {@link Builder#individualToBuild}.
		 */
		public Builder withHomeBuilding(MasonGeometry homeBuilding) {
			String warningMessage = "Home location is invalid. The built individual may be unusable!";
			validate(homeBuilding.getGeometry(), warningMessage);
			Node homeNode = getClosestNode(homeBuilding);
			validate(homeNode, warningMessage);
			initCurrentLocation(homeNode);
			initCurrentNode(homeNode);
			individualToBuild.m_homeNode = homeNode;
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have at least one other place for household and family care related activities and this method sets it for {@link Builder#individualToBuild}.
		 * This attribute represents a list of all other places (in addition to the home) where activities of {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE} can be executed.</p>
		 * 
		 * @param otherPlaceForHouseholdAndFamilyCareBuildings - a list of other locations where the individual can execute activities of {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE}.
		 * @return {@link Builder} - builder with the other places for household and family care set for {@link Builder#individualToBuild}.
		 */
		public Builder withOtherPlaceForHouseholdAndFamilyCareBuildings(ArrayList<MasonGeometry> otherPlaceForHouseholdAndFamilyCareBuildings) {
			String warningMessage = "Third place for household and family care is invalid. The built individual may be unusable!";
			individualToBuild.m_otherPlacesForHouseholdAndFamilyCareNodes = initOtherPlaces(otherPlaceForHouseholdAndFamilyCareBuildings, warningMessage);
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a work place and this method sets it for {@link Builder#individualToBuild}.
		 * More specifically, the node on the path network which is closest to the individuals work place building will be used as work place. 
		 * Thus, it serves as a proxy for the individuals work place.</p>
		 * 
		 * @param workPlaceBuilding -The {@link MasonGeometry} that represents the building where the individual works.
		 * @return {@link Builder} - builder with work place set for {@link Builder#individualToBuild}.
		 */
		public Builder withWorkPlaceBuilding(MasonGeometry workPlaceBuilding) {
			String warningMessage = "The work place building is invalid. The built individual may be unusable!";
			validate(workPlaceBuilding.getGeometry().getCoordinate(), warningMessage);
			Node workPlaceNode = getClosestNode(workPlaceBuilding);
			validate(workPlaceNode, warningMessage);
			individualToBuild.m_workPlaceNode = workPlaceNode;
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have at least one other place for work related activities and this method sets it for {@link Builder#individualToBuild}.
		 * This attribute represents a list of all other places (in addition to the work place) where activities of {@link ActivityCategory#WORK} can be executed.</p>
		 * 
		 * @param otherPlaceForWorkBuildings - a list of other locations where the individual can execute activities of {@link ActivityCategory#WORK}.
		 * @return {@link Builder} - builder with the other places for work place set for {@link Builder#individualToBuild}.
		 */
		public Builder withOtherPlaceForWorkBuildings(ArrayList<MasonGeometry> otherPlaceForWorkBuildings) {
			String warningMessage = "The third place for work is invalid. The built individual may be unusable!";
			individualToBuild.m_otherPlacesForWorkNodes = initOtherPlaces(otherPlaceForWorkBuildings, warningMessage);
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a preferred place for leisure activities and this method sets it for {@link Builder#individualToBuild}.</p>
		 * 
		 * @param leisureBuilding - The {@link MasonGeometry} that represents the building where the individual spends its leisure time (default location).
		 * @return {@link Builder} - builder with the preferred location for leisure activities set for {@link Builder#individualToBuild}.
		 */
		public Builder withLeisureBuilding(MasonGeometry leisureBuilding) {
			String warningMessage = "The place for leisure is invalid. The built individual may be unusable!";
			validate(leisureBuilding.getGeometry().getCoordinate(), warningMessage);
			Node leisureNode = getClosestNode(leisureBuilding);
			validate(leisureNode, warningMessage);
			individualToBuild.m_leisureNode = leisureNode;
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have at least one other place for leisure related activities and this method sets it for {@link Builder#individualToBuild}.</p>
		 * 
		 * @param otherPlaceForLeisureBuildings - a list of other locations where the individual can execute activities of {@link ActivityCategory#LEISURE}.
		 * @return {@link Builder} - builder with the other locations for leisure activities set for {@link Builder#individualToBuild}.
		 */
		public Builder withOtherPlaceForLeisureBuildings(ArrayList<MasonGeometry> otherPlaceForLeisureBuildings) {
			String warningMessage = "The third place for leisure is invalid. The built individual may be unusable!";
			individualToBuild.m_otherPlacesForLeisureNodes = initOtherPlaces(otherPlaceForLeisureBuildings, warningMessage);
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a household network id and this method sets it for {@link Builder#individualToBuild}.</p>
		 * 
		 * @param householdMembersNetworkId - id of the household network.
		 * @return {@link Builder} - builder with the household network id set for {@link Builder#individualToBuild}.
		 */
		public Builder withHousholdMembersNetworkId(int householdMembersNetworkId) {
			individualToBuild.m_householdMembersNetworkId = householdMembersNetworkId;
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a household network and this method sets it for {@link Builder#individualToBuild}.</p>
		 * 
		 * @param householdMembersNetwork - the household network.
		 * @return {@link Builder} - builder with the household network set for {@link Builder#individualToBuild}.
		 */
		public Builder withHousholdMembersNetwork(Network householdMembersNetwork) {
			individualToBuild.m_householdMembersNetwork = householdMembersNetwork;
			return this;
		}
		
		/**
		 * <p>Use this method to add network members individually.
		 * 
		 * @param householdMember - new member of the household network.
		 * @return {@link Builder} - builder with an additional individual added to the {@link Builder#individualToBuild}'s household network.</p>
		 */
		public Builder addHouseholdMember(Individual householdMember) {
			individualToBuild.m_householdMembersNetwork.addNode(householdMember);
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a work colleagues network id and this method sets it for {@link Builder#individualToBuild}.</p>
		 * 
		 * @param workColleaguesNetworkId - id of the work colleagues network.
		 * @return {@link Builder} - builder with the work colleagues network id set for {@link Builder#individualToBuild}.
		 */
		public Builder withWorkColleguesNetworkId(int workColleaguesNetworkId) {
			individualToBuild.m_workColleaguesNetworkId = workColleaguesNetworkId;
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a work colleagues network and this method sets it for {@link Builder#individualToBuild}.</p>
		 * 
		 * @param workColleaguesNetwork - the work colleagues network.
		 * @return {@link Builder} - builder with the work colleagues network set for {@link Builder#individualToBuild}.
		 */
		public Builder withWorkColleguesNetwork(Network workColleaguesNetwork) {
			individualToBuild.m_workColleaguesNetwork = workColleaguesNetwork;
			return this;
		}
		
		/**
		 * <p>Use this method to add network members individually.
		 * 
		 * @param workColleague - new member of the work colleagues network.
		 * @return {@link Builder} - builder with an additional individual added to the {@link Builder#individualToBuild}'s work colleagues network.
		 */
		public Builder addWorkCollegue(Individual workColleague) {
			individualToBuild.m_workColleaguesNetwork.addNode(workColleague);
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a friends network id and this method sets it for {@link Builder#individualToBuild}.
		 * 
		 * @param friendsNetworkId - id of the friends network.
		 * @return {@link Builder} - builder with the friends network id set for {@link Builder#individualToBuild}.
		 */
		public Builder withFriendsNetworkId(int friendsNetworkId) {
			individualToBuild.m_friendsNetworkId = friendsNetworkId;
			return this;
		}
		
		/**
		 * <p>Each {@link Individual} must have a friends network and this method sets it for {@link Builder#individualToBuild}.
		 * 
		 * @param friendsNetwork - the friends network.
		 * @return {@link Builder} - builder with the friends network set for {@link Builder#individualToBuild}.
		 */
		public Builder withFriendsNetwork(Network friendsNetwork) {
			individualToBuild.setFriendsNetwork(friendsNetwork);
			return this;
		}
		
		/**
		 * <p>Use this method to add network members individually.</p>
		 * 
		 * @param friend - new member of the friends network.
		 * @return {@link Builder} - builder with an additional individual added to the {@link Builder#individualToBuild}'s friends network.
		 */
		public Builder addFriend(Individual friend) {
			individualToBuild.getFriendsNetwork().addNode(friend);
			return this;
		}
		
		/**
		 * <p>Use this method to adjust <b>an already built</b> {@link Individual}. This is a convenient way of setting multiple attributes at once and thus an alternative to using the setters.</p>
		 * 
		 * @param individualToAdjust - the individual you want to adjust
		 * @return {@link Builder} - builder with the individual to adjust set as the individual to build, so that it can be rebuilt i.e. adjusted.
		 */
		public Builder adjust(Individual individualToAdjust) {
			individualToBuild = individualToAdjust;
			return this;
		}
		
		/**
		 * <p>This method returns the closest node of a given building.</p>
		 * 
		 * @param building - some building.
		 * @return {@link Node} - the node closest to the building.
		 */
		private Node getClosestNode(MasonGeometry building) {
			return individualToBuild.m_environment.getClosestNodeToBuilding(building);
		}
		
		/**
		 * <p>This method initializes the current location of the individual.</p>
		 * 
		 * @param location - the current location.
		 */
		private void initCurrentLocation(Node location) {
			Coordinate currentLocationPoint = new Coordinate(location.getCoordinate());
			individualToBuild.m_currentLocationPoint = new MasonGeometry(Environment.GEO_FACTORY.createPoint(currentLocationPoint));
			individualToBuild.m_currentLocationPoint.isMovable = true;
		}
		
		/**
		 * <p>This method initializes the current node of the individual.</p>
		 * 
		 * @param currentNode - the current node.
		 */
		private void initCurrentNode(Node currentNode) {
			individualToBuild.m_currentNode = new Node(currentNode.getCoordinate(), currentNode.getOutEdges());
			individualToBuild.m_homeNode = currentNode;
		}
		
		/**
		 * <p>This method initializes the nodes closest to the provided list of buildings.</p>
		 * 
		 * @param otherPlaceBuildings - the list of buildings for which the closest nodes are initialized.
		 * @param warningMessage - a message that will be logged if a node is invalid.
		 * @return ArrayList<Node> - a list with the closest nodes.
		 */
		private ArrayList<Node> initOtherPlaces(ArrayList<MasonGeometry> otherPlaceBuildings, String warningMessage) {
			ArrayList<Node> otherPlaceNodes = new ArrayList<>();
			for (MasonGeometry otherPlaceBuilding: otherPlaceBuildings) {
				validate(otherPlaceBuilding.getGeometry().getCoordinate(), warningMessage);
				Node thirdPlaceForHouseholdAndFamilyCareNode = getClosestNode(otherPlaceBuilding);
				otherPlaceNodes.add(thirdPlaceForHouseholdAndFamilyCareNode);
			}
			return otherPlaceNodes;
		}
		
		/**
		 * <p>This method can be used to ensure that a given object is not null and log a message otherwise.
		 * It is primarily used to ensure that important attributes used to construct an {@link Individual} are valid i.e. not <code>null</code>.</p>
		 * 
		 * @param objToValidate - the object that must not be <code>null</code>.
		 * @param message - the message logged if the object is invalid.
		 */
		private void validate(Object objToValidate, String message) {
			if (objToValidate == null) {
				LOG.log(Level.SEVERE, message);
			}
		}
		
		/**
		 * 
		 * <p>This method ensures that all mandatory variables of {@link Individual} are set i.e. not <code>null</code> or <code>-1</code>.</p>
		 * 
		 * @return The string of the first field that is not set. <code>null</code> if all mandatory fields are set.
		 */
		private String checkIfAnyFieldIsNull() {
			if (individualToBuild.m_environment == null) {
				return "m_environment";
			}
			if (individualToBuild.m_id == -1) {
				return "m_id";
			}
			if (individualToBuild.m_targetNeedTimeSplit == null) {
				return "m_targetNeedTimeSplit";
			}
			if (individualToBuild.m_currentLocationPoint == null) {
				return "m_currentLocationPoint";
			}
			if (individualToBuild.m_currentNode == null) {
				return "m_currentNode";
			}
			if (individualToBuild.m_homeNode == null) {
				return "m_homeNode";
			}
			if (individualToBuild.m_otherPlacesForHouseholdAndFamilyCareNodes == null) {
				return "m_otherPlaceForHouseholdAndFamilyCareNodes";
			}
			if (individualToBuild.m_workPlaceNode == null) {
				return "m_workPlaceNode";
			}
			if (individualToBuild.m_otherPlacesForWorkNodes == null) {
				return "m_otherPlaceForWorkNodes";
			}
			if (individualToBuild.m_leisureNode == null) {
				return "m_leisureNode";
			}
			if (individualToBuild.m_otherPlacesForLeisureNodes == null) {
				return "m_otherPlaceForLeisureNodes";
			}
			if (individualToBuild.m_householdMembersNetworkId == -1) {
				return "m_householdMembersNetworkId";
			}
			if (individualToBuild.m_householdMembersNetwork == null) {
				return "m_householdMembersNetwork";
			}
			if (individualToBuild.m_workColleaguesNetworkId == -1) {
				return "m_workColleguesNetworkId";
			}
			if (individualToBuild.m_workColleaguesNetwork == null) {
				return "m_workColleguesNetwork";
			}
			if (individualToBuild.m_friendsNetworkId == -1) {
				return "m_friendsNetworkId";
			}
			
			return null;
		}
		
		/**
		 * <p>This method builds an {@link Individual}, then it initializes a new {@link Individual} to be built. 
		 * It can be called any time you would like to build the individual independent of whether or not all attributes have been initialized.</p>
		 * 
		 * @return builtIndividual - built individual
		 */
		public Individual build() {
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual();
			return builtIndividual;
		}
		
		/**
		 * <p>This method validates and builds an {@link Individual}, then it initializes a new {@link Individual} to be built. 
		 * It is meant to be called once all the mandatory fields are set and ensures that the build individual is valid.</p>
		 * 
		 * <p><b>Note:</b> The {@link Activity} will be built independent of whether it is complete or not, but information about missing attributes will be logged. 
		 * It is very likely that a simulation with not completely initialized {@link Activity}s will crash at some point or at least lead to unexpected behavior / output. 
		 * You should therefore prevent such situations by keeping an eye on the log and fix all of the logged errors before evaluating the simulations output.</p>
		 * 
		 * @return builtIndividual - validated and built individual
		 */
		public Individual buildAndValidate() {
			if (checkIfAnyFieldIsNull() != null) {
				LOG.log(Level.SEVERE, String.format("%s is null i.e. not set! The built individual may be unusable!", checkIfAnyFieldIsNull()));
			}
			Individual builtIndividual = individualToBuild;
			individualToBuild = new Individual();
			return builtIndividual;
		}
	}
	
	/**
	 * @category Planning of joint activities
	 */
	
	/**
	 * <p>This method is used to plan joint activities for all the individual's networks.
	 * More specifically, this method schedules or reschedules all future joint activities for the rest of the current day. 
	 * To do this it relies on {@link Individual#m_jointActivityAgenda} an activity agenda where only activities with other individuals (which are members of one of the individuals networks) are recorded.</p>
	 * 
	 * <p>The planning works as follows:
	 * <ol>
	 * 	<li>The individual removes all future joint activities for each of its networks.
	 * 		A future activity is an activity whose start time is after the current simulation time.</li>
	 * 	<li>It decides whether it wants to plan future joint activities for each of its networks (see {@link Individual#isOpenForNetworkActivities(NetworkType, double)} for more details).</li>
	 * 	<li>If it wants to plan future joint activities it does so by choosing an activity, interval and location and then asking all of the networks members if they are willing to participate (see {@link Individual#planActivityForNetwork(Network, NetworkType, ActivityCategory, ArrayList)} for more details).
	 * 		If at least one other network member is willing to participate, they both write the activity into their joint activity agenda. 
	 * </ol></p> 
	 */
	public void planJointActivities() {
		removeFutureJointActivities();
		if (isOpenForNetworkActivities(NetworkType.HOUSEHOLD_NETWORK, ISimulationSettings.PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY)) {
			planActivityForNetwork(m_householdMembersNetwork, NetworkType.HOUSEHOLD_NETWORK , ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, ISimulationSettings.AVAILABLE_START_TIMES_FOR_HOUSEHOLD_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(NetworkType.WORK_COLLEGUES_NETWORK, ISimulationSettings.PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY)) {
			planActivityForNetwork(m_workColleaguesNetwork, NetworkType.WORK_COLLEGUES_NETWORK, ActivityCategory.WORK, ISimulationSettings.AVAILABLE_START_TIMES_FOR_WORK_COLLEAGUES_NETWORK_ACTIVITIES);
		}
		if (isOpenForNetworkActivities(NetworkType.FRIENDS_NETWORK, ISimulationSettings.PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY)) {
			planActivityForNetwork(m_friendsNetwork, NetworkType.FRIENDS_NETWORK, ActivityCategory.LEISURE, ISimulationSettings.AVAILABLE_START_TIMES_FOR_FRIENDS_NETWORK_ACTIVITIES);
		}
	}
	
	/**
	 * <p>This method removes the future activities for each of the different network types.</p>
	 */
	private void removeFutureJointActivities() {
		removeFutureJointActivitiesOfNetwork(m_householdMembersNetwork, NetworkType.HOUSEHOLD_NETWORK);
		removeFutureJointActivitiesOfNetwork(m_workColleaguesNetwork, NetworkType.WORK_COLLEGUES_NETWORK);
		removeFutureJointActivitiesOfNetwork(m_friendsNetwork, NetworkType.FRIENDS_NETWORK);
	}
	
	/**
	 * <p>This method removes all future joint activities for the specified combination of network and network type. 
	 * All activities with a start time after the current simulation time are future activities.
	 * <br><b>Note:</b> All future activities for all members of the specified network are removed.</p>
	 * 
	 * @param network - The network for which all future activities should be removed.
	 * @param networkType - The type of the network.
	 */
	private void removeFutureJointActivitiesOfNetwork(Network network, NetworkType networkType) {
		for (Object individualObj: network.getAllNodes()) {
			Individual individual = (Individual) individualObj;
			for (Interval futureInterval: getFutureIntervals(individual)) {
				if (individual.getJointActivityAgenda().getActivityForInterval(futureInterval).getNetworkType() == networkType) {
					individual.getJointActivityAgenda().getAgenda().remove(futureInterval);
					individual.getJointActivityAgenda().getNodes().remove(futureInterval);
					decrementNumberOfNetworkActivities(networkType, individual);
				}
			}
		}
	}
	
	/**
	 * <p>This method decrements the number of activities for the provided network type and individual.</p>
	 * 
	 * @param networkType - the network type for which the number of activities is decremented.
	 * @param individual - the individual for which the number of activities is decremented.
	 */
	private void decrementNumberOfNetworkActivities(NetworkType networkType, Individual individual) {
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
				LOG.log(Level.SEVERE, String.format("Can not apply method to the following network type: %s", String.valueOf(networkType)));
		}
	}
	
	/**
	 * <p>This method models the individuals decision mechanism for deciding on whether or not to plan or participate in a joint activity.</p>
	 * 
	 * <p>To do this it considers the following:
	 * 	<ol>
	 * 		<li>The number of joint activities it has already planned for the specified network type. 
	 * 			It is no longer open for further activities once the maximum number for the specified network type is reached.</li>
	 * 		<li>If there is still unplanned time left for the current day. If there is no unplanned time left, it obviously can not be open for any further joint activity.</li>
	 * 		<li>Its likelihood of planning a joint activity for the given network type.</p>
	 * 	</ol> 
	 * 
	 * @param networkType - the type of network for which it is planning or being asked to participate in a joint activity.
	 * @param probabilityOfPlaningActivityForNetworkType - the probability of planning of or agreeing to a joint activity of the specified network type.
	 * @return boolean - <code>true</code> if it is willing to participate, <code>false</code> otherwise.
	 */
	public boolean isOpenForNetworkActivities(NetworkType networkType, double probabilityOfPlaningActivityForNetworkType) {	
		boolean hasReachedMaxNumberOfActivitiesForNetworkType = hasReachedMaxNumberOfNetworkActivities(networkType);
		if (!hasReachedMaxNumberOfActivitiesForNetworkType
				&& !TimeUtility.isDayFullyPlanned(m_environment, m_jointActivityAgenda)
				&& m_environment.random.nextDouble(true, true) <= probabilityOfPlaningActivityForNetworkType) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * <p>This checks if the maximum number not network activities has been reached based on the provided network type and the simulation settings.</p>
	 * 
	 * @param networkType - the network type for which the number of network activities is checked.
	 * @return boolean - <code>true</code> if the number of activity exceeds the maximum configured in the settings, <code>false</code> otherwise.
	 */
	private boolean hasReachedMaxNumberOfNetworkActivities(NetworkType networkType) {
		boolean hasReachedMaxNumberOfActivitiesForNetworkType = true;
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
				LOG.log(Level.SEVERE, String.format("Can not apply method to the following network type: %s", String.valueOf(networkType)));
				break;
		}
		return hasReachedMaxNumberOfActivitiesForNetworkType;
	}
	
	/**
	 * <p>This method models the individuals steps for planning joint activities, i.e. activities for the specified type of network.</p>
	 * 
	 * <p>Those steps can be summarized as follows:
	 * 	<ol>
	 * 		<li>Check if any activity is available for this category at this day. If not, then do not plan activities for the specified type of network.</li>
	 * 		<li>Determine which members of the network are willing to participate. If no other member wants to participate, then do not plan activities for the specified type of network.</li>
	 * 		<li>Try to find some interval on which all the participating network members can agree. If no interval can be found that fits all of the individuals wanting to participate, then planning is aborted.</li>
	 * 		<li>Choose one of the available activities at random (at this point there should always be at least one activity available).</li>
	 * 		<li>Choose on of the possible locations for the activity (see {@link Individual#chooseActivityNode(Activity)} for more details.</li>
	 * 		<li>For each of the participating individuals: write the activity and its location into the joint activity agenda {@link Individual#m_jointActivityAgenda} and update the number of network activities planned accordingly.</li>
	 * 	</ol>
	 * 
	 * @param network - the network for which joint activities are planned.
	 * @param type - the type of the network for which joint activities are planned.
	 * @param activityCategory - the activity category which can be used for the specified network type.
	 * @param availableStartTimes - the points in time of a day at which joint activities can be started.
	 */
	private void planActivityForNetwork(Network network, NetworkType type, ActivityCategory activityCategory, ArrayList<DateTime> availableStartTimes) {
		// check if any activity is available for this category at this day
		if (!isAnyActivityAvailable(activityCategory)) {
			return;
		}
		ArrayList<Individual> networkMemberParticipating = determineParticipatingNetworkMembers(network, type);
		// nobody wants to participate in joint activity
		if (networkMemberParticipating.size() < 2) {
			return;
		}
		Interval baseIntervalOfJointActivity = chooseIntervalOfJointActivity(networkMemberParticipating, availableStartTimes, activityCategory);
		// no agreement on interval established
		if (baseIntervalOfJointActivity == null) {
			return;
		}
		// at this point we should have ensured that some activity is available and at least two network members agreed on some interval for conducting it
		ArrayList<Activity> availableActivities = getJointActivitiesAvailable(activityCategory, baseIntervalOfJointActivity);
		if (availableActivities.size() == 0) {
			LOG.log(Level.SEVERE, String.format("No activity availabe in category %s for interval interval: %s. Make sure there is always at least one activity available!", String.valueOf(activityCategory), String.valueOf(baseIntervalOfJointActivity)));	
		}
		// setup activity for all participating network members
		setupJointActivity(type, networkMemberParticipating, availableActivities, baseIntervalOfJointActivity);
	}
	
	/**
	 * <p>This method checks if any activity of the provided category is available on the current (simulation) day.</p>
	 * 
	 * @param activityCategory - the category for which availability of activities is checked.
	 * @return boolean - <code>true</code> if at least one activity is available, <code>otherwise</code>.
	 */
	private boolean isAnyActivityAvailable(ActivityCategory activityCategory) {
		long numberOfActivitiesAvailableAtWeekDay = m_environment.getAllActivities().values().stream()
				.filter(activity -> activity.getActivityCategory() == activityCategory)
				.filter(activity -> activity.isAvailableAt(getCurrentDayOfWeek()))
				.count();
		// no activity available at this day of week for given category
		if (numberOfActivitiesAvailableAtWeekDay > 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * <p>This method sets up the joint a joint activity for the provided network members.
	 * It does so by randomly selecting one of the available activities.
	 * The selected activity is then put into the joint activity agenda of all network members participating in the activity.</p>
	 * 
	 * @param type - the network type.
	 * @param networkMemberParticipating - the individuals participating in the activity.
	 * @param availableActivities - the activities available.
	 * @param baseIntervalOfJointActivity - the interval during which the activity is executed.
	 */
	private void setupJointActivity(NetworkType type, ArrayList<Individual> networkMemberParticipating, ArrayList<Activity> availableActivities, Interval baseIntervalOfJointActivity) {
		Interval realIntervalOfJointActivity = TimeUtility.convertToRealInterval(getCurrentDateTime(), baseIntervalOfJointActivity);
		Activity jointActivity = availableActivities.get(getRandomInt(availableActivities.size()));
		Node jointActivityNode = chooseActivityNode(jointActivity);
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
				LOG.log(Level.SEVERE, String.format("%s is an invalid NetworkType! Can not increment number of activities for this type!", String.valueOf(type)));
				break;
			}
		}
	}
	
	/**
	 * <p>This method models the individuals steps for finding out which of its network members want to participate in some joint activity.
	 * To do this it asks each of the other individuals in the specified network if it is open for a joint activity (see {@link Individual#isOpenForNetworkActivities(NetworkType, double)} for more details).</p> 
	 * 
	 * @param network - the network whose members should be asked if they want to participate in a joint activity.
	 * @param type - the type of the specified network.
	 * @return ArrayList<Individual> - the list of all network members who are willing to participate in a joint activity (including the individual who is asking).
	 */
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
						LOG.log(Level.SEVERE, String.format("%s is an invalid NetworkType! Can not plan activity for this type!", String.valueOf(type)));
						break;
				}
			}
		}
		return networkMemberParticipating;
	}
	
	/**
	 * <p>This method models the individual's steps for finding an interval during which a joint activity can be executed together with all the participating network members taking into consideration the configured start times.</p>
	 * 
	 * <p>This works as follows:
	 * <ol>
	 * 	<li>Determine all start times that are after the current simulation time.</li>
	 * 	<li>Pick one of those start times at random</li>
	 * 	<li>Sample the duration</li>
	 * 	<li>Construct a real and a base interval for this combination</li>
	 * 	<li>Check that the real interval of interest does not overlap any of the other participants agendas and that the maximum number of trials has not been exceeded yet.</li>
	 * 	<li>Repeat steps 2 - 5 until you find an interval that fits everybody or you reached the maximum number of trials.</li>
	 * </ol>
	 * 
	 * <p><b>Note:</b> It is possible that no agreement on an interval that fits everybody is possible. In this case planing is considered to have failed and thus no activity will be planned.
	 * 
	 * @param networkMemberParticipating - the individuals of of some network which are participating in the joint activity.
	 * @param startTimes - the configured start times at which activities of the specified category can be started.
	 * @param activityCategory - the category to which the activity must belong.
	 * @return Interval or <code>null</code> - the interval during which the joint activity will take place or, <code>null</code> if no agreement is possible.
	 */
	private Interval chooseIntervalOfJointActivity(ArrayList<Individual> networkMemberParticipating, ArrayList<DateTime> startTimes, ActivityCategory activityCategory) {
		Interval baseIntervalOfInterest;
		Interval realIntervalOfInterest;
		DateTime currentDateTime = getCurrentDateTime();
		int numberOfTrials = 0;
		List<DateTime> availableStartTimes = determineAvailableStartTimes(startTimes);
		// no start times available anymore
		if (availableStartTimes.size() == 0) {
			return null;
		}
		do {
			DateTime startOfJointActivityInBaseTime = availableStartTimes.get(getRandomInt(availableStartTimes.size()));
			DateTime endOfJointActivityInBaseTime = startOfJointActivityInBaseTime.plusMinutes(sampleDurationForCategory(activityCategory));
			if (endOfJointActivityInBaseTime.isAfter(ISimulationSettings.END_OF_DAY)) {
				endOfJointActivityInBaseTime = ISimulationSettings.END_OF_DAY;
			}
			baseIntervalOfInterest = new Interval(startOfJointActivityInBaseTime, endOfJointActivityInBaseTime);
			realIntervalOfInterest = TimeUtility.convertToRealInterval(currentDateTime, baseIntervalOfInterest);
			numberOfTrials++;
		} 
		while (TimeUtility.isIntervalOverlappingAnyAgenda(networkMemberParticipating, realIntervalOfInterest) 
				&& numberOfTrials < ISimulationSettings.MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY);
		if (numberOfTrials < ISimulationSettings.MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY) {
			return baseIntervalOfInterest;
		} 
		else {
			return null;
		}
	}

	/**
	 * <p>This method determines the available start times for joint activities under consideration of the current simulation time.</p>
	 * 
	 * @param startTimes - a list with all start times configured.
	 * @return List<DateTime> - a list with all start time currently available.
	 */
	private List<DateTime> determineAvailableStartTimes(ArrayList<DateTime> startTimes) {
		return startTimes.stream()
				.filter(startTime -> startTime.isAfter(m_environment.getSimulationTime().getCurrentTime()))
				.collect(Collectors.toList());
	}

	/**
	 * <p>This method lets you sample lets you sample an activity duration for a specified category based on the distributions you have specified in {@link ISimulationSettings}.<p>
	 * 
	 * <b>Note:</b> Due to the nature of random distributions (and of small means with large standard deviations) it is possible that negative samples will be drawn. 
	 * However, negative duration does not make any sense and thus samples will be drawn until a positive duration is sampled.
	 * This obviously implies that the durations drawn form the configured distribution do no exactly match the configured distribution.<p>
	 * 
	 * @param activityCategory - the activity category for which you want to sample a duration.
	 * @return double - the number of minutes 
	 */
	private int sampleDurationForCategory(ActivityCategory activityCategory) {
		double durationSampleForCategory = ISimulationSettings.s_ActivityCategoryToDurationDistributionMap.get(activityCategory).sample();
		if (durationSampleForCategory < 1) {
			LOG.log(Level.SEVERE, "The distribution must not sample negative durations! Make sure you handle this case by either changing the distribution parameters or resampling.");
		}
		return Math.toIntExact(Math.round(durationSampleForCategory));
	}
	
	/**
	 * <p>This method randomly chooses an activity location for the specified activity.</p>
	 * 
	 * @param activity - the activity for which a location is chosen at random.
	 * @return Node - the Node representing the location where the activity will be executed.
	 */
	private Node chooseActivityNode(Activity activity) {
		switch (activity.getActivityLocation()) {
		case HOME:
			return m_homeNode;
		case OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE:
			return m_otherPlacesForHouseholdAndFamilyCareNodes.get(getRandomInt(m_otherPlacesForHouseholdAndFamilyCareNodes.size()));
		case LEISURE:
			return m_leisureNode;
		case OTHER_PLACE_FOR_LEISURE:
			return m_otherPlacesForLeisureNodes.get(getRandomInt(m_otherPlacesForLeisureNodes.size()));
		case OTHER_PLACE_FOR_WORK:
			return m_otherPlacesForWorkNodes.get(getRandomInt(m_otherPlacesForWorkNodes.size()));
		case WORK:
			return m_workPlaceNode;
		default:
			LOG.log(Level.SEVERE, "Could not choose activty location!");
			return null;
		}
	}
	
	/**
	 * @category Planning of individual activities
	 */
	
	/**
	 * <p>This method models the individual's steps for planning individual activities. 
	 * 
	 * </p>This works as follows:
	 * 	<ol>
	 * 		<li>Reset all daily plans</li>
	 * 		<li>As long as not the number of plans as defined by {@link ISimulationSettings#NUMBER_OF_PLANS_TO_GENERATE} have been generated, repeat the steps 3 & 4 </li>
	 * 		<li>Clone the current activity agenda. It is called random agenda because the activities, the intervals and the locations which will be written into it are chosen randomly.
	 * 			 <b>Note:</b> At this point the agenda has to following entries: all activities executed until the current simulation time, the one which is currently being executed and all joint activities planned for the current day.</li>
	 * 		<li>As long as the cloned agenda is not completely filled with activities (i.e. has any gap in the time span from 0:00 - 23:59) do the following:
	 * 			<ol>
	 * 				<li>Randomly choose the next activity to be added to the agenda as well as its interval (see {@link Individual#chooseActivityAndIntervalInRealTime(Interval, ActivityAgenda)} for more details.</li>
	 * 				<li>Randomly choose the activity node for the activity.</li>
	 * 				<li>Write the activity and its location into the random agenda.</li>
	 * 				<li>Update the actual need time split of the random agenda to reflect the newly added activity.</li>
	 * 			</ol>
	 * 		<li>Calculate the travel time for each of the generated random plans and store it along with the plan without travel time in {@link Individual#m_allDayPlans}.</li>
	 * 	</ol>
	 */
	public void planIndividualActivities() {
		long start = System.nanoTime();
		int numberOfDiscardedPlans = 0;
		m_allDayPlans.clear();
		while (m_allDayPlans.size() < ISimulationSettings.NUMBER_OF_PLANS_TO_GENERATE) {
			ActivityAgenda randomAgenda = ActivityAgenda.newInstance(m_activityAgenda);
			while (!TimeUtility.isDayFullyPlanned(m_environment, randomAgenda)) {
				Interval availableInterval = TimeUtility.getFirstAvailableInterval(m_environment, randomAgenda);
				Tuple<Activity, Interval> activityAndIntervalInRealTime = chooseActivityAndIntervalInRealTime(randomAgenda, availableInterval);
				Activity chosenActivity = activityAndIntervalInRealTime.getKey();
				Interval chosenIntervalInRealTime = activityAndIntervalInRealTime.getValue();
				Node activityNode = chooseActivityNode(chosenActivity);
				randomAgenda.addActivityForInterval(chosenIntervalInRealTime, chosenActivity);
				randomAgenda.addNodeForInterval(chosenIntervalInRealTime, activityNode);
			}
			ActivityAgenda randomAgendaWithTravelActivities = createAgendaWithTravelActivities(randomAgenda);
			if (randomAgendaWithTravelActivities == null) {
				numberOfDiscardedPlans++;
				randomAgendaWithTravelActivities = new ActivityAgenda();
			}
			m_allDayPlans.put(randomAgendaWithTravelActivities, randomAgenda);
		}
		double fractionOfInvalidPlans = (double) numberOfDiscardedPlans / ISimulationSettings.NUMBER_OF_PLANS_TO_GENERATE * 100;
		long executionTime = (System.nanoTime() - start) / 1000000000;
		if (ISimulationSettings.IS_DEBUG) {
			System.out.println(String.format("It took %d s to plan the individual activities of individual %d and %.2f %% of the plans were discareded because they included too many different locations.", executionTime, m_id, fractionOfInvalidPlans));
		}
	}
	
	/**
	 * <p>The method models how an activity and it interval (in real time) are chosen randomly out of all possible combinations of them.</p>
	 * 
	 * <p>This works as follows:
	 * <ol>
	 * 	<li>Sample the activity duration for each category and construct an interval of interest in base time based on that.</li>
	 * 	<li>Determine all available activities. Available in this context means they can be executed during the interval of interest in base time.</li>
	 * 	<li>If one or more activities are available, then choose one of them at random and return it.</li>
	 * 	<li>If no activity could be found this means that all the sampled durations exceeded the available interval. 
	 *      Thus, in a second try we determine all activities that are available in the effectively available interval in real time (i.e. the first argument of this method).
	 *      Then we choose one of the available activities at random and return it.</li>
	 * </ol>
	 *
	 * <p><b>Note:</b> The only thing that ensures that the second attempt of selecting an available activity succeeds is <b>a correct configuration of activities</b>.
	 * It will be logged when this case occurs and the simulation will crash after this method returned <code>null</code>. 
	 * <br>A correct configuration in this context means that at all times at all locations there is at least one activity available.
	 * Currently this is ensured via activities of {@link ActivityCategory#IDLE}. Those activities do not satisfy any need but they are available any time and anywhere.
	 * Thus individuals will choose the only if they can not find a better alternative (see {@link Individual#chooseBestAgenda()}.</p>
	 * 
	 * @param availableIntervalInRealTime - the first gap between two activities in the random agenda or the time between the end of the last planned activity and the end of the current day.
	 * @param randomAgenda - the random agenda currently being constructed.
	 * @return AbstractMap.SimpleImmutableEntry<Activity, Interval> - the combination of activity and interval which has been chosen randomly, or <code>null</code> if no activity was available.
	 */
	private Tuple<Activity, Interval> chooseActivityAndIntervalInRealTime(ActivityAgenda randomAgenda, Interval availableIntervalInRealTime) {
		Interval availableIntervalInBaseTime = TimeUtility.convertToBaseInterval(availableIntervalInRealTime);
		// draw sample duration for each category
		HashMap<ActivityCategory, Interval> allCategoriesToIntervalSamples = sampleIntervalsForCategories(availableIntervalInBaseTime.getStart());
		// determine all available activities
		ArrayList<Activity> availableActivities = determineAvailableActivities(randomAgenda, availableIntervalInRealTime, allCategoriesToIntervalSamples);
		// there are some duration samples that fit into the available interval
		if (availableActivities.size() > 0) {
			Activity chosenActivity = availableActivities.get(getRandomInt(availableActivities.size()));
			Interval chosenIntervalInBaseTime = allCategoriesToIntervalSamples.get(chosenActivity.getActivityCategory());
			Interval chosenIntervalInRealTime = TimeUtility.convertToRealInterval(getCurrentDateTime(), chosenIntervalInBaseTime);
			return new Tuple<Activity, Interval>(chosenActivity, chosenIntervalInRealTime);
		}
		// none of the samples fitted
		for (ActivityCategory availableCategory: allCategoriesToIntervalSamples.keySet()) {
			List<Activity> availableActivitiesOfCategory = getAllAvailableActivitiesForCategoryAndInterval(randomAgenda, availableCategory, availableIntervalInBaseTime);
			availableActivities.addAll(availableActivitiesOfCategory);
		}
		if (availableActivities.size() > 0) {
			Activity chosenActivity = availableActivities.get(getRandomInt(availableActivities.size()));
			return new Tuple<Activity, Interval>(chosenActivity, availableIntervalInRealTime);
		}
		else {
			LOG.log(Level.SEVERE, String.format("No activity availabe for interval: %s. This can not happen unless something is configured incorrectly. Make sure you initialized all activities correctly!", availableIntervalInRealTime));
			return null;
		}
	}
	
	private HashMap<ActivityCategory, Interval> sampleIntervalsForCategories(DateTime startOfAvailableIntervalInBaseTime) {
		HashMap<ActivityCategory, Interval> allCategoriesToIntervalSamples = new HashMap<>();
		for (ActivityCategory activityCategory: ISimulationSettings.s_ActivityCategoryToDurationDistributionMap.keySet()) {
			int sampleDuration = sampleDurationForCategory(activityCategory);
			Interval intervalOfInterestInBaseTime = null;
			if (startOfAvailableIntervalInBaseTime.plusMinutes(sampleDuration).isAfter(ISimulationSettings.END_OF_DAY)) {
				intervalOfInterestInBaseTime = new Interval(startOfAvailableIntervalInBaseTime, ISimulationSettings.END_OF_DAY);
			} else {
				intervalOfInterestInBaseTime = new Interval(startOfAvailableIntervalInBaseTime, startOfAvailableIntervalInBaseTime.plusMinutes(sampleDuration));
			}
			allCategoriesToIntervalSamples.put(activityCategory, intervalOfInterestInBaseTime);
		}
		return allCategoriesToIntervalSamples;
	}
	
	private ArrayList<Activity> determineAvailableActivities(ActivityAgenda randomAgenda, Interval availableIntervalInRealTime, HashMap<ActivityCategory, Interval> allCategoriesToIntervalSamples) {
		ArrayList<Activity> availableActivities = new ArrayList<>();
		for (ActivityCategory availableCategory: allCategoriesToIntervalSamples.keySet()) {
			Interval sampeledIntervalOfInterestInBaseTime = allCategoriesToIntervalSamples.get(availableCategory);
			if (sampeledIntervalOfInterestInBaseTime.toDuration().getStandardMinutes() <= availableIntervalInRealTime.toDuration().getStandardMinutes()) {
				List<Activity> availableActivitiesOfCategory = getAllAvailableActivitiesForCategoryAndInterval(randomAgenda, availableCategory, sampeledIntervalOfInterestInBaseTime);
				availableActivities.addAll(availableActivitiesOfCategory);
			}
		}
		return availableActivities;
	}
	
	/**
	 * <p>This method let you determine all activities that fulfill certain conditions in terms of category and availability. 
	 * Additional constraints are applied when duration is small.</p>
	 * 
	 * <p>Two cases are distinguished: 
	 * <ol>
	 * 	<li>The case where the duration of the interval of interest (in base time) is smaller than the value configured for {@link ISimulationSettings#MIN_DURATION_OF_ACTIVITY_TO_TRAVEL_TO_DIFFERENT_LOCATION}.
	 * 		In this case the interval of interest's duration is so small that it is not justified to travel to another location for executing an activity. 
	 * 		Thus, there is an <b>additional constraint</b> in that only activities that are available at the individuals current location can be chosen.</li>
	 * 	<li>The case where no additional constraints apply.</li>
	 * </ol></p>
	 * 
	 * @param randomAgenda - the agenda for which available activities are chosen.
	 * @param activityCategory - the category to which the activity must belong.
	 * @param intervalOfInterestInBaseTime - the interval for which the activities must be available.
	 * @return List<Activity> - a list with all activities that fulfill the constraints.
	 */
	private List<Activity> getAllAvailableActivitiesForCategoryAndInterval(ActivityAgenda randomAgenda, ActivityCategory activityCategory, Interval intervalOfInterestInBaseTime) {
		// if duration is smaller than minimum duration, then stay at current location
		Activity previousActivity = getPreviousActivity(randomAgenda, intervalOfInterestInBaseTime);
		if ((int) intervalOfInterestInBaseTime.toDuration().getStandardMinutes() <= ISimulationSettings.MIN_DURATION_OF_ACTIVITY_TO_TRAVEL_TO_DIFFERENT_LOCATION  && previousActivity != null) {
			return m_environment.getAllActivities().values().stream()
				.filter(activity -> activity.getActivityCategory() == activityCategory)
				.filter(activity -> activity.getActivityCategory() == activityCategory || activity.getActivityCategory() == ActivityCategory.IDLE)
				.filter(activity -> activity.getActivityLocation() == previousActivity.getActivityLocation())
				.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
				.filter(activity -> !activity.isJointActivity())
				.filter(activity -> activity.isAvailableAt(getCurrentDayOfWeek(), intervalOfInterestInBaseTime.getStart()))
				.collect(Collectors.toList());
		}
		// no constraint in terms of location
		else {
			return m_environment.getAllActivities().values().stream()
					.filter(activity -> activity.getActivityCategory() == activityCategory)
					.filter(activity -> !(activity.getActivityCategory() == ActivityCategory.IDLE))
					.filter(activity -> !activity.isJointActivity())
					.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
					.filter(activity -> activity.isAvailableAt(getCurrentDayOfWeek(), intervalOfInterestInBaseTime.getStart()))
					.collect(Collectors.toList());
		}
	}
	
	private Activity getPreviousActivity(ActivityAgenda randomAgenda, Interval intervalOfInterestInBaseTime) {
		DateTime endOfPreviousActivity = TimeUtility.convertToRealInterval(getCurrentDateTime(), intervalOfInterestInBaseTime).getStart().minusMinutes(1);
		if (endOfPreviousActivity != null) {
			return randomAgenda.getActivityForDateTime(endOfPreviousActivity);
		}
		return null;
	}
	
	/**
	 * <p>This method creates an activity agenda with travel activities based on an agenda without travel activities.</p>
	 * 
	 * <p>This works as follows:
	 * 	<ol>
	 * 		<li>Check that the number of travel activities for the input agenda is not higher than {@link ISimulationSettings#MAX_NUMBER_OF_TRAVEL_ACTIVITIES}. 
	 * 			Otherwise abort the creation of an agenda with travel activities.
	 * 			<b>Note:</b> The reason for this is that agendas with a large number of travel activities require a lot of computing resources (to find all paths and caluculate the travel times etc.) but are unlikely to be chosen since travel activities do not satisfy any need.</li>
	 * 		<li>Split each of the activities into a travel activity and effective activity if necessary.
	 * 			<b>Note:</b> This is not necessary when the individual stays at the same location. 
	 * 						 However, if it does change its location then we can calculate the exact path it will take and based on this path derive the duration it will take to get to this location.
	 * 						 Using this information we can split the original activity interval into a travel interval and an effective activity interval (or only travel interval if the distance is high).</li>
	 * 	</ol></p>
	 * 
	 * @param agenda - the agenda for which travel activities should be derived.
	 * @return ActivityAgenda - a copy of the agenda but with all the travel activities as required by the input agenda, or <code>null</code> if the agenda implies more travel activities than allowed by {@link ISimulationSettings#MAX_NUMBER_OF_TRAVEL_ACTIVITIES}.
	 */
	private ActivityAgenda createAgendaWithTravelActivities(ActivityAgenda agenda) {
		ActivityAgenda activityAgendaWithTravelTimes = ActivityAgenda.newInstance(agenda);
		Activity travelActivity = m_environment.getAllActivities().get(ISimulationSettings.TRAVEL);
		
		// check number of travel episodes in agenda
		int numberOfDifferentLocations = 0;
		for (Interval interval: agenda.getIntervals()) {
			Node activityNode = agenda.getNodeForInterval(interval);
			Node nextActivityNode = agenda.getNodeForDateTime(interval.getEnd().plusMinutes(1));
			// there is a next activity (last entry has no next node) and it is conducted at a different location
			if (nextActivityNode != null && !activityNode.getCoordinate().equals(nextActivityNode.getCoordinate())) {
				numberOfDifferentLocations++;
			}
		}
		if (numberOfDifferentLocations > ISimulationSettings.MAX_NUMBER_OF_TRAVEL_ACTIVITIES) {
			return null;
		}
		// create activities for travel time
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
				activityAgendaWithTravelTimes.getAgenda().remove(interval);
				int travelDurationInMinutes = Math.toIntExact(Math.round(lengthOfPathToNextActivity / ISimulationSettings.MAX_VELOCITY));
				// only travel time
				if (travelDurationInMinutes >= interval.toDuration().getStandardMinutes()) {
					Interval travelInterval = interval;
					activityAgendaWithTravelTimes.getAgenda().put(travelInterval, travelActivity);
					updateActualNeedTimeSplit(activityAgendaWithTravelTimes, travelActivity, travelInterval);
				}
				// travel and activity time
				else {
					DateTime endOfActivity = interval.getEnd();
					DateTime startOfTravel = interval.getStart();
					DateTime endOfTravel = startOfTravel.plusMinutes(travelDurationInMinutes);
					Interval travelInterval = new Interval(startOfTravel, endOfTravel);
					Interval effectiveActivityInterval = new Interval(endOfTravel, endOfActivity);
					activityAgendaWithTravelTimes.getAgenda().put(travelInterval, travelActivity);
					activityAgendaWithTravelTimes.getAgenda().put(effectiveActivityInterval, currentActivity);
					updateActualNeedTimeSplit(activityAgendaWithTravelTimes, travelActivity, travelInterval);
					updateActualNeedTimeSplit(activityAgendaWithTravelTimes, currentActivity, effectiveActivityInterval);
				}
			}
			// the next activity takes place at the same location
			else {
				// no travel time -> just copy the entry to the agenda with travel times
				updateActualNeedTimeSplit(activityAgendaWithTravelTimes, currentActivity, interval);
			}
		}
		return activityAgendaWithTravelTimes;
	}
	
	private void updateActualNeedTimeSplit(ActivityAgenda agenda, Activity activity, Interval interval) {
		for (Need needSatisfiedByActivity: activity.getNeedTimeSplit().keySet()) {
			BigDecimal fractionForNeed = activity.getFractionForNeed(needSatisfiedByActivity);
			BigDecimal timeSpentSatisfyingNeed = fractionForNeed.multiply(CalculationUtility.createBigDecimal(interval.toDuration().getStandardMinutes()));
			agenda.getAbsoluteNeedTimeSplit().updateNeedTimeSplit(needSatisfiedByActivity, timeSpentSatisfyingNeed);
		}
	}

	
	/**
	 * <p>This method models how an individual chooses the agenda that fits its target need time split best.
	 * Best in this context means that the actual need time split of an agenda perfectly fits the target need time split of the individual.</p>
	 * <p>To quantify the deviation from the optimal plan we use (resp. adapt) the <a href="https://en.wikipedia.org/wiki/Mean_squared_error">mean squared error (MSE)</a> (see {@link CalculationUtility#calculateMeanSquaredError(ActivityAgenda, NeedTimeSplit)} for implementation details).
	 * <b>Note:</b> The MSE heavily weights outliers, which in our context is what we want. 
	 * The underlying assumption is that individuals prefer some minor deviations over one (or a few) large deviation(s).</p>
	 * 
	 * <p><b>Important:</b> the chosen agenda will be executed subsequently.</p>
	 * 
	 */
	public void chooseBestAgenda() {
		ActivityAgenda bestAgenda = null;
		BigDecimal minimumSquaredMeanError = new BigDecimal(Integer.MAX_VALUE);
		for (ActivityAgenda randomAgendaWithTravelActivities: m_allDayPlans.keySet()) {
			if (!randomAgendaWithTravelActivities.getAgenda().isEmpty()) {
				BigDecimal meanSquaredError = CalculationUtility.calculateMeanSquaredError(randomAgendaWithTravelActivities.getAbsoluteNeedTimeSplit(), getTargetNeedTimeSplit());
				if (meanSquaredError.compareTo(minimumSquaredMeanError) < 0) {
					minimumSquaredMeanError = meanSquaredError;
					bestAgenda = randomAgendaWithTravelActivities;
				}
			}
		}
		m_activityAgenda = m_allDayPlans.get(bestAgenda); // this gives the agenda without travel times
		m_allDayPlans.clear();
	}
	
	/**
	 * <p>This method checks if planning is possible at the current simulation time. 
	 * Time points at which planning is possible can be set in {@link ISimulationSettings#AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES}.
	 * The method uses an argument instead of direct access to {@link ISimulationSettings#AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES} to allow the modeler to define and use other start time points as well.</p> 
	 * 
	 * @param availableTimePoints - time points at which planning is possible.
	 * @return boolean - whether or not is is possible to plan activities.
	 */
	public boolean isPlanningPossible(ArrayList<DateTime> availableTimePoints) {
		return availableTimePoints.stream().anyMatch(timePoint -> timePoint.equals(m_environment.getSimulationTime().getCurrentTime()));
	}
	
	/**
	 * @category Carrying over joint activities
	 */
	
	/**
	 * <p>This method carries joint activities in the joint agenda over to the individual agenda ({@link Individual#m_activityAgenda}).
	 * To make sure that they can always be carried over, all future activities in the individual agenda are removed first.
	 * A future activity is any activity whose start time is after the current simulation time.</p>
	 */
	public void carryOverJointActivities() {
		// remove all future activities
		DateTime currentDateTime = getCurrentDateTime();
		List<Interval> futureIntervals = m_activityAgenda.getAgenda().keySet().stream()
			.filter(interval -> (interval.getStart().isAfter(currentDateTime)))
			.collect(Collectors.toList());
		for (Interval futureInterval: futureIntervals) {
			m_activityAgenda.getAgenda().remove(futureInterval);
			m_activityAgenda.getNodes().remove(futureInterval);
		}
		// fill future with joint activities
		for (Interval interval: m_jointActivityAgenda.getIntervals()) {
			if (interval.getStart().isAfter(currentDateTime)) {
				m_activityAgenda.addActivityForInterval(interval, m_jointActivityAgenda.getActivityForInterval(interval));
				m_activityAgenda.addNodeForInterval(interval, m_jointActivityAgenda.getNodeForInterval(interval));
			}
		}
	}
	
	/**
	 * @category Moving
	 */

	/**
	 * <p>This method models the individual's steps for moving.</p>
	 * 
	 * <p>This works as follows:
	 * 	<ol>
	 * 		<li>Initialize the path to the next activity, if necessary.</li>
	 * 		<li>Move along the path to the next activity, if the target location has not been reached yet.</li>
	 * 	</ol>
	 * </p>
	 * 
	 * <p><b>Note:</b> Check out the line comments for more details on the individual steps.</p>
	 * 
	 */
	public void move() {
		DateTime currentDateTime = getCurrentDateTime();
		m_currentActivity = m_activityAgenda.getActivityForDateTime(currentDateTime);
		m_currentTargetNode = m_activityAgenda.getNodeForDateTime(currentDateTime);
		// check if target has been reached
		if (!m_currentNode.getCoordinate().equals(m_currentTargetNode.getCoordinate())) {
			// check if path has been initialized
			if (m_pathToNextTarget.isEmpty()) {
				initPathToTarget(m_currentNode, m_currentTargetNode);
			}
			if (!hasReachedTarget()) {
				moveTowardsTarget();
			}
		}
		if (ISimulationSettings.IS_DEBUG) {
			m_environment.getClosestBuildingToNode(m_currentTargetNode).getGeometry().setUserData(DebugUtility.createLabelledPortrayal2DForBuilding(m_id, m_currentActivity));
		}
	}
	
	
	/**
	 * @category Executing activities
	 */

	/**
	 * <p>This method models the individual's steps for executing the current activity.</p>
	 * 
	 * <p>This works as follows:
	 * 	<ol>
	 * 		<li>Update the actual need time split according to the current activity (traveling, individual activity or joint activity).</li>
	 * 		<li>Update the output holder according to the current activity.</li>
	 * 	</ol>
	 * </p>
	 * 
	 * <p><b>Note:</b> Check out the line comments for more details on the individual steps.</p>
	 * 
	 */
	public void executeActivity() {
		// check if target has been reached
		if (hasReachedTarget()) {
			// move individual to target location
			if (!m_currentNode.getCoordinate().equals(m_currentTargetNode.getCoordinate())) {
				updatePosition(m_currentTargetNode.getCoordinate());
				m_currentNode = new Node(m_currentTargetNode.getCoordinate(), m_currentTargetNode.getOutEdges());
			}
			// update actual need time split for individual activity
			if (!m_currentActivity.isJointActivity()) {
				updateActualNeedTimeSplit(m_currentActivity);
			}
			// update actual need time split for joint activity
			else { // joint activity
				if (!isAloneForJointActivity()) {
					updateActualNeedTimeSplit(m_currentActivity);
				}
				else {
					updateActualNeedTimeSplit(m_currentActivity.getAlternativeActivity());
				}
			}
			m_environment.incrementIntegerValueOfOutputHolder(m_currentActivity.getActivityCategory().toString());
			m_environment.incrementIntegerValueOfOutputHolder(m_currentActivity.getActivityDescription());
		}
		else { // traveling towards target
			m_environment.incrementIntegerValueOfOutputHolder(ActivityCategory.TRAVEL.toString());
			// this has to be adjusted once more modes of transport are implemented
			m_environment.incrementIntegerValueOfOutputHolder(m_environment.getCategoryToActivities().get(ActivityCategory.TRAVEL).get(0).getActivityDescription());
		}
		m_environment.incrementIntegerValueOfOutputHolder(ISimulationSettings.TOTAL_NUMBER_OF_AGENTS);
	}
	
	/**
	 * <p>This method checks if any other individual in the respective network with the same activity is at the target location or not.
	 * It is used to find out if the joint activity can be executed or if the alternative activity has to be executed instead.</p>
	 * 
	 * @return boolean - whether or not the individual is the only one at the target location for the joint activity.
	 */
	private boolean isAloneForJointActivity() {
		boolean isAlone = true;
		switch (m_currentActivity.getNetworkType()) {
		case FRIENDS_NETWORK:
			isAlone = !isNetworkMemberPresent(m_friendsNetwork);
			break;
		case WORK_COLLEGUES_NETWORK:
			isAlone = !isNetworkMemberPresent(m_workColleaguesNetwork);
			break;
		case HOUSEHOLD_NETWORK:
			isAlone = !isNetworkMemberPresent(m_householdMembersNetwork);
			break;
		default:
			
			break;
		}
		return isAlone;
	}
	
	/**
	 * <p>This method checks for a given network if any other individual with the same activity is present at the target location or not.</p>
	 * 
	 * @param network - the network of which other member must be present to be able to conduct the activity.
	 * @return boolean - whether or not any other member of the network is present.
	 */
	private boolean isNetworkMemberPresent(Network network) {
		for (Object friendObj: network.allNodes) {
			Individual individual = (Individual) friendObj;
			if (m_currentActivity == individual.getCurrentActivity() && individual.hasReachedTarget()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @category Moving on paths of the environment
	 * 
	 * <b>Note:</b>The code for the movement partly stems from the <a href="https://github.com/eclab/mason/">Mason repository on GitHub</a> repository. 
	 * More specifically from a package called <a href="https://github.com/eclab/mason/tree/master/contrib/geomason/sim/app/geo/gridlock">"GridLock"</a>. 
	 * It has been adapted to fit this simulation's purpose. </b> 
	 *
	 */
	
	 /**
	 * <p>This method initializes the path to the next activity location (resp. the corresponding node).
	 * The <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a> is used to find the path.
	 * Afterwards the path is setup such that the individual can travel on it to its target node.</p>
	 * 
	 * @param currentNode - the node on which the individual currently is located.
	 * @param targetNode - the node to which the individual has to travel in order to be able to execute the next activity.
	 */
	private void initPathToTarget(Node currentNode, Node targetNode) {
		m_pathToNextTarget.clear();
		if (currentNode == null || targetNode == null) {
			LOG.log(Level.WARNING, String.format("Can not initialize path to target building. Got values currentNode=%s; targetNode=%s.", String.valueOf(currentNode), String.valueOf(targetNode)));
		}
		ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget = GraphUtility.astarPath(currentNode, targetNode);
		if (!pathToTarget.isEmpty()) {
			m_pathToNextTarget = pathToTarget;
			m_currentEdge = (GeomPlanarGraphEdge) pathToTarget.get(0).getEdge();
			setupEdgeOfPath(0);
			updatePosition(m_lengthIndexedLineOfEdge.extractPoint(m_currentIndexOnLineOfEdge));
			if (ISimulationSettings.IS_DEBUG) {
				colorPathToTarget();
			}
		} 
		else { // already at the target location
			// nop
		}
	}
	
	/**
	 * <p>Sets up the edge on which the individual continues to its target location based on {@link Individual#m_pathToNextTarget}.</p>
	 */
	private void setupEdgeOfPath(int indexOfEdgeToSetUp) {
		GeomPlanarGraphEdge edge = (GeomPlanarGraphEdge) m_pathToNextTarget.get(indexOfEdgeToSetUp).getEdge();
//		updateEdgeTraffic(nextEdge);
		m_currentEdge = edge;
		m_lengthIndexedLineOfEdge = new LengthIndexedLine(m_currentEdge.getLine());
		m_startIndexOfCurrentEdge = m_lengthIndexedLineOfEdge.getStartIndex();
		m_endIndexOfCurrentEdge = m_lengthIndexedLineOfEdge.getEndIndex();
		double distanceToStart = m_currentEdge.getLine().getStartPoint().distance(m_currentLocationPoint.getGeometry());
		double distanceToEnd = m_currentEdge.getLine().getEndPoint().distance(m_currentLocationPoint.getGeometry());
		if (distanceToStart <= distanceToEnd) {
			m_currentIndexOnLineOfEdge = m_lengthIndexedLineOfEdge.getStartIndex();
			m_edgeDirection = 1;
		} else {
			m_currentIndexOnLineOfEdge = m_lengthIndexedLineOfEdge.getEndIndex();
			m_edgeDirection = -1;
		}
	}
	
	/**
	 * <p>This method can be used to keep track on how many individuals are currently travelling on a given edge. 
	 * <br><b>Note:</b> It is currently not used but provides a basic mechanism to incorporate a traffic in a later stage of simulation development.</p>
	 * 
	 * @param nextEdge - the next edge on the individual's path to its target.
	 */
	@SuppressWarnings("unused")
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
	 * <p>Update the position of this individual by moving it to to the provided coordinate.</p>
	 * 
	 * @param targetCoordinate - The coordinate to which the individual is moved to.
	 */
	private void updatePosition(Coordinate targetCoordinate) {
		Coordinate copy = new Coordinate(targetCoordinate);
		m_pointMoveTo.setCoordinate(copy);
		m_environment.getIndividualsField().setGeometryLocation(m_currentLocationPoint, m_pointMoveTo);
		m_environment.getIndividualsField().updateSpatialIndex();
	}
	
	/**
	 * <p>This method check if the individual as reached its target.</p>
	 * 
	 * @return boolean - true if it reached the target, false otherwise.
	 */
	public boolean hasReachedTarget() {
		if (m_pathToNextTarget.isEmpty()) { // current location is target location
			return true;
		}
		// positive movement -> at end of edge and on last edge of path
		else if (m_edgeDirection == 1 && m_currentIndexOnLineOfEdge >= m_endIndexOfCurrentEdge && m_currentIndexOnPathToNextTarget >= m_pathToNextTarget.size()) {
				return true;
			}
		// negative movement -> at start of edge and on last edge of path 
		else if (m_edgeDirection == -1 && m_currentIndexOnLineOfEdge <= m_startIndexOfCurrentEdge && m_currentIndexOnPathToNextTarget >= m_pathToNextTarget.size()) {
			return true;
		}
		// not at target yet
		else {
			return false;
		}
	}
	
	/**
	 * <p>This method colors the path (but not exactly) of an agent to its next target. 
	 * Its can be used for visual debugging.</p>
	 */
	public void colorPathToTarget() {
		GeomVectorField pathField = m_environment.getPathField();
		List<Coordinate> coordinatesOfPath = getPathToNextTarget()
				.stream()
				.map(path -> path.getCoordinate())
				.collect(Collectors.toList());
		for (Coordinate coordinate : coordinatesOfPath) {
			ArrayList<MasonGeometry> coveringObjects = GeometryUtility.getCoveringObjects(new MasonGeometry(Environment.GEO_FACTORY.createPoint(coordinate)), pathField);
			coveringObjects.forEach(mg -> {
				mg.setUserData(DebugUtility.creatCircledPortrayal2DForPath());
			});
		}
	}
	
	/**
	 * <p>This method moves the individual towards its target. 
	 * It does so by first calculating the distance the individual can travel in the current step. 
	 * Then, depending on the direction the individual travels on the current edge, it delegates the movement on the edges to.
	 * Finally, it updates the individual's position.</p>
	 * 
	 */
	private void moveTowardsTarget() {
		double directedTravellingDistance = calculateDirectedTravellingDistance();
		while (directedTravellingDistance != 0) {
			double directedOverrun = calculateDirectedOverrun(directedTravellingDistance);
			if (!hasReachedLastEdge() && (m_edgeDirection == POSITIVE_MOVEMENT && directedOverrun > 0 || m_edgeDirection == NEGATIVE_MOVEMENT && directedOverrun < 0)) {
				m_currentIndexOnPathToNextTarget++;
				if (!hasReachedLastEdge()) {
					setupEdgeOfPath(m_currentIndexOnPathToNextTarget); // updates edge direction
				}
				if (m_edgeDirection == POSITIVE_MOVEMENT) {
					directedTravellingDistance = Math.abs(directedOverrun);
				}
				else {
					directedTravellingDistance = -Math.abs(directedOverrun);
				}
			} 
			else if (hasReachedLastEdge() && (m_edgeDirection == POSITIVE_MOVEMENT && directedOverrun > 0 || m_edgeDirection == NEGATIVE_MOVEMENT && directedOverrun < 0)) {
				directedTravellingDistance = 0;
				if (m_edgeDirection == -1) {
					m_currentIndexOnLineOfEdge = m_lengthIndexedLineOfEdge.getStartIndex();
				}
				else {
					m_currentIndexOnLineOfEdge = m_lengthIndexedLineOfEdge.getEndIndex();
				}
				m_currentIndexOnPathToNextTarget = 0;
				m_pathToNextTarget.clear();
			}
			else {
				m_currentIndexOnLineOfEdge += directedTravellingDistance;
				directedTravellingDistance = 0;
			}
			updatePosition(m_lengthIndexedLineOfEdge.extractPoint(m_currentIndexOnLineOfEdge));
		}
	}
	
	private double calculateDirectedOverrun(double directedTravellingDistance) {
		if (m_edgeDirection == POSITIVE_MOVEMENT) {
			return m_currentIndexOnLineOfEdge + directedTravellingDistance - m_endIndexOfCurrentEdge;
		}
		else {
			return m_currentIndexOnLineOfEdge + directedTravellingDistance - m_startIndexOfCurrentEdge;
		}
	}
	
	/**
	 * <p>This method calculates the travel distance. 
	 * Currently individuals travel always at max velocity. 
	 * Once traffic is incorporated into the simulation, this method can be adapted such that it calculates the distance based on traffic (i.e. number of individuals) of the current edge.</p> 
	 * 
	 * @return the travelling distance for this step
	 */
	private double calculateDirectedTravellingDistance() {
		return m_edgeDirection * ISimulationSettings.MAX_VELOCITY;
	}
	
	private boolean hasReachedLastEdge() {
		return m_currentIndexOnPathToNextTarget >= m_pathToNextTarget.size() - 1;
	}
	
	/**
	 * @category Helper functions 
	 */
	
	/**
	 * <p>This method updates the actual need time split for the provided activity and for exactly one unit of time (i.e. one minute).
	 * Since it applied to only one unit of time the fraction for each need can be directly added to the actual need time split (i.e. no multiplication is necessary).</p>
	 * 
	 * @param activity - the activity the individual is currently executing.
	 */
	private void updateActualNeedTimeSplit(Activity activity) {
		for (Need needSatisfiedByRandomActivity: activity.getNeedTimeSplit().keySet()) {
			m_actualNeedTimeSplit.updateNeedTimeSplit(needSatisfiedByRandomActivity, activity.getFractionForNeed(needSatisfiedByRandomActivity));
		}
	}
	
	/**
	 * <p>This method updates the actual need time split for the provided activity and for the provided amount of time.</p>
	 * 
	 * @param need - the need on which time was spent.
	 * @param timeSpentSatisfyingNeed - the time spent satisfying this need.
	 */
	public void updateActualNeedTimeSplit(Need need, BigDecimal timeSpentSatisfyingNeed) {
		m_actualNeedTimeSplit.updateNeedTimeSplit(need, timeSpentSatisfyingNeed);
	}
	
	/**
	 * <p>This method resets daily variables to their initial values.<p>
	 */
	public void initNewDay() {
		m_numberOfHouseholdNetworkActivitiesPlanned = 0;
		m_numberOfWorkColleguesNetworkActivitiesPlanned = 0;
		m_numberOfFriendsNetworkActivitiesPlanned = 0;
		m_actualNeedTimeSplit.clear();
		m_activityAgenda.clear();
		m_jointActivityAgenda.clear();
		m_allDayPlans.clear();
	}
	
	private DateTime getCurrentDateTime() {
		return m_environment.getSimulationTime().getCurrentDateTime();
	}
	
	private int getCurrentDayOfWeek() {
		return m_environment.getSimulationTime().getCurrentWeekDay();
	}
	
	private List<Interval> getFutureIntervals(Individual individual) {
		return individual.getJointActivityAgenda().getAgenda().keySet().stream()
			.filter(interval -> (interval.getStart().isAfter(getCurrentDateTime())))
			.collect(Collectors.toList());
	}
	
	/**
	 * <p>This method retrieves all joint activities available in the provided interval and category.</p>
	 * 
	 * @param activityCategory - the category of the activities.
	 * @param intervalInBaseTime - the interval during which the activity must be available.
	 * @return ArrayList<Activity> - the available activities.
	 */
	private ArrayList<Activity> getJointActivitiesAvailable(ActivityCategory activityCategory, Interval intervalInBaseTime) {
		return m_environment.getAllActivities().values().stream()
				.filter(activity -> activity.isJointActivity())
				.filter(activity -> activity.getActivityCategory() == activityCategory)
				.filter(activity -> activity.isAvailableAt(getCurrentDayOfWeek(), intervalInBaseTime.getStart()))
				.filter(activity -> !(activity.getActivityLocation() == ActivityLocation.TRAVEL))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * <p>This method uses the random number generator of the {@link Environment} to sample an integer.
	 * The integer will be in the range <b>0 - <code>numberOfElements</code></b>.
	 * 
	 * @param numberOfElements - the number of elements, which usually refers to the size of a list.
	 * @return integer - the random number chosen.
	 */
	private int getRandomInt(int numberOfElements) {
		return m_environment.random.nextInt(numberOfElements);
	}
	
	/**
	 * @category Getter and setter
	 */
	
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
		return m_workColleaguesNetwork;
	}

	public void setWorkColleguesNetwork(Network workColleguesNetwork) {
		m_workColleaguesNetwork = workColleguesNetwork;
	}

	public int getWorkColleguesNetworkId() {
		return m_workColleaguesNetworkId;
	}

	public void setWorkColleguesNetworkId(int workColleguesNetworkId) {
		m_workColleaguesNetworkId = workColleguesNetworkId;
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

	public AbsoluteNeedTimeSplit getActualNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}

	public void setActualNeedTimeSplit(AbsoluteNeedTimeSplit actualNeedTimeSplit) {
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

	public ArrayList<Node> getOtherPlacesForHouseholdAndFamilyCareNodes() {
		return m_otherPlacesForHouseholdAndFamilyCareNodes;
	}

	public void setOtherPlacesForHouseholdAndFamilyCareNodes(ArrayList<Node> otherPlaceForHouseholdAndFamilyCareNodes) {
		m_otherPlacesForHouseholdAndFamilyCareNodes = otherPlaceForHouseholdAndFamilyCareNodes;
	}

	public Node getWorkPlaceNode() {
		return m_workPlaceNode;
	}

	public void setWorkPlaceNode(Node workPlaceNode) {
		m_workPlaceNode = workPlaceNode;
	}

	public ArrayList<Node> getOtherPlacesForWorkNodes() {
		return m_otherPlacesForWorkNodes;
	}

	public void setOtherPlacesForWorkNodes(ArrayList<Node> otherPlaceForWorkNodes) {
		m_otherPlacesForWorkNodes = otherPlaceForWorkNodes;
	}

	public Node getLeisureNode() {
		return m_leisureNode;
	}

	public void setLeisureNode(Node leisureNode) {
		m_leisureNode = leisureNode;
	}

	public ArrayList<Node> getOtherPlacesForLeisureNodes() {
		return m_otherPlacesForLeisureNodes;
	}

	public void setOtherPlacesForLeisureNodes(ArrayList<Node> otherPlaceForLeisureNodes) {
		m_otherPlacesForLeisureNodes = otherPlaceForLeisureNodes;
	}

	public MasonGeometry getCurrentLocationPoint() {
		return m_currentLocationPoint;
	}

	public void setCurrentLocationPoint(MasonGeometry currentLocationPoint) {
		m_currentLocationPoint = currentLocationPoint;
	}

	public LengthIndexedLine getSegment() {
		return m_lengthIndexedLineOfEdge;
	}

	public void setSegment(LengthIndexedLine segment) {
		m_lengthIndexedLineOfEdge = segment;
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

	public Activity getCurrentActivity() {
		return m_currentActivity;
	}
}
