package rfs0.aitam.activity;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import rfs0.aitam.activity.Activity;
import rfs0.aitam.activity.ActivityCategory;
import rfs0.aitam.activity.ActivityInitializer;
import rfs0.aitam.activity.ActivityLocation;
import rfs0.aitam.need.Need;
import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;

public class ActivityInitializerTest {
	
	public static final ActivityInitializer ACTIVITY_INITIALIZER = new ActivityInitializer();
	
	@Test
	public void testInitWorkAtHomeAloneActivity() {
		Activity workAtHomeAloneActivity = ACTIVITY_INITIALIZER.initWorkAtHomeAloneActivity();
		assertEquals(ActivityCategory.WORK, workAtHomeAloneActivity.getActivityCategory());
		assertEquals("Work at home alone", workAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX), workAtHomeAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, workAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, workAtHomeAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, workAtHomeAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitWorkAtWorkLocationAloneActivity() {
		Activity workAtWorkLocationAloneActivity = ACTIVITY_INITIALIZER.initWorkAtWorkPlaceAloneActivity();
		assertEquals(ActivityCategory.WORK, workAtWorkLocationAloneActivity.getActivityCategory());
		assertEquals("Work at work location alone", workAtWorkLocationAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SIX), workAtWorkLocationAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workAtWorkLocationAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, workAtWorkLocationAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, workAtWorkLocationAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.WORK, workAtWorkLocationAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitWorkAtWorkLocationWithCoworkers() {
		Activity workAtWorkLocationWithCoworkers = ACTIVITY_INITIALIZER.initWorkAtWorkPlaceWithCoworkers();
		assertEquals(ActivityCategory.WORK, workAtWorkLocationWithCoworkers.getActivityCategory());
		assertEquals("Work at work location with coworkers", workAtWorkLocationWithCoworkers.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE), workAtWorkLocationWithCoworkers.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Paid work", workAtWorkLocationWithCoworkers.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, workAtWorkLocationWithCoworkers.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, workAtWorkLocationWithCoworkers.isJointActivity());
		assertEquals(ActivityLocation.WORK, workAtWorkLocationWithCoworkers.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtHomeAloneActivity() {
		Activity leisureAtHomeAloneActivity = ACTIVITY_INITIALIZER.initLeisureAtHomeAloneActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtHomeAloneActivity.getActivityCategory());
		assertEquals("Leisure at home alone", leisureAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT), leisureAtHomeAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, hobbies, reading, TV viewing, phone calls", leisureAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, leisureAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, leisureAtHomeAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, leisureAtHomeAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtHomeWithHouseholdMembersActivity() {
		Activity leisureAtHomeWithHouseholdMembersActivity = ACTIVITY_INITIALIZER.initLeisureAtHomeWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtHomeWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Leisure at home with household members", leisureAtHomeWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), leisureAtHomeWithHouseholdMembersActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, hobbies, reading, TV viewing", leisureAtHomeWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, leisureAtHomeWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, leisureAtHomeWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, leisureAtHomeWithHouseholdMembersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtHomeWithFriendsActivity() {
		Activity leisureAtHomeWithFriendsActivity = ACTIVITY_INITIALIZER.initLeisureAtHomeWithFriendsActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtHomeWithFriendsActivity.getActivityCategory());
		assertEquals("Leisure at home with friends", leisureAtHomeWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), leisureAtHomeWithFriendsActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports, TV, reading, video games", leisureAtHomeWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, leisureAtHomeWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, leisureAtHomeWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, leisureAtHomeWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtThirdPlaceAloneActivity() {
		Activity leisureAtThirdPlaceAloneActivity = ACTIVITY_INITIALIZER.initLeisureAtThirdPlaceForLeisureAloneActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtThirdPlaceAloneActivity.getActivityCategory());
		assertEquals("Leisure at 3rd place alone", leisureAtThirdPlaceAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.EIGHT), leisureAtThirdPlaceAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, culture and amusement events, hobbies, shopping, having meals", leisureAtThirdPlaceAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, leisureAtThirdPlaceAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, leisureAtThirdPlaceAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_LEISURE, leisureAtThirdPlaceAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtThirdPlaceWithHouseholdMembersActivity() {
		Activity leisureAtThirdPlaceWithHouseholdMembersActivity = ACTIVITY_INITIALIZER.initLeisureAtThirdPlaceForLeisureWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.LEISURE, leisureAtThirdPlaceWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Leisure at 3rd place with household members", leisureAtThirdPlaceWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), leisureAtThirdPlaceWithHouseholdMembersActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, culture and amusement events, hobbies, shopping, having meals", leisureAtThirdPlaceWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, leisureAtThirdPlaceWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, leisureAtThirdPlaceWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_LEISURE, leisureAtThirdPlaceWithHouseholdMembersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitLeisureAtThirdPlaceWithFriendsActivity() {
		Activity initLeisureAtThirdPlaceWithFriendsActivity = ACTIVITY_INITIALIZER.initLeisureAtThirdPlaceForLeisureWithFriendsActivity();
		assertEquals(ActivityCategory.LEISURE, initLeisureAtThirdPlaceWithFriendsActivity.getActivityCategory());
		assertEquals("Leisure at 3rd place with friends", initLeisureAtThirdPlaceWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.TWELVE), initLeisureAtThirdPlaceWithFriendsActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports and recreation, culture and amusement events, hobbies, shopping, having meals", initLeisureAtThirdPlaceWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initLeisureAtThirdPlaceWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initLeisureAtThirdPlaceWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_LEISURE, initLeisureAtThirdPlaceWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtHomeAloneActivity() {
		Activity initPersonalCareAtHomeAloneActivity = ACTIVITY_INITIALIZER.initPersonalCareAtHomeAloneActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtHomeAloneActivity.getActivityCategory());
		assertEquals("Personal care at home alone", initPersonalCareAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE), initPersonalCareAtHomeAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Personal hygiene, dressing up, sports", initPersonalCareAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, initPersonalCareAtHomeAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initPersonalCareAtHomeAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtHomeWithHouseholdMembersActivity() {
		Activity initPersonalCareAtHomeWithHouseholdMembersActivity = ACTIVITY_INITIALIZER.initPersonalCareAtHomeWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtHomeWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Personal care at home with household members", initPersonalCareAtHomeWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtHomeWithHouseholdMembersActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Cooking, having meals, talking, cleaning", initPersonalCareAtHomeWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtHomeWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initPersonalCareAtHomeWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initPersonalCareAtHomeWithHouseholdMembersActivity.getActivityLocation());
	}

	@Test
	public void testInitPersonalCareAtHomeWithFriendsActivity() {
		Activity initPersonalCareAtHomeWithFriendsActivity = ACTIVITY_INITIALIZER.initPersonalCareAtHomeWithFriendsActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtHomeWithFriendsActivity.getActivityCategory());
		assertEquals("Personal care at home with friends", initPersonalCareAtHomeWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtHomeWithFriendsActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Cooking, having meals, talking", initPersonalCareAtHomeWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtHomeWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initPersonalCareAtHomeWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initPersonalCareAtHomeWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtWorkAloneActivity() {
		Activity initPersonalCareAtWorkAloneActivity = ACTIVITY_INITIALIZER.initPersonalCareAtWorkPlaceAloneActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtWorkAloneActivity.getActivityCategory());
		assertEquals("Personal care at work alone", initPersonalCareAtWorkAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE), initPersonalCareAtWorkAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Having meals", initPersonalCareAtWorkAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtWorkAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, initPersonalCareAtWorkAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.WORK, initPersonalCareAtWorkAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtWorkWithCoworkersActivity() {
		Activity initPersonalCareAtWorkWithCoworkersActivity = ACTIVITY_INITIALIZER.initPersonalCareAtWorkPlaceWithCoworkersActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtWorkWithCoworkersActivity.getActivityCategory());
		assertEquals("Personal care at work with coworkers", initPersonalCareAtWorkWithCoworkersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtWorkWithCoworkersActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Having meals", initPersonalCareAtWorkWithCoworkersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtWorkWithCoworkersActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initPersonalCareAtWorkWithCoworkersActivity.isJointActivity());
		assertEquals(ActivityLocation.WORK, initPersonalCareAtWorkWithCoworkersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtThirdPlaceAloneActivity() {
		Activity initPersonalCareAtThirdPlaceAloneActivity = ACTIVITY_INITIALIZER.initPersonalCareAtThirdPlaceForPersonalCareAloneActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtThirdPlaceAloneActivity.getActivityCategory());
		assertEquals("Personal care at 3rd place alone", initPersonalCareAtThirdPlaceAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.FIVE), initPersonalCareAtThirdPlaceAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports", initPersonalCareAtThirdPlaceAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtThirdPlaceAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, initPersonalCareAtThirdPlaceAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initPersonalCareAtThirdPlaceAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtThirdPlaceWithHouseholdMembersActivity() {
		Activity initPersonalCareAtThirdPlaceWithHouseholdMembersActivity = ACTIVITY_INITIALIZER.initPersonalCareAtThirdPlaceForPersonalCareWithHouseholdMembersActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getActivityCategory());
		assertEquals("Personal care at 3rd place with household members", initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports", initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initPersonalCareAtThirdPlaceWithHouseholdMembersActivity.getActivityLocation());
	}
	
	@Test
	public void testInitPersonalCareAtThirdPlaceWithFriendsActivity() {
		Activity initPersonalCareAtThirdPlaceWithFriendsActivity = ACTIVITY_INITIALIZER.initPersonalCareAtThirdPlaceForPersonalCareWithFriendsActivity();
		assertEquals(ActivityCategory.PERSONAL_CARE, initPersonalCareAtThirdPlaceWithFriendsActivity.getActivityCategory());
		assertEquals("Personal care at 3rd place with friends", initPersonalCareAtThirdPlaceWithFriendsActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.FOUR), initPersonalCareAtThirdPlaceWithFriendsActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Sports", initPersonalCareAtThirdPlaceWithFriendsActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initPersonalCareAtThirdPlaceWithFriendsActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initPersonalCareAtThirdPlaceWithFriendsActivity.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initPersonalCareAtThirdPlaceWithFriendsActivity.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtHomeAloneActivity() {
		Activity initHouseholdAndFamilyCareAtHomeAloneActivity = ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtHomeAloneActivity();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtHomeAloneActivity.getActivityCategory());
		assertEquals("Household/family care at home alone", initHouseholdAndFamilyCareAtHomeAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN), initHouseholdAndFamilyCareAtHomeAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Housework, Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtHomeAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initHouseholdAndFamilyCareAtHomeAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, initHouseholdAndFamilyCareAtHomeAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.HOME, initHouseholdAndFamilyCareAtHomeAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty() {
		Activity initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty = ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getActivityCategory());
		assertEquals("Household/family care at home with household members", initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN), initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Housework, Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.isJointActivity());
		assertEquals(ActivityLocation.HOME, initHouseholdAndFamilyCareAtHomeWithHousholdMembersActivty.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtThirdPlaceAloneActivity() {
		Activity initHouseholdAndFamilyCareAtThirdPlaceAloneActivity = ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareAloneActivity();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getActivityCategory());
		assertEquals("Household/family care at 3rd place alone", initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.SEVEN), initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceAloneActivity.getActivityLocation());
	}
	
	@Test
	public void testInitHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers() {
		Activity initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers = ACTIVITY_INITIALIZER.initHouseholdAndFamilyCareAtThirdPlaceForHouseholdAndFamilyCareWithHouseholdMembers();
		assertEquals(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getActivityCategory());
		assertEquals("Household/family care at 3rd place with household members", initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getActivityDescription());
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.ELEVEN), initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getFractionForNeed(Need.SUBSISTENCE));
		assertEquals("Shopping, family business, services and civic matters", initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.isJointActivity());
		assertEquals(ActivityLocation.OTHER_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, initHouseholdAndFamilyCareAtThirdPlaceWithHouseholdMembers.getActivityLocation());
	}
	
	@Test
	public void testInitTravelActivity() {
		Activity initTravelActivity = ACTIVITY_INITIALIZER.initTravelActivity();
		assertEquals(ActivityCategory.TRAVEL, initTravelActivity.getActivityCategory());
		assertEquals("Travel", initTravelActivity.getActivityDescription());
		assertEquals(BigDecimal.ONE, initTravelActivity.getFractionForNeed(Need.NONE));
		assertEquals("Travel with any mode of transport", initTravelActivity.getExamples());		
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		assertEquals(true, initTravelActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(false, initTravelActivity.isJointActivity());
		assertEquals(ActivityLocation.TRAVEL, initTravelActivity.getActivityLocation());
	}
}
