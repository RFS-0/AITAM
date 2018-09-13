package rfs0.aitam.utilities;

import com.vividsolutions.jts.planargraph.Node;

import sim.util.geo.GeomPlanarGraphDirectedEdge;

public class AStarNodeWrapper {
	/**
	 * A wrapper to contain the A* meta information about the Nodes
	 *
	 */

	// the underlying Node associated with the metainformation
	Node node;
	// the Node from which this Node was most profitably linked
	AStarNodeWrapper cameFrom;
	// the edge by which this Node was discovered
	GeomPlanarGraphDirectedEdge edgeFrom;
	double gx, hx, fx;

	public AStarNodeWrapper(Node n) {
		node = n;
		gx = 0;
		hx = 0;
		fx = 0;
		cameFrom = null;
		edgeFrom = null;
	}
}
