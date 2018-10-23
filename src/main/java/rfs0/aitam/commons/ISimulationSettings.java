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
	public static final String SIMULATION_OUTPUT_FOLDER = "\\data\\output\\";
	public static final String CHAR_SET = "UTF-8";
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
	// TODO: write test to ensure that for each start time there is at least one activity that covers it (incl. duration) -> otherwise list of available activities might be empty during planning
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
	// TODO: write test to ensure that for each start time there is at least one activity that covers it (incl. duration) -> otherwise list of available activities might be empty during planning
	public static final ArrayList<DateTime> AVAILABLE_START_TIMES_FOR_WORK_COLLEGUES_NETWORK_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	// TODO: write test to ensure that for each start time there is at least one activity that covers it (incl. duration) -> otherwise list of available activities might be empty during planning
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
	public static final ArrayList<DateTime> AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES = Stream.of(
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
	public static final int MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY = 5;
	public static final int NUMBER_OF_PLANS_TO_GENERATE = 100;
	
	/**
	 * Constants used to configure UI of environment
	 */
	public static final double SIZE_OF_AGENT = 3.0;
	public static final double SIZE_OF_AGENT_SELECTED = 3.0;
	public static final double SIZE_OF_BUILDING = 2.5;
	public static final double SIZE_OF_BUILDING_SELCTED = 5.0; 
	public static final double SIZE_OF_PATH = 1.0; // TODO: not actually used by portrayal
	public static final Color COLOR_FOR_DEBUG = new Color(235, 59, 90); // desire (red)
	public static final Color COLOR_OF_SELECTED_ENTITY = new Color(38, 222, 129); // reptile green
	public static final Color COLOR_OF_AGENT = new Color(165, 94, 234); // lighter purple
	public static final Color COLOR_OF_BACKGROUND = Color.white;
	public static final Color COLOR_OF_BUILDING = new Color(69, 170, 242); // high blue
	public static final Color COLOR_OF_TARGET_BUILDING = new Color(250, 130, 49); // beniukon bronze
	public static final Color COLOR_OF_PATH = new Color(209, 216, 224); // twinkle blue
	
	/**
	 * Constants used for simulating time
	 */
	public static final int BASE_YEAR = 2018;
	public static final int BASE_MONTH = 1;
	public static final int BASE_DAY = 1;
	public static final int BASE_HOUR = 0;
	public static final int BASE_MINUTE = 0;
	public static final int LAST_HOUR_OF_DAY = 23;
	public static final int LAST_MINUTE_OF_HOUR = 59;
	public static final DateTime START_OF_DAY = new DateTime(BASE_YEAR, BASE_MONTH, BASE_DAY, BASE_HOUR, BASE_MINUTE);
	public static final DateTime END_OF_DAY = new DateTime(BASE_YEAR, BASE_MONTH, BASE_DAY, LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR);
	
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
	public static final int NUMBER_OF_INDIVIDUALS = 5;
	public static final int MIN_NUMBER_OF_HOUSEHOLD_MEMBERS = 1;
	public static final int MAX_NUMBER_OF_HOUSEHOLD_MEMBERS = 4;
	// TODO: maybe used different probability for planning and accepting a request? -> also justify probablilities
	public static final double PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY = 0.9;
	public static final int MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY = 3;
	public static final int MIN_NUMBER_OF_WORK_COLLEGUES = 1;
	public static final int MAX_NUMBER_OF_WORK_COLLEGUES = 4;
	public static final int MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY = 1;
	// TODO: maybe used different probability for planning and accepting a request?
	public static final double PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY = 0.7;
	public static final int MIN_NUMBER_OF_FRIENDS = 1;
	public static final int MAX_NUMBER_OF_FRIENDS = 4;
	public static final int MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY = 1;
	// TODO: maybe used different probability for planning and accepting a request?
	public static final double PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY = 0.7;
	
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
	
	/**
	 * Constants used to describe simulation output
	 */
	public static final String TIME_STAMP = "time stamp";
	public static final String TOTAL_NUMBER_OF_AGENTS = "total number of agents";
	public static final String LEISURE_AT_HOME_ALONE_ACTIVITY= "Leisure at home alone";
	public static final String LEISURE_AT_HOME_WITH_HOUSEHOLD_MEMBERS = "Leisure at home with household members";
	public static final String LEISURE_AT_HOME_WITH_FRIENDS = "Leisure at home with friends";
	public static final String LEISURE_AT_THIRD_PLACE_ALONE = "Leisure at 3rd place alone";
	public static final String LEISURE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS = "Leisure at 3rd place with household members";
	public static final String LEISURE_AT_THIRD_PLACE_WITH_FRIENDS = "Leisure at home with friends";
	public static final String WORK_AT_HOME_ALONE = "Work at home alone";
	public static final String WORK_AT_WORK_PLACE_ALONE = "Work at work location alone";
	public static final String WORK_AT_WORK_PLACE_WITH_COWORKERS = "Work at work location with coworkers";
	public static final String WORK_AT_THIRD_PLACE_ALONE = "Work at 3rd for work alone";
	public static final String WORK_AT_THIRD_PLACE_WITH_COWORKERS = "Work at 3rd for work with coworkers";
	public static final String WORK_DURING_TRAVEL_ALONE = "Work during travel alone";
	public static final String WORK_DURING_TRAVEL_WITH_COWORKERS = "Work during travel with coworkers";
	public static final String PERSONAL_CARE_AT_HOME_ALONE = "Personal care at home alone";
	public static final String PERSONAL_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS = "Personal care at home with household members";
	public static final String PERSONAL_CARE_AT_HOME_WITH_FRIENDS = "Personal care at home with friends";
	public static final String PERSONAL_CARE_AT_WORK_ALONE = "Personal care at work alone";
	public static final String PERSONAL_CARE_AT_WORK_WITH_COWORKERS = "Personal care at work with coworkers";
	public static final String PERSONAL_CARE_AT_THIRD_PLACE_ALONE = "Personal care at 3rd place alone";
	public static final String PERSONAL_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS = "Personal care at 3rd place with household members";
	public static final String PERSONAL_CARE_AT_THIRD_PLACE_WITH_FRIENDS = "Personal care at 3rd place with friends";
	public static final String HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_ALONE = "Household/family care at home alone";
	public static final String HOUSEHOLD_AND_FAMILY_CARE_AT_HOME_WITH_HOUSEHOLD_MEMBERS = "Household/family care at home with household members";
	public static final String HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_ALONE = "Household/family care at 3rd place alone";
	public static final String HOUSEHOLD_AND_FAMILY_CARE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS = "Household/family care at 3rd place with household members";
	public static final String IDLE_AT_HOME = "Idle at home";
	public static final String IDLE_AT_WORK = "Idle at work";
	public static final String IDLE_AT_LEISURE = "Idle at leisure";
	public static final String IDLE_AT_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE = "Idle at 3rd place for household and family care";
	public static final String IDLE_AT_THIRD_PLACE_FOR_WORK = "Idle at 3rd for work";
	public static final String IDLE_AT_THIRD_PLACE_FOR_LEISURE = "Idle at 3rd for leisure";
	public static final String TRAVEL = "Travel";
}
