package module3;

import graph.GraphUtils;
import graph.MGraph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * EigenValues
 * 
 * @author adeelq
 *
 */
public class EigenValues {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";

	private Map<String, Integer> vertexIndices = null;
	private Map<Integer, String> vertexIDs = null;
	private int vertexCount = 0;

	private Algebra alg = new Algebra();
	private Functions F = Functions.functions;

	public EigenValues() {
		// read graph from file
		graph = GraphUtils.readGraph(GRAPH_FILE);

		// get the adjacency matrix from the graph
		DoubleMatrix2D adjacency = graph.getAdjacency();
		processMatrix("Adjacency Matrix", adjacency);

		// get the laplacian matrix
		DoubleMatrix2D laplacian = getLaplacian(graph);
		processMatrix("Laplacian Matrix", laplacian);
	}

	/**
	 * Processes given matrix for eigen values
	 * 
	 * @param label
	 * @param matrix
	 */
	private void processMatrix(String label, DoubleMatrix2D matrix) {
		System.out.println("=== " + label + " ===");
		// calucate the eigen values
		EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(matrix);
		double[] eigenvalues = eigenvalueDecomposition.getRealEigenvalues().toArray();

		// sort the values
		Arrays.sort(eigenvalues);

		// zero value threshold
		double low = Math.pow(10, -10);
		int zeroValues = 0;

		// iterate over all eigen values
		for (int j = 0; j < eigenvalues.length; j++) {
			double d = eigenvalues[j];
			// zero value increment
			if (d <= low) {
				zeroValues++;
			}
			System.out.println("eigenvalue : " + d);
		}
		System.out.println("Number of Zero values: " + zeroValues);
	}

	/**
	 * Calculates laplacian matrix
	 * 
	 * @param graph
	 * @return
	 */
	private DoubleMatrix2D getLaplacian(MGraph graph) {
		vertexIndices = new HashMap<>();
		vertexIDs = new HashMap<>();

		int counter = 0;
		for (Vertex v : graph.getVertices()) {
			vertexIndices.put(v.getId().toString(), counter);
			vertexIDs.put(counter, v.getId().toString());
			counter++;
		}
		vertexCount = counter;

		DoubleMatrix2D degree = new SparseDoubleMatrix2D(vertexCount, vertexCount);

		for (Edge e : graph.getEdges()) {
			Vertex from = e.getVertex(Direction.OUT);
			int fromEdges = countEdges(from.getEdges(Direction.BOTH));
			int i = vertexIndices.get(from.getId().toString());
			Vertex to = e.getVertex(Direction.IN);
			int toEdges = countEdges(to.getEdges(Direction.BOTH));
			int j = vertexIndices.get(to.getId().toString());
			if (i != j) {
				degree.set(i, j, fromEdges);
				degree.set(j, i, toEdges);
			}
		}

		return degree.assign(graph.getAdjacency(), F.minus);
	}

	/**
	 * Counts edges
	 * 
	 * @param edges
	 * @return
	 */
	private int countEdges(Iterable<Edge> edges) {
		int count = 0;
		Iterator<Edge> iter = edges.iterator();
		while (iter.hasNext()) {
			iter.next();
			count++;
		}
		return count;
	}

	public static void main(String[] args) {
		new EigenValues();
	}
}
