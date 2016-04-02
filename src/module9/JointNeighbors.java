package module9;

import graph.GraphUtils;
import graph.MGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.plot.Plot;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class JointNeighbors {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";
	private static final String OUT_GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".out.graphml";

	public JointNeighbors() {
		graph = GraphUtils.readGraph(GRAPH_FILE);

//		System.out.println("[\tDirect Joint Neighbors\t]");
//		List<Double> values1 = countDirectJointNeighbors();
//		generateHistogram(values1);

		System.out.println("\n[\tNonDirect Joint Neighbors\t]");
		List<Double> values2 = countNonDirectJointNeighbors();
		generateHistogram(values2);
	}

	private List<Double> countDirectJointNeighbors() {
		List<Double> values = new ArrayList<Double>();
		Map<String, String> processed = new HashMap<String, String>();

		for (Vertex v : graph.getVertices()) {
			for (Vertex w : v.getVertices(Direction.OUT)) {
				if (processed.containsKey(v.getId() + "_" + w.getId())
						|| processed.containsKey(w.getId() + "_" + v.getId())) {
					continue;
				}
				// System.out.print(v.getId() + " - " + w.getId());
				processed.put(v.getId() + "_" + w.getId(), "");

				double count = countJointNeighbors(v, w);
				values.add(count);
//				System.out.println(v.getId() + ", " + w.getId() + " = " + count);
			}
		}
		return values;
	}

	private List<Double> countNonDirectJointNeighbors() {
		List<Double> values = new ArrayList<Double>();
		Map<String, String> processed = new HashMap<String, String>();

		for (Vertex v : graph.getVertices()) {
			for (Vertex w : graph.getVertices()) {
				if (v == w || isNeighbor(v, w)) {
					continue;
				}

				if (processed.containsKey(v.getId() + "_" + w.getId())
						|| processed.containsKey(w.getId() + "_" + v.getId())) {
					continue;
				}
				processed.put(v.getId() + "_" + w.getId(), "");

				double count = countJointNeighbors(v, w);
				values.add(count);
//				System.out.println(v.getId() + ", " + w.getId() + " = " + count);
			}
		}
		return values;
	}

	private double countJointNeighbors(Vertex v, Vertex w) {
		int numOfJointNeighbors = 0;
		for (Vertex u : graph.getVertices()) {
			if (u == v || u == w)
				continue;

			int checks = 0;
			for (Edge e : u.getEdges(Direction.OUT)) {
				if (e.getVertex(Direction.IN) == v) {
					checks++;
				} else if (e.getVertex(Direction.IN) == w) {
					checks++;
				}
			}
			if (checks >= 2) {
				numOfJointNeighbors++;
			}
		}
		return (double) numOfJointNeighbors;
	}

	private boolean isNeighbor(Vertex v, Vertex w) {
		for (Edge e : w.getEdges(Direction.OUT)) {
			if (e.getVertex(Direction.IN) == v) {
				return true;
			}
		}
		return false;
	}

	private void generateHistogram(List<Double> values) {
		double[] data = new double[values.size()];
		int i = 0;
		for (double v : values) {
			data[i++] = v;
		}

		EmpiricalDistribution histogram = new EmpiricalDistribution(data.length);
		histogram.load(data);

		int count = histogram.getBinCount();
		// we will be storing the probality for each bin here
		double[][] probabilities = new double[count][count];
		int j = 0;
		
		List<SummaryStatistics> stats = histogram.getBinStats();
		System.out.println("N == " + histogram.getSampleStats().getN());
		for (SummaryStatistics stat : stats) {
			if (stat.getN() > 0) {
				System.out.println("Number of joint neighbors=" + stat.getMax() + ", How often=" + stat.getN()
						+ ", Probablity=" + ((double) stat.getN() / histogram.getSampleStats().getN()));
			}
			probabilities[j][0] = stat.getMax(); 
			probabilities[j++][1] = ((double) stat.getN() / histogram.getSampleStats().getN());
		}
		
		JavaPlot p = new JavaPlot();
		// pass the probabilities matrix to the plot
		Plot dat = new DataSetPlot(probabilities);
		p.addPlot(dat);
		p.plot();
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
		new JointNeighbors();
	}
}
