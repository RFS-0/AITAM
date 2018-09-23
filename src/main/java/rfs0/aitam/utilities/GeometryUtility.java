package rfs0.aitam.utilities;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import rfs0.aitam.model.Environment;
import sim.field.geo.GeomVectorField;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

public final class GeometryUtility {
	
	public static double calculateDistance(Coordinate c1, Coordinate c2) {
		return c1.distance(c2);
	}
	
	public static MasonGeometry findClosestGeometry(MasonGeometry mg, Bag candidates) {
		if (candidates.isEmpty()) {
			return null;
		}
		Coordinate reference = mg.getGeometry().getCoordinate();
		MasonGeometry closestGeometry = null;
		double closestDistance = Double.MAX_VALUE;
		for (Object candidate: candidates) {
			MasonGeometry candidateGeometry = (MasonGeometry) candidate;
			Coordinate candidateCoordiante = candidateGeometry.getGeometry().getCoordinate();
			if (reference.distance(candidateCoordiante) < closestDistance) {
				closestDistance = reference.distance(candidateCoordiante);
				closestGeometry = candidateGeometry;
			}
		}
		return closestGeometry;
	}
	
	public static MasonGeometry getClosestGeometryToField(MasonGeometry referenceGeometry, GeomVectorField field) {
		double searchDistance = 0.0;
		MasonGeometry closestGeometry = null;
		while (closestGeometry == null) {
			Bag withinDistance = field.getObjectsWithinDistance(Environment.GEO_FACTORY.createPoint(referenceGeometry.getGeometry().getCoordinate()), searchDistance);
			closestGeometry = GeometryUtility.findClosestGeometry(referenceGeometry, withinDistance);
			searchDistance += 1.0;
		}
		return closestGeometry;
	}
	
	public static ArrayList<MasonGeometry> getCoveringObjects(MasonGeometry geometryCovered, GeomVectorField coveringField) {
		ArrayList<MasonGeometry> coveringMasonGeometries = new ArrayList<>();
		for (Object masonGeometry: coveringField.getCoveringObjects(Environment.GEO_FACTORY.createPoint(geometryCovered.geometry.getCoordinate()))) {
			coveringMasonGeometries.add((MasonGeometry) masonGeometry);
		}
		return coveringMasonGeometries;
	}
}
