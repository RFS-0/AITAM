package rfs0.aitam.activities;

import java.math.BigDecimal;

import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.individuals.NetworkType;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.utilities.CalculationUtility;

public final class ActivityInitializer {
	
	public static final Activity.Builder ACTIVITY_BUILDER = new Activity.Builder();
	public static final NeedTimeSplit.Builder NEED_TIME_SPLIT_BUILDER = new NeedTimeSplit.Builder();
	
	public static Activity initLeisureAtHomeAloneActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.LEISURE)
				.withActivityDescription("Leisure at home alone")
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
	
	public static Activity initLeisureAtHomeWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	public static Activity initLeisureAtHomeWithFriendsActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	public static Activity initLeisureAtThirdPlaceForLeisureAloneActivity() {
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
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	public static Activity initLeisureAtThirdPlaceForLeisureWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	public static Activity initLeisureAtThirdPlaceForLeisureWithFriendsActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	public static Activity initWorkAtHomeAloneActivity() {
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
	
	public static Activity initWorkAtWorkPlaceAloneActivity() {
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
	
	public static Activity initWorkAtWorkPlaceWithCoworkers() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.WORK)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	public static Activity initWorkAtThirdPlaceForWorkAloneActivity() {
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
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_WORK)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	public static Activity initWortAtThirdPlaceForWorkWithCoworkers() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_WORK)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	public static Activity initWorkDuringTravelAloneActivity() {
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
	
	public static Activity initWorkDuringTravelWithCoworkers() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.TRAVEL)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	public static Activity initPersonalCareAtHomeAloneActivity() {
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
	
	public static Activity initPersonalCareAtHomeWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	public static Activity initPersonalCareAtHomeWithFriendsActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	public static Activity initPersonalCareAtWorkPlaceAloneActivity() {
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
	
	public static Activity initPersonalCareAtWorkPlaceWithCoworkersActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.WORK)
				.withNetworkType(NetworkType.WORK_COLLEGUES_NETWORK)
				.build();
	}
	
	public static Activity initPersonalCareAtThirdPlaceForPersonalCareAloneActivity() {
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
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	public static Activity initPersonalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	public static Activity initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.FRIENDS_NETWORK)
				.build();
	}
	
	public static Activity initHouseholdAndFamilyCareAtHomeAloneActivity() {
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
	
	public static Activity initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.HOME)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}

	public static Activity initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity() {
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	public static Activity initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembers() {
		return ACTIVITY_BUILDER
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
				.withIsJointActivity(true)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.HOUSEHOLD_NETWORK)
				.build();
	}
	
	public static Activity initIdleAtHomeActivity() {
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
	
	public static Activity initIdleAtWorkActivity() {
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
	
	public static Activity initIdleAtLeisureActivity() {
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
	
	public static Activity initIdleAtThirdPlaceForHouseholdAndFamilyCareActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at 3rd place for household and family care")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	public static Activity initIdleAtThirdPlaceForWorkActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_WORK)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at 3rd place for work")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_WORK)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	public static Activity initIdleAtThirdPlaceForLeisureActivity() {
		return ACTIVITY_BUILDER
				.withActivityCategory(ActivityCategory.IDLE)
				.withActivityDescription(ISimulationSettings.IDLE_AT_THIRD_PLACE_FOR_LEISURE)
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
						.withNeedTimeSplit(Need.NOT_DEFINED, BigDecimal.ONE)
						.build())
				.withExamples("Doing nothing at 3rd place for leisure")
				.withAvailabilityIntervalAtDays(0, 0, 23, 59, ISimulationSettings.WEEK)
				.withIsJointActivity(false)
				.withActivityLocation(ActivityLocation.THIRD_PLACE_FOR_LEISURE)
				.withNetworkType(NetworkType.NONE)
				.build();
	}
	
	public static Activity initTravelActivity() {
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