package rfs0.aitam.utilities;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import rfs0.aitam.environment.Environment;
import sim.field.geo.GeomVectorField;
import sim.util.geo.MasonGeometry;

/**
 * <p>This class is used to handle operations related to geometric objects.</p>
 */
public final class GeometryUtility {
	
	public static double calculateDistance(Coordinate c1, Coordinate c2) {
		return c1.distance(c2);
	}

	/**
	 * <p>This method retrieves all geometries of a field that cover the provided geometry.</p>
	 * 
	 * @param geometryCovered - the geometry that is covered by some field.
	 * @param coveringField - the field that covers the provided geometry.
	 * @return ArrayList<MasonGeometry> - a list with all object of the field that cover the geometry.
	 */
	public static ArrayList<MasonGeometry> getCoveringObjects(MasonGeometry geometryCovered, GeomVectorField coveringField) {
		ArrayList<MasonGeometry> coveringMasonGeometries = new ArrayList<>();
		for (Object masonGeometry: coveringField.getCoveringObjects(Environment.GEO_FACTORY.createPoint(geometryCovered.geometry.getCoordinate()))) {
			coveringMasonGeometries.add((MasonGeometry) masonGeometry);
		}
		return coveringMasonGeometries;
	}
}
