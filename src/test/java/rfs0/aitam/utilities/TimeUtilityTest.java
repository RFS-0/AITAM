package rfs0.aitam.utilities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Test;

import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.TimeUtility;

public class TimeUtilityTest {

	@Test
	public void testSortTimePairs() {
		DateTime startOfFirstInterval = new DateTime(2018, 1, 1, 8, 0);
		DateTime endOfFirstInterval = new DateTime(2018, 1, 1, 12, 0);
		DateTime startOfSecondInterval = new DateTime(2018, 1, 1, 12, 0);
		DateTime endOfSecondInterval = new DateTime(2018, 1, 1, 16, 0);
		DateTime startOfThirdInterval = new DateTime(2018, 1, 1, 17, 0);
		DateTime endOfThirdInterval = new DateTime(2018, 1, 1, 20, 0);
		
		Interval firstInterval = new Interval(startOfFirstInterval, endOfFirstInterval);
		Interval secondInterval = new Interval(startOfSecondInterval, endOfSecondInterval);
		Interval thirdInterval = new Interval(startOfThirdInterval, endOfThirdInterval);
		
		ArrayList<Interval> unsortedIntervals = Stream.of(thirdInterval, secondInterval, firstInterval).collect(Collectors.toCollection(ArrayList::new));
		ArrayList<Interval> sortedTimeIntervals = TimeUtility.sortTimeIntervals(unsortedIntervals);
		
		assertEquals(startOfFirstInterval, sortedTimeIntervals.get(0).getStart());
		assertEquals(startOfSecondInterval, sortedTimeIntervals.get(1).getStart());
		assertEquals(startOfThirdInterval, sortedTimeIntervals.get(2).getStart());
	}
	
	@Test
	public void testGetNextDay() {
		int firstDayOfMonth = 1;
		int secondDayOfMonth = 2;
		DateTime firstMondayAfterNoon = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 59);
		assertEquals(DateTimeConstants.MONDAY, firstMondayAfterNoon.getDayOfWeek());
		
		DateTime startOfFirstTuesday = TimeUtility.getStartOfNextDay(firstMondayAfterNoon);
		assertEquals(DateTimeConstants.TUESDAY, startOfFirstTuesday.getDayOfWeek());
		assertEquals(ISimulationSettings.BASE_YEAR, startOfFirstTuesday.getYear());
		assertEquals(ISimulationSettings.BASE_MONTH, startOfFirstTuesday.getMonthOfYear());
		assertEquals(secondDayOfMonth, startOfFirstTuesday.getDayOfMonth());
		assertEquals(ISimulationSettings.BASE_HOUR, startOfFirstTuesday.getHourOfDay());
		assertEquals(ISimulationSettings.BASE_MINUTE, startOfFirstTuesday.getMinuteOfDay());
		
		DateTime lastDayOfJanuary = firstMondayAfterNoon.dayOfMonth().withMaximumValue();
		assertEquals(DateTimeConstants.WEDNESDAY, lastDayOfJanuary.getDayOfWeek());
		
		DateTime firstDayOfFebruary = TimeUtility.getStartOfNextDay(lastDayOfJanuary);
		assertEquals(ISimulationSettings.BASE_YEAR, firstDayOfFebruary.getYear());
		assertEquals(DateTimeConstants.FEBRUARY, firstDayOfFebruary.getMonthOfYear());
		assertEquals(firstDayOfMonth, firstDayOfFebruary.getDayOfMonth());
		assertEquals(DateTimeConstants.THURSDAY, firstDayOfFebruary.getDayOfWeek());
		assertEquals(ISimulationSettings.BASE_HOUR, firstDayOfFebruary.getHourOfDay());
		assertEquals(ISimulationSettings.BASE_MINUTE, firstDayOfFebruary.getMinuteOfDay());
	}
}
