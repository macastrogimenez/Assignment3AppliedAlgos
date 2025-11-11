package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    static final int maxNodes = 50;

    int n, m;

    public static class Vertex {
        public float x, y;

        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public class Edge {
        public long to;
        public int weight;
        public long contracted; // only used by contraction hierachy

        public Edge(long to, int weight, long contracted) {
            this.to = to;
            this.weight = weight;
            this.contracted = contracted;
        }
    }

    private Map<Long, List<Edge>> edges;
    private Map<Long, Vertex> vertices;
    private List<Long> ids;

    public Graph() {
        this.n = 0;
        this.m = 0;
        this.edges = new HashMap<>();
        this.vertices = new HashMap<>();
        this.ids = new ArrayList<>();
    }

    public void addVertex(long id, Vertex v) {
        this.vertices.put(id, v);
        ids.add(id);
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

    public List<Long> getVertices() {
        return ids;
    }

    public Vertex getVertex(long id) {
        return this.vertices.get(id);
    }

    public int degree(long v) {
        return this.edges.get(v).size();
    }
}
