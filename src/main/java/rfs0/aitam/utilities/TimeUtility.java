package rfs0.aitam.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.joda.time.Interval;

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
}
