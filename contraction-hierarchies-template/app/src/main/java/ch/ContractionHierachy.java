package ch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class ContractionHierachy {
    
    private Graph graph;
    private Map<Long, Integer> ranks;

    public ContractionHierachy(Graph graph) {
        this.graph = graph;
        this.ranks = graph.getRanks();
    }

    /**
     * Query algorithm for Contraction Hierarchies (Algorithm 1 from paper)
     * @param s source vertex
     * @param t target vertex
     * @return Result containing time, relaxed edges count, and distance
     */
    public Result<Integer> query(long s, long t) {
        long startTime = System.nanoTime();
        int relaxedEdges = 0;
        
        if (s == t) {
            long endTime = System.nanoTime();
            return new Result<Integer>((int)(endTime - startTime), 0, 0);
        }
        
        // Line 1: d↑:= (∞,...,∞); d↓:= (∞,...,∞); d:= ∞;
        HashMap<Long, Integer> dUp = new HashMap<>();     // d↑ - forward distances
        HashMap<Long, Integer> dDown = new HashMap<>();   // d↓ - backward distances
        int d = Integer.MAX_VALUE;                        // best distance found
        
        // Line 2: d↑[s]:= 0; d↓[t]:= 0;
        dUp.put(s, 0);
        dDown.put(t, 0);
        
        // Line 3: Q↑ = {(0,s)}; Q↓ = {(0,t)}; r:= ↑;
        PriorityQueue<PQElem> qUp = new PriorityQueue<>();
        PriorityQueue<PQElem> qDown = new PriorityQueue<>();
        qUp.add(new PQElem(0, s));
        qDown.add(new PQElem(0, t));
        boolean directionUp = true;  // r := ↑ (true = up, false = down)
        try {
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        // Line 4: while (Q↑ ≠ ∅ or Q↓ ≠ ∅) and (d > min {min Q↑, min Q↓}) do
        while ((!qUp.isEmpty() || !qDown.isEmpty())) {
            // Check termination condition
            int minUp = qUp.isEmpty() ? Integer.MAX_VALUE : qUp.peek().key;
            int minDown = qDown.isEmpty() ? Integer.MAX_VALUE : qDown.peek().key;
            int minTotal = Math.min(minUp, minDown);
            
            if (minTotal == Integer.MAX_VALUE || d <= minTotal) {
                break;
            }
            
            // Line 5: if Q¬r ≠ ∅ then r:= ¬r;  // interleave direction
            PriorityQueue<PQElem> qCurrent;
            HashMap<Long, Integer> dCurrent;
            
            if (directionUp) {
                // Check if we should switch to down
                if (!qDown.isEmpty()) {
                    directionUp = false;
                }
            } else {
                // Check if we should switch to up
                if (!qUp.isEmpty()) {
                    directionUp = true;
                }
            }
            
            // Set current direction's queue and distance map
            if (directionUp) {
                qCurrent = qUp;
                dCurrent = dUp;
            } else {
                qCurrent = qDown;
                dCurrent = dDown;
            }
            
            // Skip if current queue is empty
            if (qCurrent.isEmpty()) {
                directionUp = !directionUp;
                continue;
            }
            
            // Line 6: (·,u):= Qr.deleteMin();  // settle u
            PQElem elem = qCurrent.poll();
            long u = elem.v;
            int distU = elem.key;
            
            // Skip if we already found a better path to u
            if (distU > dCurrent.getOrDefault(u, Integer.MAX_VALUE)) {
                continue;
            }
            
            // Line 7: d:= min {d, d↑[u] + d↓[u]};  // u is potential candidate
            int duUp = dUp.getOrDefault(u, Integer.MAX_VALUE);
            int duDown = dDown.getOrDefault(u, Integer.MAX_VALUE);
            if (duUp != Integer.MAX_VALUE && duDown != Integer.MAX_VALUE) {
                d = Math.min(d, duUp + duDown);
            }
            
            // Line 8: foreach e = (u,v) ∈ E* do  // relax edges
            List<Graph.Edge> neighbors = graph.getNeighbours(u);
            if (neighbors == null) continue;
            
            for (Graph.Edge e : neighbors) {
                long v = e.to;
                int weight = e.weight;
                
                // Line 9: if r(e) and (dr[u] + c(e) < dr[v]) then  // shorter path found
                // r(e) means: use only upward edges in the hierarchy
                boolean isUpwardEdge = isUpward(u, v);
                
                if (directionUp) {
                    // Forward search: only follow upward edges
                    if (isUpwardEdge) {
                        int newDist = distU + weight;
                        int oldDist = dCurrent.getOrDefault(v, Integer.MAX_VALUE);
                        
                        if (newDist < oldDist) {
                            // Line 10: dr[v]:= dr[u] + c(e);  // update tentative distance
                            dCurrent.put(v, newDist);
                            
                            // Line 11: Qr.update(dr[v],v);  // update priority queue
                            qCurrent.add(new PQElem(newDist, v));
                            relaxedEdges++;
                        }
                    }
                } else {
                    // Backward search: only follow downward edges (which are upward in reverse)
                    // In backward search, we need edges where rank[u] < rank[v] (upward from v's perspective)
                    if (isUpwardEdge) {
                        int newDist = distU + weight;
                        int oldDist = dCurrent.getOrDefault(v, Integer.MAX_VALUE);
                        
                        if (newDist < oldDist) {
                            dCurrent.put(v, newDist);
                            qCurrent.add(new PQElem(newDist, v));
                            relaxedEdges++;
                        }
                    }
                }
                
                // Update best distance if both searches reached v
                int dvUp = dUp.getOrDefault(v, Integer.MAX_VALUE);
                int dvDown = dDown.getOrDefault(v, Integer.MAX_VALUE);
                if (dvUp != Integer.MAX_VALUE && dvDown != Integer.MAX_VALUE) {
                    d = Math.min(d, dvUp + dvDown);
                }
            }
        }
        
        // Line 12: return d;
        long endTime = System.nanoTime();
        int finalDist = (d == Integer.MAX_VALUE) ? -1 : d;
        return new Result<Integer>((int)(endTime - startTime), relaxedEdges, finalDist);
    }
    
    /**
     * Check if edge (u,v) is an upward edge in the hierarchy
     * An edge is upward if rank[v] > rank[u]
     */
    private boolean isUpward(long u, long v) {
        Integer rankU = ranks.getOrDefault(u, -1);
        Integer rankV = ranks.getOrDefault(v, -1);
        
        // If ranks are not set, treat as upward (fallback for non-CH graphs)
        if (rankU == -1 || rankV == -1) {
            return true;
        }
        
        return rankV > rankU;
    }

    /** 
     * @param g The augmented graph (original + shortcuts).
     * @param filename Output file path.
     * @param contractionOrder Map from vertex ID → contraction rank.
     *                         (Rank 0 means contracted first, higher = later)
     */
    public static void storeGraph(Graph g, String filename) {
        
        Graph augmentedG = g.AugmentedGraph(10000);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            int numVertices = augmentedG.getVertices().size();
            int numEdges = 0;
            for (List<Graph.Edge> edges : augmentedG.getEdges().values()) {
                numEdges += edges.size();
            }

            // First line: number of vertices and edges
            writer.write(numVertices + " " + numEdges);
            writer.newLine();

            // Vertices
            for (Map.Entry<Long, Graph.Vertex> entry : augmentedG.getVertices().entrySet()) {
                long id = entry.getKey();
                Graph.Vertex v = entry.getValue();
                int rank = augmentedG.getRanks().getOrDefault(id, -1); // -1 if not found
                writer.write(id + " " + v.x + " " + v.y + " " + rank);
                writer.newLine();
            }

            // Edges
            for (Map.Entry<Long, List<Graph.Edge>> entry : augmentedG.getEdges().entrySet()) {
                long from = entry.getKey();
                for (Graph.Edge e : entry.getValue()) {
                    writer.write(from + " " + e.to + " " + e.weight + " " + e.contracted);
                    writer.newLine();
                }
            }

            System.out.println("Graph successfully written to: " + filename);
            System.out.println("Vertices: " + numVertices + ", Edges: " + numEdges);

        } catch (IOException e) {
            System.err.println("Error writing graph to file: " + e.getMessage());
        }
    }
}
