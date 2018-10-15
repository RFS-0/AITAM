package activities;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import rfs0.aitam.model.needs.ActualNeedTimeSplit;
import sim.util.geo.MasonGeometry;

public class ActivityAgenda implements Cloneable {
	
	/**
	 * The Interval of this map must not overlap, but they must abut each other. 
	 * The implementation for adding new key-value pairs guarantees this
	 */
	private TreeMap<Interval, Activity> m_agenda = new TreeMap<>(new IntervalComparator());
	private TreeMap<Interval, MasonGeometry> m_locations = new TreeMap<>(new IntervalComparator());
	private ActualNeedTimeSplit m_actualNeedTimeSplit = new ActualNeedTimeSplit();
	
	public ActivityAgenda() {}
	
	private ActivityAgenda(TreeMap<Interval, Activity> agenda, TreeMap<Interval, MasonGeometry> locations) {
		m_agenda = agenda;
		m_locations = locations;
	}
	
	public static ActivityAgenda newInstance(ActivityAgenda activtyAgenda) {
		return new ActivityAgenda(activtyAgenda.getAgenda(), activtyAgenda.getLocations());
	}
	
	public void addActivityForInterval(Interval activityInterval, Activity activity) {
		m_agenda.put(activityInterval, activity);
	}
	
	public Activity getActivityForInterval(Interval interval) {
		for (Interval key: m_agenda.keySet()) {
			if (key.contains(interval)) {
				return m_agenda.get(key);
			}
		}
		return null;
	}
	
	public Activity getActivityForDateTime(DateTime time) {
		for (Interval key: m_agenda.keySet()) {
			if (key.contains(time)) {
				return m_agenda.get(key);
			}
		}
		return null;
	}
	
	public void addLocationForInterval(Interval activityInterval, MasonGeometry targetBuilding) {
		if (activityInterval == null || targetBuilding == null) {
			Logger.getLogger(ActivityAgenda.class.getName()).log(Level.SEVERE, String.format("At least one argument is invalid: activityInterval=%s;  targetBuilding=%s. Can not add this, since this would result in an invalid agenda.", Objects.toString(activityInterval), Objects.toString(targetBuilding)));
		}
		m_locations.put(activityInterval, targetBuilding);
	}
	
	public MasonGeometry getLocationForInterval(Interval interval) {
		for (Interval key: m_locations.keySet()) {
			if (key.contains(interval)) {
				return m_locations.get(key);
			}
		}
		return null;
	}
	
	public Set<Interval> getIntervals() {
		if (!m_agenda.keySet().equals(m_locations.keySet())) {
			Logger.getLogger(ActivityAgenda.class.getName()).log(Level.SEVERE, "Intervals for activities and locations are not identical. This agenda is invalid!");
		}
		return m_agenda.keySet();
	}
	
	public Interval getFirstPlannedInterval() {
		if (m_agenda.keySet().isEmpty()) {
			return null;
		}
		return m_agenda.firstKey();
	}
	
	public Interval getLastPlannedActivityInterval() {
		if (m_agenda.keySet().isEmpty()) {
			return null;
		}
		return m_agenda.lastKey();
	}
	
	public Interval getLastPlannedLocationInterval() {
		if (m_locations.keySet().isEmpty()) {
			return null;
		}
		return m_locations.lastKey();
	}
	
	public ActualNeedTimeSplit getActualNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}
	
	public TreeMap<Interval, Activity> getAgenda() {
		return m_agenda;
	}
	
	public void clearAgenda() {
		m_agenda = new TreeMap<>(new IntervalComparator());
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
