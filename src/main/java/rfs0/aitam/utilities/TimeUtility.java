package rfs0.aitam.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import rfs0.aitam.activity.ActivityAgenda;
import rfs0.aitam.environment.Environment;
import rfs0.aitam.individual.Individual;
import rfs0.aitam.settings.ISimulationSettings;

/**
 * <p>This class is used to handle all operations related to time.</p>
 */
public final class TimeUtility {
	
	private static final Logger LOG = Logger.getLogger(TimeUtility.class.getName());
	
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
	
	public static boolean isIntervalOverlappingAnyAgenda(ArrayList<Individual> networkMembersParticipating, Interval intervalOfInterest) {
		for (Individual individual: networkMembersParticipating) {
			for (Interval plannedJointActivityInterval: individual.getJointActivityAgenda().getIntervals()) {
				if (plannedJointActivityInterval.overlaps(intervalOfInterest)) {
					return true;
				}
			}
			for (Interval plannedIndividualActivityInterval: individual.getActivityAgenda().getIntervals()) {
				if (plannedIndividualActivityInterval.overlaps(intervalOfInterest)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isDayFullyPlanned(Environment environment, ActivityAgenda randomAgenda) {
		DateTime currentTime = environment.getSimulationTime().getCurrentDateTime();
		DateTime endOfCurrentDay = TimeUtility.getEndOfCurrentDay(currentTime);
		DateTime startOfCurrentDay = TimeUtility.getStartOfCurrentDay(currentTime);
		// nothing planned yet 
		if (randomAgenda.getFirstPlannedInterval() == null) {
			return false;
		}
		// last activity does not end at end of day
		if (!randomAgenda.getLastPlannedInterval().getEnd().equals(TimeUtility.getEndOfCurrentDay(currentTime))) {
			return false;
		}
		Interval preceding = null;
		Interval latter = null;
		Iterator<Interval> intervalIterator = randomAgenda.getIntervals().iterator();
		if (!intervalIterator.hasNext()) {
			return false;
		}
		// first planned interval
		else {
			preceding = intervalIterator.next();
		}
		// only one interval planned and it covers the whole day
		if (preceding.getStart().equals(startOfCurrentDay) && preceding.getEnd().equals(endOfCurrentDay)) {
			return true;
		}
		else if (!intervalIterator.hasNext()) {
			return false;
		}
		// two or more intervals planned
		latter = preceding;
		while (intervalIterator.hasNext()) {
			// no gap found yet & end not reached yet -> advance
			preceding = latter;
			latter = intervalIterator.next();
			// gap between two consecutive intervals
			if (!latter.abuts(preceding)) {
				return false;
			}
			// last planned interval
			if (!intervalIterator.hasNext()) {
				// end is at end of day -> covers whole day
				if (latter.getEnd().equals(endOfCurrentDay) || latter.getEnd().isAfter(endOfCurrentDay)) {
					return true;
				}
				// end is before end of day -> does not cover whole day
				else {
					return false;
				}
			}
		}
		LOG.log(Level.SEVERE, "Could not determine whether day is fully planned or not!");
		return false;
	}
	
	public static Interval getFirstAvailableInterval(Environment environment, ActivityAgenda agenda) {
		DateTime currentTime = environment.getSimulationTime().getCurrentDateTime();
		DateTime endOfCurrentDay = TimeUtility.getEndOfCurrentDay(currentTime);
		Interval preceding = null;
		Interval latter = null;
		Iterator<Interval> intervalIterator = agenda.getIntervals().iterator();
		// nothing planned yet
		if (!intervalIterator.hasNext()) {
			return new Interval(currentTime, endOfCurrentDay);
		}
		else {
			// first interval
			preceding = intervalIterator.next();
			// gap between now and first interval
			if (preceding.getStart().isAfter(currentTime)) {
				return new Interval(currentTime, preceding.getStart());
			}
		}
		// only one interval planned
		if (!intervalIterator.hasNext()) {
			// gap between first interval and end of current day
			return new Interval(preceding.getEnd(), endOfCurrentDay);
		}
		latter = preceding;
		// two or more intervals planned
		while (intervalIterator.hasNext()) {
			preceding = latter;
			latter = intervalIterator.next();
			// gap between two consecutive intervals
			if (!latter.abuts(preceding)) {
				return new Interval(preceding.getEnd(), latter.getStart());
			}
			// last planned interval & everything before was abutting
			if (!intervalIterator.hasNext()) {
				// whole day planned -> no interval available
				if (latter.getEnd().equals(endOfCurrentDay)) {
					return null;
				}
				// gap between last interval planned and end of day
				return new Interval(latter.getEnd(), endOfCurrentDay);
			}
			
		}
		LOG.log(Level.SEVERE, "Did not find any available interval!");
		return null;
	}
	
	public static Interval convertToBaseInterval(Interval realInterval) {
		DateTime baseStart = realInterval.getStart()
				.withYear(ISimulationSettings.BASE_YEAR)
				.withMonthOfYear(ISimulationSettings.BASE_MONTH)
				.withDayOfMonth(ISimulationSettings.BASE_DAY);
		DateTime baseEnd = realInterval.getEnd()
				.withYear(ISimulationSettings.BASE_YEAR)
				.withMonthOfYear(ISimulationSettings.BASE_MONTH)
				.withDayOfMonth(ISimulationSettings.BASE_DAY);
		return new Interval(baseStart, baseEnd);
	}
	
	public static Interval convertToRealInterval(DateTime currentDateTime, Interval baseInterval) {
		DateTime realStart = baseInterval.getStart()
				.withYear(currentDateTime.getYear())
				.withMonthOfYear(currentDateTime.getMonthOfYear())
				.withDayOfMonth(currentDateTime.getDayOfMonth());
		DateTime realEnd = baseInterval.getEnd()
				.withYear(currentDateTime.getYear())
				.withMonthOfYear(currentDateTime.getMonthOfYear())
				.withDayOfMonth(currentDateTime.getDayOfMonth());
		return new Interval(realStart, realEnd);
	}
}
