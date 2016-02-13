package graph;

import org.apache.commons.lang3.time.StopWatch;

import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Query.Compare;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author C. Savkli, Dec 20, 2013
 * @version 1.0
 */

public class TestQueryGraph
{
	private static String graphName = "tinker_graph";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY+"/"+graphName+".graphml";
	
	public TestQueryGraph()
	{
		graph = GraphUtils.readGraph(GRAPH_FILE);
		
        //System.out.println("Exact query: ");
        iterateVerticesWithQuery();
        
        //System.out.println("Range query: ");
        iterateVerticesWithRangeQuery();
	}
	
	public void iterateVerticesWithQuery()
	{
		StopWatch timer = new StopWatch();
		timer.start();
		GraphQuery query = graph.query().has("firstname", "John1").has("lastname", "Doe1");

		for(Vertex v: query.vertices())
		{
		    System.out.println(v.getId()+" person : "+v.getProperty("firstname") +" "+ v.getProperty("lastname"));
		}
		System.out.println("range query time: "+(timer.getTime() / 1000.0) + "s\n");	
	}
	
	//
	// Note : Interval queries are exclusive of boundaries
	//
	public void iterateVerticesWithRangeQuery()
	{
		StopWatch timer = new StopWatch();
		timer.start();
		GraphQuery query = graph.query().interval("firstname", "John1", "John5").has("lastname", "Doe2", Compare.GREATER_THAN);

		for(Vertex v: query.vertices())
		{
		    System.out.println(v.getId()+" person : "+v.getProperty("firstname") +" "+ v.getProperty("lastname"));
		}
		System.out.println("range query time: "+(timer.getTime() / 1000.0) + "s\n");	
	}
		
	public static void main(String[] args)
	{
		new TestQueryGraph();
	}
}
