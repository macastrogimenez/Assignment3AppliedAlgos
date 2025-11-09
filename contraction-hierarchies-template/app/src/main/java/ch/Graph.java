package ch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class Graph {
    int n, m;

    public static class Vertex {
        float x, y;

        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public class Edge {
        long to;
        int weight;
        long contracted; // only used by contraction hierachy, marks the vertex from which this edge resulted.

        public Edge(long to, int weight, long contracted) {
            this.to = to;
            this.weight = weight;
            this.contracted = contracted;
        }
    }

    // Helper class to store complete shortcut information
    public static class Shortcut {
        long from;
        long to;
        long contracted;
        int weight;

        public Shortcut(long from, long to, long contracted, int weight) {
            this.from = from;
            this.to = to;
            this.contracted = contracted;
            this.weight = weight;
        }
    }

    private Map<Long, List<Edge>> edges;
    private Map<Long, Vertex> vertices;
    private Map<Long,Integer> ranks;

    public Graph() {
        this.n = 0;
        this.m = 0;
        this.edges = new HashMap<>();
        this.vertices = new HashMap<>();
        this.ranks = new HashMap<>();
    }

    public void addVertex(long id, Vertex v) {
        this.vertices.put(id, v);
        this.n++;
    }

    public void addEdge(long from, long to, long contracted, int weight) {
        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new ArrayList<>());
        }
        this.edges.get(from).add(new Edge(to, weight, contracted));
        this.m++;
    }

    public void addUndirectedEdge(long u, long v, long contracted, int weight) {
        addEdge(u, v, contracted, weight);
        addEdge(v, u, contracted, weight);
    }

    public void addUndirectedEdge(long u, long v, int weight) {
        addUndirectedEdge(u, v, -1, weight);
    }

    public List<Edge> getNeighbours(long u) {
        return this.edges.get(u);
    }

    public Vertex getVertex(long id) {
        return this.vertices.get(id);
    }

    public int degree(long v) {
        return this.edges.get(v).size();
    }


    
    public List<Shortcut> contract(long u) {
        List<Edge> neighbors = this.getNeighbours(u);
        List<Shortcut> addedShortcuts = new ArrayList<>();

        // Handle isolated vertex
        if (neighbors == null || neighbors.isEmpty()) {
            this.edges.remove(u);
            this.vertices.remove(u);
            return addedShortcuts;
        }

        // Find max edge weight for early-stop condition
        int maxWeight = 0;
        for (Edge e : neighbors) {
            if (e.weight > maxWeight) {
                maxWeight = e.weight;
            }
        }

    // Try all neighbor pairs (v, w)
        for (int i = 0; i < neighbors.size(); i++) {
            Edge vEdge = neighbors.get(i);
            long v = vEdge.to;

            for (int j = 0; j < neighbors.size(); j++) {
                Edge wEdge = neighbors.get(j);
                long w = wEdge.to;

                // Skip self-pair and symmetric duplicate (for undirected)
                if (v == w || v > w) continue;

                int shortcutWeight = vEdge.weight + wEdge.weight;
                int distanceLimit = vEdge.weight + maxWeight;

                // run Dijkstra, skipping vertex u
                Result<Integer> res = Dijkstra.shortestPath(this, v, w, u, distanceLimit);
                int delta_vw = res.result;

                // Add shortcut if δv(w) > c(v,u) + c(u,w)
                if (delta_vw == -1 || delta_vw > shortcutWeight) {

                    boolean exists = false;
                    List<Edge> vNeighbors = this.getNeighbours(v);

                    //checking if the shortcut already exists
                    if (vNeighbors != null) {
                        for (Edge e : vNeighbors) {
                            if (e.to == w && e.weight <= shortcutWeight) {
                                exists = true;
                                break;
                            }
                        }
                    }

                    if (!exists) {
                        this.addUndirectedEdge(v, w, u, shortcutWeight);
                        addedShortcuts.add(new Shortcut(v, w, u, shortcutWeight));
                    }
                }
            }
        }

        // Remove vertex u and its incident edges
        this.edges.remove(u);
        this.vertices.remove(u);

        for (Map.Entry<Long, List<Edge>> entry : this.edges.entrySet()) {
            List<Edge> edgeList = entry.getValue();
            for (int i = 0; i < edgeList.size(); i++) {
                if (edgeList.get(i).to == u) {
                    edgeList.remove(i);
                    i--; // adjust index after removal
                }
            }
        }

        // Return list of added shortcuts
        return addedShortcuts;
    }


    //It doesnt edit existing graph in any way
    public int getEdgeDifference(long v) {


        int removedEdges = getNeighbours(v).size();

        List<Edge> neighbors = this.getNeighbours(v);
        if (neighbors == null || neighbors.isEmpty()) return 0;

        // Precompute max{ c(u,w) | w ∈ N(u) \ {v} } for pruning
        int maxWeight = 0;
        for (Edge e : neighbors) {
            if (e.weight > maxWeight) maxWeight = e.weight;
        }
        int addedShortcuts = 0;

        for (Edge uEdge : neighbors) {
            long u = uEdge.to;

            for (Edge wEdge : neighbors) {
                long w = wEdge.to;
                if (u == w) continue;

                int uwShortcutWeight = uEdge.weight + wEdge.weight;
                int distanceLimit = uEdge.weight + maxWeight;

                // Run local Dijkstra *excluding vertex u* 
                Result<Integer> result = Dijkstra.shortestPath(this, u, w, v, distanceLimit);

                if (result.result > uwShortcutWeight || result.result == -1){
                // Add shortcut edge (v, w)
                    //this.addUndirectedEdge(u, w, v, uwShortcutWeight);
                    addedShortcuts++;
                }
            }
        }
        
        return -removedEdges + addedShortcuts; // Edge difference
    }

    public PreprocessResult preprocess(int checkInterval){
        List<Shortcut> allShortcuts = new ArrayList<>();
        //Map<Long, Integer> contractionOrder = new HashMap<>();

        Map<Long, Integer> currentKey = new HashMap<>();   // vertex -> key (edge difference)
        PriorityQueue<PQElem> pq = new PriorityQueue<>();
        Set<Long> verticesSet = new HashSet<>(this.vertices.keySet());

        int totalVertices = verticesSet.size();
        int contracted = 0;

        System.out.println("Preprocessing CH: total vertices = " + totalVertices);

        // Compute initial keys for all vertices
        int idxInit = 0;
        for (Long v : verticesSet) {
            idxInit++;
            int key = getEdgeDifference(v);
            currentKey.put(v, key);
            pq.add(new PQElem(key, v));
            if (idxInit % 1000 == 0) {
                System.out.println("  computed initial keys for " + idxInit + " / " + totalVertices + " vertices");
            }
        }

        int lazyUpdates = 0;
        int rank = 0;        // number of successful lazy updates in current interval
        //int attemptsSinceCheck = 0; // attempts since last check
        long contractedCount = 0;
        long startTime = System.nanoTime();

        while (!pq.isEmpty()) {
            PQElem top = pq.poll();
            long v = top.v;

            // If vertex already removed/contracted, skip
            if (!this.vertices.containsKey(v)) {
                continue;
            }
            int recomputedKey = getEdgeDifference(v);
            Integer storedKey = currentKey.get(v);
            if (storedKey == null || recomputedKey != storedKey) {
                // Key changed -> lazy update: reinsert with new key
                currentKey.put(v, recomputedKey);
                pq.add(new PQElem(recomputedKey, v));

                lazyUpdates++;
                //attemptsSinceCheck++;
                

                // Debug
                if (lazyUpdates % 100 == 0) {
                    //System.out.println("  lazy updates so far: " + lazyUpdates + " (last updated vertex " + v + ")");
                }

                // Check whether to trigger full refresh
                //if (attemptsSinceCheck >= checkInterval) {
                    if (lazyUpdates > checkInterval) {
                        // Perform full re-evaluation of all remaining vertices
                        System.out.println("  High lazy update rate detected (" + lazyUpdates + "/" +  "). Doing full priority refresh...");
                        // rebuild PQ and currentKey from scratch for remaining vertices
                        pq.clear();
                        currentKey.clear();
                        int reevalCount = 0;
                        for (Long w : new ArrayList<>(this.vertices.keySet())) {
                            int k = getEdgeDifference(w);
                            currentKey.put(w, k);
                            pq.add(new PQElem(k, w));
                            reevalCount++;
                            if (reevalCount % 1000 == 0) {
                                System.out.println("    re-evaluated keys for " + reevalCount + " vertices");
                            }
                        }
                        System.out.println("  Full refresh done. PQ size = " + pq.size());
                        // reset counters
                        lazyUpdates = 0;
                    }
                    // reset attempts counter regardless
                    //attemptsSinceCheck = 0;
                //}

                // continue to next iteration (don't contract this vertex yet)
                continue;
            }

            ranks.put(v, rank++);
            // System.out.println("Contracting vertex " + v + " (rank " + (rank - 1) + ")");

            // Key matched: we can now contract v
            // Contract and update graph in-place; contractAndUpdate returns list of added shortcuts
            List<Shortcut> newShortcuts = this.contract(v);
            contracted++;
            if (contracted%10000==0){
                System.out.println("Contracted vertices: " + contracted);
            }
            allShortcuts.addAll(newShortcuts); // collect them
            contractedCount++;
            currentKey.remove(v);

            // After contraction, many keys changed, but we rely on lazy updates (they will be recomputed when popped)
            // If desired, we could proactively update neighbors, but lazy approach is fine.
        }
        //ranks = contractionOrder;
        long totalNs = System.nanoTime() - startTime;
        System.out.printf("Preprocessing finished. Contracted %d vertices in %.1f s%n", contractedCount, totalNs / 1e9);
        return new PreprocessResult(allShortcuts, ranks);

    }


    

    public Map<Long, List<Edge>> getEdges() {
        return edges;
    }

    public Map<Long, Vertex> getVertices() {
        return vertices;
    }

    public Map<Long, Integer> getRanks() {
        return ranks;
    }

    public Graph copyGraph() {
        Graph gCopy = new Graph();

        // copy vertices
        for (Map.Entry<Long, Vertex> entry : this.vertices.entrySet()) {
            gCopy.addVertex(entry.getKey(), new Vertex(entry.getValue().x, entry.getValue().y));
        }

        // copy edges
        for (Map.Entry<Long, List<Edge>> entry : this.edges.entrySet()) {
            long from = entry.getKey();
            for (Edge e : entry.getValue()) {
                gCopy.addEdge(from, e.to, e.contracted, e.weight);
            }
        }

        return gCopy;
    }


    public Graph AugmentedGraph(int checkInterval) {
        // 1. Copy original graph
        Graph gCopy = this.copyGraph();

        // 2. Run preprocessing on the working graph (this modifies 'this' graph)
        Graph.PreprocessResult result = this.preprocess(checkInterval);

        // 3. Add all shortcuts to the copy
        for (Shortcut s : result.allShortcuts) {
            // Now we have complete information: from, to, contracted vertex, and weight
            gCopy.addUndirectedEdge(s.from, s.to, s.contracted, s.weight);
        }

        // 4. Copy contraction order (ranks) into gCopy so callers can read them
        gCopy.ranks = new HashMap<>(result.contractionOrder);

        return gCopy;
    }


    public class PreprocessResult {
        public final List<Shortcut> allShortcuts;
        public final Map<Long, Integer> contractionOrder;

        public PreprocessResult(List<Shortcut> allShortcuts, Map<Long, Integer> contractionOrder) {
            this.allShortcuts = allShortcuts;
            this.contractionOrder = contractionOrder;
        }
    }


}
