package rfs0.aitam.activities;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;

import activities.Activity;
import activities.ActivityAgenda;
import rfs0.aitam.commons.ISimulationSettings;
import sim.util.geo.MasonGeometry;

public class ActivityAgendaTest {
	
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
	public static MasonGeometry s_secondLocation;
	public static MasonGeometry s_thirdLocation;
	public static Interval s_intervalWithNoActivity;
	public static ActivityAgenda s_plan;
	
	@BeforeClass
	public static void setup() {
		s_startOfFirstInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, ISimulationSettings.BASE_HOUR, ISimulationSettings.BASE_MINUTE);
		s_endOfFirstInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 1, 30);
		s_firstInterval = new Interval(s_startOfFirstInterval, s_endOfFirstInterval);
		s_firstActivity = ACTIVITY_BUILDER.build();
		s_startOfSecondInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 1, 30);
		s_endOfSecondInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 3, 0);
		s_secondInterval = new Interval(s_startOfSecondInterval, s_endOfSecondInterval);
		s_secondActivity = ACTIVITY_BUILDER.build();
		s_intervalWithNoActivity = new Interval(s_startOfFirstInterval, s_endOfSecondInterval);
		s_thirdActivity = ACTIVITY_BUILDER.build();
		s_firstLocation = new MasonGeometry();
		s_secondLocation = new MasonGeometry();
		s_thirdLocation = new MasonGeometry();
		s_plan = new ActivityAgenda();
		s_plan.addActivityForInterval(s_firstInterval, s_firstActivity);
		s_plan.addActivityForInterval(s_secondInterval, s_secondActivity);
		s_plan.addLocationForInterval(s_firstInterval, s_firstLocation);
		s_plan.addLocationForInterval(s_secondInterval, s_secondLocation);
	}
	
	@Test
	public void testGetActivityForInterval() {
		assertEquals(s_firstActivity, s_plan.getActivityForInterval(s_firstInterval));
		assertEquals(s_secondActivity, s_plan.getActivityForInterval(s_secondInterval));
		assertEquals(null, s_plan.getActivityForInterval(s_intervalWithNoActivity));
	}
	
	@Test
	public void testGetLastPlannedInterval() {
		assertEquals(s_secondInterval, s_plan.getLastPlannedActivityInterval());
	}
	
	@Test
	public void testGetLocationForInterval() {
		Interval invalidInterval = new Interval(s_startOfFirstInterval, s_endOfSecondInterval);
		assertEquals(s_firstLocation, s_plan.getLocationForInterval(s_firstInterval));
		assertEquals(s_secondLocation, s_plan.getLocationForInterval(s_secondInterval));
		assertEquals(null, s_plan.getLocationForInterval(invalidInterval));
	}
}
