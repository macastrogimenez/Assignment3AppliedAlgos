package ch;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class TestContractionHierarchies {
    private Graph g;
    private Graph f;

    
    @Before
    public void setUp() {
        g = new Graph();
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 1));
        g.addVertex(3, new Graph.Vertex(2, 2));
        g.addVertex(4, new Graph.Vertex(3, 3));

        // Graph structure:
        // 1 --(4)-- 2 --(1)-- 3
        //  \                /
        //   (8)          (2)
        //     \          /
        //          4

        g.addUndirectedEdge(1, 2, 4);
        g.addUndirectedEdge(2, 3, 1);
        g.addUndirectedEdge(1, 4, 8);
        g.addUndirectedEdge(3, 4, 2);


        // preprocessing graph to file testgraph
        
        

        f = new Graph();
        f.addVertex(0, new Graph.Vertex(1, 2));
        f.addVertex(1, new Graph.Vertex(2, 3));
        f.addVertex(2, new Graph.Vertex(3, 3));
        f.addVertex(3, new Graph.Vertex(4, 2));
        f.addVertex(4, new Graph.Vertex(3, 1));
        f.addVertex(5, new Graph.Vertex(2, 1));
        f.addVertex(6, new Graph.Vertex(1, 1));

        f.addUndirectedEdge(0, 1, 3);
        f.addUndirectedEdge(1, 2, 4);
        f.addUndirectedEdge(2, 3, 4);
        f.addUndirectedEdge(3, 4, 2);
        f.addUndirectedEdge(4, 5, 3);
        f.addUndirectedEdge(5, 0, 1);
        f.addUndirectedEdge(4, 6, 1);
        f.addUndirectedEdge(6, 2, 1);
        f.addUndirectedEdge(5, 6, 2);
        f.addUndirectedEdge(1, 6, 1);

        // test input -> result should be 6
// 7 10
// 0 1.0 2.0
// 1 2.0 3.0
// 2 3.0 3.0
// 3 4.0 2.0
// 4 3.0 1.0
// 5 2.0 1.0
// 6 2.0 1.0
// 0 1 3
// 1 2 4
// 2 3 4
// 3 4 2
// 4 5 3 
// 5 0 1
// 4 6 1 
// 6 2 1
// 5 6 2
// 1 6 1

//     1 ────────4──────── 2
//    /│\                  │
// 3 / │ \ 1               │
//  /  │  \                │1
// 0   │   6               │
//  \  │  /│\              │
// 1 \ │ / │ \2            │4
//    \|/  │  \            │
//     5   │   \           │
//      \  │1   \          │
//     3 \ │     \         │
//        \│      \        │
//         4 ──────2───────3


    }

    @Test
    public void testContractionHierarchy_basic() {
        Result<Integer> resultDijkstra = Dijkstra.shortestPath(g, 1L, 4L) ;
        try {
            ContractionHierachy.storeGraph(g, "testgraphG");
            // Read the preprocessed graph from file
            File file = new File("testgraphG");
            Scanner sc = new Scanner(file);
            // Create CH instance with the preprocessed graph
            Graph chGraph = readCHGraph(sc);
            ContractionHierachy ch = new ContractionHierachy(chGraph);
            sc.close();
            Result<Integer> result = ch.query(1L, 4L);
            assertEquals("Distance 1→2->3->4 should have total cost 7", resultDijkstra.result, result.result);
        } catch (Exception e) {
            fail("Failed to read preprocessed graph: " + e.getMessage());
        }
    }

    @Test
    public void testDijkstraVsContractionHierarchies_basic_larger() {
        Result<Integer> resultDijkstra = Dijkstra.shortestPath(f, 0L, 3L) ;
        
        try {
            // Read the preprocessed graph from file
            ContractionHierachy.storeGraph(f, "testgraphF");
            File file = new File("testgraphF");
            Scanner sc = new Scanner(file);
            // Create CH instance with the preprocessed graph
            Graph chGraph = readCHGraph(sc);
            ContractionHierachy ch = new ContractionHierachy(chGraph);
            sc.close();
            Result<Integer> result = ch.query(0L, 3L);
            assertEquals("Distance 0→5→6→4→3 should have total cost 6", resultDijkstra.result, result.result, 0.001);
        } catch (Exception e) {
            fail("Failed to read preprocessed graph: " + e.getMessage());
        }
    }

    @Test
    public void testBidirectionalDijkstra_directConnection() {
        try {
            // Read the preprocessed graph from file
            ContractionHierachy.storeGraph(f, "testgraphF");
            File file = new File("testgraphF");
            Scanner sc = new Scanner(file);
            // Create CH instance with the preprocessed graph
            Graph chGraph = readCHGraph(sc);
            ContractionHierachy ch = new ContractionHierachy(chGraph);
            sc.close();
            Result<Integer> result = ch.query(0L, 1L);
            assertEquals("Distance 0→1 should have total cost 3", 3, result.result, 0.001);
        } catch (Exception e) {
            fail("Failed to read preprocessed graph: " + e.getMessage());
        }
    }

    // @Test
    // public void testBidirectionalDijkstra_noPathExists() {
    //     Graph disconnected = new Graph();
    //     disconnected.addVertex(1, new Graph.Vertex(0, 0));
    //     disconnected.addVertex(2, new Graph.Vertex(1, 1));
    //     disconnected.addVertex(3, new Graph.Vertex(2, 2));
    //     disconnected.addUndirectedEdge(1, 2, 4);
    //     try {
    //         ContractionHierachy.storeGraph(disconnected, "disconnectedTest");
    //         File file = new File("testgraphF");
    //         Scanner sc = new Scanner(file);
    //         Graph chGraph = readCHGraph(sc);
    //         ContractionHierachy ch = new ContractionHierachy(chGraph);
    //         sc.close();
    //         Result<Integer> result = ch.query(1L, 3L);
    //         assertEquals("Unreachable vertex should return -1",-1.0,result.result, 0.001);    
    //     } catch (Exception e) {
    //         fail("Failed to read preprocessed graph: " + e.getMessage());
    //     }
    // }
    
    @Test
    public void testBidirectionalDijkstra_sameNode() {
        // BidirectionalDijkstra bd = new BidirectionalDijkstra();
        // Result<Double> result = bd.distance(g, 1, 1);
        // assertEquals("Distance from a node to itself should be 0", 0.0, result.result, 0.001);
        try {
            // Read the preprocessed graph from file
            ContractionHierachy.storeGraph(f, "testgraphF");
            File file = new File("testgraphF");
            Scanner sc = new Scanner(file);
            // Create CH instance with the preprocessed graph
            Graph chGraph = readCHGraph(sc);
            ContractionHierachy ch = new ContractionHierachy(chGraph);
            sc.close();
            Result<Integer> result = ch.query(0L, 0L);
            assertEquals("Distance from node 0 to itself should be 0", 0.0, result.result, 0.001);
        } catch (Exception e) {
            fail("Failed to read preprocessed graph: " + e.getMessage());
        }
    }
    
    private static Graph readCHGraph(Scanner sc) {
        int n = sc.nextInt();
        int m = sc.nextInt();

        Graph g = new Graph();

        // Read vertices with ranks
        for (int i = 0; i < n; i++) {
            long id = sc.nextLong();
            float x = Float.parseFloat(sc.next());
            float y = Float.parseFloat(sc.next());
            int rank = sc.nextInt();  // Read rank
            
            g.addVertex(id, new Graph.Vertex(x, y));
            g.getRanks().put(id, rank);  // Store rank
        }

        // Read edges with contracted vertex info
        for (int i = 0; i < m; i++) {
            long from = sc.nextLong();
            long to = sc.nextLong();
            int weight = sc.nextInt();
            long contracted = sc.nextLong();  // Read contracted vertex
            
            g.addEdge(from, to, contracted, weight);
        }
        return g;
    }
}

                