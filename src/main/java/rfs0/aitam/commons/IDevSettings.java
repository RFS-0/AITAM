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
	public static final int START_BUILDING = 0;
	public static final int TARGET_BUILDING = 100;
	
}
