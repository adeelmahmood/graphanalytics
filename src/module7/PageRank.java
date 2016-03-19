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

	private static final int ITERATIONS = 1;
	private static final String PR = "PR";

	public PageRank() {
		graph = GraphUtils.readGraph(GRAPH_FILE);
		int n = count(graph.getVertices());

		for (Vertex vertex : graph.getVertices()) {
			vertex.setProperty(PR, 0.8);
		}

		for (Vertex vertex : graph.getVertices()) {
			System.out.println("Processing vertex " + vertex);
			double values = 0;

			for (Vertex v : graph.getVertices()) {
				if (v == vertex)
					continue;

				// System.out.println("Checking vertex " + v);
				int outEdges = count(v.getEdges(Direction.OUT));
				if (outEdges > 0) {
					// System.out.println("Out edges " + outEdges);
					double rank = v.getProperty(PR);
					// System.out.println("Previous rank " + rank);
					double value = (1D / outEdges) * rank;
					// System.out.println("Value " + value);

					values += value;
				}
			}

			double newRank = values * 0.8;
			newRank += (1 - 0.8) / n;

			System.out.println("Final page rank " + newRank);
			// break;
		}
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
