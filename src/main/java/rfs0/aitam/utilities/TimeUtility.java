package rfs0.aitam.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import rfs0.aitam.commons.ISimulationSettings;

public final class TimeUtility {
	
	public TimeUtility() {}
	
	public static ArrayList<Interval> sortTimeIntervals(ArrayList<Interval> unsortedTimeIntervals) {
		Comparator<Interval> compareStartOfIntervals = (i1, i2) -> {
			if (i1.getStart().isBefore(i2.getStart())) {
				return -1;
			}
			else {
				return 1;
			}
		};
		return unsortedTimeIntervals.stream().sorted(compareStartOfIntervals).collect(Collectors.toCollection(ArrayList::new));
	}
	
	public static DateTime getStartOfCurrentDay(DateTime currentDay) {
		return currentDay.withHourOfDay(ISimulationSettings.BASE_HOUR).withMinuteOfHour(ISimulationSettings.BASE_MINUTE);
	}
	
	public static DateTime getEndOfCurrentDay(DateTime currentDay) {
		return currentDay.withHourOfDay(23).withMinuteOfHour(59);
	}
	
	public static DateTime getStartOfNextDay(DateTime day) {
		return day.plusDays(1).withHourOfDay(ISimulationSettings.BASE_HOUR).withMinuteOfHour(ISimulationSettings.BASE_MINUTE);
	}
}
