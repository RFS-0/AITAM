package rfs0.aitam.commons;

import java.awt.Color;

public interface ISimulationSettings {
	
	/**
	 * Constants used to configure environment
	 */
	public static final int ENVIRONMENT_HEIGHT = 1000;
	public static final int ENVIRONMENT_WIDTH = 1000;
	public static final String BUILDINGS_FILE = "\\data\\environment\\buildings\\buildings.shp";
	public static final String PATHS_FILE = "\\data\\environment\\paths\\paths.shp";
	public static final int NUMBER_OF_AGENTS = 2;
	public static final double MAX_TRAFFIC_CAPACITY_PER_UNIT_LENGHT = 0.04; // assuming reaction time of 1.8 s and average velocity of 50 km/h
	public static final double MAX_VELOCITY = 13.9; // max velocity in m/s (equivalent to 50 km/h)
	public static final double MAX_SLOW_DOWN_FACTOR = 0.2;
	
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
	 * Constants used to for simulating time
	 */
	public static final int BASE_YEAR = 2018;
	public static final int BASE_MONTH = 1;
	public static final int BASE_DAY = 1;
	
}
