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
        //List<Map<Long, Double>> maps = new ArrayList<>(2);
        HashMap<Long, Integer> dLeft = new HashMap<>();
        HashMap<Long, Integer> dRight = new HashMap<>();
        dLeft.put(from, 0);
        dRight.put(to, 0);
        //maps.add(dLeft);
        //maps.add(dRight);

        HashSet<Long> settled = new HashSet<>();
        double distance = Integer.MAX_VALUE;
        PriorityQueue<PQElem> pqLeft = new PriorityQueue<>();
        PriorityQueue<PQElem> pqRight = new PriorityQueue<>();
        pqLeft.add(new PQElem(0, from));
        // start the right search from the target
        pqRight.add(new PQElem(0, to));
        //List<PriorityQueue<PQElem>> queues = new ArrayList<>(2);
        //queues.add(pqLeft);
        //queues.add(pqRight);
        
        
        while (!pqLeft.isEmpty() || !pqRight.isEmpty()) {
            int side = 0; //0 is left 1 is right 
            long u = 0;
            // choose the queue with the smaller head; handle empty queues safely
            PQElem leftTop = pqLeft.peek();
            PQElem rightTop = pqRight.peek();
            if (leftTop != null && (rightTop == null || leftTop.compareTo(rightTop) <= 0)) {
                u = pqLeft.poll().v;
                side = 0;
            } else {
                side = 1;
                u = pqRight.poll().v;
            }
            if (settled.contains(u)) {
                // u was settled by another instance
                break;
            }
            settled.add(u); 
            try {
                for (Graph.Edge e : g.getNeighbours(u)) {
                    relaxed++; 
                    int w = e.weight;
                    Long v = e.to;
                    if (side == 0) { // left
                        int du = dLeft.getOrDefault(u, Integer.MAX_VALUE);
                        int dv = dLeft.getOrDefault(v, Integer.MAX_VALUE);
                        if ((long) du + w < dv) {
                            int newDist = du + w;
                            dLeft.put(v, newDist);
                            pqLeft.add(new PQElem(newDist, v));
                        }
                    } else {
                        int du = dRight.getOrDefault(u, Integer.MAX_VALUE);
                        int dv = dRight.getOrDefault(v, Integer.MAX_VALUE);
                        if ((long) du + w < dv) {
                            int newDist = du + w;
                            dRight.put(v, newDist);
                            pqRight.add(new PQElem(newDist, v));
                        }
                    }

                    int dl = dLeft.getOrDefault(v, Integer.MAX_VALUE);
                    int dr = dRight.getOrDefault(v, Integer.MAX_VALUE);
                    if (dl < Integer.MAX_VALUE && dr < Integer.MAX_VALUE) {
                        distance = Math.min(distance, (double) dl + (double) dr);
                    }
                }
            } catch (NullPointerException e) {
                    long end = System.nanoTime(); 
                    return new Result<>(end - start, relaxed, -1.0);
                    // get the neighbours of an edge and receives null
                    // then BD returns -1.0
            }
            
        }
        long end = System.nanoTime(); 
        return new Result<Double>(end - start, relaxed, distance);
    }   
}