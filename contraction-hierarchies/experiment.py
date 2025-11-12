#!/usr/bin/env python3
"""
Experiment script to test Bidirectional Dijkstra on denmark.graph
Runs 1000 random vertex pairs and records performance metrics.
"""

import subprocess
import random
import re
from pathlib import Path
from typing import List, Tuple

# Configuration
GRAPH_FILE = "app/denmark.graph"
JAR_PATH = "app/build/libs/app.jar"
ALGORITHMS = ["D", "BD"]  # Regular Dijkstra and Bidirectional Dijkstra
NUM_PAIRS = 1000
RANDOM_SEED = 42


class ExperimentResult:
    """Stores results from a single JAR execution"""
    def __init__(self, vertex1: int, vertex2: int, algorithm: str, time_ns: int, relaxed_edges: int, distance: float):
        self.vertex1 = vertex1
        self.vertex2 = vertex2
        self.algorithm = algorithm
        self.time_ns = time_ns
        self.relaxed_edges = relaxed_edges
        self.distance = distance
    
    def __repr__(self):
        return f"ExperimentResult({self.vertex1} -> {self.vertex2} [{self.algorithm}]: time={self.time_ns}ns, relaxed={self.relaxed_edges}, dist={self.distance})"


def read_vertex_ids(graph_file: str) -> List[int]:
    """
    Read vertex IDs from the graph file.
    Returns a list of all vertex IDs.
    """
    vertex_ids = []
    
    with open(graph_file, 'r') as f:
        # Read first line: n (vertices) and m (edges)
        first_line = f.readline().strip().split()
        n = int(first_line[0])
        m = int(first_line[1])
        
        print(f"Graph has {n} vertices and {m} edges")
        
        # Read next n lines to get vertex IDs
        for _ in range(n):
            line = f.readline().strip().split()
            vertex_id = int(line[0])
            vertex_ids.append(vertex_id)
    
    return vertex_ids


def generate_random_pairs(vertex_ids: List[int], num_pairs: int, seed: int) -> List[Tuple[int, int]]:
    """
    Generate random pairs of vertex IDs using a fixed seed for reproducibility.
    """
    random.seed(seed)
    pairs = []
    
    for _ in range(num_pairs):
        # Select two different vertices randomly
        v1, v2 = random.sample(vertex_ids, 2)
        pairs.append((v1, v2))
    
    return pairs


def run_jar(graph_file: str, algorithm: str, vertex1: int, vertex2: int) -> ExperimentResult:
    """
    Execute the JAR file with given arguments and parse the output.
    Expected output format: "Time: <time_ns>, Relaxed edges: <count>, Result: <distance>"
    """
    cmd = [
        "java", "-jar", JAR_PATH,
        graph_file, algorithm, str(vertex1), str(vertex2)
    ]
    
    try:
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            check=True
        )
        
        # Parse output: "Time: 657208, Relaxed edges: 0, Result: -1.0"
        output = result.stdout.strip()
        
        # Extract values using regex
        time_match = re.search(r'Time:\s*(\d+)', output)
        relaxed_match = re.search(r'Relaxed edges:\s*(\d+)', output)
        result_match = re.search(r'Result:\s*([-\d.]+)', output)
        
        if not (time_match and relaxed_match and result_match):
            raise ValueError(f"Failed to parse output: {output}")
        
        time_ns = int(time_match.group(1))
        relaxed_edges = int(relaxed_match.group(1))
        distance = float(result_match.group(1))
        
        return ExperimentResult(vertex1, vertex2, algorithm, time_ns, relaxed_edges, distance)
    
    except subprocess.CalledProcessError as e:
        print(f"Error running JAR for {vertex1} -> {vertex2} with {algorithm}: {e}")
        print(f"stderr: {e.stderr}")
        raise


def run_experiment(graph_file: str, num_pairs: int, seed: int) -> List[ExperimentResult]:
    """
    Main experiment function: runs the JAR for random vertex pairs with both algorithms and collects results.
    """
    print(f"\n{'='*60}")
    print(f"Starting experiment with {num_pairs} random vertex pairs")
    print(f"Testing algorithms: {', '.join(ALGORITHMS)}")
    print(f"Random seed: {seed}")
    print(f"{'='*60}\n")
    
    # Step 1: Read vertex IDs from graph file
    print("Step 1: Reading vertex IDs from graph file...")
    vertex_ids = read_vertex_ids(graph_file)
    print(f"Found {len(vertex_ids)} vertices")
    
    # Step 2: Generate random pairs
    print(f"\nStep 2: Generating {num_pairs} random vertex pairs (seed={seed})...")
    pairs = generate_random_pairs(vertex_ids, num_pairs, seed)
    print(f"Generated {len(pairs)} pairs")
    
    # Step 3: Run JAR for each pair with both algorithms and collect results
    print(f"\nStep 3: Running JAR for each pair with both algorithms...")
    results = []
    total_runs = num_pairs * len(ALGORITHMS)
    run_count = 0
    
    for i, (v1, v2) in enumerate(pairs, 1):
        for algorithm in ALGORITHMS:
            run_count += 1
            if run_count % 100 == 0:
                print(f"Progress: {run_count}/{total_runs} runs completed ({i}/{num_pairs} pairs)")
            
            result = run_jar(graph_file, algorithm, v1, v2)
            results.append(result)
    
    print(f"\nCompleted all {total_runs} executions ({num_pairs} pairs × {len(ALGORITHMS)} algorithms)!")
    return results


def analyze_results(results: List[ExperimentResult]) -> None:
    """
    Analyze and print statistics from the experiment results, comparing both algorithms.
    """
    print(f"\n{'='*60}")
    print("EXPERIMENT RESULTS")
    print(f"{'='*60}")
    
    # Separate results by algorithm
    results_by_algo = {algo: [r for r in results if r.algorithm == algo] for algo in ALGORITHMS}
    
    print(f"\nTotal executions: {len(results)}")
    
    # Analyze each algorithm
    for algo in ALGORITHMS:
        algo_results = results_by_algo[algo]
        successful = [r for r in algo_results if r.distance != -1.0]
        failed = [r for r in algo_results if r.distance == -1.0]
        
        algo_name = "Regular Dijkstra" if algo == "D" else "Bidirectional Dijkstra"
        
        print(f"\n{'='*60}")
        print(f"{algo_name} ({algo})")
        print(f"{'='*60}")
        print(f"Total runs: {len(algo_results)}")
        print(f"Successful: {len(successful)}")
        print(f"Failed (no path): {len(failed)}")
        
        if successful:
            # Calculate averages for successful runs
            avg_time_ns = sum(r.time_ns for r in successful) / len(successful)
            avg_relaxed = sum(r.relaxed_edges for r in successful) / len(successful)
            avg_distance = sum(r.distance for r in successful) / len(successful)
            
            # Convert to more readable units
            avg_time_ms = avg_time_ns / 1_000_000
            avg_time_us = avg_time_ns / 1_000
            
            print(f"\n--- Average Metrics (successful paths only) ---")
            print(f"Average runtime: {avg_time_ns:.2f} ns ({avg_time_us:.2f} μs, {avg_time_ms:.4f} ms)")
            print(f"Average relaxed edges: {avg_relaxed:.2f}")
            print(f"Average path distance: {avg_distance:.2f}")
        
        # All results (including failures)
        total_time_ns = sum(r.time_ns for r in algo_results)
        total_relaxed = sum(r.relaxed_edges for r in algo_results)
        
        avg_time_all_ns = total_time_ns / len(algo_results)
        avg_relaxed_all = total_relaxed / len(algo_results)
        
        print(f"\n--- Average Metrics (all executions) ---")
        print(f"Average runtime: {avg_time_all_ns:.2f} ns ({avg_time_all_ns/1000:.2f} μs)")
        print(f"Average relaxed edges: {avg_relaxed_all:.2f}")
    
    # Comparison between algorithms
    print(f"\n{'='*60}")
    print("ALGORITHM COMPARISON")
    print(f"{'='*60}")
    
    if len(ALGORITHMS) == 2:
        d_results = [r for r in results_by_algo["D"] if r.distance != -1.0]
        bd_results = [r for r in results_by_algo["BD"] if r.distance != -1.0]
        
        if d_results and bd_results:
            d_avg_time = sum(r.time_ns for r in d_results) / len(d_results)
            bd_avg_time = sum(r.time_ns for r in bd_results) / len(bd_results)
            
            d_avg_relaxed = sum(r.relaxed_edges for r in d_results) / len(d_results)
            bd_avg_relaxed = sum(r.relaxed_edges for r in bd_results) / len(bd_results)
            
            speedup = d_avg_time / bd_avg_time if bd_avg_time > 0 else float('inf')
            relaxation_reduction = ((d_avg_relaxed - bd_avg_relaxed) / d_avg_relaxed * 100) if d_avg_relaxed > 0 else 0
            
            print(f"\nBidirectional Dijkstra vs Regular Dijkstra:")
            print(f"  Speedup: {speedup:.2f}x faster")
            print(f"  Relaxation reduction: {relaxation_reduction:.2f}% fewer edges relaxed")
            print(f"\nDetailed comparison:")
            print(f"  D  - Avg time: {d_avg_time:.2f} ns, Avg relaxed edges: {d_avg_relaxed:.2f}")
            print(f"  BD - Avg time: {bd_avg_time:.2f} ns, Avg relaxed edges: {bd_avg_relaxed:.2f}")


def save_results_to_file(results: List[ExperimentResult], filename: str = "experiment_results.csv") -> None:
    """
    Save detailed results to a CSV file for further analysis.
    """
    with open(filename, 'w') as f:
        # Write header
        f.write("vertex1,vertex2,algorithm,time_ns,relaxed_edges,distance\n")
        
        # Write data
        for r in results:
            f.write(f"{r.vertex1},{r.vertex2},{r.algorithm},{r.time_ns},{r.relaxed_edges},{r.distance}\n")
    
    print(f"\nDetailed results saved to: {filename}")


def main():
    """Main entry point"""
    # Check if JAR exists
    jar_path = Path(JAR_PATH)
    if not jar_path.exists():
        print(f"Error: JAR file not found at {JAR_PATH}")
        print("Please run 'gradle jar' first to build the JAR file.")
        return
    
    # Check if graph file exists
    graph_path = Path(GRAPH_FILE)
    if not graph_path.exists():
        print(f"Error: Graph file not found at {GRAPH_FILE}")
        return
    
    # Run the experiment with both algorithms
    results = run_experiment(GRAPH_FILE, NUM_PAIRS, RANDOM_SEED)
    
    # Analyze and display results
    analyze_results(results)
    
    # Save results to CSV
    save_results_to_file(results)
    
    print(f"\n{'='*60}")
    print("Experiment completed successfully!")
    print(f"{'='*60}\n")


if __name__ == "__main__":
    main()
