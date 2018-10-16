package rfs0.aitam.activities;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.activities.Activity;
import rfs0.aitam.activities.ActivityAgenda;
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
	public static Node s_firstNode;
	public static Node s_secondNode;
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
		s_firstNode = new Node(new Coordinate());
		s_secondNode = new Node(new Coordinate());
		s_thirdLocation = new MasonGeometry();
		s_plan = new ActivityAgenda();
		s_plan.addActivityForInterval(s_firstInterval, s_firstActivity);
		s_plan.addActivityForInterval(s_secondInterval, s_secondActivity);
		s_plan.addNodeForInterval(s_firstInterval, s_firstNode);
		s_plan.addNodeForInterval(s_secondInterval, s_secondNode);
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
		assertEquals(s_firstNode, s_plan.getNodeForInterval(s_firstInterval));
		assertEquals(s_secondNode, s_plan.getNodeForInterval(s_secondInterval));
		assertEquals(null, s_plan.getNodeForInterval(invalidInterval));
	}
}
