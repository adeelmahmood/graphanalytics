package module5;

import graph.GraphUtils;
import graph.MGraph;

import java.util.HashSet;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

/**
 * ClusteringCoefficient
 * 
 * Two main parameters that this program finds are 
 * 
 * 1. number of closed paths of length two
 *    These are two hop paths that are closed to form a triangle
 * 2. number of paths of length two
 *    These are two hop paths that do not form a triangle
 * 
 * @author adeelq
 *
 */
public class ClusteringCoefficient {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";

	Set<String> twoHopsLabels = new HashSet<String>();
	Set<String> trianglesLabels = new HashSet<String>();

	public ClusteringCoefficient() {
		// read graph from file
		graph = GraphUtils.readGraph(GRAPH_FILE);

		int twoHops = 0;
		int triangles = 0;
		// go through all vertices
		for (Vertex v : graph.getVertices()) {
			// for each vertex, get its neighbors
			Set<Vertex> neighbors = getNeighbors(v);
			// go through all neighbors
			for (Vertex w : neighbors) {
				// for each neighbor get next neighbor
				// this should give us the two hop connections
				for (Vertex z : getNeighbors(w)) {
					String label = v.getId() + "," + w.getId() + "," + z.getId();
					if (z == v) {
						continue;
					}

					// keep track of duplicate routes
					if (!containsLabel(label, twoHopsLabels)) {
						// System.out.println("Two hops " + label);
						// if the end point of two hop is a neighbor
						// of original vertex, then its a closed traingle
						if (neighbors.contains(z)) {

							// duplicates
							if (!containsLabel(label, trianglesLabels)) {
								triangles++;
								// System.out.println("Found triangle at " +
								// label);
								trianglesLabels.add(label);
							}
						}
						// just two hops, no triangle
						else {
							twoHops++;
						}
					}

					twoHopsLabels.add(label);
				}
			}
		}

		System.out.println("Total Two Hops = " + twoHops);
		System.out.println("Total Triangles = " + triangles);
		System.out.println("Clustering Coefficient = " + (double) triangles / twoHops);
	}

	private boolean containsLabel(String label, Set<String> s) {
		boolean contains = false;
		for (String _s : s) {
			if (_s.contains(label.split(",")[0]) && _s.contains(label.split(",")[1])
					&& _s.contains(label.split(",")[2])) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	private Set<Vertex> getNeighbors(Vertex vertex) {
		Set<Vertex> neighbors = new HashSet<Vertex>();
		for (Vertex v : vertex.getVertices(Direction.BOTH)) {
			neighbors.add(v);
		}
		return neighbors;
	}

	public static void main(String[] args) {
		new ClusteringCoefficient();
	}
}
