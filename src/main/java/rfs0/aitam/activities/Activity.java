package rfs0.aitam.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;

import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.individuals.Individual;
import rfs0.aitam.individuals.NetworkType;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;

/**
 * <p>This class is used to model real world activities or abstractions thereof. As such they are described by the following information:</p>
 * 
 * <p>{@link Activity#m_activityCategory}: The category of an activity. This classification serves as main means to group similar activities.</p>
 * <p>{@link Activity#m_activityDescription}: A description of the activity, which is also used to refer to simulation output related to it.</p>
 * <p>{@link Activity#m_activityLocation}: The location where the activity can be executed. This does not refer to the exact location but the type of location.</p>
 * <p>{@link Activity#m_isJointActivity}: This attribute is used to declare an activity as either being an individual or joint activity. Joint activities can only be executed.</p>
 * <p>together with other individuals.</p>
 * <p>{@link Activity#m_availability}: The availability in terms of days of week and time of day referring to possible start times of an activity. This serves as a way.</p>
 * <p>of constraining when an activity can be started.</p>
 * <p>{@link Activity#m_needTimeSplit}: The need time split is a construct that allows to define what needs an activity satisfies for each unit of time it is being executed.</p>
 * <p>{@link Activity#m_examples}: Some examples of this activity. This allows to make abstractions of activities more concrete. However, it serves only as information to </p>
 * <p>the modeler and is not being used in any functionality.</p>
 * <p>{@link Activity#m_networkType}: The network type determines with which members of a network an activity must be conducted with.</p>
 */
public class Activity {

	private ActivityCategory m_activityCategory;
	private String m_activityDescription;
	private ActivityLocation m_activityLocation;
	private boolean m_isJointActivity;
	private HashMap<Integer, ArrayList<Interval>> m_availability = new HashMap<>();
	private NeedTimeSplit m_needTimeSplit;
	private String m_examples;
	private NetworkType m_networkType;

	private Activity() {}
	
	@Override
	public String toString() {
		return m_activityDescription;
	}
	
	/**
	 * @category Builder
	 */

	/**
	 * <p>This builder must be used to instantiate {@link Activity}'s. In addition to making creation of instances easier it ensures the object to be built has all relevant attributes set (see {@link Builder#activityToBuild}).</p>
	 */
	public static class Builder {

		private Activity activityToBuild;

		public Builder() {
			activityToBuild = new Activity();
		}

		/**
		 * <p>Each {@link Activity} must belong to an {@link ActivityCategory} and this method sets it for {@link Builder#activityToBuild}.</p>
		 * 
		 * @param activityCategory - category of the activity to be built.
		 * @return {@link Builder} - builder with an activity category set for {@link Builder#activityToBuild}.
		 */
		public Builder withActivityCategory(ActivityCategory activityCategory) {
			activityToBuild.m_activityCategory = activityCategory;
			return this;
		}
		
		/**
		 * <p>Each {@link Activity} must have a description and this method sets it for {@link Builder#activityToBuild}.</p> 
		 * 
		 * <p><b>Note:</b> Make sure to use a constant as described and defined in {@link ISimulationSettings} -> (Configuration of aspects related to simulation output).</p>
		 * 
		 * @param activityDescription - description of the activity to be built.
		 * @return {@link Builder} - builder with an activity description set for {@link Builder#activityToBuild}.
		 */
		public Builder withActivityDescription(String activityDescription) {
			activityToBuild.m_activityDescription = activityDescription;
			return this;
		}
		
		/**
		 * <p>Each {@link Activity} must have a location and this method sets it for {@link Builder#activityToBuild}.</p>		
		 * 
		 * @param activityLocation - location where the activity to be built can be conducted.
		 * @return {@link Builder} - builder with an activity location set for {@link Builder#activityToBuild}.
		 */
		public Builder withActivityLocation(ActivityLocation activityLocation) {
			activityToBuild.m_activityLocation = activityLocation;
			return this;
		}
		
		/**
		 * <p>Each {@link Activity} must be declared as either a joint or an individual activity and this method sets this information for {@link Builder#activityToBuild}.</p>
		 * 
		 * <p><b>Note:</b> If you declare an activity as being a joint activity, then make sure to set the correct {@link NetworkType} in {@link Builder#withNetworkType(NetworkType)} as well. Otherwise,
		 * i.e. if it is an individual activity it is suggested to set the network type to {@link NetworkType#NONE}.</p>
		 * 
		 * @param isJointActivity - indication whether this activity is a joint or a individual activity.
		 * @return {@link Builder} - builder with an indication on whether {@link Builder#activityToBuild} is a joint or individual activity.
		 */
		public Builder withIsJointActivity(boolean isJointActivity) {
			activityToBuild.m_isJointActivity = isJointActivity;
			return this;
		}
		
		/**
		 * <p>Each {@link Activity} must have the time defined in terms of day of week and time of day (referring to its start time) it is available and this method sets this information for {@link Builder#activityToBuild}.</p>
		 * 
		 * <p><b>Note:</b>The parameters of this methods allow you to define an interval for a given day of week which represents all <b>start times</b> on which the activity to be initialized can be <b>started</b>. E.g. by using
		 * (1, 8, 0, 9, 0) as parameters you allow individuals to start this activity on any monday from 08:00 until 09:00 independent of how long its duration will be. Use {@link DateTimeConstants} to indicate the day of week.
		 * You can call this method any number of times to declare different intervals of available start times. Alternatively, use any of the other methods to set the availability.</p>
		 * 
		 * <p><b>Important:</b>Ideally you should not declare overlapping intervals in order to prevent confusion and improve performance. It is also recommended to take the empirical duration (in terms of mean and standard deviation) 
		 * of an activity into consideration when defining its availability.</p>
		 * 
		 * @param dayOfWeek - day of week the interval of start times is being declared for.
		 * @param startHourOfDay - hour of day representing the start of the interval representing possible start times.
		 * @param startMinuteOfDay - minute of day representing the start of the interval representing possible start times.
		 * @param endHourOfDay - hour of day representing the end of the interval representing possible start times.
		 * @param endMinuteOfDay - minute of day representing the end of the interval of possible start times.
		 * @return {@link Builder} - builder with information about available start times for the {@link Builder#activityToBuild} expressed as intervals on days of the week.
		 */
		public Builder withAvailabilityInterval(int dayOfWeek, int startHourOfDay, int startMinuteOfDay, int endHourOfDay, int endMinuteOfDay) {
			if (startHourOfDay >= endHourOfDay) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, "Interval is invalid: start is after end. The built activity may be unusable!");
			}
			DateTime startOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, startHourOfDay, startMinuteOfDay);
			DateTime endOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, endHourOfDay, endMinuteOfDay);
			if (activityToBuild.m_availability.get(dayOfWeek) == null) {
				activityToBuild.m_availability.put(dayOfWeek, new ArrayList<>());
			}
			activityToBuild.m_availability.get(dayOfWeek).add(new Interval(startOfInterval, endOfInterval));
			return this;
		}

		/**
		 * <p>Each {@link Activity} must have the time defined in terms of days of week and time of day (referring to its start time) it is available and this method sets this information for {@link Builder#activityToBuild}.</p>
		 * 
		 * <p><b>Note:</b> This methods works the same as {@link Builder#withAvailabilityInterval(int, int, int, int, int)} except that it allows you to define an interval for more than day of week at once.
		 * 
		 * <p><b>Important:</b>Ideally you should not declare overlapping intervals in order to prevent confusion and improve performance. It is also recommended to take the empirical duration (in terms of mean and standard deviation) 
		 * of an activity into consideration when defining its availability.</p>
		 * 
		 * @param startHourOfDay - hour of day representing the start of the interval representing possible start times.
		 * @param startMinuteOfDay - minute of day representing the start of the interval representing possible start times.
		 * @param endHourOfDay - hour of day representing the end of the interval representing possible start times.
		 * @param endMinuteOfDay - minute of day representing the end of the interval of possible start times.
		 * @param daysOfWeek - days of week the interval of start times is being declared for.
		 * @return {@link Builder} - builder with information about available start times for the {@link Builder#activityToBuild} expressed as intervals on days of the week.
		 */
		public Builder withAvailabilityIntervalAtDays(int startHourOfDay, int startMinuteOfDay, int endHourOfDay, int endMinuteOfDay, ArrayList<Integer> daysOfWeek) {
			if (startHourOfDay >= endHourOfDay) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, "Interval is invalid: start is after end. The built activity may be unusable!");
			}
			DateTime startOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, startHourOfDay, startMinuteOfDay);
			DateTime endOfInterval = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, endHourOfDay, endMinuteOfDay);
			for (Integer day: daysOfWeek) {
				if (activityToBuild.m_availability.get(day) == null) {
					activityToBuild.m_availability.put(day, new ArrayList<>());
				}
				activityToBuild.m_availability.get(day).add(new Interval(startOfInterval, endOfInterval));
			}
			return this;
		}
		
		/**
		 * <p>Each {@link Activity} must have defined a {@link NeedTimeSplit} and this method sets it for {@link Builder#activityToBuild}. See {@link NeedTimeSplit} for more information about the concept of a
		 * need time split. In the context of an activity the {@link NeedTimeSplit} defines how the time an {@link Individual} spends on executing the activity is allocated resp. split to the variouse needs 
		 * fulfilled by the activity for each unit of time (i.e. minute). E.g. suppose the {@link NeedTimeSplit} for an activity is defined as {@link Need#LEISURE}=0.75 and {@link Need#FREEDOM}=0.25 then for each
		 * minute the {@link Individual} spends executing the activity 45s will be added to the total (i.e. absolute) time spent satisfying {@link Need#LEISURE} and 15s will be added to the total time spent 
		 * satisfying {@link Need#FREEDOM}.</p>
		 * 
		 * @param needTimeSplit - the need time split defining how each unit of time spent executing this activity is allocated to the respective needs satisfied by this activity.
		 * @return {@link Builder} - builder with information about need time split of the {@link Builder#activityToBuild}.
		 */
		public Builder withNeedTimeSplit(NeedTimeSplit needTimeSplit) {
			activityToBuild.m_needTimeSplit = needTimeSplit;
			return this;
		}
		
		/**
		 * <p>Each {@link Activity} can have of some examples of what it actually covers and this method sets this information for {@link Builder#activityToBuild}.</p>
		 * 
		 * @param examples - examples illustrating what this activity represents
		 * @return {@link Builder} - builder with information about examples of the {@link Builder#activityToBuild}.
		 */
		public Builder withExamples(String examples) {
			activityToBuild.m_examples = examples;
			return this;
		}
		
		/**
		 * <p>Each {@link Activity} must have a {@link NetworkType} and this method sets this information for {@link Builder#activityToBuild}. The network type determines with what network this activity
		 * must be executed. E.g. if the {@link NetworkType} is set to {@link NetworkType#HOUSEHOLD_NETWORK} then it can only be executed together with members of the household. If the type is set to
		 * {@link NetworkType#NONE} then it can only be executed alone.</p>
		 * 
		 * @param networkType - the network type with members of which this activity must be executed together with, or none if the activity must be executed alone.
		 * @return {@link Builder} - builder with information about the network type of the {@link Builder#activityToBuild}.
		 */
		public Builder withNetworkType(NetworkType networkType) {
			activityToBuild.m_networkType = networkType;
			return this;
		}
		
		/**
		 * <p>This method ensures that all mandatory variables of an {@link Activity} are set i.e. not <code>null</code>.</p>
		 * 
		 * <p><b>Note:</b> As the default for boolean variables is <code>false</code> we do not cover it here. However, keep in mind that it will be <code>false</code> if not set when building the activity.
		 * 
		 * @return The string of the first field that is not set. <code>null</code> if all mandatory fields are set.
		 */
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
			return null;
		}
		
		/**
		 * <p>This method is used to build an {@link Activity} once all the mandatory fields are set and initialize a new {@link Activity} to be built.</p>
		 * 
		 * <p><b>Note:</b> The {@link Activity} will be built independent of wheter it is complete or not, but information about missing attributes will be logged. It is very likely
		 * that a simulation with not completely inizialized {@link Activity}s will crash at some point or at least lead to unexpected behavior / output. You should therfore prevent such
		 * situations by keeping an eye on the log and fix all of the logged errors before evaluating the simulations output.</p>
		 * 
		 * @return builtActivity - the activity built
		 */
		public Activity build() {
			if (checkIfAnyFieldIsNull() != null) {
				Logger.getLogger(Activity.class.getName()).log(Level.SEVERE, String.format("%s is null i.e. not set! The built activity may be unusable!", checkIfAnyFieldIsNull()));
			}
			Activity builtActivity = activityToBuild;
			activityToBuild = new Activity();
			return builtActivity;
		}
	}

	/**
	 * @category Getters
	 * 
	 * This section contains all getters for {@link Activity}s.
	 */
	
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
	
	public boolean isAvailableAt(int weekDay) {
		if (m_availability.get(weekDay) == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	private boolean isAvailableAt(int weekDay, Interval baseInterval) {
		if (m_availability.get(weekDay) == null) {
			return false;
		}
		Predicate<Interval> overlapsOrAbuts = i -> i.overlaps(baseInterval) || baseInterval.abuts(i);
		return m_availability.get(weekDay).stream().anyMatch(overlapsOrAbuts);
	}
	
	public boolean isAvailableAt(int weekDay, DateTime currentTimeInBaseTime) {
		if (m_availability.get(weekDay) == null) {
			return false;
		}
		Interval currentTimeAsInterval = new Interval(currentTimeInBaseTime, currentTimeInBaseTime);
		return isAvailableAt(weekDay, currentTimeAsInterval);
	}
	
	public NeedTimeSplit getNeedTimeSplit() {
		return m_needTimeSplit;
	}
	
	public String getExamples() {
		return m_examples;
	}

	public NetworkType getNetworkType() {
		return m_networkType;
	}
}
