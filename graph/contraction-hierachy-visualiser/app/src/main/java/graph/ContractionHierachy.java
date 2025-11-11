package graph;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

import graph.Graph.Edge;
import graph.Graph.Vertex;

public class ContractionHierachy {
    Graph g;
    Map<Long, Integer> ranks;

    public static Pair<long[], ContractionHierachy> read(Scanner sc) {
        Graph g = new Graph();
        Map<Long, Integer> ranks = new HashMap<>();

        int n = sc.nextInt();
        int m = sc.nextInt();

        long id;
        float x, y;
        long[] ids = new long[n];
        int rank;

        for (int i = 0; i < n; i++) {
            id = sc.nextLong();

            ids[i] = id;
            // System.out.println(sc.next());
            x = Float.parseFloat(sc.next());
            y = Float.parseFloat(sc.next());

            rank = sc.nextInt();

            g.addVertex(id, new Graph.Vertex(x, y));
            ranks.put(id, rank);
        }

        long from, to, contracted;
        int weight;

        for (int i = 0; i < m; i++) {
            from = sc.nextLong();
            to = sc.nextLong();
            weight = sc.nextInt();
            contracted = sc.nextLong();

            g.addUndirectedEdge(from, to, contracted, weight);
        }

        return new Pair<>(ids, new ContractionHierachy(g, ranks));
    }

    public ContractionHierachy(Graph g, Map<Long, Integer> ranks) {
        this.g = g;
        this.ranks = ranks;
    }

    public Result<Integer> query(long s, long t) {
        long start = System.nanoTime();
        int relaxed = 0;
        Map<Long, Integer> distUp = new HashMap<>();
        Map<Long, Integer> distDown = new HashMap<>();

        PriorityQueue<PQElem> pqUp = new PriorityQueue<>();
        PriorityQueue<PQElem> pqDown = new PriorityQueue<>();

        pqUp.add(new PQElem(0, s));
        distUp.put(s, 0);
        pqDown.add(new PQElem(0, t));
        distDown.put(t, 0);
        int d = Integer.MAX_VALUE;
        Map<Boolean, PriorityQueue<PQElem>> pqs = new HashMap<>();
        Map<Boolean, Map<Long, Integer>> dists = new HashMap<>();
        pqs.put(false, pqUp);
        pqs.put(true, pqDown);
        dists.put(false, distUp);
        dists.put(true, distDown);
        boolean down = true;

        PriorityQueue<PQElem> pq;
        Map<Long, Integer> dist;
        pq = pqs.get(down);
        while ((!pqUp.isEmpty() || !pqDown.isEmpty())
                && d > Math.min(pqUp.isEmpty() ? 0 : pqUp.peek().key, pqDown.isEmpty() ? 0 : pqDown.peek().key)) {
            if (!pqs.get(!down).isEmpty()) {
                down = !down;
            }
            pq = pqs.get(down);
            dist = dists.get(down);

            PQElem elem = pq.poll();
            long u = elem.v;

            if (dist.get(u) < elem.key)
                continue;

            if (distUp.containsKey(u) && distDown.containsKey(u))
                d = Math.min(d, distUp.get(u) + distDown.get(u));

            for (Edge e : g.getNeighbours(u)) {
                if (ranks.get(e.to) <= ranks.get(u))
                    continue;
                if (!dist.containsKey(e.to))
                    relaxed++;
                if ((!dist.containsKey(e.to) || dist.get(u) + e.weight < dist.get(e.to))) {
                    dist.put(e.to, dist.get(u) + e.weight);
                    pq.add(new PQElem(dist.get(e.to), e.to));
                }
            }
        }
        long end = System.nanoTime();
        return new Result<Integer>(end - start, relaxed, d);
    }

    public Graph getGraph() {
        return this.g;
    }

    public void print() {
        System.out.println(g.n + " " + (g.m / 2));
        for (long v : g.getVertices()) {
            Vertex vert = g.getVertex(v);
            System.out.println(v + " " + vert.x + " " + vert.y + " " + ranks.get(v));
        }

        for (long v : g.getVertices()) {
            for (Edge e : g.getNeighbours(v)) {
                if (ranks.get(v) < ranks.get(e.to)) // this prevents double printing of edges
                    System.out.println(v + " " + e.to + " " + e.weight + " " + e.contracted);
            }
        }
    }
}
