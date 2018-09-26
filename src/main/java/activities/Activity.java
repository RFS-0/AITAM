package activities;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import rfs0.aitam.model.Type;
import rfs0.aitam.model.needs.NeedTimeSplit;

public class Activity {

	private Type m_type;
	private String m_activityDescription;
	private NeedTimeSplit m_needTimeSplit;
	private ArrayList<Interval> m_availability = new ArrayList<>();

	private Activity() {}

	public static class Builder {

		private Activity generalActivityToBuild;

		public Builder() {
			generalActivityToBuild = new Activity();
		}

		public Activity build() {
			Activity builtActivityType = generalActivityToBuild;
			generalActivityToBuild = new Activity();
			return builtActivityType;
		}
		
		public Builder withType(Type type) {
			generalActivityToBuild.m_type = type;
			return this;
		}
		
		public Builder withActivityDescription(String activityDescription) {
			generalActivityToBuild.m_activityDescription = activityDescription;
			return this;
		}
		
		public Builder withNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			generalActivityToBuild.m_needTimeSplit = needTimeSplit;
			return this;
		}
		
		/**
		 *  Add <b>non-overlapping intervals<b> that represent the start and end time in hours of an activity type. Start must be before end
		 * 
		 * @param start - hour that represents the start time of an availability interval
		 * @param end   - hour that represents the end time of an availability interval
		 * @return
		 */
		public Builder withAvailabilityInterval(int start, int end) {
			if (start >= end) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, "Interval is invalid: start is after end. The built activity may be unusable!");
			}
			DateTime startOfInterval = new DateTime(2018, 1, 1, start, 0);
			DateTime endOfInterval = new DateTime(2018, 1, 1, end, 0);
			generalActivityToBuild.m_availability.add(new Interval(startOfInterval, endOfInterval));
			return this;
		}

		public Builder withAvailability(ArrayList<Interval> availability) {
			generalActivityToBuild.m_availability = availability;
			return this;
		}
	}

	public Type getType() {
		return m_type;
	}
	
	public String getActivityDescription() {
		return m_activityDescription;
	}

	public NeedTimeSplit getNeedTimeSplit() {
		return m_needTimeSplit;
	}

	public ArrayList<Interval> getAvailability() {
		return m_availability;
	}
	
	public boolean isAvailableAt(Interval interval) {
		Predicate<Interval> overlaps = i -> i.overlaps(interval);
		return m_availability.stream().anyMatch(overlaps);
	}
}
