package rfs0.aitam.utilities;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import rfs0.aitam.model.Environment;
import sim.field.geo.GeomVectorField;
import sim.util.geo.MasonGeometry;

public final class GeometryUtility {
	
	public static double calculateDistance(Coordinate c1, Coordinate c2) {
		return c1.distance(c2);
	}

	public static ArrayList<MasonGeometry> getCoveringObjects(MasonGeometry geometryCovered, GeomVectorField coveringField) {
		ArrayList<MasonGeometry> coveringMasonGeometries = new ArrayList<>();
		for (Object masonGeometry: coveringField.getCoveringObjects(Environment.GEO_FACTORY.createPoint(geometryCovered.geometry.getCoordinate()))) {
			coveringMasonGeometries.add((MasonGeometry) masonGeometry);
		}
		return coveringMasonGeometries;
	}
}
