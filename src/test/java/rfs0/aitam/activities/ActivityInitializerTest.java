package rfs0.aitam.activities;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Test;

import activities.Activity;
import activities.ActivityCategory;
import activities.ActivityInitializer;
import activities.ActivityLocation;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.utilities.CalculationUtility;

public class ActivityInitializerTest {
	
	@Test
	public void testInitWorkAtHomeAloneActivity() {
		Activity workAtHomeAloneActivity = ActivityInitializer.initWorkAtHomeAloneActivity();
		assertEquals(ActivityCategory.WORK, workAtHomeAloneActivity.getActivityCategory());
		assertEquals("Work at home alone", workAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX), workAtHomeAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, workAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, workAtHomeAloneActivity.isJointActivity());
		assertEquals(workAtHomeAloneActivity.getActivityLocation(), ActivityLocation.HOME);
	}
	
	@Test
	public void testInitWorkAtWorkLocationAloneActivity() {
		Activity workAtWorkLocationAloneActivity = ActivityInitializer.initWorkAtWorkPlaceAloneActivity();
		assertEquals(ActivityCategory.WORK, workAtWorkLocationAloneActivity.getActivityCategory());
		assertEquals("Work at work location alone", workAtWorkLocationAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX), workAtWorkLocationAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workAtWorkLocationAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, workAtWorkLocationAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, workAtWorkLocationAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.WORK, workAtWorkLocationAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitWorkAtWorkLocationWithCoworkers() {
		Activity workAtWorkLocationWithCoworkers = ActivityInitializer.initWorkAtWorkPlaceWithCoworkers();
		assertEquals(ActivityCategory.WORK, workAtWorkLocationWithCoworkers.getActivityCategory());
		assertEquals("Work at work location with coworkers", workAtWorkLocationWithCoworkers.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE), workAtWorkLocationWithCoworkers.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workAtWorkLocationWithCoworkers.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, workAtWorkLocationWithCoworkers.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, workAtWorkLocationWithCoworkers.isJointActivity());
		assertEquals(ActivityLocation.WORK, workAtWorkLocationWithCoworkers.getActivityLocation());
	}
	
	@Test
	public void testInitWorkDuringTravelAloneActivity() {
		Activity workDuringTravelAloneActivity = ActivityInitializer.initWorkDuringTravelAloneActivity();
		assertEquals(ActivityCategory.WORK, workDuringTravelAloneActivity.getActivityCategory());
		assertEquals("Work during travel alone", workDuringTravelAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX), workDuringTravelAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workDuringTravelAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, workDuringTravelAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, workDuringTravelAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.TRAVEL, workDuringTravelAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitWorkDuringTravelWithCoworkersinitWorkDuringTravelWithCoworkers() {
		Activity workDuringTravelWithCoworkers = ActivityInitializer.initWorkDuringTravelWithCoworkers();
		assertEquals(ActivityCategory.WORK, workDuringTravelWithCoworkers.getActivityCategory());
		assertEquals("Work during travel with coworkers", workDuringTravelWithCoworkers.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE), workDuringTravelWithCoworkers.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workDuringTravelWithCoworkers.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, workDuringTravelWithCoworkers.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, workDuringTravelWithCoworkers.isJointActivity());
		assertEquals(ActivityLocation.TRAVEL, workDuringTravelWithCoworkers.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtHomeAloneActivity() {
		Activity leisureAtHomeAloneActivity = ActivityInitializer.initLeisureAtHomeAloneActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtHomeAloneActivity.getActivityCategory());
		assertEquals("Leisure at home alone", leisureAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT), leisureAtHomeAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, hobbies, reading, TV viewing, phone calls", leisureAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, leisureAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, leisureAtHomeAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, leisureAtHomeAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtHomeWithHouseholdMembersActivity() {
		Activity leisureAtHomeWithHouseholdMembersActivity = ActivityInitializer.initLeisureAtHomeWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtHomeWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Leisure at home with household member(s)", leisureAtHomeWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), leisureAtHomeWithHouseholdMembersActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, hobbies, reading, TV viewing", leisureAtHomeWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, leisureAtHomeWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, leisureAtHomeWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, leisureAtHomeWithHouseholdMembersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtHomeWithFriendsActivity() {
		Activity leisureAtHomeWithFriendsActivity = ActivityInitializer.initLeisureAtHomeWithFriendsActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtHomeWithFriendsActivity.getActivityCategory());
		assertEquals("Leisure at home with friends", leisureAtHomeWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), leisureAtHomeWithFriendsActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports, TV, reading, video games", leisureAtHomeWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, leisureAtHomeWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, leisureAtHomeWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, leisureAtHomeWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtThirdPlaceAloneActivity() {
		Activity leisureAtThirdPlaceAloneActivity = ActivityInitializer.initLeisureAtThirdPlaceForLeisureAloneActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtThirdPlaceAloneActivity.getActivityCategory());
		assertEquals("Leisure at 3rd place alone", leisureAtThirdPlaceAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT), leisureAtThirdPlaceAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, culture and amusement events, hobbies, shopping, having meals", leisureAtThirdPlaceAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, leisureAtThirdPlaceAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, leisureAtThirdPlaceAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_LEISURE, leisureAtThirdPlaceAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtThirdPlaceWithHouseholdMembersActivity() {
		Activity leisureAtThirdPlaceWithHouseholdMembersActivity = ActivityInitializer.initLeisureAtThirdPlaceForLeisureWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtThirdPlaceWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Leisure at third place with household members", leisureAtThirdPlaceWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), leisureAtThirdPlaceWithHouseholdMembersActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, culture and amusement events, hobbies, shopping, having meals", leisureAtThirdPlaceWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, leisureAtThirdPlaceWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, leisureAtThirdPlaceWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_LEISURE, leisureAtThirdPlaceWithHouseholdMembersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtThirdPlaceWithFriendsActivity() {
		Activity initLeisureAtThirdPlaceWithFriendsActivity = ActivityInitializer.initLeisureAtThirdPlaceForLeisureWithFriendsActivity();
		assertEquals(ActivityCategory.LEISURE, initLeisureAtThirdPlaceWithFriendsActivity.getActivityCategory());
		assertEquals("Leisure at third place with friends", initLeisureAtThirdPlaceWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), initLeisureAtThirdPlaceWithFriendsActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, culture and amusement events, hobbies, shopping, having meals", initLeisureAtThirdPlaceWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initLeisureAtThirdPlaceWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initLeisureAtThirdPlaceWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_LEISURE, initLeisureAtThirdPlaceWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtHomeAloneActivity() {
		Activity initPersonalCareAtHomeAloneActivity = ActivityInitializer.initPersonalCareAtHomeAloneActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtHomeAloneActivity.getActivityCategory());
		assertEquals("Personal care at home alone", initPersonalCareAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE), initPersonalCareAtHomeAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Personal hygiene, dressing up, sports", initPersonalCareAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, initPersonalCareAtHomeAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initPersonalCareAtHomeAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtHomeWithHouseholdMembersActivity() {
		Activity initPersonalCareAtHomeWithHouseholdMembersActivity = ActivityInitializer.initPersonalCareAtHomeWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtHomeWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Personal care at home with household member(s)", initPersonalCareAtHomeWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtHomeWithHouseholdMembersActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Cooking, having meals, talking, cleaning", initPersonalCareAtHomeWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtHomeWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initPersonalCareAtHomeWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initPersonalCareAtHomeWithHouseholdMembersActivity.getActivityLocation());
	}

	@Test
	public void testInitPersonalCareAtHomeWithFriendsActivity() {
		Activity initPersonalCareAtHomeWithFriendsActivity = ActivityInitializer.initPersonalCareAtHomeWithFriendsActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtHomeWithFriendsActivity.getActivityCategory());
		assertEquals("Personal care at home with friend(s)", initPersonalCareAtHomeWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtHomeWithFriendsActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Cooking, having meals, talking", initPersonalCareAtHomeWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtHomeWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initPersonalCareAtHomeWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initPersonalCareAtHomeWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtWorkAloneActivity() {
		Activity initPersonalCareAtWorkAloneActivity = ActivityInitializer.initPersonalCareAtWorkPlaceAloneActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtWorkAloneActivity.getActivityCategory());
		assertEquals("Personal care at work alone", initPersonalCareAtWorkAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE), initPersonalCareAtWorkAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Having meals", initPersonalCareAtWorkAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtWorkAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, initPersonalCareAtWorkAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.WORK, initPersonalCareAtWorkAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtWorkWithCoworkersActivity() {
		Activity initPersonalCareAtWorkWithCoworkersActivity = ActivityInitializer.initPersonalCareAtWorkPlaceWithCoworkersActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtWorkWithCoworkersActivity.getActivityCategory());
		assertEquals("Personal care at work with coworkers", initPersonalCareAtWorkWithCoworkersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtWorkWithCoworkersActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Having meals", initPersonalCareAtWorkWithCoworkersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtWorkWithCoworkersActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initPersonalCareAtWorkWithCoworkersActivity.isJointActivity());
		assertEquals(ActivityLocation.WORK, initPersonalCareAtWorkWithCoworkersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtThirdPlaceAloneActivity() {
		Activity initPersonalCareAtThirdPlaceAloneActivity = ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareAloneActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtThirdPlaceAloneActivity.getActivityCategory());
		assertEquals("Personal care at 3rd place alone", initPersonalCareAtThirdPlaceAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE), initPersonalCareAtThirdPlaceAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports", initPersonalCareAtThirdPlaceAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtThirdPlaceAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, initPersonalCareAtThirdPlaceAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initPersonalCareAtThirdPlaceAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtThirdPlaceWithHouseholdMembersActivity() {
		Activity initPersonalCareAtThirdPlaceWithHouseholdMembersActivity = ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Personal care at 3rd place with household member(s)", initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports", initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtThirdPlaceWithFriendsActivity() {
		Activity initPersonalCareAtThirdPlaceWithFriendsActivity = ActivityInitializer.initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtThirdPlaceWithFriendsActivity.getActivityCategory());
		assertEquals("Personal care at 3rd place with friend(s)", initPersonalCareAtThirdPlaceWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtThirdPlaceWithFriendsActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports", initPersonalCareAtThirdPlaceWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initPersonalCareAtThirdPlaceWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initPersonalCareAtThirdPlaceWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initPersonalCareAtThirdPlaceWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtHomeAloneActivity() {
		Activity initHouseholdAndFamilyCareAtHomeAloneActivity = ActivityInitializer.initHouseholdAndFamilyCareAtHomeAloneActivity();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtHomeAloneActivity.getActivityCategory());
		assertEquals("Household/family care at home alone", initHouseholdAndFamilyCareAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN), initHouseholdAndFamilyCareAtHomeAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Housework, Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initHouseholdAndFamilyCareAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, initHouseholdAndFamilyCareAtHomeAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initHouseholdAndFamilyCareAtHomeAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty() {
		Activity initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty = ActivityInitializer.initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getActivityCategory());
		assertEquals("Household/family care at home with household member(s)", initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN), initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Housework, Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.isJointActivity());
		assertEquals(ActivityLocation.HOME, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtThirdPlaceAloneActivity() {
		Activity initHouseholdAndFamilyCareAtThirdPlaceAloneActivity = ActivityInitializer.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getActivityCategory());
		assertEquals("Household/family care at 3rd place alone", initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN), initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers() {
		Activity initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers = ActivityInitializer.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembers();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getActivityCategory());
		assertEquals("Household/family care at 3rd place with household member(s)", initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN), initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(true, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.isJointActivity());
		assertEquals(ActivityLocation.THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getActivityLocation());
	}
	
	@Test
	public void testInitTravelActivity() {
		Activity initTravelActivity = ActivityInitializer.initTravelActivity();
		assertEquals(ActivityCategory.TRAVEL, initTravelActivity.getActivityCategory());
		assertEquals("Travel", initTravelActivity.getActivityDescription());
		assertEquals(BigDecimal.ONE, initTravelActivity.getNeedTimeSplit().getFractionForNeed(Need.NOT_DEFINED));
		assertEquals("Travel with any mode of transport", initTravelActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0);
		Interval available = new Interval(availableStartTime, availableEndTime);
		assertEquals(true, initTravelActivity.isAvailableAt(DateTimeConstants.MONDAY, available));
		assertEquals(false, initTravelActivity.isJointActivity());
		assertEquals(ActivityLocation.TRAVEL, initTravelActivity.getActivityLocation());
	}
}
