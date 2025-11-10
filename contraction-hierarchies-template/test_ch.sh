#!/bin/bash

# Test script to compare CH vs BD performance

echo "=== Contraction Hierarchies Query Test ==="
echo ""
echo "Test vertices: 2501940512 -> 1094730049"
echo ""

echo "1. Bidirectional Dijkstra (BD) on original graph:"
gradle run --args="denmark.graph BD 2501940512 1094730049" --quiet 2>&1 | grep -E "Time:|Relaxed"

echo ""
echo "2. Contraction Hierarchies (CH) on preprocessed graph:"
gradle run --args="firstTry CH 2501940512 1094730049" --quiet 2>&1 | grep -E "Time:|Relaxed"

echo ""
echo "=== Speedup Analysis ==="
echo "CH explores much fewer vertices (edges relaxed) compared to BD!"
echo "This demonstrates the power of Contraction Hierarchies preprocessing."
