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
			for (Interval plannedInterval: individual.getJointActivityAgenda().getIntervals()) {
				if (plannedInterval.overlaps(intervalOfInterest)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isDayFullyPlanned(Environment environment, ActivityAgenda randomAgenda) {
		DateTime currentTime = environment.getSimulationTime().getCurrentDateTime();
		DateTime endOfCurrentDay = TimeUtility.getEndOfCurrentDay(currentTime);
		if (randomAgenda.getFirstPlannedInterval() == null || !randomAgenda.getFirstPlannedInterval().getStart().equals(TimeUtility.getStartOfCurrentDay(currentTime))) {
			return false;
		}
		if (!randomAgenda.getLastPlannedActivityInterval().getEnd().equals(TimeUtility.getEndOfCurrentDay(currentTime))) {
			return false;
		}
		Interval preceding = null;
		Interval latter = null;
		Iterator<Interval> intervalIterator = randomAgenda.getIntervals().iterator();
		if (!intervalIterator.hasNext()) {
			return false;
		}
		else {
			preceding = intervalIterator.next();
		}
		if (preceding.getEnd().equals(endOfCurrentDay)) {
			return true;
		}
		else if (!intervalIterator.hasNext()) {
			return false;
		}
		else {
			latter = intervalIterator.next();
		}
		do {
			if (!latter.abuts(preceding)) {
				return false;
			}
			if (!intervalIterator.hasNext()) {
				if (!latter.getEnd().equals(endOfCurrentDay)) {
					return false;
				}
				else {
					return true;
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
		if (!intervalIterator.hasNext()) {
			return new Interval(currentTime, endOfCurrentDay);
		}
		else {
			preceding = intervalIterator.next();
			if (!preceding.getStart().equals(currentTime)) {
				return new Interval(currentTime, preceding.getStart());
			}
		}
		if (!intervalIterator.hasNext()) {
			return new Interval(preceding.getEnd(), endOfCurrentDay);
		}

		do {
			latter = intervalIterator.next();
			if (!latter.abuts(preceding)) {
				if (preceding.getEnd().isAfter(latter.getStart())) {
					System.out.println(preceding);
					System.out.println(latter);
				}
				return new Interval(preceding.getEnd(), latter.getStart());
			}
			else if (!intervalIterator.hasNext()) {
				if (latter.getEnd().equals(endOfCurrentDay)) {
					return null;
				}
				return new Interval(latter.getEnd(), endOfCurrentDay);
			}
			else {
				preceding = latter;
			}
		} 
		while (intervalIterator.hasNext());
		return null;
	}
}
