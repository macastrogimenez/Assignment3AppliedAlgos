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

The implementation follows a modular architecture with three main algorithm classes (`Dijkstra`, `BidirectionalDijkstra`, `ContractionHierachy`) built on a shared `Graph` data structure that manages vertices, edges, ranks, and shortcuts. The workflow separates preprocessing (node contraction with shortcut creation in `Graph.java`) from query execution (bidirectional hierarchical search in `ContractionHierachy.java`), with preprocessed graphs stored as files for reuse across multiple queries. Evaluation infrastructure includes Python scripts for automated experimentation (`experimentWithCH.py`) and visualization (`plot_ch_vs_d.py`), generating performance comparisons on 1000 random vertex pairs from the Denmark road network dataset. The project maintains clean separation of concerns with dedicated test suites, comprehensive documentation, and a command-line interface (`Main.java`) supporting multiple algorithm modes (D, BD, CH, PREPROCESS) for flexible experimentation and validation.

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
gradle run --args="app/denmark.graph PREPROCESS 0 0" # for Pre-processing


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

## Experiments

- `experiment.py` experiment with 1000 vertex pairs on Dijkstra and Bidirectional Dijkstra.
- `experimentWithCH.py` experiment with 1000 vertex pairs on Dijkstra and Contraction Hierarchies.

Both experiments compare performance between algorithms (speedup, relaxation reduction) and saves detailed results to CSV file.

### Requirements

- Python 3.6+
- Java JDK (to run the JAR)
- Built JAR file at `app/build/libs/app.jar`

### Usage

#### 1. Build the JAR first

```bash
gradle jar
```

#### 2. Run the experiment

```bash
python3 experiment.py
python3 experimentWithCH.py
```

### Troubleshooting

**Graph file not found:**
Make sure `denmark.graph` and `preprocessedDk` are in app the directory.
