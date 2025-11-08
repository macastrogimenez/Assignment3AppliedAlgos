package ch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TestBidirectionalDijkstra {
    private Graph g;
    private Graph f;
    private Graph zeroes;

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

        zeroes = new Graph();
        zeroes.addUndirectedEdge(0, 1, 2);
        zeroes.addUndirectedEdge(1, 2, 0);
        zeroes.addUndirectedEdge(2, 3, 1);
        zeroes.addUndirectedEdge(3, 4, 2);
        zeroes.addUndirectedEdge(4, 5, 0);
        zeroes.addUndirectedEdge(5, 0, 2);
        zeroes.addUndirectedEdge(4, 6, 0);
        zeroes.addUndirectedEdge(6, 2, 0);
        zeroes.addUndirectedEdge(5, 6, 0);
        zeroes.addUndirectedEdge(1, 6, 0);

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
    public void testBidirectionalDijkstra_basic() {
        BidirectionalDijkstra2 bd = new BidirectionalDijkstra2();
        Result<Double> result = bd.distance(g, 1, 4) ;
        assertEquals("Distance 1→2→3→4 should have total cost 7", 7.0, result.result, 0.001);
    }

    @Test
    public void testBidirectionalDijkstra_basic_larger() {
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Result<Double> result = bd.distance(f, 0, 3) ;
        assertEquals("Distance 0→1→2→3 should have total cost 6", 6.0, result.result, 0.001);
    }

    @Test
    public void testBidirectionalDijkstra_basic_zeroes() {
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Result<Double> result = bd.distance(f, 0, 3) ;
        assertEquals("Distance 0→1→2→3 should have total cost 3", 3.0, result.result, 0.001);
    }

    @Test
    public void testBidirectionalDijkstra_directConnection() {
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Result<Double> result = bd.distance(g, 1, 2);
        assertEquals("Direct edge 1→2 should cost 4", 4.0, result.result, 0.0001);
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
        Result<Double> result = bd.distance(disconnected, 1, 3);
        assertEquals("Unreachable vertex should return -1",-1.0,result.result, 0.001);
    }
    
    @Test
    public void testBidirectionalDijkstra_sameNode() {
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        Result<Double> result = bd.distance(g, 1, 1);
        assertEquals("Distance from a node to itself should be 0", 0.0, result.result, 0.001);
}
}

                