# Dijkstra Algorithm Comparison Experiment

This Python script runs performance experiments comparing Regular Dijkstra and Bidirectional Dijkstra implementations.

## Features

1. ✅ Reads vertex IDs from `denmark.graph`
2. ✅ Generates 1000 random vertex pairs with fixed seed (reproducible)
3. ✅ Tests **both algorithms** on the same pairs: Regular Dijkstra (D) and Bidirectional Dijkstra (BD)
4. ✅ Executes JAR for each pair and algorithm: `java -jar app/build/libs/app.jar denmark.graph {D|BD} vertex1 vertex2`
5. ✅ Records all outputs (time, relaxed edges, distance)
6. ✅ Calculates average runtime and average relaxed edges for each algorithm
7. ✅ **Compares performance** between algorithms (speedup, relaxation reduction)
8. ✅ Saves detailed results to CSV file

## Requirements

- Python 3.6+
- Java JDK (to run the JAR)
- Built JAR file at `app/build/libs/app.jar`

## Usage

### 1. Build the JAR first:
```bash
gradle jar
```

### 2. Run the experiment:
```bash
python3 experiment.py
```

Or:
```bash
./experiment.py
```

## Configuration

Edit these variables at the top of `experiment.py`:

```python
GRAPH_FILE = "denmark.graph"         # Graph file to use
JAR_PATH = "app/build/libs/app.jar"  # Path to JAR
ALGORITHMS = ["D", "BD"]             # Algorithms to test
NUM_PAIRS = 1000                     # Number of random pairs
RANDOM_SEED = 42                     # Seed for reproducibility
```

**Note:** The experiment will run **both algorithms** on each pair, resulting in 2000 total executions (1000 pairs × 2 algorithms).

## Output

The script will:
1. Print progress every 100 runs (out of 2000 total)
2. Display comprehensive statistics **for each algorithm**:
   - Total executions
   - Successful vs failed (no path) executions
   - Average runtime (ns, μs, ms)
   - Average relaxed edges
   - Average path distance
3. Display **algorithm comparison**:
   - Speedup factor (how many times faster BD is)
   - Relaxation reduction percentage
   - Side-by-side metrics
4. Save detailed results to `experiment_results.csv`

### Sample Output:
```
============================================================
Starting experiment with 1000 random vertex pairs
Testing algorithms: D, BD
Random seed: 42
============================================================

Step 1: Reading vertex IDs from graph file...
Graph has 569586 vertices and 587643 edges
Found 569586 vertices

Step 2: Generating 1000 random vertex pairs (seed=42)...
Generated 1000 pairs

Step 3: Running JAR for each pair with both algorithms...
Progress: 100/2000 runs completed (50/1000 pairs)
Progress: 200/2000 runs completed (100/1000 pairs)
...
Progress: 2000/2000 runs completed (1000/1000 pairs)

Completed all 2000 executions (1000 pairs × 2 algorithms)!

============================================================
EXPERIMENT RESULTS
============================================================

Total executions: 2000

============================================================
Regular Dijkstra (D)
============================================================
Total runs: 1000
Successful: 950
Failed (no path): 50

--- Average Metrics (successful paths only) ---
Average runtime: 2500000.00 ns (2500.00 μs, 2.5000 ms)
Average relaxed edges: 15000.00
Average path distance: 123456.78

--- Average Metrics (all executions) ---
Average runtime: 2375000.00 ns (2375.00 μs)
Average relaxed edges: 14250.00

============================================================
Bidirectional Dijkstra (BD)
============================================================
Total runs: 1000
Successful: 950
Failed (no path): 50

--- Average Metrics (successful paths only) ---
Average runtime: 1250000.00 ns (1250.00 μs, 1.2500 ms)
Average relaxed edges: 7500.00
Average path distance: 123456.78

--- Average Metrics (all executions) ---
Average runtime: 1187500.00 ns (1187.50 μs)
Average relaxed edges: 7125.00

============================================================
ALGORITHM COMPARISON
============================================================

Bidirectional Dijkstra vs Regular Dijkstra:
  Speedup: 2.00x faster
  Relaxation reduction: 50.00% fewer edges relaxed

Detailed comparison:
  D  - Avg time: 2500000.00 ns, Avg relaxed edges: 15000.00
  BD - Avg time: 1250000.00 ns, Avg relaxed edges: 7500.00

Detailed results saved to: experiment_results.csv

============================================================
Experiment completed successfully!
============================================================
```

## CSV Output Format

The `experiment_results.csv` file contains results for both algorithms:
```csv
vertex1,vertex2,algorithm,time_ns,relaxed_edges,distance
12345,67890,D,2500000,900,123456.5
12345,67890,BD,1250000,450,123456.5
23456,78901,D,1960000,640,98765.3
23456,78901,BD,980000,320,98765.3
...
```

Each vertex pair appears **twice** in the CSV (once for each algorithm), making it easy to compare performance.

## Modifying the Experiment

### Test with fewer pairs (faster):
```python
NUM_PAIRS = 100  # Quick test with 100 pairs (200 total runs)
```

### Test only one algorithm:
```python
ALGORITHMS = ["BD"]  # Test only Bidirectional Dijkstra
# or
ALGORITHMS = ["D"]   # Test only Regular Dijkstra
```

### Add more algorithms to compare:
```python
ALGORITHMS = ["D", "BD", "CH"]  # Add Contraction Hierarchies when implemented
```

### Use different random seed:
```python
RANDOM_SEED = 123  # Different random pairs
```

### Use test.graph for testing:
```python
GRAPH_FILE = "test.graph"
NUM_PAIRS = 10  # Fewer pairs for small graph (20 total runs)
```

## Troubleshooting

**JAR not found:**
```bash
gradle jar
```

**Graph file not found:**
Make sure `denmark.graph` is in the project root directory.

**Python version:**
```bash
python3 --version  # Should be 3.6 or higher
```

**Permission denied:**
```bash
chmod +x experiment.py
```
