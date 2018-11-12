package rfs0.aitam.utilities;

import com.vividsolutions.jts.planargraph.Node;

import sim.util.geo.GeomPlanarGraphDirectedEdge;

/**
 * <p>This is a wrapper to contain the A* meta information of nodes.</p>
 * 
 * <p><b>Note:</b>The code for the A*-Algorithm stems from the <a href="https://github.com/eclab/mason/">Mason repository on GitHub</a> repository. 
 * More specifically from a package called <a href="https://github.com/eclab/mason/tree/master/contrib/geomason/sim/app/geo/gridlock">"GridLock"</a>. 
 * It has been adapted to fit this simulation's purpose. </b></p> 
 */
public class AStarNodeWrapper {
	/**
	 * A wrapper to contain the A* meta information about the Nodes
	 */
	Node m_node; // the underlying Node associated with the meta information
	AStarNodeWrapper m_cameFrom; // the Node from which this Node was most profitably linked
	GeomPlanarGraphDirectedEdge m_edgeFrom; // the edge by which this Node was discovered
	double m_gx;
	double m_hx;
	double m_fx;

	public AStarNodeWrapper(Node n) {
		m_node = n;
		m_gx = 0;
		m_hx = 0;
		m_fx = 0;
		m_cameFrom = null;
		m_edgeFrom = null;
	}
}
