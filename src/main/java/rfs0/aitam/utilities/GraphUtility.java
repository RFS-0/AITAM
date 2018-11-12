package rfs0.aitam.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.planargraph.DirectedEdgeStar;
import com.vividsolutions.jts.planargraph.Node;

import sim.util.geo.GeomPlanarGraphDirectedEdge;

/**
 * <p>This class is used to handle all operations related to graphs.
 * In particular it provides a method for finding the shortest path between two nodes.
 * This method is an implementation of the <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a>.</p>
 * 
 * <p><b>Note:</b> The code for the A*-Algorithm stems from the <a href="https://github.com/eclab/mason/">Mason repository on GitHub</a> repository. 
 * More specifically from a package called <a href="https://github.com/eclab/mason/tree/master/contrib/geomason/sim/app/geo/gridlock">"GridLock"</a>. 
 * It has been adapted to fit this simulation's purpose. </b></p> 
 */
public class GraphUtility {

	/**
	 * <p>This method finds the shortest path between two nodes using the <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a>.</p>
	 * 
	 * @param start - the node at which the path starts.
	 * @param goal - the node to which the path should lead.
	 * @return ArrayList<GeomPlanarGraphDirectedEdge> - a list of edges which make up the path from the start node to the target node. 
	 * The indices represent the order in which each of the edges of the path have to be traversed.
	 */
	public static ArrayList<GeomPlanarGraphDirectedEdge> astarPath(Node start, Node goal) {
		// initial check
		if (start == null || goal == null) {
			System.err.println("Error: invalid node provided to AStar");
		}

		// set up the containers for the result
		ArrayList<GeomPlanarGraphDirectedEdge> result = new ArrayList<GeomPlanarGraphDirectedEdge>();

		// containers for the meta information about the Nodes relative to the
		// A* search
		HashMap<Node, AStarNodeWrapper> foundNodes = new HashMap<Node, AStarNodeWrapper>();

		AStarNodeWrapper startNode = new AStarNodeWrapper(start);
		AStarNodeWrapper goalNode = new AStarNodeWrapper(goal);
		foundNodes.put(start, startNode);
		foundNodes.put(goal, goalNode);

		startNode.m_gx = 0;
		startNode.m_hx = heuristic(start, goal);
		startNode.m_fx = heuristic(start, goal);

		// A* containers: nodes to be investigated, nodes that have been investigated
		ArrayList<AStarNodeWrapper> closedSet = new ArrayList<AStarNodeWrapper>();
		ArrayList<AStarNodeWrapper> openSet = new ArrayList<AStarNodeWrapper>();
		openSet.add(startNode);

		while (openSet.size() > 0) { // while there are reachable nodes to investigate

			AStarNodeWrapper x = findMin(openSet); // find the shortest path so far
			if (x.m_node == goal) { // we have found the shortest possible path to the goal!
									// Reconstruct the path and send it back.
				return reconstructPath(goalNode);
			}
			openSet.remove(x); // maintain the lists
			closedSet.add(x);

			// check all the edges out from this Node
			DirectedEdgeStar des = x.m_node.getOutEdges();
			for (Object o : des.getEdges().toArray()) {
				GeomPlanarGraphDirectedEdge l = (GeomPlanarGraphDirectedEdge) o;
				Node next = null;
				next = l.getToNode();

				// get the A* meta information about this Node
				AStarNodeWrapper nextNode;
				if (foundNodes.containsKey(next)) {
					nextNode = foundNodes.get(next);
				} else {
					nextNode = new AStarNodeWrapper(next);
					foundNodes.put(next, nextNode);
				}

				if (closedSet.contains(nextNode)) // it has already been considered
				{
					continue;
				}

				// otherwise evaluate the cost of this node/edge combo
				double tentativeCost = x.m_gx + length(l);
				boolean better = false;

				if (!openSet.contains(nextNode)) {
					openSet.add(nextNode);
					nextNode.m_hx = heuristic(next, goal);
					better = true;
				} else if (tentativeCost < nextNode.m_gx) {
					better = true;
				}

				// store A* information about this promising candidate node
				if (better) {
					nextNode.m_cameFrom = x;
					nextNode.m_edgeFrom = l;
					nextNode.m_gx = tentativeCost;
					nextNode.m_fx = nextNode.m_gx + nextNode.m_hx;
				}
			}
		}
		return result;
	}

	/**
	 * <p>Helper method for the A* search algorithm.
	 * Calculates the euclidean distance between two nodes.
	 * Is used as a heuristic for the real distance.</p>
	 * 
	 * @param startNode - the start node.
	 * @param endNode - the end node.
	 * @return double - a heuristic number for the distance between the two nodes.
	 */
	private static double heuristic(Node startNode, Node endNode) {
		Coordinate startCoordinate = startNode.getCoordinate();
		Coordinate endCoordinate = endNode.getCoordinate();
		return Math.sqrt(Math.pow(startCoordinate.x - endCoordinate.x, 2) + Math.pow(startCoordinate.y - endCoordinate.y, 2));
	}

	/**
	 *<p>Helper method for the A* search algorithm.
	 * Find wrapper with minimal value for m_fx.</p>
	 * 
	 * @param set list of open nodes.
	 * @return AStarNodeWrapper - the wrapper with the min value for m_fx.
	 */
	private static AStarNodeWrapper findMin(ArrayList<AStarNodeWrapper> set) {
		double min = 100000;
		AStarNodeWrapper minNode = null;
		for (AStarNodeWrapper n : set) {
			if (n.m_fx < min) {
				min = n.m_fx;
				minNode = n;
			}
		}
		return minNode;
	}

	/**
	 * <p>Takes the information about the given node n and returns the path that found it.</p>
	 * 
	 * @param n - the end point of the path.
	 * @return ArrayList - a list of edges that lead from the given node to the node from which the search began.
	 */
	private static ArrayList<GeomPlanarGraphDirectedEdge> reconstructPath(AStarNodeWrapper n) {
		ArrayList<GeomPlanarGraphDirectedEdge> result = new ArrayList<GeomPlanarGraphDirectedEdge>();
		AStarNodeWrapper x = n;
		while (x.m_cameFrom != null) {
			result.add(0, x.m_edgeFrom); // add this edge to the front of the list
			x = x.m_cameFrom;
		}

		return result;
	}

	/**
	 * <p>This method calculates an approximation of the length of an edge.</p>
	 * 
	 * @param e - an edge
	 * @return double - an approximation of the length of an edge.
	 */
	private static double length(GeomPlanarGraphDirectedEdge e) {
		Coordinate xnode = e.getFromNode().getCoordinate();
		Coordinate ynode = e.getToNode().getCoordinate();
		return Math.sqrt(Math.pow(xnode.x - ynode.x, 2) + Math.pow(xnode.y - ynode.y, 2));
	}
}
