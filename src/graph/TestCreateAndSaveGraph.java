package graph;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.tg.*;

/**
 * @author C. Savkli, Dec 20, 2013
 * @version 1.0
 */

public class TestCreateAndSaveGraph
{
	private static String graphName = "tinker_graph";
	private MGraph graph;
	public static final String BASE_DIRECTORY = "GraphDatabases";
	private static final String GRAPH_FILE = BASE_DIRECTORY+"\\"+graphName+".graphml";
	
	public TestCreateAndSaveGraph()
	{
		// Create graph
		graph = new MGraph();
        ingestPairs(10);	
		//ingestPairsWithCalculatedIDs(10);
        
        // Create index:
        ((TinkerGraph) graph).createKeyIndex("firstname", Vertex.class);
        ((TinkerGraph) graph).createKeyIndex("lastname", Vertex.class);
        System.out.println(((TinkerGraph) graph).getIndexedKeys(Vertex.class)+"\n");

        // Save graph
		GraphUtils.saveGraph(graph, GRAPH_FILE);
	}
	
	public void ingestPairs(int vert)
	{		
		StopWatch timer = new StopWatch();
		timer.start();
		for(int i=0; i< vert; i++)
		{
			Vertex person = graph.addVertex("p"+i);
			person.setProperty("type", "person");
			person.setProperty("age", i);
			person.setProperty("firstname", "John"+i);
			person.setProperty("lastname", "Doe"+i);
			
			Vertex location = graph.addVertex("l"+i);
			location.setProperty("type", "location");
			location.setProperty("address", "address" + i);
			
			Edge edge = graph.addEdge(i, person, location, "e");
			edge.setProperty("type", "lived at");
			edge.setProperty("from", "199"+i);
			edge.setProperty("to", "201"+i);
		}
		System.out.println("Created graph\n");
	}
	
	//
	// Example of how to create vertices using calculated IDs
	//
	
	public void ingestPairsWithCalculatedIDs(int vert)
	{		
		int idLength = 8;
		
		for(int i=0; i< vert; i++)
		{
			Map<String,String> personProperties = new HashMap<String,String>();
			personProperties.put("age", i+"");
			personProperties.put("firstname", "John"+i);
			personProperties.put("lastname", "Doe"+i);
			
			// Calculate id of given size from attributes:
            String id = IDUtils.md5(personProperties, idLength);
            
            Vertex person = graph.addVertex(id);
           
			person.setProperty("age", i);
			person.setProperty("firstname", "John"+i);
			person.setProperty("lastname", "Doe"+i);

			Map<String,String> locationProperties = new HashMap<String,String>();
			locationProperties.put("address", i+"");
			
			// Calculate id of given size from attributes:
            String id2 = IDUtils.md5(locationProperties, idLength);
            
            Vertex location = graph.addVertex(id2);
            
			location.setProperty("address", i);
			Edge edge = graph.addEdge(i, person, location, "edge");
			edge.setProperty("fr", i);
		}
	}
	
	public static void main(String[] args)
	{
		new TestCreateAndSaveGraph();
	}

}
