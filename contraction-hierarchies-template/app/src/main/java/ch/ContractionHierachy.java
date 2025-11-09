package ch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class ContractionHierachy {

    public ContractionHierachy() {
      // To be filled out
    }

    public Result<Integer> query(long s, long t) {
        
        // To be filled out

        return new Result<Integer>(0,0,0); 
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
