package module10;

import graph.GraphUtils;
import graph.MGraph;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class PeerPressuePartitioning {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";
	private static final String OUT_GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".out.graphml";

	private Algebra alg = new Algebra();

	private static final double p = 0.2;

	private DoubleMatrix2D clusterMatrix;
	private DoubleMatrix2D adjacencyPrimeMatrix;

	public PeerPressuePartitioning() {
		graph = GraphUtils.readGraph(GRAPH_FILE);
		adjacencyPrimeMatrix = calculateAMatrix(graph.getAdjacency());

		int count = 0;
		boolean done = false;
		while (!done) {
			updateClusterMatrix(graph);

			DoubleMatrix2D clusterPrimeMatrix = calculateCMatrix(clusterMatrix);
			DoubleMatrix2D tallyMatrix = alg.mult(clusterPrimeMatrix, adjacencyPrimeMatrix);

			done = !applyClusterAssignments(tallyMatrix);
			count++;
			if(count>=1) {
				break;
			}
		}

		System.out.println("Took " + count + " iterations");
		GraphUtils.saveGraph(graph, OUT_GRAPH_FILE);

		for (Vertex v : graph.getVertices()) {
			System.out.println(v.getId() + " = " + v.getProperty("cluster"));
		}
	}

	private DoubleMatrix2D calculateAMatrix(DoubleMatrix2D matrix) {

		int rows = matrix.rows();
		int cols = matrix.columns();
		DoubleMatrix2D aMatrix = new SparseDoubleMatrix2D(rows, cols);

		for (int i = 0; i < rows; i++) {
			double rowSum = 0;
			for (int j = 0; j < cols; j++) {
				rowSum += matrix.get(i, j) + delta(i, j);
			}

			for (int j = 0; j < cols; j++) {
				double value = (matrix.get(i, j) + delta(i, j)) / rowSum;
				aMatrix.set(i, j, value);
			}
		}

		return aMatrix;
	}

	private DoubleMatrix2D calculateCMatrix(DoubleMatrix2D matrix) {
		int rows = matrix.rows();
		int cols = matrix.columns();
		DoubleMatrix2D cMatrix = new SparseDoubleMatrix2D(rows, cols);

		for (int i = 0; i < rows; i++) {
			int rowSum = 0;
			for (int j = 0; j < cols; j++) {
				rowSum += matrix.get(i, j);
			}
			double value = Math.pow(rowSum, p);

			for (int j = 0; j < cols; j++) {
				double v = value == 0 ? 0 : matrix.get(i, j) / value;
				cMatrix.set(i, j, v);
			}
		}

		return cMatrix;
	}

	private int delta(int i, int j) {
		return i == j ? 1 : 0;
	}

	private void updateClusterMatrix(Graph g) {
		int count = count(g.getVertices());
		if (clusterMatrix == null) {
			clusterMatrix = new SparseDoubleMatrix2D(count, count);
		}

		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {

				String vertexId = graph.getVertexIDForMatrixElement(j);
				Vertex vertex = graph.getVertex(vertexId);
				Integer cluster = vertex.getProperty("cluster");

				if (cluster == null) {
					if (i == j) {
						clusterMatrix.set(i, j, 1);
						vertex.setProperty("cluster", i);
					}
				} else if (cluster.intValue() == i) {
					clusterMatrix.set(i, j, 1);
				}

			}
		}
	}

	private boolean applyClusterAssignments(DoubleMatrix2D matrix) {
		int rows = matrix.rows();
		int cols = matrix.columns();
		boolean changed = false;

		for (int j = 0; j < cols; j++) {
			double maxVote = -1;
			int maxIndx = -1;
			for (int i = 0; i < rows; i++) {
				if (matrix.get(i, j) > maxVote) {
					maxVote = matrix.get(i, j);
					maxIndx = i;
				}
			}

			if (maxIndx > -1) {
				String vertexId = graph.getVertexIDForMatrixElement(j);
				Vertex vertex = graph.getVertex(vertexId);

				int oldCluster = vertex.getProperty("cluster");
				if (oldCluster != maxIndx) {
					vertex.setProperty("cluster", maxIndx);
					changed = true;
				}
			}
		}
		return changed;
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
		new PeerPressuePartitioning();
	}
}
