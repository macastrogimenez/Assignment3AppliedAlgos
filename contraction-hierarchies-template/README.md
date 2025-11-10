# Template Contraction Hierarchies Assignment

This repository presents a Java-based template to start with the contraction hierarchies assignment in Applied algorithms. 

## Build

The project uses gradle. Carry out the tests using `gradle test` and build the jar file using `gradle jar`. 

The graph is read from standard input and a test graph `test.graph` is provided in this repository. 

Basic file reading is already implemented in the Main class. An example run is 

```
gradle jar
java -jar app/build/libs/app.jar < test.graph
```

(Note that we simulate undirected edges by adding directed edges in both directions, so the count that is printed is twice as large as the number in the graph provided.)

## Code structure

We have implemented basic functionality such as graph reading, a graph data structure using HashMaps, and a basic Dijkstra implementation that keeps track of visited vertices using HashSets.  Basic unit tests for the Dijkstra implementation are available as well. 

To solve the assignment, you probably need to update the API by changing method signatures to incorporate additional functionality.

## How to run the Program

From Gradle:
```
bash

# From the project root
gradle run --args="app/denmark.graph BD 1096800199 1124791586" # for Bidirectional Dijkstra
gradle run --args="app/denmark.graph D 115739 115775" # for Dijkstra
gradle run --args="preprocessedDk CH 115739 115775"

# Or
gradle run --args="test.graph BD 0 3" # for Bidirectional Dijkstra
```

From JAR file:
```
bash
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
