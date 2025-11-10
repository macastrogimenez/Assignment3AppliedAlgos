package ch;


import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class BidirectionalDijkstra {
    
    public Result<Double> distance(Graph g, long from, long to) {
        long start = System.nanoTime();
        int relaxed = 0;

        if (from == to) {
            long end = System.nanoTime();
            return new Result<>(end - start, relaxed, 0.0);
        }

        HashMap<Long, Integer> dLeft = new HashMap<>();
        HashMap<Long, Integer> dRight = new HashMap<>();
        dLeft.put(from, 0);
        dRight.put(to, 0);

        HashSet<Long> settledLeft = new HashSet<>();
        HashSet<Long> settledRight = new HashSet<>();

        PriorityQueue<PQElem> pqLeft = new PriorityQueue<>();
        PriorityQueue<PQElem> pqRight = new PriorityQueue<>();
        pqLeft.add(new PQElem(0, from));
        pqRight.add(new PQElem(0, to));

        double distance = Double.POSITIVE_INFINITY;
        Long meetingNode = null;

        while (!pqLeft.isEmpty() || !pqRight.isEmpty()) {
            int side = 0; // 0 = left, 1 = right
            PQElem elem;

            PQElem leftTop = pqLeft.peek();
            PQElem rightTop = pqRight.peek();

            // pick queue with smaller current distance
            if (leftTop != null && (rightTop == null || leftTop.compareTo(rightTop) <= 0)) {
                elem = pqLeft.poll();
                side = 0;
            } else {
                elem = pqRight.poll();
                side = 1;
            }

            long u = elem.v;
            int dist = elem.key;

            // skip already settled
            if (side == 0 && settledLeft.contains(u)) continue;
            if (side == 1 && settledRight.contains(u)) continue;

            // mark as settled
            if (side == 0) {
                settledLeft.add(u);
                if (settledRight.contains(u)) {
                    meetingNode = u;
                    distance = dLeft.get(u) + dRight.get(u);
                    break; // both sides have finalized u
                }
            } else {
                settledRight.add(u);
                if (settledLeft.contains(u)) {
                    meetingNode = u;
                    distance = dLeft.get(u) + dRight.get(u);
                    break;
                }
            }

            // relax neighbors
            try {
                for (Graph.Edge e : g.getNeighbours(u)) {
                    int w = e.weight;
                    long v = e.to;

                    if (side == 0) { // left
                        int dv = dLeft.getOrDefault(v, Integer.MAX_VALUE);
                        if ((long) dist + w < dv) {
                            relaxed++;
                            int newDist = dist + w;
                            dLeft.put(v, newDist);
                            pqLeft.add(new PQElem(newDist, v));
                        }
                    } else { // right
                        int dv = dRight.getOrDefault(v, Integer.MAX_VALUE);
                        if ((long) dist + w < dv) {
                            relaxed++;
                            int newDist = dist + w;
                            dRight.put(v, newDist);
                            pqRight.add(new PQElem(newDist, v));
                        }
                    }
                }
            } catch (NullPointerException e) {
                long end = System.nanoTime();
                return new Result<>(end - start, relaxed, -1.0);
            }
        }

        long end = System.nanoTime();
        if (meetingNode == null) {
            return new Result<>(end - start, relaxed, -1.0); // disconnected
        }
        return new Result<>(end - start, relaxed, distance);
    }   
}