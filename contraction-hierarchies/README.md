# Contraction Hierarchies Assignment

Contraction Hierarchies exploits graph hierarchy through node contraction preprocessing: vertices are iteratively contracted in order of importance, creating shortcuts that preserve shortest path distances while reducing graph complexity. Query-time searches become dramatically more efficient by restricting exploration to upward edges in both forward (from source) and backward (from target) directions, ensuring both searches climb the hierarchy and meet at high-importance nodes. This hierarchical filtering reduces the search space from O(n) to O(√n) or better on road networks, achieving 20-1000× speedups (e.g., 6ms vs 177ms) with 99%+ reduction in edge relaxations while maintaining optimality. The one-time preprocessing cost is amortized across potentially millions of queries, making CH ideal for static graphs in production routing systems where query performance is critical.

## Build

The project uses gradle. Carry out the tests using `gradle test` and build the jar file using `gradle jar`.

The graph is read from standard input and a test graph `test.graph` is provided in this repository.

Basic file reading is already implemented in the Main class. An example run is:

```bash
gradle jar
java -jar app/build/libs/app.jar < test.graph
```

(Note that we simulate undirected edges by adding directed edges in both directions, so the count that is printed is twice as large as the number in the graph provided.)

## Code structure

We have implemented basic functionality such as graph reading, a graph data structure using HashMaps, and a basic Dijkstra implementation that keeps track of visited vertices using HashSets.  Basic unit tests for the Dijkstra implementation are available as well.

To solve the assignment, you probably need to update the API by changing method signatures to incorporate additional functionality.

## Preprocess a graph (create CH with shortcuts)

```bash
gradle run --args="<input-graph-path> PREPROCESS 0 0"
```

This creates a file `preprocessed` with:

- Vertex ranks
- Original edges + shortcut edges
- Contracted vertex information

## How to run the Program

### From Gradle

```bash
# From the project root
gradle run --args="app/denmark.graph BD 1096800199 1124791586" # for Bidirectional Dijkstra
gradle run --args="app/denmark.graph D 115739 115775" # for Dijkstra
gradle run --args="app/preprocessedDk CH 115739 115775" # for Contraction Hierarchies

# Or
gradle run --args="test.graph BD 0 3" # for Bidirectional Dijkstra
```

### From JAR file

```bash
# Navigate to project root
cd /Users/miguela/Documents/1.\ ITU/1.\ Applied\ Algorithms/Assignment3/contraction-hierarchies-template

# Build JAR
gradle jar

# Run with test graph
java -jar app/build/libs/app.jar test.graph BD 0 3

# Run with denmark graph
java -jar app/build/libs/app.jar denmark.graph BD 0 100

# Run with regular Dijkstra
java -jar app/build/libs/app.jar denmark.graph D 0 100
```
