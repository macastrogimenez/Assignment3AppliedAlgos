package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
// INPUT
// first line: n = number of vertices , m = number of edges
// n lines (vertices - nodes): int = number of the vertex, float = longitude, float = latitude 
// m lines (edges): int = start (vertex), int = end (vertex), int = cost

public class BidirectionalDijkstra {

    Set<Integer> settled = new HashSet<>();
    Map<Integer, Double> dijkstraLeft = new HashMap<>();
    Map<Integer, Double> dijkstraRight = new HashMap<>();
    int d = Integer.MAX_VALUE;

    //TODO: check that this is correctly initiated to the right values of s and t
    public BidirectionalDijkstra(int s, int t, Graph g){ 
        dijkstraLeft.put(s, 0.0);
        dijkstraRight.put(t, 0.0);
        
        // initiate priority queues to keep next nodes to be visited - ordered from minor distance to higher (minor being the head)
        PriorityQueue<Vertex> queueLeft = new PriorityQueue<>();
        PriorityQueue<Vertex> queueRight = new PriorityQueue<>();

        // initiate the queues with the start and target nodes (which have 0 distance to themselves)
        queueLeft.add(new Vertex(s, 0.0));
        queueRight.add(new Vertex(t, 0.0));

        
        while (!queueLeft.isEmpty() || !queueRight.isEmpty()){ // as long as neither queue is empty keep doing the following
            Vertex minQLeft = queueLeft.peek(); // retrieves and deletes minimum leftQ
            Vertex minQRight = queueRight.peek(); // retrieves and deletes minimum rightQ

            PriorityQueue<Vertex> currentQueue; // to keep a copy of the next vertex to visit;

            if (!queueLeft.isEmpty() && (minQLeft.compareTo(minQRight)<0)){ //if leftQ not empty and min of leftQ < min of rightQ 
                currentQueue = queueLeft; // choose the leftQ
            }
            else {
                currentQueue = queueRight; // else choose the rightQ
            }
            
            // retrieve the id of the vertex with lowest distance
            var elem = currentQueue.poll();
            int u = elem.getVertexId();

            if (settled.contains(u)){ // if vertex has already been visited break
                break;
            }

            settled.add(u); // else add the vertex to settled

            // TODO: from point 16 of pseudocode onwards
            





        }

        



    }
    public static void main(String[] args) {
        
    }
}