package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.checkerframework.checker.units.qual.s;

// INPUT
// first line: n = number of vertices , m = number of edges
// n lines (nodes): int = number of the vertex, float = longitude, float = latitude 
// m lines (edges): int = start (vertex), int = end (vertex), int = cost

public class BidirectionalDijkstra {

    Set<Integer> settled = new HashSet<>();
    Map<Integer, Float> dijkstraLeft = new HashMap<>();
    Map<Integer, Float> dijkstraRight = new HashMap<>();
    int d = Integer.MAX_VALUE;

    //TODO: check that this is correctly initiated to the right values of s and t
    public BidirectionalDijkstra(){ 
        dijkstraLeft.put(0, 0.0f);
        dijkstraRight.put(0, 0.0f);
        
        PriorityQueue<PQElem> queueLeft = new PriorityQueue<>();
        PriorityQueue<PQElem> queueRight = new PriorityQueue<>();



    }



}