#!/usr/bin/env python3
"""
Script to create a log-log scatter plot comparing Dijkstra and Bidirectional Dijkstra.
X-axis: Time in nanoseconds (log scale)
Y-axis: Number of relaxed edges (log scale)
"""

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Read the CSV file
df = pd.read_csv('experiment_resultsBDvsD.csv')

# Separate data by algorithm
dijkstra = df[df['algorithm'] == 'D']
bd = df[df['algorithm'] == 'BD']

# Create the plot
plt.figure(figsize=(12, 8))

# Plot Dijkstra (blue) and Bidirectional Dijkstra (red)
plt.scatter(dijkstra['time_ns'], dijkstra['relaxed_edges'], 
           c='blue', alpha=0.6, s=30, label='Dijkstra', edgecolors='none')
plt.scatter(bd['time_ns'], bd['relaxed_edges'], 
           c='red', alpha=0.6, s=30, label='Bidirectional Dijkstra', edgecolors='none')

# Set log-log scale
plt.xscale('log')
plt.yscale('log')

# Labels and title
plt.xlabel('Time (nanoseconds, log scale)', fontsize=12)
plt.ylabel('Number of Relaxed Edges (log scale)', fontsize=12)
plt.title('Algorithm Performance: Dijkstra vs Bidirectional Dijkstra\n(Log-Log Scale)', fontsize=14, fontweight='bold')

# Add grid for better readability
plt.grid(True, which="both", ls="-", alpha=0.2)

# Legend
plt.legend(loc='upper left', fontsize=11, framealpha=0.9)

# Add statistics text box
d_avg_time = dijkstra['time_ns'].mean()
d_avg_relaxed = dijkstra['relaxed_edges'].mean()
bd_avg_time = bd['time_ns'].mean()
bd_avg_relaxed = bd['relaxed_edges'].mean()

speedup = d_avg_time / bd_avg_time if bd_avg_time > 0 else 0
relaxation_reduction = ((d_avg_relaxed - bd_avg_relaxed) / d_avg_relaxed * 100) if d_avg_relaxed > 0 else 0

stats_text = f'Average Performance:\n' \
             f'Dijkstra: {d_avg_relaxed:.0f} edges, {d_avg_time/1e6:.2f} ms\n' \
             f'BD: {bd_avg_relaxed:.0f} edges, {bd_avg_time/1e6:.2f} ms\n' \
             f'Speedup: {speedup:.2f}x\n' \
             f'Relaxation reduction: {relaxation_reduction:.1f}%'

plt.text(0.98, 0.02, stats_text, transform=plt.gca().transAxes,
         fontsize=10, verticalalignment='bottom', horizontalalignment='right',
         bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.8))

# Tight layout
plt.tight_layout()

# Save the plot
plt.savefig('bd_vs_dijkstra_loglog.png', dpi=300, bbox_inches='tight')
print("Plot saved as 'bd_vs_dijkstra_loglog.png'")

# Display the plot
plt.show()

# Print statistics
print("\n" + "="*60)
print("STATISTICS")
print("="*60)
print(f"\nDijkstra:")
print(f"  Average relaxed edges: {d_avg_relaxed:.2f}")
print(f"  Average time: {d_avg_time:.2f} ns ({d_avg_time/1e6:.4f} ms)")

print(f"\nBidirectional Dijkstra:")
print(f"  Average relaxed edges: {bd_avg_relaxed:.2f}")
print(f"  Average time: {bd_avg_time:.2f} ns ({bd_avg_time/1e6:.4f} ms)")

print(f"\nComparison:")
print(f"  Speedup: {speedup:.2f}x")
print(f"  Relaxation reduction: {relaxation_reduction:.2f}% fewer edges")
print("="*60)
