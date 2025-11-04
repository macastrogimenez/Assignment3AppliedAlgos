package ch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TestBidirectionalDijkstra {
    private Graph g;
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
    }

    @Test
    public void testBidirectionalDijkstra_basic() {
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Double result = bd.distance(g, 1, 4) ;
        assertEquals("Distance 1→2→3→4 should have total cost 7", 7.0, result, 0.001);
    }

    @Test
    public void testBidirectionalDijkstra_directConnection() {
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Double result = bd.distance(g, 1, 2);
        assertEquals("Direct edge 1→2 should cost 4", 4.0, result, 0.0001);
    }

    @Test
    public void testBidirectionalDijkstra_noPathExists() {
        Graph disconnected = new Graph();
        disconnected.addVertex(1, new Graph.Vertex(0, 0));
        disconnected.addVertex(2, new Graph.Vertex(1, 1));
        disconnected.addVertex(3, new Graph.Vertex(2, 2));
        disconnected.addUndirectedEdge(1, 2, 4);
        // no edges between 1 and 3
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Double result = bd.distance(disconnected, 1, 3);
        assertEquals("Unreachable vertex should return -1",-1.0,result, 0.001);
    }
    
    @Test
    public void testBidirectionalDijkstra_sameNode() {
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Double result = bd.distance(g, 1, 1);
        assertEquals("Distance from a node to itself should be 0", 0.0, result, 0.001);
}
}

