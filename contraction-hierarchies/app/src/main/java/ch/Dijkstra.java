package ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {
    /**
     * Computes the shortest path distance between two vertices using Dijkstra's algorithm.
     * Returns -1 if there is no path.
     * @param g    The graph to search.
     * @param from The starting vertex ID.
     * @param to   The target vertex ID.
     * @param skipVertex  A vertex to ignore during search (-1 if none).
     * @param distanceLimit  A limit beyond which the search is stopped (use Integer.MAX_VALUE if none).

     * @return A triple containing the duration (in ns), the number of relaxed edges, and the shortest path distance. The distance is -1 if no path exists.
     */
    public static Result<Integer> shortestPath(Graph g, long from, long to, long forbidden, int limit) {
    long start = System.nanoTime();

    PriorityQueue<PQElem> pq = new PriorityQueue<>();
    Map<Long, Integer> dist = new HashMap<>();
    Set<Long> visited = new HashSet<>();

    pq.add(new PQElem(0, from));
    dist.put(from, 0);

    int relaxed = 0;

    while (!pq.isEmpty()) {
        PQElem current = pq.poll();
        long u = current.v;
        int d = current.key;

        if (d > limit) break;                  // stop early
        if (u == to) break;                    // stop when reached
        if (visited.contains(u)) continue;

        visited.add(u);

        List<Graph.Edge> neighbors = g.getNeighbours(u);
        if (neighbors == null) continue;

        for (Graph.Edge e : neighbors) {
            if (e.to == forbidden) continue;   // skip forbidden vertex

            int newDist = d + e.weight;
            if (newDist > limit) continue;     // prune long paths

            Integer oldDist = dist.get(e.to);
            if (oldDist == null || newDist < oldDist) {
                dist.put(e.to, newDist);
                pq.add(new PQElem(newDist, e.to));
                relaxed++;
            }
        }
    }

    long end = System.nanoTime();
    int result = dist.getOrDefault(to, -1);
    return new Result<>(end - start, relaxed, result);
}


    /**
     * Overload: shortest path without skipVertex and distanceLimit (uses defaults).
     */
    public static Result<Integer> shortestPath(Graph g, long from, long to) {
        return shortestPath(g, from, to, -1L, Integer.MAX_VALUE);
    }

    /**
     * Overload: shortest path with skipVertex but default distanceLimit.
     */
    public static Result<Integer> shortestPath(Graph g, long from, long to, long skipVertex) {
        return shortestPath(g, from, to, skipVertex, Integer.MAX_VALUE);
    }

}