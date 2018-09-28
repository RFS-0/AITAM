package rfs0.aitam.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;

import activities.Activity;
import activities.ActivityCategory;
import activities.ActivityLocation;
import activities.WeekDay;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.utilities.CalculationUtility;

public class ActivityTest {

	public static final Activity.Builder ACTIVITY_BUILDER = new Activity.Builder();
	public static final NeedTimeSplit.Builder NEED_TIME_SPLIT_BUILDER = new NeedTimeSplit.Builder();
	public static final ArrayList<WeekDay> WORK_WEEK = Stream
			.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY)
			.collect(Collectors.toCollection(ArrayList::new));
	public static ActivityCategory s_testActivityCategory;
	public static String s_testActivityDescription;
	public static NeedTimeSplit s_testNeedTimeSplit;
	public static Set<Need> s_testNeeds = Stream.of(Need.SUBSISTENCE, Need.AFFECTION, Need.UNDERSTANDING, Need.PARTICIPATION, Need.CREATION, Need.FREEDOM)
			.collect(Collectors.toSet());
	public static String s_testExamples;
	public static int s_testStartHourOfDay;
	public static int s_testStartMinuteOfDay;
	public static int s_testEndHourOfDay;
	public static int s_testEndMinuteOfDay;
	public static ArrayList<WeekDay> s_weekDays = WORK_WEEK;
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
		s_testActivityLocation = ActivityLocation.THIRD_WORK_PLACE_LOCATION;

		s_testActivity = ACTIVITY_BUILDER.withActivityCategory(ActivityCategory.WORK)
				.withActivityDescription(s_testActivityDescription)
				.withNeedTimeSplit(s_testNeedTimeSplit)
				.withExamples(s_testExamples)
				.withAvailabilityIntervalAtDays(s_testStartHourOfDay, s_testStartMinuteOfDay, s_testEndHourOfDay,
						s_testEndMinuteOfDay, WORK_WEEK)
				.withIsJointActivity(s_testIsJointActivity)
				.withActivityLocation(ActivityLocation.THIRD_WORK_PLACE_LOCATION)
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
		assertEquals(s_testNeeds, s_testActivity.getNeedTimeSplit().getNeeds());
		Activity incompleteNeedTimeSplitActivity = ACTIVITY_BUILDER
				.withNeedTimeSplit(NEED_TIME_SPLIT_BUILDER.withNeedTimeSplit(Need.AFFECTION, BigDecimal.valueOf(0.5)).build())
				.build();
		assertEquals(Stream.of(Need.AFFECTION, Need.NOT_DEFINED).collect(Collectors.toSet()), incompleteNeedTimeSplitActivity.getNeedTimeSplit().getNeeds());
		assertEquals(BigDecimal.valueOf(0.5), incompleteNeedTimeSplitActivity.getNeedTimeSplit().getFractionForNeed(Need.NOT_DEFINED));
		assertEquals(CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE), s_testActivity.getNeedTimeSplit().getFractionForNeed(Need.SUBSISTENCE));
	}
	
	@Test
	public void testExamples() {
		assertEquals(s_testExamples, s_testActivity.getExamples());
	}
	
	@Test
	public void testAvailabilityIntervalAtDays() {
		DateTime availableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0);
		DateTime notAvailableStartTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 0);
		DateTime availableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 59);
		DateTime notAvailableEndTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 0);
		
		Interval available = new Interval(availableStartTime, availableEndTime);
		Interval notAvailable = new Interval(notAvailableStartTime, notAvailableEndTime);
		Interval notAvailableStart = new Interval(notAvailableStartTime, availableEndTime);
		Interval notAvailableEnd = new Interval(availableStartTime, notAvailableEndTime);
		assertEquals(true, s_testActivity.isAvailableAt(WeekDay.MONDAY, available));
		assertEquals(false, s_testActivity.isAvailableAt(WeekDay.SATURDAY, available));
		assertEquals(false, s_testActivity.isAvailableAt(WeekDay.MONDAY, notAvailable));
		assertEquals(false, s_testActivity.isAvailableAt(WeekDay.MONDAY, notAvailableStart));
		assertEquals(false, s_testActivity.isAvailableAt(WeekDay.MONDAY, notAvailableEnd));
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
