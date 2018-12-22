package rfs0.aitam.activities;

import java.math.BigDecimal;

import rfs0.aitam.individuals.NetworkType;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;

/**
 * <p>This class is used to initialize all activities of the simulation. 
 * As such it contains one method to instantiate each of the activities mentioned in the <a href="activity_configuration.xlsx">configuration file</a>. 
 * Furthermore, it relies on the following builders:</p>
 * 
 * <p>{@link ActivityInitializer#ACTIVITY_BUILDER}: The builder used to build {@link Activity}s.</p>
 * <p>{@link ActivityInitializer#NEED_TIME_SPLIT_BUILDER}: The builder used to build {@link NeedTimeSplit}s.</p>
 */
public final class ActivityInitializer {
	
	/**
	 * <p>The builder used to build {@link Activity}s.</p>
	 */
	public static final Activity.Builder ACTIVITY_BUILDER = new Activity.Builder();
	/**
	 * <p>The builder used to build {@link NeedTimeSplit}s.</p>
	 */
	public static final NeedTimeSplit.Builder NEED_TIME_SPLIT_BUILDER = new NeedTimeSplit.Builder();
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initLeisureAtHomeAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription(ISimulationSettings.LEISURE_AT_HOME_ALONE_ACTIVITY)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Sports and recreation, hobbies, reading, TV viewing, phone calls")
				.withAvailabilityIntervalAtDays(6,0,23,0, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initLeisureAtHomeWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initLeisureAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription(ISimulationSettings.LEISURE_AT_HOME_WITH_HOUSEHOLD_MEMBERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Sports and recreation, hobbies, reading, TV viewing")
				.withAvailabilityIntervalAtDays(6,0,23,0, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initLeisureAtHomeWithFriendsActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initLeisureAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription(ISimulationSettings.LEISURE_AT_HOME_WITH_FRIENDS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Sports, TV, reading, video games")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initLeisureAtThirdPlaceForLeisureAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription(ISimulationSettings.LEISURE_AT_THIRD_PLACE_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Sports and recreation, culture and amusement events, hobbies, shopping, having meals")
				.withAvailabilityIntervalAtDays(6,0,23,0, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initLeisureAtThirdPlaceForLeisureWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initLeisureAtThirdPlaceForLeisureAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription(ISimulationSettings.LEISURE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Sports and recreation, culture and amusement events, hobbies, shopping, having meals")
				.withAvailabilityIntervalAtDays(6,0,23,0, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initLeisureAtThirdPlaceForLeisureWithFriendsActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initLeisureAtThirdPlaceForLeisureAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription(ISimulationSettings.LEISURE_AT_THIRD_PLACE_WITH_FRIENDS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Sports and recreation, culture and amusement events, hobbies, shopping, having meals")
				.withAvailabilityIntervalAtDays(6,0,23,0, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initWorkAtHomeAloneActivity() {
		return ACTIVITY_BUILDER
		.withActivityCategory(ActivityCategory.WORK)
		.withActivityDescription(ISimulationSettings.WORK_AT_HOME_ALONE)
		.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
				.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
				.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
				.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
				.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
				.build())
		.withExamples("Paid work")
		.withAvailabilityIntervalAtDays(7, 0, 18, 0, ISimulationSettings.WORK_WEEK)
		.withIsJointActivity(false)
		.withActivityLocation(ActivityLocation.HOME)
		.withNetworkType(NetworkType.NONE)
		.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initWorkAtWorkPlaceAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription(ISimulationSettings.WORK_AT_WORK_PLACE_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(7, 0, 18, 0, ISimulationSettings.WORK_WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.WORK)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initWorkAtWorkPlaceWithCoworkers() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initWorkAtWorkPlaceAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription(ISimulationSettings.WORK_AT_WORK_PLACE_WITH_COWORKERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(7, 0, 18, 0, ISimulationSettings.WORK_WEEK)
				.withActivityLocation(ActivityLocation.WORK)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initWorkAtThirdPlaceForWorkAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription(ISimulationSettings.WORK_AT_THIRD_PLACE_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(7, 0, 18, 0, ISimulationSettings.WORK_WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_WORK)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initWorkAtThirdPlaceForWorkWithCoworkers() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initWorkAtThirdPlaceForWorkAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription(ISimulationSettings.WORK_AT_THIRD_PLACE_WITH_COWORKERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(7, 0, 18, 0, ISimulationSettings.WORK_WEEK)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_WORK)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtHomeAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_HOME_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.build())
				.withExamples("Personal hygiene, dressing up, sports")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtHomeWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initPersonalCareAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Cooking, having meals, talking, cleaning")
				.withAvailabilityIntervalAtDays(6,0,21,0, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtHomeWithFriendsActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initPersonalCareAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_HOME_WITH_FRIENDS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Cooking, having meals, talking")
				.withAvailabilityIntervalAtDays(8,0,21,0, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtWorkPlaceAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_WORK_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.build())
				.withExamples("Having meals")
				.withAvailabilityIntervalAtDays(7,0,18,0, ISimulationSettings.WORK_WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.WORK)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtWorkPlaceWithCoworkersActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initPersonalCareAtWorkPlaceAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_WORK_WITH_COWORKERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Having meals")
				.withAvailabilityIntervalAtDays(7,0,18,0, ISimulationSettings.WORK_WEEK)
				.withActivityLocation(ActivityLocation.WORK)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtThirdPlaceForPersonalCareAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.build())
				.withExamples("Sports")
				.withAvailabilityIntervalAtDays(8,0,20,0, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initPersonalCareAtThirdPlaceForPersonalCareAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Sports")
				.withAvailabilityIntervalAtDays(8,0,20,0, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initPersonalCareAtThirdPlaceForPersonalCareAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription(ISimulationSettings.PERSONAL_CARE_AT_THIRD_PLACE_WITH_FRIENDS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Sports")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initHouseholdAndFamilyCareAtHomeAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE)
				.withActivityDescription(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.SEVEN))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.SEVEN))
						.build())
				.withExamples("Housework, Shopping, family business, services and civic matters")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initHouseholdAndFamilyCareAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE)
				.withActivityDescription(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.ELEVEN))
						.build())
				.withExamples("Housework, Shopping, family business, services and civic matters")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}

	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE)
				.withActivityDescription(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_ALONE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.SEVEN))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.SEVEN))
						.build())
				.withExamples("Shopping, family business, services and civic matters")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembers() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE)
				.withActivityDescription(ISimulationSettings.HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.ELEVEN))
						.build())
				.withExamples("Shopping, family business, services and civic matters")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initIdleAtHomeActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_HOME)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NONE, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at home")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initIdleAtWorkActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_WORK)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NONE, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at work")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.WORK)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initIdleAtLeisureActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_LEISURE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NONE, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at leisure")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.LEISURE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initIdleAtThirdPlaceForHouseholdAndFamilyCareActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NONE, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at 3rd place for household and family care")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initIdleAtThirdPlaceForWorkActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_WORK)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NONE, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at 3rd place for work")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_WORK)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initIdleAtThirdPlaceForLeisureActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_LEISURE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NONE, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at 3rd place for leisure")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initTravelActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.TRAVEL)
				.withActivityDescription(ISimulationSettings.TRAVEL)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NONE, BigDecimal.ONE)
						.build())
				.withExamples("Travel with any mode of transport")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.TRAVEL)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initSleepAtHomeActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.SLEEP_AND_REST)
				.withActivityDescription(ISimulationSettings.SLEEP_AT_HOME)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(CalculationUtility.SIX, BigDecimal.TEN))
						.withNeedTimeSplit(Need.PROTECTION, CalculationUtility.divide(CalculationUtility.TWO, BigDecimal.TEN))
						.withNeedTimeSplit(Need.IDLENESS, CalculationUtility.divide(CalculationUtility.TWO, BigDecimal.TEN))
						.build())
				.withExamples("Travel with any mode of transport")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.TRAVEL)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
}