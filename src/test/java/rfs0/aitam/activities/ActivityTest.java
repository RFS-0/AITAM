package rfs0.aitam.activities;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.BeforeClass;
import org.junit.Test;

import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;

public class ActivityTest {

	public static final Activity.Builder ACTIVITY_BUILDER = new Activity.Builder();
	public static final NeedTimeSplit.Builder NEED_TIME_SPLIT_BUILDER = new NeedTimeSplit.Builder();
	public static ActivityCategory s_testActivityCategory;
	public static String s_testActivityDescription;
	public static NeedTimeSplit s_testNeedTimeSplit;
	public static Set<Need> s_testNeeds = Stream.of(Need.SUBSISTENCE, Need.AFFECTION, Need.UNDERSTANDING, Need.PARTICIPATION, Need.CREATION, Need.FREEDOM, Need.NONE)
			.collect(Collectors.toSet());
	public static String s_testExamples;
	public static int s_testStartHourOfDay;
	public static int s_testStartMinuteOfDay;
	public static int s_testEndHourOfDay;
	public static int s_testEndMinuteOfDay;
	public static boolean s_testIsJointActivity;
	public static ActivityLocation s_testActivityLocation;
	public static Activity s_testActivity;

	@BeforeClass
	public static void setupTest() {
		s_testActivityCategory = ActivityCategory.WORK;
		s_testActivityDescription = "Work at work location with coworkers";
		s_testNeedTimeSplit = NEED_TIME_SPLIT_BUILDER
				.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
				.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
				.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
				.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
				.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(CalculationUtility.TWO, CalculationUtility.NINE))
				.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
				.build();
		s_testExamples = "Paid work";
		s_testStartHourOfDay = 8;
		s_testStartMinuteOfDay = 0;
		s_testEndHourOfDay = 18;
		s_testEndMinuteOfDay = 59;
		s_testIsJointActivity = true;
		s_testActivityLocation = ActivityLocation.OTHER_PLACE_FOR_WORK;

		s_testActivity = ACTIVITY_BUILDER.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription(s_testActivityDescription)
				.withNeedTimeSplit(s_testNeedTimeSplit)
				.withExamples(s_testExamples)
				.withAvailabilityIntervalAtDays(s_testStartHourOfDay, s_testStartMinuteOfDay, s_testEndHourOfDay,
						s_testEndMinuteOfDay, ISimulationSettings.WORK_WEEK)
				.withIsJointActivity(s_testIsJointActivity)
				.withActivityLocation(ActivityLocation.OTHER_PLACE_FOR_WORK)
				.build();
	}

	@Test
	public void testActivityCategory() {
		assertEquals(s_testActivityCategory, s_testActivity.getActivityCategory());
	}

	@Test
	public void testActivityDescription() {
		assertEquals(s_testActivityDescription, s_testActivity.getActivityDescription());
	}

	@Test
	public void testNeedTimeSplit() {
		assertEquals(s_testNeedTimeSplit, s_testActivity.getNeedTimeSplit());
		assertEquals(s_testNeeds, s_testActivity.getNeeds());
		Activity incompleteNeedTimeSplitActivity = ACTIVITY_BUILDER
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER.withNeedTimeSplit(Need.AFFECTION, BigDecimal.valueOf(0.5)).build())
				.build();
		assertEquals(Stream.of(Need.AFFECTION, Need.NONE).collect(Collectors.toSet()), incompleteNeedTimeSplitActivity.getNeeds());
		assertEquals(CalculationUtility.createBigDecimal(0.5), incompleteNeedTimeSplitActivity.getFractionForNeed(Need.NONE));
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE), s_testActivity.getFractionForNeed(Need.SUBSISTENCE));
	}
	
	@Test
	public void testExamples() {
		assertEquals(s_testExamples, s_testActivity.getExamples());
	}
	
	@Test
	public void testAvailabilityIntervalAtDays() {
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime latestAvailableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 59);
		DateTime notAvailableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 0);
		
		assertEquals(true, s_testActivity.isAvailableAt(DateTimeConstants.MONDAY, availableStartTime));
		assertEquals(true, s_testActivity.isAvailableAt(DateTimeConstants.MONDAY, latestAvailableStartTime));
		assertEquals(false, s_testActivity.isAvailableAt(DateTimeConstants.SATURDAY, availableStartTime));
		assertEquals(false, s_testActivity.isAvailableAt(DateTimeConstants.MONDAY, notAvailableStartTime));
	}
	
	@Test
	public void testIsJointActivity() {
		assertEquals(true, s_testActivity.isJointActivity());
	}
	
	@Test
	public void withActivityLocation() {
		assertEquals(s_testActivityLocation, s_testActivity.getActivityLocation());
	}
}
