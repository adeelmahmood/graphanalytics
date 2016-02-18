package histogram;

import graph.GraphUtils;
import graph.MGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.plot.Plot;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * 
 * StudentHistogram
 * 
 * @author adeelq
 *
 */
public class StudentHistogram {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";

	private Algebra alg = new Algebra();

	public StudentHistogram() {
		// read graph from file
		graph = GraphUtils.readGraph(GRAPH_FILE);

		// get the adjacency matrix from the graph which
		// shows the vertices adjacent to each other
		// this is needed to calculate the degrees of the vertices
		DoubleMatrix2D adjacency = graph.getAdjacency();
		int cols = adjacency.columns();
		int rows = adjacency.rows();

		// create a 1s matrix
		DoubleMatrix2D vector = new SparseDoubleMatrix2D(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				vector.set(i, j, 1);
			}
		}

		// multiple the 1s matrix with adjacency matrix
		// to calculate the degree matrix
		DoubleMatrix2D result = alg.mult(adjacency, vector);

		// unique degrees will be the bins
		Map<String, String> bins = new HashMap<String, String>();
		double[] data = new double[rows];
		for (int i = 0; i < rows; i++) {
			data[i] = result.get(i, 0);
			if (!bins.containsKey(data[i])) {
				bins.put(String.valueOf(data[i]), "");
			}
		}

		// iterateEdges();
		// System.out.println(adjacency);
		// System.out.println(result);
		// System.out.println(Arrays.toString(data));

		// create a histogram
		EmpiricalDistribution histogram = new EmpiricalDistribution(bins.size());
		// load degree matrix in the histogram
		histogram.load(data);

		// Print general statistics of the histogram:
		System.out.println("Histogram stats:");
		System.out.println(histogram.getSampleStats());

		// Print statistics for each bin
		System.out.println("Bin stats (" + histogram.getSampleStats().getN() + "):");
		List<SummaryStatistics> stats = histogram.getBinStats();
		int count = histogram.getBinCount();
		// we will be storing the probality for each bin here
		double[][] probabilities = new double[count][count];
		int i = 0;
		// iterate over all bins
		for (SummaryStatistics stat : stats) {
			System.out.println("Bin=" + stat.getMax() + ", N=" + stat.getN() + ", P="
					+ ((double) stat.getN() / histogram.getSampleStats().getN()));

			// store probablity distribution plot data
			probabilities[i][0] = stat.getMax(); // how many connections
			probabilities[i++][1] = ((double) stat.getN() / histogram.getSampleStats().getN()); // probability
																								// for
																								// connections
		}

		// generate the plot
		JavaPlot p = new JavaPlot();
		// pass the probabilities matrix to the plot
		Plot dat = new DataSetPlot(probabilities);
		p.addPlot(dat);
		p.plot();
	}

	public void iterateEdges() {
		for (Edge e : graph.getEdges()) {
			Vertex fromVertex = e.getVertex(Direction.OUT);
			Vertex toVertex = e.getVertex(Direction.IN);
			System.out.println(fromVertex.getId() + " -> " + toVertex.getId() + " by " + e.getId());
		}
	}

	public static void main(String[] args) {
		new StudentHistogram();
	}
}
