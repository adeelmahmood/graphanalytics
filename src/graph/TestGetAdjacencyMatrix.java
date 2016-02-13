package graph;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * @author C. Savkli, Dec 20, 2013
 * @version 1.0
 */

public class TestGetAdjacencyMatrix
{
	private static String graphName = "tinker_graph";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY+"/"+graphName+".graphml";
	
	public TestGetAdjacencyMatrix()
	{
		graph = GraphUtils.readGraph(GRAPH_FILE);
		        
        DoubleMatrix2D adjacency = graph.getAdjacency();
        
        System.out.println(adjacency);
	}
	
	public static void main(String[] args)
	{
		new TestGetAdjacencyMatrix();
	}

}
