package rfs0.aitam.settings;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import rfs0.aitam.activity.Activity;
import rfs0.aitam.activity.ActivityCategory;
import rfs0.aitam.environment.Environment;
import rfs0.aitam.environment.EnvironmentOutputRecorder;
import rfs0.aitam.environment.EnvironmentWithUI;
import rfs0.aitam.individual.Individual;
import rfs0.aitam.utilities.CalculationUtility;
import sim.field.network.Network;
import sim.util.geo.MasonGeometry;

public interface ISimulationSettings {
		
	
	/**
	 * @category Configuration of aspects related to randomness.
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to randomness.</p>
	 * 
	 * <p>{@link ISimulationSettings#SEED}: The seed used for all random number generators.</p>
	 * <p>{@link ISimulationSettings#RANDOM_NUMBER_GENERATOR}: The random number generator used to sample activity durations.</p>
	 */
	public static final long SEED = 1L;
	/**
	 * <p>The random number generator used to sample activity durations.</p>
	 */
	public static final MersenneTwister RANDOM_NUMBER_GENERATOR = new MersenneTwister(SEED);
		
	/**
	 * @category Configuration of data representing the {@link Environment}<p>
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to reading in data.</p>
	 * 
	 * <p>{@link ISimulationSettings#BUILDINGS_FILE}: The relative path to the shape file representing the buildings in the {@link Environment}. 
	 * <br><b>Note:</b> Replace this file or this path to adapt the simulated environment (or city) to your needs.</p>
	 * <p>{@link ISimulationSettings#PATHS_FILE}: The relative path to the shape file representing the paths in the {@link Environment}.
	 * <br><b>Note:</b>: Replace this file or this path to adapt the simulated environment (or city) to your needs.</p>
	 * <p><b>Important:</b> The dimensions of the environment are defined by the coordinate reference system (CRS) used by the shape files. 
	 * (See <a href="https://en.wikipedia.org/wiki/Spatial_reference_system">CRS</a> for more information). 
	 * <br>The shape files used to develop the simulation represent the city of Zurich and use the following CRS: <b>EPSG:2056 - CH1903+ / LV95 - Projected</b>. 
	 * <br>The unit of this CRS is: <b>meter</b></p>
	 */
	public static final String BUILDINGS_FILE = "/data/environment/buildings/buildings.shp";
	/**
	 * <p>The relative path to the shape file representing the paths in the {@link Environment}.
	 */
	public static final String PATHS_FILE = "/data/environment/paths/paths.shp";
	
	/**
	 * @category Configuration of aspects related to buildings in the environment
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to buildings ({@link Environment#m_buildingsField}).</p>
	 * 
	 * <p>{@link ISimulationSettings#ATTRIBUTE_FOR_ACTIVITY_CATEGORY}: Name of the attribute used to set and get the {@link ActivityCategory} on a {@link MasonGeometry} representing a building in the {@link Environment}.
	 * <br><b>Note:</b>Each building can only belong to one {@link ActivityCategory}, but it can be used by any number of {@link Individual}'s for conducting {@link Activity}s.</p>
	 */
	public static final String ATTRIBUTE_FOR_ACTIVITY_CATEGORY = "ACTIVITY_CATEGORY";
	
	/**
	 * @category Configuration for serializing data
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to writing out data</p>
	 * 
	 * <p>{@link ISimulationSettings#SIMULATION_OUTPUT_FOLDER}: The relative path to the folder where the simulation output is stored. 
	 * <br><b>Note:</b> The output is stored in CSV format.</p>
	 * <p>{@link ISimulationSettings#CHAR_SET}: The char set used to write the output CSV-file to disk</p>
	 */
	public static final String SIMULATION_OUTPUT_FOLDER = "/data/output/";
	/**
	 * <p>The char set used to write the output CSV-file to disk</p>
	 */
	public static final String CHAR_SET = "UTF-8";
	
	
	/**
	 * @category Configuration of UI related aspects
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to the UI.</p>
	 * 
	 * <p>{@link ISimulationSettings#ENVIRONMENT_HEIGHT}: The height in display units of the display representing the environment</p>
	 * <p>{@link ISimulationSettings#ENVIRONMENT_WIDTH}: The width in display units of the display representing the environment</p>
	 */
	public static final int ENVIRONMENT_HEIGHT = 1000;
	/**
	 * <p>The width of the display representing the environment</p>
	 */
	public static final int ENVIRONMENT_WIDTH = 1000;
	
	
	/**
	 * @category Configuration of traffic related aspects
	 * 
	 * This sections contains all constants used to configure or handle aspects related to traffic and travel.
	 * 
	 * <p>{@link ISimulationSettings#MAX_VELOCITY}: The maximum velocity an {@link Individual} can travel on the paths of the environment. 
	 * Additionally, the velocity is used to calculate travel times in {@link Individual#createAgendaWithTravelTime}.
	 * <br><b>Note:</b> Currently the simulation does not model traffic and thus all {@link Individual}'s travel constantly at {@link ISimulationSettings#MAX_VELOCITY}</p>
	 */
	public static final double MAX_VELOCITY = 166.66667; // max velocity in m/min (equivalent to 30 km/h)
	
	/**
	 * @category Configuration of time related aspects
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to simulation time.</p>
	 * 
	 * <b>Note:</b> There are two different time scales used in this simulation: <b>base time</b> and <b>real time</b>.
	 * <br>The <b>base time</b> is used to refer to the time of day on the starting day of the simulation. 
	 * As such it always has the format BASE_YEAR:CurrentDayOfWeek:hh:mm. 
	 * It serves as a way to refer to the time of day at the current day of week independent of the date. 
	 * This is necessary in order to handle the availability of activities, which is only defined in terms of days of week and time of day.
	 * <br>The <b>real time</b> is used to refer to the real time in the simulation. 
	 * 			
	 * <p><b>Important:</b> Each step in the simulation corresponds to one minute (in real time). 
	 * The simulation starts at the following date: BASE_YEAR, BASE_MONTH, BASE_DAY, BASE_HOUR, BASE_MINUTE</p>
	 * 
	 * <p>{@link ISimulationSettings#BASE_YEAR}: The base year of the simulation. It is set to 2019.</p>
	 * <p>{@link ISimulationSettings#BASE_MONTH}: The base month of the simulation. It is set to January.</p>
	 * <p>{@link ISimulationSettings#BASE_DAY}: The base day of the simulation. It is set to Monday.</p>
	 * <p>{@link ISimulationSettings#BASE_HOUR}: The base hour of the simulation. It is set to 0.</p>
	 * <p>{@link ISimulationSettings#BASE_MINUTE}: The base minute of the simulation. It is set to 0.</p>
	 * <p>{@link ISimulationSettings#LAST_HOUR_OF_DAY}: The last hour of a day i.e. 23.<p>
	 * <p>{@link ISimulationSettings#LAST_MINUTE_OF_HOUR}: The last minute of an hour, i.e. 59.</p>
	 * <p>{@link ISimulationSettings#START_OF_DAY}: The start of a day in <b>base time</b>.</p>
	 * <p>{@link ISimulationSettings#END_OF_DAY}: The end of a day in <b>base time</b>.</p>
	 * <p>{@link ISimulationSettings#WEEK}: The days of a week, i.e. Monday - Sunday.</p>
	 * <p>{@link ISimulationSettings#WORK_WEEK}: The days of a work week, i.e. Monday - Friday.</p> 
	 * <p>{@link ISimulationSettings#WEEKEND}: The weekend, i.e. Saturday & Sunday</p>
	 */
	public static final int BASE_YEAR = 2019;
	/**
	 * <p>The base month of the simulation. It is set to January.</p>
	 */
	public static final int BASE_MONTH = 1;
	/**
	 * <p>The base month of the simulation. It is set to January.</p>
	 */
	public static final int BASE_DAY = 1; // Tuesday
	/**
	 * <p>The base hour of the simulation. It is set to 0.</p>
	 */
	public static final int BASE_HOUR = 0;
	/**
	 * The base minute of the simulation. It is set to 0.</p>
	 */
	public static final int BASE_MINUTE = 0;
	/**
	 * <p>The last hour of a day i.e. 23.<p>
	 */
	public static final int LAST_HOUR_OF_DAY = 23;
	/**
	 * The last minute of an hour, i.e. 59.</p>
	 */
	public static final int LAST_MINUTE_OF_HOUR = 59;
	/**
	 * <p>The start of a day in <b>base time</b>.</p>
	 */
	public static final DateTime START_OF_DAY = new DateTime(BASE_YEAR, BASE_MONTH, BASE_DAY, BASE_HOUR, BASE_MINUTE);
	/**
	 * <p>The end of a day in <b>base time</b>.</p>
	 */
	public static final DateTime END_OF_DAY = new DateTime(BASE_YEAR, BASE_MONTH, BASE_DAY, LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR);
	/**
	 * <p>The days of a week, i.e. Monday - Sunday.</p>
	 */
	public static final ArrayList<Integer> WEEK = Stream.of(
			DateTimeConstants.MONDAY, 
			DateTimeConstants.TUESDAY, 
			DateTimeConstants.WEDNESDAY, 
			DateTimeConstants.THURSDAY, 
			DateTimeConstants.FRIDAY, 
			DateTimeConstants.SATURDAY, 
			DateTimeConstants.SUNDAY)
			.collect(Collectors.toCollection(ArrayList::new));
	/**
	 * <p>The days of a work week, i.e. Monday - Friday.</p> 
	 */
	public static final ArrayList<Integer> WORK_WEEK = Stream.of(
			DateTimeConstants.MONDAY, 
			DateTimeConstants.TUESDAY, 
			DateTimeConstants.WEDNESDAY, 
			DateTimeConstants.THURSDAY, 
			DateTimeConstants.FRIDAY)
			.collect(Collectors.toCollection(ArrayList::new));
	/**
	 * <p>The day of weekend, i.e. Saturday & Sunday</p>
	 */
	public static final ArrayList<Integer> WEEKEND = Stream.of(
			DateTimeConstants.SATURDAY, 
			DateTimeConstants.SUNDAY)
			.collect(Collectors.toCollection(ArrayList::new));	
	
	/**
	 * @category Configuration of aspects related to individuals
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to {@link Individual}s.</p>
	 * 
	 * <p>{@link ISimulationSettings#NUMBER_OF_INDIVIDUALS}: The number of {@link Individual}s simulated.</p>
	 * <p>{@link ISimulationSettings#MIN_NUMBER_OF_HOUSEHOLD_MEMBERS}: The minimum number of {@link Individual}s in each {@link Network} representing a household or family.</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_HOUSEHOLD_MEMBERS}: The maximum number of {@link Individual}s in each {@link Network} representing a household or family.</p>
	 * <p>{@link ISimulationSettings#PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY}: The probability of an {@link Individual} participating in an {@link Activity} with household members upon request by some other member of the same household network.</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY}: The maximum number of {@link Activity}s with household or family members an {@link Individual} is willing to participate in.</p>
	 * <p>{@link ISimulationSettings#MIN_NUMBER_OF_WORK_COLLEGUES}: The minimum number of {@link Individual}s in each {@link Network} representing work colleagues.</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_WORK_COLLEGUES}: The maximum number of {@link Individual}s in each {@link Network} representing work colleagues.</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY}: The maximum number of {@link Activity}s with work colleagues an {@link Individual} is willing to participate in.</p>
	 * <p>{@link ISimulationSettings#PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY}: The probability of an {@link Individual} participating in an {@link Activity} with work colleagues upon request by some other member of the same household network.</p>
	 * <p>{@link ISimulationSettings#MIN_NUMBER_OF_FRIENDS}: The minimum number of {@link Individual}s in each {@link Network} representing friends.</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_FRIENDS}: The maximum number of {@link Individual}s in each {@link Network} representing friends.</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY}: The maximum number of {@link Activity}s with friends an {@link Individual} is willing to participate in.</p>
	 * <p>{@link ISimulationSettings#PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY}: The probability of an {@link Individual} participating in an {@link Activity} with friends upon request by some other member of the same household network.</p>
	 */
	public static final int NUMBER_OF_INDIVIDUALS = 5;
	/**
	 * <p>The minimum number of {@link Individual}s in each {@link Network} representing a household or family.</p>
	 */
	public static final int MIN_NUMBER_OF_HOUSEHOLD_MEMBERS = 1;
	/**
	 * <p>The maximum number of {@link Individual}s in each {@link Network} representing a household or family.</p>
	 */
	public static final int MAX_NUMBER_OF_HOUSEHOLD_MEMBERS = 5;
	/**
	 * <p>The probability of an {@link Individual} participating in an {@link Activity} with household members upon request by some other member of the same household network.</p>
	 */
	public static final double PROBABILITY_OF_PLANNING_HOUSEHOLD_NETWORK_ACTIVITY = 0.8;
	/**
	 * <p>The maximum number of {@link Activity}s with household or family members an {@link Individual} is willing to participate in.</p>
	 */
	public static final int MAX_NUMBER_OF_HOUSEHOLD_NETWORK_ACTIVITIES_PER_DAY = 10;
	/**
	 * <p>The minimum number of {@link Individual}s in each {@link Network} representing work colleagues.</p>
	 */
	public static final int MIN_NUMBER_OF_WORK_COLLEGUES = 2;
	/**
	 * <p>The maximum number of {@link Individual}s in each {@link Network} representing work colleagues.</p>
	 */
	public static final int MAX_NUMBER_OF_WORK_COLLEGUES = 10;
	/**
	 * <p>The maximum number of {@link Activity}s with work colleagues an {@link Individual} is willing to participate in.</p>
	 */
	public static final int MAX_NUMBER_OF_WORK_COLLEGUES_NETWORK_ACTIVITIES_PER_DAY = 3;
	/**
	 * <p>The probability of an {@link Individual} participating in an {@link Activity} with work colleagues upon request by some other member of the same household network.</p>
	 */
	public static final double PROBABILITY_OF_PLANNING_WORK_COLLEGUES_NETWORK_ACTIVITY = 0.3;
	/**
	 * <p>The minimum number of {@link Individual}s in each {@link Network} representing friends.</p>
	 */
	public static final int MIN_NUMBER_OF_FRIENDS = 2;
	/**
	 * <p>The maximum number of {@link Individual}s in each {@link Network} representing friends.</p>
	 */
	public static final int MAX_NUMBER_OF_FRIENDS = 20;
	/**
	 * <p>The maximum number of {@link Activity}s with friends an {@link Individual} is willing to participate in.</p>
	 */
	public static final int MAX_NUMBER_OF_FRIENDS_NETWORK_ACTIVITIES_PER_DAY = 5;
	/**
	 * <p>The probability of an {@link Individual} participating in an {@link Activity} with friends upon request by some other member of the same household network.</p>
	 */
	public static final double PROBABILITY_OF_PLANNING_FRIENDS_NETWORK_ACTIVITY = 0.5;
	
	
	/**
	 * @category Configuration of activity related aspects
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to {@link Activity}s.</p>
	 * 
	 * <p>{@link ISimulationSettings#NUMBER_OF_OTHER_PLACES_FOR_HOUSEHOLD_AND_FAMILY_CARE}: The number of alternative places to conduct {@link Activity}s of {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE}.</p>
	 * <p>{@link ISimulationSettings#NUMBER_OF_OTHER_PLACES_FOR_LEISURE}: The number of alternative places to conduct {@link Activity}s of {@link ActivityCategory#LEISURE}.</p>
	 * <p>{@link ISimulationSettings#NUMBER_OF_OTHER_PLACES_FOR_WORK}: The number of alternative places to conduct {@link Activity}s of {@link ActivityCategory#WORK}.</p>
	 * <p>{@link ISimulationSettings#NUMBER_OF_OTHER_PLACES_FOR_PERSONAL_CARE}: The number of alternative places to conduct {@link Activity}s of {@link ActivityCategory#PERSONAL_CARE}.</p>
	 * <p>{@link ISimulationSettings#AVAILABLE_START_TIMES_FOR_HOUSEHOLD_NETWORK_ACTIVITIES}: The possible start times for {@link Activity}s with household members.</p>
	 * <p>{@link ISimulationSettings#AVAILABLE_START_TIMES_FOR_WORK_COLLEAGUES_NETWORK_ACTIVITIES}: The possible start times for {@link Activity}s with work colleagues.</p>
	 * <p>{@link ISimulationSettings#AVAILABLE_START_TIMES_FOR_FRIENDS_NETWORK_ACTIVITIES}: The possible start times for {@link Activity}s with friends.</p>
	 * <p>{@link ISimulationSettings#MAX_DISTANCE_TO_OTHER_PLACES_FOR_HOUSEHOLD_AND_FAMILY_CARE}: The maximum distance to other places for household and family care. 
	 * Used to determine candidate buildings resp. nodes for {@link Individual#m_otherPlaceForHouseholdAndFamilyCareNodes}.
	 * <br><b>Note:</b> The distance is measured in unites as defined by the CRS (see "Configuration of data representing the" above for more details). 
	 * The current unit is <b>meter</b></p>
	 * <p>{@link ISimulationSettings#MAX_DISTANCE_TO_OTHER_PLACES_FOR_WORK}: The maximum distance to other places for work. 
	 * Used to determine candidate buildings resp. nodes for {@link Individual#m_otherPlaceForWorkNodes}.
	 * <br><b>Note:</b> The distance is measured in unites as defined by the CRS (see "Configuration of data representing the" above for more details). 
	 * The current unit is <b>meter</b></p>
	 * <p>{@link ISimulationSettings#MAX_DISTANCE_TO_OTHER_PLACES_FOR_LEISURE}: The maximum distance to other places for leisure. 
	 * Used to determine candidate buildings resp. nodes for {@link Individual#m_otherPlaceForLeisureNodes}.
	 * <br><b>Note:</b> The distance is measured in unites as defined by the CRS (see "Configuration of data representing the" above for more details). 
	 * The current unit is <b>meter</b></p>
	 */
	public static final int NUMBER_OF_OTHER_PLACES_FOR_HOUSEHOLD_AND_FAMILY_CARE = 5;
	/**
	 * <p>The number of alternative places to conduct {@link Activity}s of {@link ActivityCategory#LEISURE}.</p>
	 */
	public static final int NUMBER_OF_OTHER_PLACES_FOR_LEISURE = 5;
	/**
	 * <p>The number of alternative places to conduct {@link Activity}s of {@link ActivityCategory#WORK}.</p>
	 */
	public static final int NUMBER_OF_OTHER_PLACES_FOR_WORK = 5;
	/**
	 * <p>The number of alternative places to conduct {@link Activity}s of {@link ActivityCategory#PERSONAL_CARE}.</p>
	 */
	public static final int NUMBER_OF_OTHER_PLACES_FOR_PERSONAL_CARE = 5;
	/**
	 * <p>The possible start times for {@link Activity}s with household members.</p>
	 */
	public static final ArrayList<DateTime> AVAILABLE_START_TIMES_FOR_HOUSEHOLD_NETWORK_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 6, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 7, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 20, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	/**
	 * <p>The possible start times for {@link Activity}s with work colleagues.</p>
	 */
	public static final ArrayList<DateTime> AVAILABLE_START_TIMES_FOR_WORK_COLLEAGUES_NETWORK_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 10, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 13, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	/**
	 * <p>The possible start times for {@link Activity}s with friends.</p>
	 */
	public static final ArrayList<DateTime> AVAILABLE_START_TIMES_FOR_FRIENDS_NETWORK_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 8, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 9, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 10, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 16, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 17, 45),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 15),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 18, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 19, 30),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 20, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	/**
	 * <p>The maximum distance to other places for household and family care. 
	 * Used to determine candidate buildings resp. nodes for {@link Individual#m_otherPlaceForHouseholdAndFamilyCareNodes}.
	 * <br><b>Note:</b> The distance is measured in units as defined by the CRS (see "Configuration of data representing the" above for more details). 
	 * The current unit is <b>meter</b></p>
	 */
	public static final double MAX_DISTANCE_TO_OTHER_PLACES_FOR_HOUSEHOLD_AND_FAMILY_CARE = 5000;
	/**
	 * <p>The maximum distance to other places for work. 
	 * Used to determine candidate buildings resp. nodes for {@link Individual#m_otherPlaceForWorkNodes}.
	 * <br><b>Note:</b> The distance is measured in unites as defined by the CRS (see "Configuration of data representing the" above for more details). 
	 * The current unit is <b>meter</b></p>
	 */
	public static final double MAX_DISTANCE_TO_OTHER_PLACES_FOR_WORK = 10000;
	/**
	 * <p>The maximum distance to other places for leisure. 
	 * Used to determine candidate buildings resp. nodes for {@link Individual#m_otherPlaceForLeisureNodes}.
	 * <br><b>Note:</b> The distance is measured in unites as defined by the CRS (see "Configuration of data representing the" above for more details). 
	 * The current unit is <b>meter</b></p>
	 */
	public static final double MAX_DISTANCE_TO_OTHER_PLACES_FOR_LEISURE = 15000;
	
	/**
	 * @category Configuration of activity category related aspects
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to {@link ActivityCategory}s.</p>
	 * 
	 * <p><b>Note:</b>The values for the mean and standard deviation per {@link ActivityCategory} should be derived from empirical data.</p>
	 * 
	 * <p>{@link ISimulationSettings#MEAN_OF_LEISURE_ACTIVITY_DURATION}: The mean duration of activities in {@link ActivityCategory#LEISURE} in minutes.</p>
	 * <p>{@link ISimulationSettings#STANDARD_DEVIATION_OF_LEISURE_ACTIVITY_DURATION}: The standard deviation of the duration of activities in {@link ActivityCategory#LEISURE} in minutes.</p>
	 * <p>{@link ISimulationSettings#DISTRIBUTION_OF_LEISURE_DURATION}: The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#LEISURE}.
	 * It is used to sample durations for this category.</p>
	 * <p>{@link ISimulationSettings#MEAN_OF_WORK_ACTIVITY_DURATION}: The mean duration of activities in {@link ActivityCategory#WORK} in minutes.</p>
	 * <p>{@link ISimulationSettings#STANDARD_DEVIATION_OF_WORK_ACTIVITY_DURATION}: The standard deviation of the duration of activities in {@link ActivityCategory#WORK} in minutes.</p>
	 * <p>{@link ISimulationSettings#DISTRIBUTION_OF_WORK_DURATION}: The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#WORK}.
	 * It is used to sample durations for this category.</p>
	 * <p>{@link ISimulationSettings#MEAN_OF_PERSONAL_CARE_ACTIVITY_DURATION}: The mean duration of activities in {@link ActivityCategory#PERSONAL_CARE} in minutes.</p>
	 * <p>{@link ISimulationSettings#STANDARD_DEVIATION_OF_PERSONAL_CARE_ACTIVITY_DURATION}: The standard deviation of the duration of activities in {@link ActivityCategory#PERSONAL_CARE} in minutes.</p>
	 * <p>{@link ISimulationSettings#DISTRIBUTION_OF_PERSONAL_CARE_DURATION}: The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#PERSONAL_CARE}.
	 * It is used to sample durations for this category.</p>
	 * <p>{@link ISimulationSettings#MEAN_OF_HOUSEHOLD_AND_FAMILY_CARE_ACTIVITY_DURATION}: The mean duration of activities in {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE} in minutes.</p>
	 * <p>{@link ISimulationSettings#STANDARD_DEVIATION_OF_HOUSEHOLD_AND_FAMILY_CARE_ACTIVITY_DURATION}: The standard deviation of the duration of activities in {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE} in minutes.</p>
	 * <p>{@link ISimulationSettings#DISTRIBUTION_OF_HOUSEHOLD_AND_FAMILY_CARE_DURATION}: The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE}. It is used to sample durations for this category.</p>
	 * <p>{@link ISimulationSettings#s_ActivityCategoryToDurationDistributionMap}: A mapping from activity category to the duration distribution of durations for this category.</p>
	 */
	public static final double MEAN_OF_LEISURE_ACTIVITY_DURATION = 4.4636;
	/**
	 * <p>The standard deviation of the duration of activities in {@link ActivityCategory#LEISURE} in minutes.</p>
	 */
	public static final double STANDARD_DEVIATION_OF_LEISURE_ACTIVITY_DURATION = 0.7532;
	/**
	 * <p>The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#LEISURE}.
	 */
	public static final LogNormalDistribution DISTRIBUTION_OF_LEISURE_DURATION = new LogNormalDistribution(RANDOM_NUMBER_GENERATOR, MEAN_OF_LEISURE_ACTIVITY_DURATION, STANDARD_DEVIATION_OF_LEISURE_ACTIVITY_DURATION);
	/**
	 * <p>The mean duration of activities in {@link ActivityCategory#WORK} in minutes.</p>
	 */
	public static final double MEAN_OF_WORK_ACTIVITY_DURATION = 5.0943;
	/**
	 * <p>The standard deviation of the duration of activities in {@link ActivityCategory#WORK} in minutes.</p>
	 */
	public static final double STANDARD_DEVIATION_OF_WORK_ACTIVITY_DURATION = 0.8349;
	/**
	 * <p>The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#WORK}.
	 */
	public static final LogNormalDistribution DISTRIBUTION_OF_WORK_DURATION = new LogNormalDistribution(RANDOM_NUMBER_GENERATOR, MEAN_OF_WORK_ACTIVITY_DURATION, STANDARD_DEVIATION_OF_WORK_ACTIVITY_DURATION);
	/**
	 * <p>The mean duration of activities in {@link ActivityCategory#PERSONAL_CARE} in minutes.</p>
	 */
	public static final double MEAN_OF_PERSONAL_CARE_ACTIVITY_DURATION = 4.1460;
	/**
	 * <p>The standard deviation of the duration of activities in {@link ActivityCategory#PERSONAL_CARE} in minutes.</p>
	 */
	public static final double STANDARD_DEVIATION_OF_PERSONAL_CARE_ACTIVITY_DURATION = 0.8606;
	/**
	 * <p>The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#PERSONAL_CARE}.
	 * It is used to sample durations for this category.</p>
	 */
	public static final LogNormalDistribution DISTRIBUTION_OF_PERSONAL_CARE_DURATION = new LogNormalDistribution(RANDOM_NUMBER_GENERATOR, MEAN_OF_PERSONAL_CARE_ACTIVITY_DURATION, STANDARD_DEVIATION_OF_PERSONAL_CARE_ACTIVITY_DURATION);
	/**
	 * <p>The mean duration of activities in {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE} in minutes.</p>
	 */
	public static final double MEAN_OF_HOUSEHOLD_AND_FAMILY_CARE_ACTIVITY_DURATION = 3.9545;
	/**
	 * <p>The standard deviation of the duration of activities in {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE} in minutes.</p>
	 */
	public static final double STANDARD_DEVIATION_OF_HOUSEHOLD_AND_FAMILY_CARE_ACTIVITY_DURATION = 0.7740;
	/**
	 * <p>The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#HOUSEHOLD_AND_FAMILY_CARE}. It is used to sample durations for this category.</p>
	 */
	public static final LogNormalDistribution DISTRIBUTION_OF_HOUSEHOLD_AND_FAMILY_CARE_DURATION = new LogNormalDistribution(RANDOM_NUMBER_GENERATOR, MEAN_OF_HOUSEHOLD_AND_FAMILY_CARE_ACTIVITY_DURATION, STANDARD_DEVIATION_OF_HOUSEHOLD_AND_FAMILY_CARE_ACTIVITY_DURATION);
	/**
	 * <p>The mean duration of activities in {@link ActivityCategory#SLEEP_AND_REST} in minutes.</p>
	 */
	public static final double MEAN_OF_SLEEP_AND_REST_ACTIVITY_DURATION = 6.2278;
	/**
	 * <p>The standard deviation of the duration of activities in {@link ActivityCategory#SLEEP_AND_REST} in minutes.</p>
	 */
	public static final double STANDARD_DEVIATION_OF_SLEEP_AND_REST_ACTIVITY_DURATION = 0.2091;
	/**
	 * <p>The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#SLEEP_AND_REST}. It is used to sample durations for this category.</p>
	 */
	public static final LogNormalDistribution DISTRIBUTION_OF_SLEEP_AND_REST_DURATION = new LogNormalDistribution(RANDOM_NUMBER_GENERATOR, MEAN_OF_SLEEP_AND_REST_ACTIVITY_DURATION, STANDARD_DEVIATION_OF_SLEEP_AND_REST_ACTIVITY_DURATION);	
	
	/**
	 * <p>The mean duration of activities in {@link ActivityCategory#IDLE} in minutes.</p>
	 */
	public static final double MEAN_OF_IDLE_ACTIVITY_DURATION = 3.2157;
	/**
	 * <p>The standard deviation of the duration of activities in {@link ActivityCategory#IDLE} in minutes.</p>
	 */
	public static final double STANDARD_DEVIATION_OF_IDLE_ACTIVITY_DURATION = 0.0799;
	/**
	 * <p>The normal distribution of the duration of {@link Activity}s in {@link ActivityCategory#IDLE}. It is used to sample durations for this category.</p>
	 */
	public static final LogNormalDistribution DISTRIBUTION_OF_IDLE_DURATION = new LogNormalDistribution(RANDOM_NUMBER_GENERATOR, MEAN_OF_IDLE_ACTIVITY_DURATION, STANDARD_DEVIATION_OF_IDLE_ACTIVITY_DURATION);
	/**
	 * <p>A mapping from activity category to the duration distribution of durations for this category.</p>
	 */
	public static HashMap<ActivityCategory, AbstractRealDistribution> s_ActivityCategoryToDurationDistributionMap = initActivityCategoryToDurationDistributionMap();
	
	/**
	 * @category Configuration of planning related aspects
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to planning {@link Activity}s.</p>
	 * 
	 * <p>{@link ISimulationSettings#NUMBER_OF_PLANS_TO_GENERATE}: The number of plans that each {@link Individual} generates when planning its own {@link Activity}s for the rest of a day (see {@link Individual#planIndividualActivities()}).</p>
	 * <p>{@link ISimulationSettings#AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES}: The time points an {@link Individual} can plan resp. replan it's activities for the current day (see {@link Environment#start()}).</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY}: The maximum number of trials to find a time slot for a joint activity.
	 * This is necessary, since it is possible that all of the network members potentially participating in the proposed joint activity have already planned some other activity for the proposed time slot.</p>
	 * <p>{@link ISimulationSettings#MAX_NUMBER_OF_TRAVEL_ACTIVITIES}: The maximum number of travel activities an {@link Individual} incorporates in the planning of a day. 
	 * This is used to improve the performance of the simulation since calculating the travel time for plans with many travel activities consumes a lot of computing resources to calculate travel times, but those plans are not likely to be selected since travel time decreases the time the individual can spend on satisfying its needs.</p>
	 * <p>{@link ISimulationSettings#MIN_DURATION_OF_ACTIVITY_TO_TRAVEL_TO_DIFFERENT_LOCATION}: If the duration of an {@link Activity} is smaller than this constant, the {@link Individual} must stay at it's current location, since traveling would otherwise consume most or all of the time the {@link Individual} intends to spend on the activity.</p>
	 */
	public static final int NUMBER_OF_PLANS_TO_GENERATE = 100;
	/**
	 * <p>The time points an {@link Individual} can plan resp. plan its activities anew for the current day (see {@link Environment#start()}).</p>
	 */
	public static final ArrayList<DateTime> AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES = Stream.of(
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 0, 0),
			new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 12, 0))
			.collect(Collectors.toCollection(ArrayList::new));
	/**
	 * <p>The maximum number of trials to find a time slot for a joint activity.
	 * This is necessary, since it is possible that all of the network members potentially participating in the proposed joint activity have already planned some other activity for the proposed time slot.</p>
	 */
	public static final int MAX_NUMBER_OF_TRIALS_TO_FIND_TIME_SLOT_FOR_JOINT_ACTIVITY = 5;
	/**
	 * <p>The maximum number of travel activities an {@link Individual} incorporates in the planning of a day. 
	 * This is used to improve the performance of the simulation since calculating the travel time for plans with many travel activities consumes a lot of computing resources to calculate travel times, but those plans are not likely to be selected since travel time decreases the time the individual can spend on satisfying its needs.</p>
	 */
	public static final int MAX_NUMBER_OF_TRAVEL_ACTIVITIES = 10;
	/**
	 * <p>If the duration of an {@link Activity} is smaller than this constant, the {@link Individual} must stay at it's current location, since traveling would otherwise consume most or all of the time the {@link Individual} intends to spend on the activity.</p>
	 */
	public static final int MIN_DURATION_OF_ACTIVITY_TO_TRAVEL_TO_DIFFERENT_LOCATION = 30;
	
	/**
	 * @category Configuration of UI related aspects
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to the user interface of the simulation (see {@link EnvironmentWithUI}).</p>
	 * 
	 * <p>{@link ISimulationSettings#SIZE_OF_INDIVIDUAL}: The size of the circle representing an {@link Individual}.</p>
	 * <p>{@link ISimulationSettings#SIZE_OF_BUILDING}: The size of the circle representing a building.</p>
	 * <p>{@link ISimulationSettings#SIZE_OF_PATH}: The thickness of the line representing a path.</p>
	 * <p>{@link ISimulationSettings#COLOR_FOR_DEBUG}: The color used to mark UI elements of interest for debugging. This color is not used by the simulation but provides a mean to facilitate debugging in the UI.</p>
	 * <p> {@link ISimulationSettings#COLOR_OF_SELECTED_ENTITY}: The color used to mark selected {@link Individual}s.</p>
	 * <p>{@link ISimulationSettings#COLOR_OF_INDIVIDUAL}: The color used to mark {@link Individual}s.</p>
	 * <p>{@link ISimulationSettings#COLOR_OF_BACKGROUND}: The color used as background color.</p>
	 * <p>{@link ISimulationSettings#COLOR_OF_BUILDING}: The color used to mark buildings.</p>
	 * <p>{@link ISimulationSettings#COLOR_OF_TARGET_BUILDING}: The color used to mark the current target building of each {@link Individual}. Is only introduced for debugging purposes.</p>
	 * <p>{@link ISimulationSettings#COLOR_OF_PATH}: The color used to mark the paths.</p>
	 */
	public static final double SIZE_OF_INDIVIDUAL = 3.0;
	/**
	 * <p>The size of the circle representing a building.</p>
	 */
	public static final double SIZE_OF_BUILDING = 2.5;
	/**
	 * <p>The thickness of the line representing a path.</p>
	 */
	public static final double SIZE_OF_PATH = 1.0;
	/**
	 * <p>The color used to mark UI elements of interest for debugging. This color is not used by the simulation but provides a mean to facilitate debugging in the UI.</p>
	 */
	public static final Color COLOR_FOR_DEBUG = new Color(235, 59, 90); // desire (red)
	/**
	 * <p>The color used to mark selected entities.</p>
	 */
	public static final Color COLOR_OF_SELECTED_ENTITY = new Color(38, 222, 129); // reptile green
	/**
	 * <p>The color used to mark {@link Individual}s.</p>
	 */
	public static final Color COLOR_OF_INDIVIDUAL = new Color(165, 94, 234); // lighter purple
	/**
	 * <p>The color used as background color.</p>
	 */
	public static final Color COLOR_OF_BACKGROUND = Color.white;
	/**
	 * <p>The color used to mark buildings.</p>
	 */
	public static final Color COLOR_OF_BUILDING = new Color(69, 170, 242); // high blue
	/**
	 * <p>The color used to mark the current target building of each {@link Individual}. Is only introduced for debugging purposes.</p>
	 */
	public static final Color COLOR_OF_TARGET_BUILDING = new Color(250, 130, 49); // beniukon bronze
	/**
	 * <p>The color used to mark the paths.</p>
	 */
	public static final Color COLOR_OF_PATH = new Color(209, 216, 224); // twinkle blue
	
	/**
	 * @category Configuration of aspects related to simulation output 
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to simulation output.</p>
	 * 
	 * <p>These constants are used as labels for the simulations output. As such they are used by {@link EnvironmentOutputRecorder}, {@link Environment#m_outputHolder}, {@link Environment#m_activityCategoryDataset} and {@link Activity#m_activityDescription}
	 * to ensure consistent referral to information serialized as CSV to disk.</p>
	 * 
	 * <p><b>Note:</b>If you need additional attributes in order to capture the simulation's output make sure to add a corresponding constant here and use it at the aforementioned locations to refer to it.</p>
	 * 
	 */
	public static final String TIME_STAMP = "time stamp";
	public static final String DAY_OF_WEEK = "day of week";
	public static final String HOUR_OF_DAY = "hour of day";
	public static final String MINUTE_OF_HOUR = "minute of hour";
	public static final String TOTAL_NUMBER_OF_AGENTS = "total number of agents";
	public static final String LEISURE_AT_HOME_ALONE_ACTIVITY= "Leisure at home alone";
	public static final String LEISURE_AT_HOME_WITH_HOUSEHOLD_MEMBERS = "Leisure at home with household members";
	public static final String LEISURE_AT_HOME_WITH_FRIENDS = "Leisure at home with friends";
	public static final String LEISURE_AT_THIRD_PLACE_ALONE = "Leisure at 3rd place alone";
	public static final String LEISURE_AT_THIRD_PLACE_WITH_HOUSEHOLD_MEMBERS = "Leisure at 3rd place with household members";
	public static final String LEISURE_AT_THIRD_PLACE_WITH_FRIENDS = "Leisure at 3rd place with friends";
	public static final String WORK_AT_HOME_ALONE = "Work at home alone";
	public static final String WORK_AT_WORK_PLACE_ALONE = "Work at work location alone";
	public static final String WORK_AT_WORK_PLACE_WITH_COWORKERS = "Work at work location with coworkers";
	public static final String WORK_AT_THIRD_PLACE_ALONE = "Work at 3rd place for work alone";
	public static final String WORK_AT_THIRD_PLACE_WITH_COWORKERS = "Work at 3rd place for work with coworkers";
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
	public static final String IDLE_AT_THIRD_PLACE_FOR_WORK = "Idle at 3rd place for work";
	public static final String IDLE_AT_THIRD_PLACE_FOR_LEISURE = "Idle at 3rd place for leisure";
	public static final String TRAVEL = "Travel";
	public static final String SLEEP_AT_HOME = "Sleep at home";
	public static final String TITLE_OF_BARCHART = "Activities";
	
	/**
	 * @category Configuration of aspects related to numbers & calculations
	 * 
	 * <p>This section contains all constants used to configure or handle aspects related to calculations and numbers output (see {@link CalculationUtility}).</p>
	 * 
	 *  <p><b>Note:</b> It is recommended to only create and used {@link BigDecimal}'s via the {@link CalculationUtility} since this it takes issues related
	 *  to rounding into account and thus ensures that the precision of any {@link BigDecimal} is always as specified by the constants defined here.</p>
	 * 
	 * <p>{@link ISimulationSettings#PRECISION_USED_FOR_BIG_DECIMAL}: The precision used when operating with {@link BigDecimal}s.</p>
	 * <p>{@link ISimulationSettings#SCALE_USED_FOR_BIG_DECIMAL}: The scale used when operating with {@link BigDecimal}s.</p>
	 * <p>{@link ISimulationSettings#ROUNDING_MODE_USED_FOR_BIG_DECIMAL}: The rounding mode used when operating with {@link BigDecimal}s.</p>
	 * <p>{@link ISimulationSettings#TOLERATED_ROUNDING_ERROR}: The tolerated rounding error when operating with {@link BigDecimal}s.</p>
	 * 
	 */
	public static final int PRECISION_USED_FOR_BIG_DECIMAL = 6;
	/**
	 * <p>The scale used when operating with {@link BigDecimal}s.</p>
	 */
	public static final int SCALE_USED_FOR_BIG_DECIMAL = 6;
	/**
	 * <p>The rounding mode used when operating with {@link BigDecimal}s.</p>
	 */
	public static final RoundingMode ROUNDING_MODE_USED_FOR_BIG_DECIMAL = RoundingMode.HALF_UP;
	/**
	 * <p>The tolerated rounding error when operating with {@link BigDecimal}s.</p>
	 */
	public static final BigDecimal TOLERATED_ROUNDING_ERROR = BigDecimal.valueOf(0.00001);
	
	/**
	 * @category Configuration of aspects related to debugging
	 * 
	 * {@link ISimulationSettings#IS_DEBUG}: Flag for whether or not the simulation should show information helpful for debugging.
	 */
	public static final boolean IS_DEBUG = true;
	
	/**
	 * This function is used to initialize the mapping of {@link ActivityCategory}s to {@link AbstractRealDistribution} used to define
	 * the duration distribution of {@link Activity}s within the respective categories.
	 * 
	 * @return HashMap<ActivityCategory, AbstractRealDistribution> - a mapping with the duration distribution of each activity category.
	 */
	public static HashMap<ActivityCategory, AbstractRealDistribution> initActivityCategoryToDurationDistributionMap() {
		HashMap<ActivityCategory, AbstractRealDistribution> activityCategoryToDurationDistributionMap = new HashMap<>();
		activityCategoryToDurationDistributionMap.put(ActivityCategory.LEISURE, DISTRIBUTION_OF_LEISURE_DURATION);
		activityCategoryToDurationDistributionMap.put(ActivityCategory.WORK, DISTRIBUTION_OF_WORK_DURATION);
		activityCategoryToDurationDistributionMap.put(ActivityCategory.PERSONAL_CARE, DISTRIBUTION_OF_PERSONAL_CARE_DURATION);
		activityCategoryToDurationDistributionMap.put(ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE, DISTRIBUTION_OF_HOUSEHOLD_AND_FAMILY_CARE_DURATION);
		activityCategoryToDurationDistributionMap.put(ActivityCategory.SLEEP_AND_REST, DISTRIBUTION_OF_SLEEP_AND_REST_DURATION);
		activityCategoryToDurationDistributionMap.put(ActivityCategory.IDLE, DISTRIBUTION_OF_IDLE_DURATION);
		return activityCategoryToDurationDistributionMap;
	}
}
