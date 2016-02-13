package graph;

import org.apache.commons.lang3.time.StopWatch;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author C. Savkli, Dec 20, 2013
 * @version 1.0
 */

public class TestReadAndIterate
{
	private static String graphName = "tinker_graph";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY+"\\"+graphName+".graphml";
	
	public TestReadAndIterate()
	{
		graph = GraphUtils.readGraph(GRAPH_FILE);
		
        iterateVertices();
        iterateEdges(); 
	}
	
	public void iterateVertices()
	{
		StopWatch timer = new StopWatch();
		timer.start();
		for(Vertex v: graph.getVertices())
		{
			String type = v.getProperty("type");
			
			if(type.equals("person"))
			{
			    System.out.println(v.getId()+" person : "+v.getProperty("firstname") +" "+ v.getProperty("lastname"));
			}
			else
			{
			    System.out.println(v.getId()+" address : "+v.getProperty("address"));
			}
		}
		timer.stop();
		System.out.println("iterate vertices time: "+(timer.getTime() / 1000.0) + "s\n");	
	}
	
	public void iterateEdges()
	{
		StopWatch timer = new StopWatch();
		timer.start();
		for(Edge e: graph.getEdges())
		{
			Vertex fromVertex = e.getVertex(Direction.OUT);
			Vertex toVertex = e.getVertex(Direction.IN);
			String person = fromVertex.getProperty("firstname");
			String location =  toVertex.getProperty("address");
			String edgeType =  e.getProperty("type");
			System.out.println(person + " " + edgeType + " " + location +" from " + e.getProperty("from")+ " to " + e.getProperty("to"));
		}
		timer.stop();
		System.out.println("iterate edges time: "+(timer.getTime() / 1000.0) + "s\n");	
	}
	
	public static void main(String[] args)
	{
		new TestReadAndIterate();
	}
}
