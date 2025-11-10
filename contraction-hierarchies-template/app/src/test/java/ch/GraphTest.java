package ch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class GraphTest {
     private Graph g;

    @Before
    public void setUp() {
        g = new Graph();
        // small test graph
        g.addVertex(1, new Graph.Vertex(0, 0));
        g.addVertex(2, new Graph.Vertex(1, 0));
        g.addVertex(3, new Graph.Vertex(0, 1));
        g.addVertex(4, new Graph.Vertex(1, 1));

        g.addUndirectedEdge(1, 2, 10);
        g.addUndirectedEdge(2, 3, 5);
        g.addUndirectedEdge(3, 4, 2);
        g.addUndirectedEdge(1, 4, 15);
    }


    @Test
    public void testGetNeighbours() {
        List<Graph.Edge> n2 = g.getNeighbours(2);
        assertNotNull(n2);
        assertTrue(n2.stream().anyMatch(e -> e.to == 1));
        assertTrue(n2.stream().anyMatch(e -> e.to == 3));
    }

    @Test
    public void testCopyGraph() {
        Graph copy = g.copyGraph();
        assertEquals(g.getVertices().size(), copy.getVertices().size());
        assertEquals(g.getEdges().size(), copy.getEdges().size());
        assertNotSame(g, copy);
    }

    @Test
    public void testEdgeDifferenceSimple() {
        int diff = g.getEdgeDifference(2);
        // contraction of vertex 2 probably creates one shortcut between 1–3
        assertTrue(diff <= 1 && diff >= -3);
    }
    @Test
    public void testContractRemovesVertexAndAddsShortcuts() {
        int beforeEdges = g.getEdges().values().stream().mapToInt(List::size).sum();
        List<Graph.Shortcut> added = g.contract(2);
        int afterEdges = g.getEdges().values().stream().mapToInt(List::size).sum();

        assertFalse("Vertex 2 should be removed after contraction", g.getVertices().containsKey(2));
        assertTrue("Edge count should change after contraction", beforeEdges != afterEdges);
        assertTrue("All shortcuts should store correct contracted vertex id", added.stream().allMatch(s -> s.contracted == 2));
    }
    

    @Test
    public void testPreprocessProducesRanks() {
        Graph small = new Graph();
        small.addVertex(1, new Graph.Vertex(0, 0));
        small.addVertex(2, new Graph.Vertex(1, 0));
        small.addVertex(3, new Graph.Vertex(2, 0));
        small.addUndirectedEdge(1, 2, 5);
        small.addUndirectedEdge(2, 3, 5);

        Graph.PreprocessResult result = small.preprocess(100);
        assertNotNull(result.allShortcuts);
        assertFalse(result.contractionOrder.isEmpty());
        assertEquals(3, result.contractionOrder.size());
    }

    @Test
    public void testAugmentedGraphCopiesAndAddsShortcuts() {
        Graph aug = g.AugmentedGraph(100);
        assertNotNull(aug);
        assertTrue(g.getVertices().size() < aug.getVertices().size());
        // Should contain more edges due to shortcuts
        int originalEdges = g.getEdges().values().stream().mapToInt(List::size).sum();
        int augmentedEdges = aug.getEdges().values().stream().mapToInt(List::size).sum();
        assertTrue(augmentedEdges >= originalEdges);
    }
    @Test
    public void testStoreGraphCreatesValidFile() throws IOException {
        String tempFile = Files.createTempFile("augmentedGraph", ".txt").toString();
        ContractionHierachy.storeGraph(g, tempFile);

        assertTrue(Files.exists(Path.of(tempFile)));
        List<String> lines = Files.readAllLines(Path.of(tempFile));
        assertFalse("File should not be empty", lines.isEmpty());

        // First line: numVertices numEdges
        String[] firstLine = lines.get(0).split(" ");
        assertEquals(2, firstLine.length);

        // Vertex line example check
        String[] vertexLine = lines.get(1).split(" ");
        assertEquals("Vertex lines should have 4 columns (id, x, y, rank)", 4, vertexLine.length);

        // Edge line example check
        String[] edgeLine = lines.get(lines.size() - 1).split(" ");
        assertEquals("Edge lines should have 4 columns (from, to, weight, contracted)", 4, edgeLine.length);

        Files.deleteIfExists(Path.of(tempFile));
}
}
