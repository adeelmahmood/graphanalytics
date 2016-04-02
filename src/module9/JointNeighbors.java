package module9;

import graph.GraphUtils;
import graph.MGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class JointNeighbors {

	private static String graphName = "students";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY + "/" + graphName + ".graphml";

	private Map<Double, Double> directProbs = new HashMap<Double, Double>();
	private Map<Double, Double> nonDirectProbs = new HashMap<Double, Double>();

	public JointNeighbors() {
		graph = GraphUtils.readGraph(GRAPH_FILE);

		System.out.println("[\tDirect Joint Neighbors\t]");
		List<Double> values1 = countDirectJointNeighbors();
		generateHistogram(values1, directProbs);

		System.out.println("\n[\tNonDirect Joint Neighbors\t]");
		List<Double> values2 = countNonDirectJointNeighbors();
		generateHistogram(values2, nonDirectProbs);

		System.out.println();
		for (Entry<Double, Double> e : directProbs.entrySet()) {
			Double num = e.getValue();
			Double denom = nonDirectProbs.get(e.getKey());
			System.out.print(e.getKey() + " => " + num + "/" + denom + " = " + num / denom);
			if (num / denom > 1) {
				System.out.println(" (link)");
			} else {
				System.out.println();
			}
		}
	}

	/**
	 * Direct joint neighbors are the vertices that are connected to v and w
	 * vertices when v and w are connected to each other
	 * 
	 * @return
	 */
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
				// System.out.println(v.getId() + ", " + w.getId() + " = " +
				// count);
			}
		}
		return values;
	}

	/**
	 * Non direct joint neighbors are the vertices that are connected to v and w
	 * vertices when v and w are not connected to each other
	 * 
	 * @return
	 */
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
				// System.out.println(v.getId() + ", " + w.getId() + " = " +
				// count);
			}
		}
		return values;
	}

	/**
	 * Counts the number of joint neighbors for given vertex v and w
	 */
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

	/**
	 * Determines if given vertex w is connected to vertex v
	 */
	private boolean isNeighbor(Vertex v, Vertex w) {
		for (Edge e : w.getEdges(Direction.OUT)) {
			if (e.getVertex(Direction.IN) == v) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Given a set of values, this function generates histogram and populates a
	 * given map of probablities where the keys are number of joint neighbors
	 * and values are how often
	 */
	private void generateHistogram(List<Double> values, Map<Double, Double> probs) {
		double[] data = new double[values.size()];
		int i = 0;
		for (double v : values) {
			data[i++] = v;
		}

		EmpiricalDistribution histogram = new EmpiricalDistribution(data.length);
		histogram.load(data);

		List<SummaryStatistics> stats = histogram.getBinStats();
		System.out.println("N == " + histogram.getSampleStats().getN());
		for (SummaryStatistics stat : stats) {
			if (stat.getN() > 0) {
				System.out.println("Number of joint neighbors=" + stat.getMax() + ", How often=" + stat.getN()
						+ ", Probablity=" + ((double) stat.getN() / histogram.getSampleStats().getN()));
				probs.put(stat.getMax(), ((double) stat.getN() / histogram.getSampleStats().getN()));
			}
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
		new JointNeighbors();
	}
}
