package rfs0.aitam.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.planargraph.DirectedEdgeStar;
import com.vividsolutions.jts.planargraph.Node;

import sim.util.geo.GeomPlanarGraphDirectedEdge;

public class GraphUtility {

	public static ArrayList<GeomPlanarGraphDirectedEdge> astarPath(Node start, Node goal) {
		// initial check
		if (start == null || goal == null) {
			System.out.println("Error: invalid node provided to AStar");
		}

		// set up the containers for the result
		ArrayList<GeomPlanarGraphDirectedEdge> result = new ArrayList<GeomPlanarGraphDirectedEdge>();

		// containers for the metainformation about the Nodes relative to the
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
	 * Measure of the estimated distance between two Nodes. Extremely basic, just
	 * Euclidean distance as implemented here.
	 * 
	 * @param startNode
	 * @param endNode
	 * @return notional "distance" between the given nodes.
	 */
	private static double heuristic(Node startNode, Node endNode) {
		Coordinate startCoordinate = startNode.getCoordinate();
		Coordinate endCoordinate = endNode.getCoordinate();
		return Math.sqrt(Math.pow(startCoordinate.x - endCoordinate.x, 2) + Math.pow(startCoordinate.y - endCoordinate.y, 2));
	}

	/**
	 * Considers the list of Nodes open for consideration and returns the node with
	 * minimum fx value
	 * 
	 * @param set list of open Nodes
	 * @return
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
	 * Takes the information about the given node n and returns the path that found
	 * it.
	 * 
	 * @param n the end point of the path
	 * @return an ArrayList of GeomPlanarGraphDirectedEdges that lead from the given
	 *         Node to the Node from which the serach began
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
	 * @param e
	 * @return The length of an edge
	 */
	private static double length(GeomPlanarGraphDirectedEdge e) {
		Coordinate xnode = e.getFromNode().getCoordinate();
		Coordinate ynode = e.getToNode().getCoordinate();
		return Math.sqrt(Math.pow(xnode.x - ynode.x, 2) + Math.pow(xnode.y - ynode.y, 2));
	}
}
