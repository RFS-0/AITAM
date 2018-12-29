package rfs0.aitam.activities;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.model.needs.AbsoluteNeedTimeSplit;
import rfs0.aitam.model.needs.NeedTimeSplit;

/**
 * <p>This class is used to model agendas of activities. As such it is described by the following information:</p>
 * 
 * <p>{@link ActivityAgenda#m_agenda}: The actual agenda containing all the activities planned sorted by their starting points. 
 * The key is an interval describing what time of day each activity of the agenda is executed. 
 * The values are the activities themselves.</p>
 * <p>{@link ActivityAgenda#m_locations}: Information about where exactly each activity is executed. 
 * Thus, the key is again an interval and it has to match exactly one of the intervals in the agenda. 
 * The values are the actual locations where the activity is executed.</p> 
 * <p>{@link ActivityAgenda#m_actualNeedTimeSplit}: The actual need time spilt is composed of two {@link NeedTimeSplit}s. 
 * In the context of an agenda this data structure allows to record the absolute time (measured in minutes) spent on satisfying each need during when executing an activity. 
 * Furthermore, it allows to convert those absolute recordings to be converted into relative measures i.e. percentages. (See {@link AbsoluteNeedTimeSplit} for more details).</p>
 * 
 * <p><b>Important:</b> The intervals used as keys for {@link ActivityAgenda#m_agenda} and {@link ActivityAgenda#m_locations} must always be abutting each other and must never be overlapping each other. 
 * This is <b>not</b> checked by the method to add new intervals because of its negative impact on performance. 
 * (The check has been removed and could be added once again by checking earlier versions of this class via VCS).</p>
 */
public class ActivityAgenda implements Cloneable {
	
	private static final Logger LOG = Logger.getLogger(ActivityAgenda.class.getName());
	
	/**
	 * <p>The actual agenda containing all the activities planned sorted by their starting points. 
	 * The key is an interval describing what time of day each activity of the agenda is executed. 
	 * The values are the activities themselves.</p>
	 */
	private TreeMap<Interval, Activity> m_agenda = new TreeMap<>(new IntervalComparator());
	/**
	 * <p>Information about where exactly each activity is executed. 
	 * Thus, the key is again an interval and it must match exactly one of the intervals in the agenda. 
	 * The values are the actual locations where the activity is executed.</p> 
	 */
	private TreeMap<Interval, Node> m_locations = new TreeMap<>(new IntervalComparator());
	/**
	 * <p>The actual need time spilt is composed of two {@link NeedTimeSplit}s. 
	 * In the context of an agenda this data structure allows to record the absolute time (measured in minutes) spent on satisfying each need during when executing an activity. 
	 * Furthermore, it allows to convert those absolute recordings to be converted into relative measures i.e. percentages. (See {@link AbsoluteNeedTimeSplit} for more details).</p>
	 */
	private AbsoluteNeedTimeSplit m_actualNeedTimeSplit = new AbsoluteNeedTimeSplit();
	
	public ActivityAgenda() {}
	
	/**
	 * <p>This constructor is used to create deep copies (see {@link ActivityAgenda#newInstance(ActivityAgenda)}).</p>
	 * 
	 * @param agenda - the agenda to be copied
	 * @param locations - the locations to be copied
	 */
	private ActivityAgenda(TreeMap<Interval, Activity> agenda, TreeMap<Interval, Node> locations) {
		m_agenda = new TreeMap<>(new IntervalComparator());
		m_locations = new TreeMap<>(new IntervalComparator());
		for (Interval interval: agenda.keySet()) {
			m_agenda.put(interval, agenda.get(interval));
		}
		for (Interval interval: locations.keySet()) {
			m_locations.put(interval, locations.get(interval));
		}
	}
	
	/**
	 * @category Functionality
	 * 
	 * <p>This method can be used to create deep copies of {@link ActivityAgenda}s.</p>
	 * 
	 * @param activtyAgenda - the agenda to be copied
	 * @return - a deep copy of the activity agenda
	 */
	public static ActivityAgenda newInstance(ActivityAgenda activtyAgenda) {
		return new ActivityAgenda(activtyAgenda.getAgenda(), activtyAgenda.getNodes());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Activities:\n");
		sb.append(m_agenda.toString());
		sb.append("Locations:\n");
		sb.append(m_locations.toString());
		return super.toString();
	}
	
	/**
	 * This method is used to add a new activity for some interval to the agenda.
	 * 
	 * <p><b>Important:</b> The intervals used as keys must always be abutting each other and must never be overlapping each other. 
	 * This is <b>not</b> checked by the method to add new intervals because of its negative impact on performance. 
	 * (The check has been removed and could be added once again by checking earlier versions of this class via VCS).</p>
	 * 
	 * @param activityInterval - the interval during which the activity is executed.
	 * @param activity - the activity which is being executed during the specified interval.
	 */
	public void addActivityForInterval(Interval activityInterval, Activity activity) {
		m_agenda.put(activityInterval, activity);
	}
	
	/**
	 * <p>This method is used to retrieve the activity executed during the specified interval.</p>
	 * 
	 * <p><b>Important:</b> Make sure to only use intervals smaller than or equal to those you used when adding the activities. 
	 * For more details on how retrieving works check out {@link Interval#contains(org.joda.time.ReadableInterval)}.</p>
	 * 
	 * @param interval - the interval for which the activity should be retrieved.
	 * @return - the activity executed during the specified interval or <code>null</code> if there is no entry for it.
	 */
	public Activity getActivityForInterval(Interval interval) {
		for (Interval key: m_agenda.keySet()) {
			if (key.contains(interval)) {
				return m_agenda.get(key);
			}
		}
		return null;
	}
	
	/**
	 * <p>This method is used to retrieve the activity executed at the specified point in time.</p> 
	 * 
	 * @param time - the point in time for which the activity should be retrieved.
	 * @return - the activity executed during the specified point in time or <code>null</code> if there is no entry for it.
	 */
	public Activity getActivityForDateTime(DateTime time) {
		for (Interval key: m_agenda.keySet()) {
			if (key.contains(time) || key.getEnd().equals(time)) {
				return m_agenda.get(key);
			}
		}
		return null;
	}
	
	/**
	 * <p>This method is used to add a new activity location i.e. node for some interval to the agenda.</p>
	 * 
	 * <p><b>Important:</b> The intervals used as keys must always be abutting each other and must never be overlapping each other. 
	 * This is <b>not</b> checked by the method to add new intervals because of its negative impact on performance. 
	 * (The check has been removed and could be added once again by checking earlier versions of this class via VCS).</p>
	 * 
	 * @param activityInterval - the interval during which the activity is executed.
	 * @param targetNode - the activity location at which the activity is being executed during the specified interval.
	 */
	public void addNodeForInterval(Interval activityInterval, Node targetNode) {
		if (activityInterval == null || targetNode == null) {
			LOG.log(Level.SEVERE, String.format("At least one argument is invalid: activityInterval=%s;  targetNode=%s. Can not add this, since this would result in an invalid agenda.", Objects.toString(activityInterval), Objects.toString(targetNode)));
		}
		m_locations.put(activityInterval, targetNode);
	}
	
	/**
	 * <p>This method is used to retrieve the activity location for the activity executed at the specified point in time.</p>
	 * 
	 * @param interval - the interval for which the activity location should be retrieved.
	 * @return - the activity location for the activity executed during the specified interval or <code>null</code> if there is no entry for it.
	 */
	public Node getNodeForInterval(Interval interval) {
		for (Interval key: m_locations.keySet()) {
			if (key.contains(interval)) {
				return m_locations.get(key);
			}
		}
		return null;
	}
	
	/**
	 * <p>This method is used to retrieve the activity location for the activity executed at the specified point in time.</p> 
	 * 
	 * @param time - the point in time for which the activity location should be retrieved.
	 * @return - the activity location of the activity executed during the specified point in time or <code>null</code> if there is no entry for it.
	 */
	public Node getNodeForDateTime(DateTime time) {
		for (Interval key: m_locations.keySet()) {
			if (key.contains(time) || key.getEnd().equals(time)) {
				return m_locations.get(key);
			}
		}
		return null;
	}
	
	/**
	 * <p>This method returns the intervals for which the activity agenda has defined activities. 
	 * As such it represents the key set for both, the activities and their locations.
	 * Thus it can be used to iterate over both, the activities as well as the locations. 
	 * For this reason the method also ensures that the key sets are indeed equal, otherwise an error is logged, since the agenda is not valid.</p>
	 * 
	 * @return - the set of intervals representing the key set for both {@link ActivityAgenda#m_agenda} and {@link ActivityAgenda#m_locations}.
	 */
	public Set<Interval> getIntervals() {
		if (!m_agenda.keySet().equals(m_locations.keySet())) {
			LOG.log(Level.SEVERE, "Intervals for activities and locations are not identical. This agenda is invalid!");
		}
		return m_agenda.keySet();
	}
	
	/**
	 * <p>This method returns the first {@link Interval} planned i.e. the interval with the earliest start time or <code>null</code> if there is no interval planned yet.</p>
	 * 
	 * <b>Note:</b>: This is possible since the agendas keys are at any point in time sorted by their start times (see {@link TreeMap} and {@link IntervalComparator} for more details).
	 * 
	 * @return - the first interval planned or <code>null</code> if there is no interval planned yet.
	 */
	public Interval getFirstPlannedInterval() {
		if (m_agenda.keySet().isEmpty()) {
			return null;
		}
		return m_agenda.firstKey();
	}
	
	/**
	 * <p>This method returns the last {@link Interval} planned i.e. the interval with the latest start time or <code>null</code> if there is no interval planned yet.</p>
	 * 
	 * <b>Note:</b>: This is possible since the agendas keys are at any point in time sorted by their start times (see {@link TreeMap} and {@link IntervalComparator} for more details).
	 * 
	 * @return - the last interval planned or <code>null</code> if there is no interval planned yet.
	 */
	public Interval getLastPlannedInterval() {
		if (m_agenda.keySet().isEmpty()) {
			return null;
		}
		return m_agenda.lastKey();
	}
	
	/**
	 * <p>This method resets the activity agenda by resetting both the agenda and the locations.</p>
	 */
	public void clear() {
		m_agenda = new TreeMap<>(new IntervalComparator());
		m_locations = new TreeMap<>(new IntervalComparator());
	}

	/**
	 * @category Getter
	 */
	public AbsoluteNeedTimeSplit getAbsoluteNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}
	
	public TreeMap<Interval, Activity> getAgenda() {
		return m_agenda;
	}
	
	public TreeMap<Interval, Node> getNodes() {
		return m_locations;
	}

	/**
	 * <p>This class is used to define the ordering of both {@link ActivityAgenda#m_agenda} and {@link ActivityAgenda#m_locations}.</p>
	 */
	public class IntervalComparator implements Comparator<Interval> {

		@Override
		public int compare(Interval i1, Interval i2) {
			return i1.getStart().compareTo(i2.getStart());
		}
	}
}
