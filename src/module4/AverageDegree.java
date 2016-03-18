package module4;

import graph.GraphUtils;
import graph.MGraph;

import java.util.HashSet;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * AverageDegree
 * 
 * @author adeelq
 *
 */
public class AverageDegree {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";

	public AverageDegree() {
		// read graph from file
		graph = GraphUtils.readGraph(GRAPH_FILE);

		int totalNeighborDegrees = 0;
		int totalNeighborCount = 0;
		int numVertices = 0;
		int sumDegrees = 0;

		// go through all vertices
		for (Vertex v : graph.getVertices()) {
			// calculate the degree of the vertex
			int degree = getDegree(v);
			// output vertex id and its degree
			System.out.println(v.getId() + " = " + degree);

			int neighborDegreeSum = 0;
			int neighborCount = 0;
			// get all neighbors for this vertex and iterate over them
			for (Vertex n : getNeighbors(v)) {
				// get neighbor degree
				int neighborDegree = getDegree(n);
				// output neighbor id and its degree
				System.out.println("Neighbor " + n + " = " + neighborDegree);

				// increment vertex neighbor vars
				neighborCount++;
				neighborDegreeSum += neighborDegree;

				// increment overall neighbor vars
				totalNeighborDegrees += neighborDegree;
				totalNeighborCount++;
			}
			// output average degree for the neighbors for this vertex
			System.out.println("Average degree of neighbors = " + ((float) neighborDegreeSum / neighborCount));

			// increment average degree vars
			numVertices++;
			sumDegrees += degree;
			System.out.println();
		}

		// output final average degree and average of neighbors for the whole
		// graph
		System.out.println("Average degree of the graph = " + ((float) sumDegrees / numVertices));
		System.out.println("Average degree of the neighbors for the graph = "
				+ ((float) totalNeighborDegrees / totalNeighborCount));

	}

	/**
	 * Calculate the degree for given vertex
	 * 
	 * @param vertex
	 * @return
	 */
	private int getDegree(Vertex vertex) {
		int degree = 0;
		for (final Edge e : vertex.getEdges(Direction.BOTH)) {
			degree++;
		}
		return degree;
	}

	/**
	 * Returns all neighbors of given vertex
	 * 
	 * @param vertex
	 * @return
	 */
	private Set<Vertex> getNeighbors(Vertex vertex) {
		Set<Vertex> neighbors = new HashSet<Vertex>();
		for (Edge edge : vertex.getEdges(Direction.IN)) {
			neighbors.add(edge.getVertex(Direction.OUT));
		}
		for (Edge edge : vertex.getEdges(Direction.OUT)) {
			neighbors.add(edge.getVertex(Direction.IN));
		}
		return neighbors;
	}

	public static void main(String[] args) {
		new AverageDegree();
	}

}
