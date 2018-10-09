package activities;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.Interval;

import rfs0.aitam.model.needs.ActualNeedTimeSplit;
import sim.util.geo.MasonGeometry;

public class ActivityPlan {
	
	/**
	 * The Interval of this map must not overlap, but they must abut each other. 
	 * The implementation for adding new key-value pairs guarantees this
	 */
	TreeMap<Interval, Activity> m_plan = new TreeMap<>(new IntervalComparator());
	TreeMap<Interval, MasonGeometry> m_locations = new TreeMap<>(new IntervalComparator());
	private ActualNeedTimeSplit m_actualNeedTimeSplit = new ActualNeedTimeSplit();
	
	public Activity getActivityForInterval(Interval interval) {
		for (Interval key: m_plan.keySet()) {
			if (key.contains(interval)) {
				return m_plan.get(key);
			}
		}
		return null;
	}
	
	public Set<Interval> getIntervals() {
		return m_plan.keySet();
	}
	
	public Interval getFirstPlannedInterval() {
		if (m_plan.keySet().isEmpty()) {
			return null;
		}
		return m_plan.firstKey();
	}
	
	public Interval getLastPlannedInterval() {
		if (m_plan.keySet().isEmpty()) {
			return null;
		}
		return m_plan.lastKey();
	}
	
	public boolean addActivityForInterval(Interval activityInterval, Activity activity) {
		if (getLastPlannedInterval() != null && !activityInterval.abuts(getLastPlannedInterval())) {
			Logger.getLogger(ActivityPlan.class.getName()).log(Level.SEVERE, "New interval does not abut last planned activity. Can not add activity with this interval to plan, since this would result in an invalid plan!");
			return false;
		}
		m_plan.put(activityInterval, activity);
		return true;
	}
	
	public MasonGeometry getLocationForInterval(Interval interval) {
		return m_locations.get(interval);
	}
	
	public void addLocationForInterval(Interval activityInterval, MasonGeometry targetBuilding) {
		m_locations.put(activityInterval, targetBuilding);
	}
	
	public TreeMap<Interval, Activity> getPlan() {
		return m_plan;
	}
	
	public ActualNeedTimeSplit getActualNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}
	
	public void clearPlan() {
		m_plan = new TreeMap<>(new IntervalComparator());
	}
	
	public TreeMap<Interval, MasonGeometry> getLocations() {
		return m_locations;
	}
	
	public void clearLocations() {
		m_locations = new TreeMap<>(new IntervalComparator());
	}

	public class IntervalComparator implements Comparator<Interval> {

		@Override
		public int compare(Interval i1, Interval i2) {
			return i1.getStart().compareTo(i2.getStart());
		}
	}
}
