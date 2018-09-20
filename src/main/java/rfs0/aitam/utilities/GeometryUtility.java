package rfs0.aitam.utilities;

import com.vividsolutions.jts.geom.Coordinate;

import sim.util.Bag;
import sim.util.geo.MasonGeometry;

public final class GeometryUtility {
	
	public static double calculateDistance(Coordinate c1, Coordinate c2) {
		return c1.distance(c2);
	}
	
	public static MasonGeometry findClosestCoordinate(MasonGeometry mg, Bag candidates) {
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
}
