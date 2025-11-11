# Contraction Hierarchies Implementation

This implementation follows Algorithm 1 from the Contraction Hierarchies paper for efficient shortest path queries.

## Usage

### 1. Preprocess a graph (create CH with shortcuts)

```bash
gradle run --args="<input-graph> PREPROCESS 0 0"
```

This creates a file `preprocessed` with:

- Vertex ranks
- Original edges + shortcut edges
- Contracted vertex information

### 2. Query on preprocessed CH graph

```bash
gradle run --args="<preprocessed-graph> CH <source> <target>"
```

Example:

```
bash
gradle run --args="firstTry CH 2501940512 1094730049"
```

### 3. Compare with other algorithms

**Regular Dijkstra:**

```bash
gradle run --args="denmark.graph D <source> <target>"
```

**Bidirectional Dijkstra:**

```bash
gradle run --args="denmark.graph BD <source> <target>"
```

## Algorithm Details

The CH query algorithm (Algorithm 1) uses:

1. **Bidirectional search**: Forward search from source `s`, backward search from target `t`
2. **Upward edges only**: Each direction only follows edges going UP in the hierarchy
   - Forward: `rank[v] > rank[u]`
   - Backward: same condition (upward edges in reverse graph)
3. **Interleaved direction switching**: Alternates between forward and backward search
4. **Early termination**: Stops when `min(Q↑) + min(Q↓) >= best_distance`

## Key Components

### `ContractionHierachy.java`

- `query(s, t)`: Main CH query algorithm
- `isUpward(u, v)`: Checks if edge (u,v) is upward in hierarchy
- `storeGraph()`: Saves preprocessed graph with ranks and shortcuts

### `Graph.java`

- `preprocess()`: Contracts vertices and creates shortcuts
- `contractAndUpdate()`: Contracts a single vertex
- `getEdgeDifference()`: Priority heuristic for contraction order
- `AugmentedGraph()`: Creates graph copy with all shortcuts

### File Formats

**Regular graph (3 columns per vertex, 3 per edge):**

```
<n> <m>
<vertex_id> <x> <y>
...
<from> <to> <weight>
...
```

**Preprocessed CH graph (4 columns):**

```
<n> <m>
<vertex_id> <x> <y> <rank>
...
<from> <to> <weight> <contracted_vertex>
...
```

## Performance

Contraction Hierarchies provides significant speedup:

- **BD**: ~2000 relaxed edges
- **CH**: ~150 relaxed edges (10x fewer!)

This speedup comes from preprocessing that creates shortcuts, allowing queries to only explore the "highway" (high-rank) portion of the graph.

