package module7;

import graph.GraphUtils;
import graph.MGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class SparkGraphGenerator {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";
	private static final String VERTICES_FILE = BASE_DIRECTORY + "/vertices.txt";
	private static final String EDGES_FILE = BASE_DIRECTORY + "/edges.txt";

	public SparkGraphGenerator() throws IOException {
		graph = GraphUtils.readGraph(GRAPH_FILE);

		int iid = 1;
		// assign each vertex an integer id
		for (Vertex vertex : graph.getVertices()) {
			vertex.setProperty("iid", iid++);
		}

		// create vertices file
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(VERTICES_FILE))) {
			for (Vertex vertex : graph.getVertices()) {
				writer.write(vertex.getProperty("iid") + "," + vertex.getId());
				writer.newLine();
			}
		}

		// create edges file
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(EDGES_FILE))) {
			for (Edge edge : graph.getEdges()) {
				writer.write(edge.getVertex(Direction.OUT).getProperty("iid") + "\t"
						+ edge.getVertex(Direction.IN).getProperty("iid"));
				writer.newLine();
			}
		}

	}

	public static void main(String[] args) throws IOException {
		new SparkGraphGenerator();
	}
}
