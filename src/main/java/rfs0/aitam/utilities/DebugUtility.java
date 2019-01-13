package rfs0.aitam.utilities;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vividsolutions.jts.geom.Coordinate;

import rfs0.aitam.activity.Activity;
import rfs0.aitam.environment.Environment;
import rfs0.aitam.settings.ISimulationSettings;
import sim.field.geo.GeomVectorField;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.util.geo.GeomPlanarGraphDirectedEdge;
import sim.util.geo.MasonGeometry;

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
	 * @param householdMembersIndices - a list of all individuals for which this building is their home location.
	 * @return LabelledPortrayal2D - a portrayal helpful for visual debugging
	 */
	public static LabelledPortrayal2D createLabelledPortrayal2DForBuilding(ArrayList<Integer> householdMembersIndices) {
		return new LabelledPortrayal2D(
						new GeomPortrayal(Color.black, ISimulationSettings.SIZE_OF_BUILDING, true), 
					10,
					5,
					0.5,
					0.5,
					new Font("SansSerif",Font.BOLD, 15),
					LabelledPortrayal2D.ALIGN_LEFT,
					null, 
					Color.black, 
					false) {
				private static final long serialVersionUID = 1L;
				@Override
				public String getLabel(Object object, DrawInfo2D info) {
					return String.format("Home of: %s ", String.valueOf(householdMembersIndices));
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
	
	/**
	 * <p>This method colors the path (but not exactly) of an agent to its next target. 
	 * Its can be used for visual debugging.</p>
	 */
	public static void colorPathToTarget(Environment environment, ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget) {
		GeomVectorField pathField = environment.getPathField();
		List<Coordinate> coordinatesOfPath = pathToTarget.stream()
				.map(path -> path.getCoordinate())
				.collect(Collectors.toList());
		for (Coordinate coordinate : coordinatesOfPath) {
			ArrayList<MasonGeometry> coveringObjects = GeometryUtility.getCoveringObjects(new MasonGeometry(Environment.GEO_FACTORY.createPoint(coordinate)), pathField);
			coveringObjects.forEach(mg -> {
				mg.setUserData(DebugUtility.creatCircledPortrayal2DForPath());
			});
		}
	}
	
	/**
	 * <p>This method removes the color of the path (but not exactly) of an agent to its next target. 
	 * Its can be used for visual debugging.</p>
	 */
	public static void removeColorFromPathToTarget(Environment environment, ArrayList<GeomPlanarGraphDirectedEdge> pathToTarget) {
		GeomVectorField pathField = environment.getPathField();
		List<Coordinate> coordinatesOfPath = pathToTarget.stream()
				.map(path -> path.getCoordinate())
				.collect(Collectors.toList());
		for (Coordinate coordinate : coordinatesOfPath) {
			ArrayList<MasonGeometry> coveringObjects = GeometryUtility.getCoveringObjects(new MasonGeometry(Environment.GEO_FACTORY.createPoint(coordinate)), pathField);
			coveringObjects.forEach(mg -> {
				mg.setUserData(null);
			});
		}
	}
	
	/**
	 * <p>This method removes the label of the old target building.</p>
	 */
	public static void removeLabelFromBuilding(MasonGeometry building) {
		if (building != null) {
			building.setUserData(null);
		}
	}
	
	/**
	 * <p>This method labels the current target building.</p>
	 * 
	 * @param targetBuilding - the target building.
	 * @param id - the id of the individual whose target the building is.
	 * @param currentActivity - the activity the individual is going to execute at the target building.
	 */
	public static void labelTargetBuilding(MasonGeometry targetBuilding, int id, Activity currentActivity) {
		targetBuilding.getGeometry().setUserData(DebugUtility.createLabelledPortrayal2DForBuilding(id, currentActivity));
	}
}
