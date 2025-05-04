# FoodFinder

A Java-based simulation that demonstrates pathfinding algorithms through an interactive terrain editor. This application simulates an animal searching for food across various terrains using Dijkstra's algorithm for single food finding and the Held-Karp algorithm (solution to the Traveling Salesman Problem) for optimal multi-food collection.

## Features

- **Interactive Terrain Editor**: Draw different terrain types with adjustable brush sizes
- **Pathfinding Visualization**: Watch an animal navigate to food using optimal paths
- **Multiple Algorithms**:
  - Dijkstra's algorithm for single target pathfinding
  - Held-Karp algorithm for solving the Traveling Salesman Problem
  - Floyd-Warshall algorithm for all-pairs shortest paths (Graph Condensing)
- **Terrain Randomization**: Generate random terrain layouts

## Terrain Types

Different terrain types have different traversal costs:
- Path (Grey): 1
- Grass (Green): 5
- Sand (Brown): 10
- Forest (Dark Green): 15
- Mud (Dark Brown): 20
- Water (Blue): 25

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Running the Application

1. Compile the Java files:
   ```
   javac src/*.java -d out/FoodFinder
   ```

2. Run the application:
   ```
   java -cp out/FoodFinder Main
   ```

## Usage

1. Use the color buttons at the top to select terrain types
2. Adjust brush size with the slider
3. Place an animal (brown) and food (red) on the grid using the bottom panel buttons
4. Click "Eat" to find the shortest path to the nearest food
5. Click "Eat All" to find the optimal route to collect all food
6. Adjust the simulation speed using the slider

## Implementation Details

- **src/APP.java**: Main application interface and control logic
- **src/PathFinding.java**: Implementation of pathfinding algorithms
- **src/Node.java**: Data structure for grid points
- **src/CondensedGraph.java**: Helper for the Traveling Salesman Problem solution
- **src/PriorityQueue.java**: Custom priority queue for Dijkstra's algorithm
- **src/Main.java**: Application entry point 