package activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.needs.NeedTimeSplit;

public class Activity {

	private ActivityCategory m_activityCategory;
	private String m_activityDescription;
	private ActivityLocation m_activityLocation;
	private boolean m_isJointActivity;
	private HashMap<Integer, ArrayList<Interval>> m_availability = new HashMap<>();
	private NeedTimeSplit m_needTimeSplit;
	private String m_examples;

	private Activity() {}
	
	@Override
	public String toString() {
		return m_activityDescription;
	}

	public static class Builder {

		private Activity activityToBuild;

		public Builder() {
			activityToBuild = new Activity();
		}

		public Builder withActivityCategory(ActivityCategory type) {
			activityToBuild.m_activityCategory = type;
			return this;
		}
		
		public Builder withActivityDescription(String activityDescription) {
			activityToBuild.m_activityDescription = activityDescription;
			return this;
		}
		
		public Builder withActivityLocation(ActivityLocation activityLocation) {
			activityToBuild.m_activityLocation = activityLocation;
			return this;
		}
		
		public Builder withIsJointActivity(boolean isJointActivity) {
			activityToBuild.m_isJointActivity = isJointActivity;
			return this;
		}
		
		public Builder withAvailabilityInterval(int weekDay, int startHourOfDay, int startMinuteOfDay, int endHourOfDay, int endMinuteOfDay) {
			if (startHourOfDay >= endHourOfDay) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, "Interval is invalid: start is after end. The built activity may be unusable!");
			}
			DateTime startOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, startHourOfDay, startMinuteOfDay);
			DateTime endOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, endHourOfDay, endMinuteOfDay);
			if (activityToBuild.m_availability.get(weekDay) == null) {
				activityToBuild.m_availability.put(weekDay, new ArrayList<>());
			}
			activityToBuild.m_availability.get(weekDay).add(new Interval(startOfInterval, endOfInterval));
			return this;
		}

		public Builder withAvailabilityIntervalAtDays(int startHourOfDay, int startMinuteOfDay, int endHourOfDay, int endMinuteOfDay, int ...weekDays) {
			if (startHourOfDay >= endHourOfDay) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, "Interval is invalid: start is after end. The built activity may be unusable!");
			}
			DateTime startOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, startHourOfDay, startMinuteOfDay);
			DateTime endOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, endHourOfDay, endMinuteOfDay);
			for (int day: weekDays) {
				if (activityToBuild.m_availability.get(day) == null) {
					activityToBuild.m_availability.put(day, new ArrayList<>());
				}
				activityToBuild.m_availability.get(day).add(new Interval(startOfInterval, endOfInterval));
			}
			return this;
		}
		
		public Builder withAvailabilityIntervalAtDays(int startHourOfDay, int startMinuteOfDay, int endHourOfDay, int endMinuteOfDay, ArrayList<Integer> weekDays) {
			if (startHourOfDay >= endHourOfDay) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, "Interval is invalid: start is after end. The built activity may be unusable!");
			}
			DateTime startOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, startHourOfDay, startMinuteOfDay);
			DateTime endOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, endHourOfDay, endMinuteOfDay);
			for (Integer day: weekDays) {
				if (activityToBuild.m_availability.get(day) == null) {
					activityToBuild.m_availability.put(day, new ArrayList<>());
				}
				activityToBuild.m_availability.get(day).add(new Interval(startOfInterval, endOfInterval));
			}
			return this;
		}
		
		public Builder withAvailability(HashMap<Integer, ArrayList<Interval>> availability) {
			activityToBuild.m_availability = availability;
			return this;
		}
		
		public Builder withNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			activityToBuild.m_needTimeSplit = needTimeSplit;
			return this;
		}
		
		public Builder withExamples(String examples) {
			activityToBuild.m_examples = examples;
			return this;
		}
		
		private String checkIfAnyFieldIsNull() {
			if (activityToBuild.m_activityCategory == null) {
				return "m_activityCategory";
			}
			if (activityToBuild.m_activityDescription == null) {
				return "m_activityDescription";
			}
			if (activityToBuild.m_activityLocation == null) {
				return "m_activityLocation";
			}
			if (activityToBuild.m_availability == null) {
				return "m_availability";
			}
			if (activityToBuild.m_availability.isEmpty()) {
				return "m_availability";
			}
			if (activityToBuild.m_needTimeSplit == null) {
				return "m_needTimeSplit";
			}
			if (activityToBuild.m_examples == null || activityToBuild.m_examples.isEmpty()) {
				return "m_examples";
			}
			return null;
		}
		
		public Activity build() {
			if (checkIfAnyFieldIsNull() != null) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, String.format("%s is null i.e. not set!The built activity may be unusable!", checkIfAnyFieldIsNull()));
			}
			Activity builtActivityType = activityToBuild;
			activityToBuild = new Activity();
			return builtActivityType;
		}
	}

	public ActivityCategory getActivityCategory() {
		return m_activityCategory;
	}
	
	public String getActivityDescription() {
		return m_activityDescription;
	}
	
	public ActivityLocation getActivityLocation() {
		return m_activityLocation;
	}
	
	public boolean isJointActivity() {
		return m_isJointActivity;
	}
	
	public boolean isAvailableAtActivityLocation(ActivityLocation activitiyLocation) {
		return m_activityLocation == activitiyLocation;
	}

	public HashMap<Integer, ArrayList<Interval>> getAvailability() {
		return m_availability;
	}
	
	public boolean isAvailableAt(int weekDay, Interval interval) {
		if (m_availability.get(weekDay) == null) {
			return false;
		}
		Predicate<Interval> contains = i -> i.contains(interval);
		return m_availability.get(weekDay).stream().anyMatch(contains);
	}
	
	public boolean isAvailableAt(int weekDay, DateTime currentTime) {
		Interval currentTimeAsInterval = new Interval(currentTime);
		return isAvailableAt(weekDay, currentTimeAsInterval);
	}
	
	public NeedTimeSplit getNeedTimeSplit() {
		return m_needTimeSplit;
	}
	
	public String getExamples() {
		return m_examples;
	}
}
