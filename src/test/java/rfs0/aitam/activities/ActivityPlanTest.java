package rfs0.aitam.activities;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;

import activities.Activity;
import activities.ActivityPlan;
import rfs0.aitam.commons.ISimulationSettings;
import sim.util.geo.MasonGeometry;

public class ActivityPlanTest {
	
	public static final Activity.Builder ACTIVITY_BUILDER = new Activity.Builder();
	public static DateTime s_startOfFirstInterval;
	public static DateTime s_endOfFirstInterval;
	public static Interval s_firstInterval;
	public static Activity s_firstActivity;
	public static DateTime s_startOfSecondInterval;
	public static DateTime s_endOfSecondInterval;
	public static Interval s_secondInterval;
	public static Activity s_secondActivity;
	public static Activity s_thirdActivity;
	public static MasonGeometry s_firstLocation;
	public static Interval s_intervalWithNoActivity;
	public static ActivityPlan s_plan;
	
	@BeforeClass
	public static void setup() {
		s_startOfFirstInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, 1, 0, 0);
		s_endOfFirstInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, 1, 1, 30);
		s_firstInterval = new Interval(s_startOfFirstInterval, s_endOfFirstInterval);
		s_firstActivity = ACTIVITY_BUILDER.build();
		s_startOfSecondInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, 1, 1, 30);
		s_endOfSecondInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, 1, 3, 0);
		s_secondInterval = new Interval(s_startOfSecondInterval, s_endOfSecondInterval);
		s_secondActivity = ACTIVITY_BUILDER.build();
		s_intervalWithNoActivity = new Interval(s_startOfFirstInterval, s_endOfSecondInterval);
		s_thirdActivity = ACTIVITY_BUILDER.build();
		s_firstLocation = new MasonGeometry();
		s_plan = new ActivityPlan();
		s_plan.addActivityForInterval(s_secondInterval, s_secondActivity);
		s_plan.addActivityForInterval(s_firstInterval, s_firstActivity);
	}
	
	@Test
	public void testGetActivityForInterval() {
		assertEquals(s_firstActivity, s_plan.getActivityForInterval(s_firstInterval));
		assertEquals(s_secondActivity, s_plan.getActivityForInterval(s_secondInterval));
		assertEquals(null, s_plan.getActivityForInterval(s_intervalWithNoActivity));
	}
	
	@Test
	public void testGetLastPlannedInterval() {
		assertEquals(s_secondInterval, s_plan.getLastPlannedInterval());
	}
	
	@Test
	public void testAddActivityForInterval() {
		DateTime startOfThirdInterval = s_endOfSecondInterval;
		DateTime endOfThirdInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, 1, 4, 0);
		Interval validThirdInterval = new Interval(startOfThirdInterval, endOfThirdInterval);
		Interval doesNotAbut = new Interval(endOfThirdInterval.plusMinutes(1), endOfThirdInterval.plusMinutes(60));
		Interval overlapsExsisting = new Interval(endOfThirdInterval.minusMinutes(1), endOfThirdInterval.plusMinutes(60));
		Activity someActivity = ACTIVITY_BUILDER.build();
		
		assertEquals(true, s_plan.addActivityForInterval(validThirdInterval, s_thirdActivity));
		assertEquals(false, s_plan.addActivityForInterval(doesNotAbut, someActivity));
		assertEquals(false, s_plan.addActivityForInterval(overlapsExsisting, someActivity));
	}
	
	@Test
	public void testGetLocationForInterval() {
		Interval invalidInterval = new Interval(s_startOfFirstInterval, s_endOfSecondInterval);
		assertEquals(s_firstActivity, s_plan.getActivityForInterval(s_firstInterval));
		assertEquals(null, s_plan.getActivityForInterval(invalidInterval));
	}
	
	@Test
	public void testAddLocationForInterval() {
		s_plan.addLocationForInterval(s_firstInterval, s_firstLocation);
		s_plan.getLocationForInterval(s_firstInterval);
	}
}
