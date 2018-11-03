package rfs0.aitam.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import rfs0.aitam.activities.ActivityAgenda;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.individuals.Individual;
import rfs0.aitam.model.Environment;

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
		// only one interval planned and it does not cover rest of day
		if (preceding.getEnd().equals(endOfCurrentDay)) {
			return true;
		}
		else if (!intervalIterator.hasNext()) {
			return false;
		}
		// two or more intervals planned
		else {
			latter = intervalIterator.next();
		}
		do {
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
			else {
				preceding = latter;
				latter = intervalIterator.next();
			}
		}
		while (intervalIterator.hasNext());
		return true;
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
		// two or more intervals planned
		do {
			latter = intervalIterator.next();
			// gap between two consecutive intervals
			if (!latter.abuts(preceding)) {
				return new Interval(preceding.getEnd(), latter.getStart());
			}
			// last planned interval
			else if (!intervalIterator.hasNext()) {
				// whole day planned -> no interval available
				if (latter.getEnd().equals(endOfCurrentDay)) {
					return null;
				}
				// gap between last interval planned and end of day
				return new Interval(latter.getEnd(), endOfCurrentDay);
			}
			// no gap found yet and end of agenda not reached yet
			else {
				preceding = latter;
			}
		} 
		while (intervalIterator.hasNext());
		// can never happen
		return null;
	}
	
	public static Interval convertToBaseInterval(Interval realInterval) {
		DateTime baseStart = realInterval.getStart()
				.withYear(ISimulationSettings.BASE_YEAR)
				.withMonthOfYear(ISimulationSettings.BASE_MONTH)
				.withDayOfWeek(realInterval.getStart().getDayOfWeek());
		DateTime baseEnd = realInterval.getEnd()
				.withYear(ISimulationSettings.BASE_YEAR)
				.withMonthOfYear(ISimulationSettings.BASE_MONTH)
				.withDayOfWeek(realInterval.getStart().getDayOfWeek());
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
