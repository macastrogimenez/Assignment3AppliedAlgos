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
        var graph = readGraph(sc);
        sc.close();
        System.out.println(graph.n + " " + graph.m); //TODO: discuss the purpose of this
        BidirectionalDijkstra d = new BidirectionalDijkstra();
        System.out.println(d.shortestPath(graph, 1, 15));
    }
}