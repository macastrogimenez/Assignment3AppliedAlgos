package ch;

import java.io.File;
import java.util.Scanner;

class Main {

    private static Graph readGraph(Scanner sc) {
        int n = sc.nextInt();
        int m = sc.nextInt();

        Graph g = new Graph();

        // temporary variables to save vertex data from input
        long id; 
        float x, y;
        long[] ids = new long[n];

        for (int i = 0; i < n; i++) { // loop over vertex lines
            id = sc.nextLong(); // vertex id 

            ids[i] = id; // map the vertex on an array of vertices
            x = Float.parseFloat(sc.next()); // coord x of vertex 
            y = Float.parseFloat(sc.next()); // coord y of vertex 

            g.addVertex(id, new Graph.Vertex(x, y)); // add vertex with id, coord x and y to graph
        }

        // temporary variables to save edge data from input
        long from, to;
        int weight;

        for (int i = 0; i < m; i++) { // loop over edge lines
            from = sc.nextLong(); 
            to = sc.nextLong();
            weight = sc.nextInt();
            g.addUndirectedEdge(from, to, weight); // saving the edges to the graph
        }

        return g;
    }

    /**
     * Read a preprocessed CH graph with ranks and shortcuts
     * Format: vertices have rank as 4th column, edges have contracted vertex as 4th column
     */
    private static Graph readCHGraph(Scanner sc) {
        int n = sc.nextInt();
        int m = sc.nextInt();

        Graph g = new Graph();

        // Read vertices with ranks
        for (int i = 0; i < n; i++) {
            long id = sc.nextLong();
            float x = Float.parseFloat(sc.next());
            float y = Float.parseFloat(sc.next());
            int rank = sc.nextInt();  // Read rank
            
            g.addVertex(id, new Graph.Vertex(x, y));
            g.getRanks().put(id, rank);  // Store rank
        }

        // Read edges with contracted vertex info
        for (int i = 0; i < m; i++) {
            long from = sc.nextLong();
            long to = sc.nextLong();
            int weight = sc.nextInt();
            long contracted = sc.nextLong();  // Read contracted vertex
            
            g.addEdge(from, to, contracted, weight);
        }

        return g;
    }

    public static void main(String[] args) throws Exception {
        String filePath;
        String algorithm;
        long start, target;  
        
        // Check if file path is provided as command line argument
        if (args.length >= 4) {
            // Read from command line arguments: <file> <algorithm> <start> <target>
            filePath = args[0];
            algorithm = args[1];
            start = Long.parseLong(args[2]);  
            target = Long.parseLong(args[3]); 
        } else {
            // Fall back to reading from stdin
            Scanner sc = new Scanner(System.in);
            filePath = sc.nextLine().trim();
            
            // Create a new scanner to read from the .graph file
            File graphFile = new File(filePath);
            if (!graphFile.exists()) {
                System.err.println("Error: File not found: " + filePath);
                System.err.println("Current working directory: " + System.getProperty("user.dir"));
                System.err.println("Absolute path tried: " + graphFile.getAbsolutePath());
                System.exit(1);
            }
            Scanner fileScanner = new Scanner(graphFile);
            var graph = readGraph(fileScanner);
            fileScanner.close();

            algorithm = sc.nextLine();
            start = sc.nextLong();  
            target = sc.nextLong(); 
            sc.close();
            
            runAlgorithm(graph, algorithm, start, target);
            return;
        }
        
        // Read graph from file
        File graphFile = new File(filePath);
        if (!graphFile.exists()) {
            System.err.println("Error: File not found: " + filePath);
            System.err.println("Current working directory: " + System.getProperty("user.dir"));
            System.err.println("Absolute path tried: " + graphFile.getAbsolutePath());
            System.exit(1);
        }
        Scanner fileScanner = new Scanner(graphFile);
        
        // Determine if this is a preprocessed CH graph or regular graph
        // CH graphs have 4 columns for vertices (id x y rank) and edges (from to weight contracted)
        Graph graph;
        if (algorithm.equals("CH")) {
            // Read as preprocessed CH graph
            graph = readCHGraph(fileScanner);
        } else {
            // Read as regular graph
            graph = readGraph(fileScanner);
        }
        fileScanner.close();
        
        runAlgorithm(graph, algorithm, start, target);
    }
    
    private static void runAlgorithm(Graph graph, String algorithm, long start, long target) {
        if(algorithm.equals("BD")){
            BidirectionalDijkstra d = new BidirectionalDijkstra();
            Result<Double> result = d.distance(graph, start, target);
            System.out.println("Time: "+result.time+", Relaxed edges: "+result.relaxed+", Result: "+result.result);
            //ContractionHierachy.storeGraph(graph, "Dk");
        }
        else if (algorithm.equals("D")){
            Result<Integer> result = Dijkstra.shortestPath(graph, start, target);
            System.out.println("Time: "+result.time+", Relaxed edges: "+result.relaxed+", Result: "+result.result);
        }
        else if (algorithm.equals("CH")){
            // Contraction Hierarchies query
            ContractionHierachy ch = new ContractionHierachy(graph);
            Result<Integer> result = ch.query(start, target);
            System.out.println("Time: "+result.time+", Relaxed edges: "+result.relaxed+", Result: "+result.result);
        }
        else if (algorithm.equals("PREPROCESS")){
            // Preprocess graph and store it
            System.out.println("Preprocessing graph...");
            ContractionHierachy.storeGraph(graph, "preprocessed");
        }
        else {
            System.err.println("Unknown algorithm: " + algorithm);
            System.err.println("Available algorithms: D (Dijkstra), BD (Bidirectional Dijkstra), CH (Contraction Hierarchies), PREPROCESS");
        }
    }
}

// test input -> result should be 6
// 7 10
// 0 1.0 2.0
// 1 2.0 3.0
// 2 3.0 3.0
// 3 4.0 2.0
// 4 3.0 1.0
// 5 2.0 1.0
// 6 2.0 1.0
// 0 1 3
// 1 2 4
// 2 3 4
// 3 4 2
// 4 5 3 
// 5 0 1
// 4 6 1 
// 6 2 1
// 5 6 2
// 1 6 1

//     1 ────────4──────── 2
//    /│\                  │
// 3 / │ \ 1               │
//  /  │  \                │1
// 0   │   6               │
//  \  │  /│\              │
// 1 \ │ / │ \2            │4
//    \|/  │  \            │
//     5   │   \           │
//      \  │1   \          │
//     3 \ │     \         │
//        \│      \        │
//         4 ──────2───────3