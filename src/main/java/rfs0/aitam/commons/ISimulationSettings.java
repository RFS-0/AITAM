package rfs0.aitam.commons;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public interface ISimulationSettings {
	
	/**
	 * Constants used to configure environment
	 */
	public static final int ENVIRONMENT_HEIGHT = 1000;
	public static final int ENVIRONMENT_WIDTH = 1000;
	public static final String BUILDINGS_FILE = "\\data\\environment\\buildings\\buildings.shp";
	public static final String PATHS_FILE = "\\data\\environment\\paths\\paths.shp";
	public static final double MAX_TRAFFIC_CAPACITY_PER_UNIT_LENGHT = 0.04; // assuming reaction time of 1.8 s and average velocity of 50 km/h
	public static final double MAX_VELOCITY = 13.9; // max velocity in m/s (equivalent to 50 km/h)
	public static final double MAX_SLOW_DOWN_FACTOR = 0.2;
	public static final ArrayList<Integer> WEEK = Stream.of(
			DateTimeConstants.MONDAY, 
			DateTimeConstants.TUESDAY, 
			DateTimeConstants.WEDNESDAY, 
			DateTimeConstants.THURSDAY, 
			DateTimeConstants.FRIDAY, 
			DateTimeConstants.SATURDAY, 
			DateTimeConstants.SUNDAY)
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<Integer> WORK_WEEK = Stream.of(
			DateTimeConstants.MONDAY, 
			DateTimeConstants.TUESDAY, 
			DateTimeConstants.WEDNESDAY, 
			DateTimeConstants.THURSDAY, 
			DateTimeConstants.FRIDAY)
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<Integer> WEEKEND = Stream.of(
			DateTimeConstants.SATURDAY, 
			DateTimeConstants.SUNDAY)
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<DateTime> AVAILABLE_START_TIMES_FOR_HOUSEHOLD_NETWORK_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 20, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<DateTime> AVAILABLE_START_TIMES_FOR_WORK_COLLEGUES_NETWORK_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 20, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<DateTime> AVAILABLE_START_TIMES_FOR_FRIENDS_NETWORK_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 20, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<DateTime> AVAILABLE_TIME_POINTS_FOR_PLANNING_OF_JOINT_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 0, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 30))
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<DateTime> AVAILABLE_TIME_POINTS_FOR_PLANNING_OF_INDIVIDUAL_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 0, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 30))
			.collect(Collectors.toCollection(ArrayList::new));
	public static final ArrayList<BigDecimal> ACTIVITY_DURATIONS_IN_MINUTES = Stream.of(
			BigDecimal.valueOf(60), 
			BigDecimal.valueOf(90), 
			BigDecimal.valueOf(120))
			.collect(Collectors.toCollection(ArrayList::new));
	public static final int MIN_DURATION = 30;
	public static final int MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY = 3;
	public static final int NUMBER_OF_PLANS_TO_GENERATE = 10;
	
	/**
	 * Constants used to configure UI of environment
	 */
	public static final double SIZE_OF_AGENT = 3.0;
	public static final double SIZE_OF_BUILDING = 3.0;
	public static final double SIZE_OF_PATH = 1.0; // not actually used by portrayal
	public static final Color COLOR_OF_AGENT = new Color(255, 255, 255); // white
	public static final Color COLOR_OF_AGENT_SELECTED = new Color(200, 20, 120);
	public static final Color COLOR_OF_BACKGROUND = new Color(30, 40, 50);
	public static final Color COLOR_OF_BUILDING = new Color(70, 90, 100);
	public static final Color COLOR_OF_BUILDING_SELECTED = new Color(255, 100, 160);
	public static final Color COLOR_OF_PATH = new Color(80, 140, 160);
	public static final Color COLOR_OF_PATH_SELECTED = new Color(250, 70, 130);
	public static final Color COLOR_OF_START = new Color(250, 180, 190);
	public static final Color COLOR_OF_TARGET = new Color(255, 100, 160);
	
	/**
	 * Constants used for simulating time
	 */
	public static final int BASE_YEAR = 2018;
	public static final int BASE_MONTH = 1;
	public static final int BASE_DAY = 1;
	public static final int BASE_HOUR = 0;
	public static final int BASE_MINUTE = 0;
	
	/**
	 * Constants used for handling numbers & calculations
	 */
	public static final int PRECISION_USED_FOR_BIG_DECIMAL = 6;
	public static final int SCALE_USED_FOR_BIG_DECIMAL = 6;
	public static final RoundingMode ROUNDING_MODE_USED_FOR_BIG_DECIMAL = RoundingMode.HALF_UP;
	public static final BigDecimal TOLERATED_ROUNDING_ERROR = BigDecimal.valueOf(0.00001);
	
	/**
	 * Constants used for simulating individuals
	 */
	public static final int NUMBER_OF_INDIVIDUALS = 30;
	public static final int MIN_NUMBER_OF_HOUSEHOLD_MEMBERS = 1;
	public static final int MAX_NUMBER_OF_HOUSEHOLD_MEMBERS = 4;
	// TODO: maybe used different probability for planning and accepting a request? -> also justify probablilities
	public static final double PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY = 0.8;
	public static final int MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY = 3;
	public static final int MIN_NUMBER_OF_WORK_COLLEGUES = 1;
	public static final int MAX_NUMBER_OF_WORK_COLLEGUES = 4;
	public static final int MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY = 1;
	// TODO: maybe used different probability for planning and accepting a request?
	public static final double PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY = 0.25;
	public static final int MIN_NUMBER_OF_FRIENDS = 1;
	public static final int MAX_NUMBER_OF_FRIENDS = 4;
	public static final int MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY = 1;
	// TODO: maybe used different probability for planning and accepting a request?
	public static final double PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY = 0.5;
	
	/**
	 * Attributes of buildings
	 */
	public static final String ATTRIBUTE_MASON_GEOMETRY_OF_CLOSEST_PATH = "MASON_GEOMETRY_OF_CLOSEST_PATH";
	public static final String ATTRIBUTE_ACTIVITY_CATEGORY = "ACTIVITY_CATEGORY";
	
	/**
	 * Max distances to related activity locations
	 */
	public static final double MAX_DISTANCE_TO_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE = 100;
	public static final double MAX_DISTANCE_TO_THIRD_PLACE_FOR_WORK = 100;
	public static final double MAX_DISTANCE_TO_THIRD_PLACE_FOR_LEISURE = 100;
}
