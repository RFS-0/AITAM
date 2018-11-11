package rfs0.aitam.activities;

import java.math.BigDecimal;

import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.individuals.NetworkType;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.utilities.CalculationUtility;

/**
 * <p>This class is used to initialize all activities of the simulation. As such it contains one method to instantiate 
 * each of the activities mentioned in the <a href="activity_configuration.xlsx">configuration file</a>. Furtermore, it
 * relies on the following builders:</p>
 * 
 * <p>{@link ActivityInitializer#ACTIVITY_BUILDER}: The builder used to build {@link Activity}s.</p>
 * <p>{@link ActivityInitializer#NEED_TIME_SPLIT_BUILDER}: The builder used to build {@link NeedTimeSplit}s.</p>
 */
public final class ActivityInitializer {
	
	public static final Activity.Builder ACTIVITY_BUILDER = new Activity.Builder();
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
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Sports and recreation, hobbies, reading, TV viewing, phone calls")
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
	public Activity initLeisureAtHomeWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initLeisureAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription("Leisure at home with household members")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Sports and recreation, hobbies, reading, TV viewing")
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
	public Activity initLeisureAtHomeWithFriendsActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initLeisureAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription("Leisure at home with friends")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
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
				.withActivityDescription("Leisure at 3rd place alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Sports and recreation, culture and amusement events, hobbies, shopping, having meals")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
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
				.withActivityDescription("Leisure at 3rd place with household members")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Sports and recreation, culture and amusement events, hobbies, shopping, having meals")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
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
				.withActivityDescription("Leisure at 3rd place with friends")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Sports and recreation, culture and amusement events, hobbies, shopping, having meals")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
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
		.withActivityDescription("Work at home alone")
		.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
				.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
				.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
				.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
				.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
				.build())
		.withExamples("Paid work")
		.withAvailabilityIntervalAtDays(8, 0, 16, 0, ISimulationSettings.WORK_WEEK)
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
				.withActivityDescription("Work at work location alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(8, 0, 16, 0, ISimulationSettings.WORK_WEEK)
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
				.withAlternativeActivity(initWorkAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription("Work at work location with coworkers")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(8, 0, 16, 0, ISimulationSettings.WORK_WEEK)
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
				.withActivityDescription("Work at 3rd place for work alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(8, 0, 16, 0, ISimulationSettings.WORK_WEEK)
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
				.withActivityDescription("Work at 3rd place for work with coworkers")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(8, 0, 16, 0, ISimulationSettings.WORK_WEEK)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_WORK)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	/**
	 * Initializes the activity.
	 * 
	 * @return - the initialized activity
	 */
	public Activity initWorkDuringTravelAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription("Work during travel alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(8, 0, 16, 0, ISimulationSettings.WORK_WEEK)
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
	public Activity initWorkDuringTravelWithCoworkers() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initWorkDuringTravelAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription("Work during travel with coworkers")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
						.build())
				.withExamples("Paid work")
				.withAvailabilityIntervalAtDays(8, 0, 16, 0, ISimulationSettings.WORK_WEEK)
				.withActivityLocation(ActivityLocation.TRAVEL)
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
				.withActivityDescription("Personal care at home alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FIVE))
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
				.withActivityDescription("Personal care at home with household members")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Cooking, having meals, talking, cleaning")
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
	public Activity initPersonalCareAtHomeWithFriendsActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initPersonalCareAtHomeAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription("Personal care at home with friends")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Cooking, having meals, talking")
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
	public Activity initPersonalCareAtWorkPlaceAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription("Personal care at work alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.build())
				.withExamples("Having meals")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
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
				.withActivityDescription("Personal care at work with coworkers")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Having meals")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
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
				.withActivityDescription("Personal care at 3rd place alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FIVE))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE))
						.build())
				.withExamples("Sports")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
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
				.withActivityDescription("Personal care at 3rd place with household members")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.build())
				.withExamples("Sports")
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
	public Activity initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity() {
		return ACTIVITY_BUILDER
				.withAlternativeActivity(initPersonalCareAtThirdPlaceForPersonalCareAloneActivity())
				.withIsJointActivity(true)
				.withActivityCategory(ActivityCategory.PERSONAL_CARE)
				.withActivityDescription("Personal care at 3rd place with friends")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT))
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
				.withActivityDescription("Household/family care at home alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.SEVEN))
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
				.withActivityDescription("Household/family care at home with household members")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.ELEVEN))
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
				.withActivityDescription("Household/family care at 3rd place alone")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.SEVEN))
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
				.withActivityDescription("Household/family care at 3rd place with household members")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.FOUR, CalculationUtility.ELEVEN))
						.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.ELEVEN))
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
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
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
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
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
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
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
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
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
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
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
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
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
				.withActivityDescription("Travel")
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
						.build())
				.withExamples("Travel with any mode of transport")
				.withAvailabilityIntervalAtDays(0,0,23,59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.TRAVEL)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
}