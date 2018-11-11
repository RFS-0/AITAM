package rfs0.aitam.utilities;

import java.awt.Font;

import rfs0.aitam.activities.Activity;
import rfs0.aitam.settings.ISimulationSettings;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;

public final class DebugUtility {
	
	/**
	 * <p>This method creates a portrayal which is designed to be helpful for debugging visually issues related to buildings.</p>
	 * 
	 * @param idOfIndividual - the id of the individual for which this building is a target.
	 * @param currentActivity - the current activity of this individual.
	 * @return LabelledPortrayal2D - a portrayal helpful for visual debugging.
	 */
	public static LabelledPortrayal2D createLabelledPortrayal2DForBuilding(int idOfIndividual, Activity currentActivity) {
		return new LabelledPortrayal2D(
				new GeomPortrayal(ISimulationSettings.COLOR_OF_TARGET_BUILDING, ISimulationSettings.SIZE_OF_BUILDING, true), 
				10,
				5,
				0.5,
				0.5,
				new Font("SansSerif",Font.BOLD, 15),
				LabelledPortrayal2D.ALIGN_LEFT,
				null, 
				ISimulationSettings.COLOR_OF_TARGET_BUILDING, 
				false) {
			private static final long serialVersionUID = 1L;
			@Override
			public String getLabel(Object object, DrawInfo2D info) {
				return String.format("Target of %s for activity %s", String.valueOf(idOfIndividual), currentActivity.getActivityDescription());
			}
		};
	}
	
	/**
	 * <p>This method creates a portrayal which is designed to be helpful for debugging visually issues related to buildings.</p>
	 * 
	 * @return CircledPortrayal2D - a portrayal helpful for visual debugging.
	 */
	public static CircledPortrayal2D creatCircledPortrayal2DForPath() {
		return new CircledPortrayal2D(
				new GeomPortrayal(
						ISimulationSettings.COLOR_OF_SELECTED_ENTITY,
						ISimulationSettings.SIZE_OF_PATH
						),
				ISimulationSettings.COLOR_OF_SELECTED_ENTITY,
				true
				);
	}
}
