package module7;

import graph.GraphUtils;
import graph.MGraph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

public class PageRank {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";
	private static final String OUT_GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".out.graphml";

	private static final int ITERATIONS = 20;
	private static final String PR = "PR";
	private static final String PR_TEMP = "PR_TEMP";
	private static final double S = 0.8D;

	public PageRank() {
		graph = GraphUtils.readGraph(GRAPH_FILE);

		// count the number of vertices
		int n = count(graph.getVertices());

		// initialize rank for all vertices
		for (Vertex vertex : graph.getVertices()) {
			vertex.setProperty(PR, 1D / n);
		}

		// page rank calculation iterations
		for (int i = 1; i <= ITERATIONS; i++) {
			// process each vertex
			for (Vertex vertex : graph.getVertices()) {
				double values = 0;

				// calculate rank from all other vertices
				for (Vertex v : graph.getVertices()) {
					if (v == vertex)
						continue;

					// get current level rank
					double rank = v.getProperty(PR);
					double value = 0;

					// count the number of out edges from this vertex
					int outEdges = count(v.getEdges(Direction.OUT));
					if (outEdges > 0) {
						value = (1D / outEdges) * rank;
					} else {
						// if no outgoing edges, use 1/n
						value = (1D / n) * rank;
					}

					values += value;
				}

				// final rank for this vertex for this iteration
				double newRank = values * S + (1 - S) / n;
				// set calculated rank on a temp property
				vertex.setProperty(PR_TEMP, newRank);
			}

			// iteration has completed, updated ranks on all vertices to
			// calculated rank in this round
			for (Vertex vertex : graph.getVertices()) {
				vertex.setProperty(PR, vertex.getProperty(PR_TEMP));
				vertex.removeProperty(PR_TEMP);
			}
		}

		for (Vertex vertex : graph.getVertices()) {
			System.out.println("- - Vertex " + vertex + " Page rank " + vertex.getProperty(PR));
		}

		// // save the graph in a new file
		GraphUtils.saveGraph(graph, OUT_GRAPH_FILE);
	}

	@SuppressWarnings("unused")
	private <T> int count(Iterable<T> recs) {
		int count = 0;
		for (T t : recs) {
			count++;
		}
		return count;
	}

	public static void main(String[] args) {
		new PageRank();
	}
}
