package module6;

import graph.GraphUtils;
import graph.MGraph;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

import com.tinkerpop.blueprints.Vertex;

public class EigenVectorCentrality {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";
	private static final String OUT_GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".out.graphml";

	public EigenVectorCentrality() {
		graph = GraphUtils.readGraph(GRAPH_FILE);

		DoubleMatrix2D adjacency = graph.getAdjacency();

		// calculate eigenvalues for the graph
		EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(adjacency);
		// extract eigenvalues for nodes
		double[] eigenvalues = eigenvalueDecomposition.getRealEigenvalues().toArray();
		// extract eigenvector for nodes
		DoubleMatrix2D vector = eigenvalueDecomposition.getV();

		// find index of max eigen value
		int maxIndex = 0;
		for (int i = 0; i < eigenvalues.length; i++) {
			double value = eigenvalues[i];
			if (value > eigenvalues[maxIndex]) {
				maxIndex = i;
			}
		}

		// extract eigenvector corresponding to max eigen value
		// this represents the eigenvector centrality for all nodes
		// in the graph
		DoubleMatrix1D centrality = vector.viewColumn(maxIndex);
		for (int i = 0; i < centrality.size(); i++) {
			// look up id for vector from matrix element
			String id = graph.getVertexIDForMatrixElement(i);
			// get the vector instance
			Vertex v = graph.getVertex(id);
			// add eigenvector centrality as property on the vector
			v.setProperty("eigenvector-centrality", centrality.get(i));
		}

		// save the graph in a new file
		GraphUtils.saveGraph(graph, OUT_GRAPH_FILE);
	}

	public static void main(String[] args) {
		new EigenVectorCentrality();
	}
}
