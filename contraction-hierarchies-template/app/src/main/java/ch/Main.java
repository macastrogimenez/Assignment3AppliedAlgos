package ch;

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

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String algorithm = sc.nextLine();
        int start = sc.nextInt();
        int target = sc.nextInt();

        var graph = readGraph(sc);
        sc.close();
        if(algorithm.equals("BD")){
            //System.out.println(graph.n + " " + graph.m); //TODO: discuss the purpose of this -> why does it double edges?
            BidirectionalDijkstra d = new BidirectionalDijkstra();
            Result<Double> result = d.distance(graph, start, target); // Ula values: graph,1,15
            System.out.println("Time: "+result.time+", Relaxed edges: "+result.relaxed+", Result: "+result.result);
        }
        else if (algorithm.equals("D")){
            // TODO: run the graph with regular dijkstra and return result with time, relaxed edges and result
            Result<Integer> result = Dijkstra.shortestPath(graph, start, target); // Ula values: graph,1,15
            System.out.println("Time: "+result.time+", Relaxed edges: "+result.relaxed+", Result: "+result.result);
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