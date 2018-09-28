package rfs0.aitam.commons;

import java.util.ArrayList;

import sim.util.geo.MasonGeometry;

/**
 * Used to collect all variables that are introduced only to facilitate the development of the model
 * 
 * @author Remo
 *
 */
public interface IDevSettings {
	
	public static ArrayList<MasonGeometry> DEV_BUILDINGS = new ArrayList<>();
	public static final int HOME_BUILDING_1 = 500;
	public static final int HOME_BUILDING_2 = 100;
	public static final int INDIVIDUAL_ACTIVITY_BUILDING_1 = 600;
	public static final int INDIVIDUAL_ACTIVITY_BUILDING_2 = 700;
	public static final int GENERAL_ACTIVITY_BUILDING_1 = 1000;
	public static final int GENERAL_ACTIVITY_BUILDING_2 = 1200;
	
	public static final int INDEX_OF_HOME_BUILDING_1 = 0;
	public static final int INDEX_OF_HOME_BUILDING_2 = 1;
	public static final int INDEX_OF_INDIVIDUAL_ACTIVITY_BUILDING_1 = 2;
	public static final int INDEX_OF_INDIVIDUAL_ACTIVITY_BUILDING_2 = 3;
	public static final int INDEX_OF_GENERAL_ACTIVITY_BUILDING_1 = 4;
	public static final int INDEX_OF_GENERAL_ACTIVITY_BUILDING_2 = 5;
	
}
