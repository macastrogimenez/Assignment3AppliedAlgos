package ch;


import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class BidirectionalDijkstra {
    
    public Result<Double> distance(Graph g, long from, long to) {
        long start = System.nanoTime(); 
        int relaxed = 0; 

        if (from == to) { // if we are checking the distance from a vertex to itself it should be 0
            long end = System.nanoTime();
            return new Result<>(end - start, relaxed, 0.0);
        }
        
        HashMap<Long, Integer> dLeft = new HashMap<>();
        HashMap<Long, Integer> dRight = new HashMap<>();
        dLeft.put(from, 0);
        dRight.put(to, 0);
        Long meetingNode = null;

        HashSet<Long> settled = new HashSet<>();
        double distance = Integer.MAX_VALUE;
        PriorityQueue<PQElem> pqLeft = new PriorityQueue<>();
        PriorityQueue<PQElem> pqRight = new PriorityQueue<>();
        // start left and right search from 'from' and 'to'
        pqLeft.add(new PQElem(0, from));
        pqRight.add(new PQElem(0, to));
        
        while (!pqLeft.isEmpty() || !pqRight.isEmpty()) {
            int side = 0; //0 is left 1 is right 
            long u = 0;
            // choose the queue with the smaller head to handle empty queues safely
            PQElem leftTop = pqLeft.peek();
            PQElem rightTop = pqRight.peek();
            PQElem elem = new PQElem(0,0L);
            if (leftTop != null && (rightTop == null || leftTop.compareTo(rightTop) <= 0)) {
                elem = pqLeft.poll();
                u = elem.v;
            } else {
                side = 1;
                elem = pqRight.poll();
                u = elem.v;
            }

            //we skip the vertices that have already been settled
            if (settled.contains(u)) {
                continue;
            }
            
            settled.add(u);             
            
            int dist = elem.key; //distance to u 

            try { 
                for (Graph.Edge e : g.getNeighbours(u)) {
                    int w = e.weight;
                    Long v = e.to;
                    if (side == 0) { // left
                        int dv = dLeft.getOrDefault(v, Integer.MAX_VALUE);
                        if ((long) dist + w < dv) {
                            relaxed++;  // Only count actual relaxations
                            int newDist = dist + w;
                            dLeft.put(v, newDist);
                            pqLeft.add(new PQElem(newDist, v));
                            //System.out.println("LEFT: updated "+ v+ " to distance "+ newDist);
                        }
                    } else { // right
                        int dv = dRight.getOrDefault(v, Integer.MAX_VALUE);
                        if ((long) dist + w < dv) {
                            relaxed++;  // Only count actual relaxations
                            int newDist = dist + w;
                            dRight.put(v, newDist);
                            pqRight.add(new PQElem(newDist, v));
                            // System.out.println("RIGHT: updated "+ v+ " to distance "+ newDist);
                        }
                    }

                    int dl = dLeft.getOrDefault(v, Integer.MAX_VALUE);
                    int dr = dRight.getOrDefault(v, Integer.MAX_VALUE);
                    //break when vertice occured in both maps
                    if (dl < Integer.MAX_VALUE && dr < Integer.MAX_VALUE) {
                        distance = Math.min(distance, (double) dl + (double) dr);
                        meetingNode = v;
                        break;
                    }
                }
            } catch (NullPointerException e) {
                    long end = System.nanoTime(); 
                    return new Result<>(end - start, relaxed, -1.0);
                    // get the neighbours of an edge and receives null
                    // then BD returns -1.0
            }

            if (meetingNode != null) {
                break; // now we have a connection between sides
            }
        }
        
        long end = System.nanoTime(); 
        return new Result<Double>(end - start, relaxed, distance);
    }   
}