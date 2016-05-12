package module11;

import graph.GraphUtils;
import graph.MGraph;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;

import com.tinkerpop.blueprints.Vertex;

public class LaplacianPartitioning {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";
	private static final String OUT_GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".out.graphml";

	private Algebra alg = new Algebra();

	public LaplacianPartitioning() {
		graph = GraphUtils.readGraph(GRAPH_FILE);

		DoubleMatrix2D adjacency = graph.getAdjacency();

		DoubleMatrix2D degree = getDegreeMatrix(adjacency);

		DoubleMatrix2D laplacian = getLaplacianMatrix(adjacency, degree);

		DoubleMatrix2D inverseDegree = getInverseMatrix(degree);

		DoubleMatrix2D normLaplacian = getNormalizedLaplacianMatrix(laplacian, inverseDegree);

		EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(normLaplacian);
		double[] eigenvalues = eigenvalueDecomposition.getRealEigenvalues().toArray();
		DoubleMatrix2D vector = eigenvalueDecomposition.getV();

		DoubleMatrix1D column = vector.viewColumn(findSecondSmallestEigenValueIndex(eigenvalues));
		for (int j = 0; j < column.size(); j++) {
			String id = graph.getVertexIDForMatrixElement(j);
			Vertex v = graph.getVertex(id);
			String partition = (column.get(j) > 0 ? "1" : "2");
			System.out.println("Vertex " + v.getId() + ", Partition = " + partition);
			v.setProperty("partition", partition);
		}

		GraphUtils.saveGraph(graph, OUT_GRAPH_FILE);
	}

	public DoubleMatrix2D getNormalizedLaplacianMatrix(DoubleMatrix2D laplacian, DoubleMatrix2D inverseDegree) {
		return alg.mult(inverseDegree, alg.mult(laplacian, inverseDegree));
	}

	private DoubleMatrix2D getLaplacianMatrix(DoubleMatrix2D adjacency, DoubleMatrix2D degree) {
		return degree.assign(adjacency, Functions.minus);
	}

	private DoubleMatrix2D getDegreeMatrix(DoubleMatrix2D matrix) {
		int rows = matrix.rows();
		int cols = matrix.columns();

		DoubleMatrix2D degreeMatrix = new SparseDoubleMatrix2D(rows, cols);

		int _j = 0;
		for (int i = 0; i < rows; i++) {
			int degree = 0;
			for (int j = 0; j < cols; j++) {
				degree += matrix.get(i, j);
			}
			degreeMatrix.set(i, _j++, degree);
		}

		return degreeMatrix;
	}

	private DoubleMatrix2D getInverseMatrix(DoubleMatrix2D matrix) {
		int rows = matrix.rows();
		int cols = matrix.columns();
		DoubleMatrix2D inverseMatrix = new SparseDoubleMatrix2D(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i == j) {
					double value = matrix.get(i, j);
					inverseMatrix.set(i, j, value == 0 ? 0 : Math.pow(value, -0.5));
				}
			}
		}
		return inverseMatrix;
	}

	private int findSecondSmallestEigenValueIndex(double[] values) {
		int indx = 0;
		double smallest = Integer.MAX_VALUE, secondSmallest = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (values[i] < smallest) {
				secondSmallest = smallest;
				smallest = values[i];
				indx = i;
			} else if (values[i] < secondSmallest) {
				secondSmallest = values[i];
				indx = i;
			}
		}
		return indx;
	}

	public static void main(String[] args) {
		new LaplacianPartitioning();
	}
}
