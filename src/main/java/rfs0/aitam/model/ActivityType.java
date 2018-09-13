package rfs0.aitam.model;

import java.util.ArrayList;
import java.util.function.Predicate;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public abstract class ActivityType {

	private Type m_type;
	private NeedTimeSplit m_needTimeSplit;
	private ArrayList<Interval> m_availability = new ArrayList<>();

	private ActivityType(Type type) {
	}

	public static abstract class Builder {

		private Type type;
		private ActivityType activityTypeToBuild;

		public Builder(Type type) {
			this.type = type;
			activityTypeToBuild = initActivity(type);
		}

		public ActivityType build() {
			ActivityType builtActivityType = activityTypeToBuild;
			activityTypeToBuild = initActivity(this.type);
			return builtActivityType;
		}

		public abstract ActivityType initActivity(Type type);

		public Builder withNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			activityTypeToBuild.m_needTimeSplit = needTimeSplit;
			return this;
		}

		/**
		 *  Add <b>non-overlapping intervals<b> that represent the start and end time in hours of an activity type. Start must be before end
		 * 
		 * @param start - hour that represents the start time of an availability interval
		 * @param end   - hour that represents the end time of an availability interval
		 * @return
		 * @throws Exception
		 */
		public Builder withAvailabilityInterval(int start, int end) throws Exception {
			if (start >= end) {
				throw new Exception("Start time must be before end time!");
			}
			DateTime startOfInterval = new DateTime(2018, 1, 1, start, 0);
			DateTime endOfInterval = new DateTime(2018, 1, 1, end, 0);
			activityTypeToBuild.m_availability.add(new Interval(startOfInterval, endOfInterval));
			return this;
		}

		public Builder withAvailability(ArrayList<Interval> availability) {
			activityTypeToBuild.m_availability = availability;
			return this;
		}
	}

	public Type getType() {
		return m_type;
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
