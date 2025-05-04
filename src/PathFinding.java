import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathFinding {
    private final Node[] adjacencyList;
    // special adjacency matrix UP, DOWN, LEFT, RIGHT
    private final int[][] adjacencyMatrix;
    private final Node[] vertices;
    private final int GRID_ROWS;
    private final int GRID_COLS;
    private int[][] dijkstraShortestPath;
    private int closestFood = -1;

    private int[][] heldKarpTSP;


    public PathFinding(APP app, boolean allFood) {
        GRID_ROWS = app.GRID_ROWS;
        GRID_COLS = app.GRID_COLS;
        adjacencyList = new Node[app.GRID_ROWS * app.GRID_COLS];
        adjacencyMatrix = new int[app.GRID_COLS * app.GRID_ROWS][4];
        vertices = new Node[app.GRID_ROWS * app.GRID_COLS];
        dijkstraShortestPath = null;

        initVertices(app);
        initAdjacency(app);

        if (allFood) {
            int[][][] floydWarshallResult = floydWarshall(adjacencyMatrix, app);
            int[][] costs = floydWarshallResult[0];
            int[][] next = floydWarshallResult[1];
            CondensedGraph condensedGraph = new CondensedGraph(next, costs, app);
            List<Integer> minCostTour = heldKarp(condensedGraph.getAdjacencyMatrix(), condensedGraph.getN());
            heldKarpTSP = reconstructMinCostTour(minCostTour, condensedGraph);
        }
        else {
            dijkstraShortestPath = dijkstra(app);
        }
    }

    private void initVertices(APP app) {
        // initialize weights
        Color[][] maskedColors = app.getMaskedColors();
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLS; j++) {
                Color c = maskedColors[i][j];
                vertices[i * app.GRID_COLS + j] = new Node(getWeight(c), i, j);
            }
        }
    }
    private void initAdjacency(APP app) {
        // initialize adjacency list and adjacency matrix
        for (int i = 0; i < adjacencyList.length; i++) {
            Node left = null;
            Node right = null;
            Node up = null;
            Node down = null;

            int upWeight = Integer.MAX_VALUE;
            int downWeight = Integer.MAX_VALUE;
            int leftWeight = Integer.MAX_VALUE;
            int rightWeight = Integer.MAX_VALUE;

            if (i % app.GRID_COLS != 0) {
                leftWeight = vertices[i - 1].getWeight();
                int row = vertices[i - 1].getRow();
                int col = vertices[i - 1].getCol();
                left = new Node(leftWeight, row, col);
            }
            if ((i + 1) % app.GRID_COLS != 0) {
                rightWeight = vertices[i + 1].getWeight();
                int row = vertices[i + 1].getRow();
                int col = vertices[i + 1].getCol();
                right = new Node(rightWeight, row, col);
            }
            if (i - app.GRID_COLS >= 0) {
                upWeight = vertices[i - app.GRID_COLS].getWeight();
                int row = vertices[i - app.GRID_COLS].getRow();
                int col = vertices[i - app.GRID_COLS].getCol();
                up = new Node(upWeight, row, col);
            }
            if (i + app.GRID_COLS < adjacencyList.length) {
                downWeight = vertices[i + app.GRID_COLS].getWeight();
                int row = vertices[i + app.GRID_COLS].getRow();
                int col = vertices[i + app.GRID_COLS].getCol();
                down = new Node(downWeight, row, col);
            }

            // add to the adjacency list
            adjacencyList[i] = addToList(adjacencyList[i], left);
            adjacencyList[i] = addToList(adjacencyList[i], right);
            adjacencyList[i] = addToList(adjacencyList[i], up);
            adjacencyList[i] = addToList(adjacencyList[i], down);

            // add to adjacency matrix
            adjacencyMatrix[i][0] = upWeight;
            adjacencyMatrix[i][1] = downWeight;
            adjacencyMatrix[i][2] = leftWeight;
            adjacencyMatrix[i][3] = rightWeight;
        }
    }

    private Node addToList(Node head, Node newNode) {
        if (newNode != null) {
            newNode.setNext(head);
            return newNode;
        }
        return head;
    }

    private int getWeight(Color c) {
        if (c == APP.PATH_GREY) {
            return 1;
        }
        else if (c == APP.GRASS_GREEN) {
            return 5;
        }
        else if (c == APP.SAND_BROWN) {
            return 10;
        }
        else if (c == APP.FOREST_GREEN) {
            return 15;
        }
        else if (c == APP.MUD_BROWN) {
            return 20;
        }
        else if (c == APP.WATER_BLUE) {
            return 25;
        }
        else {
            return -1;
        }
    }

    private int[][][] floydWarshall(int[][] adjacencyMatrix, APP gui) {
        int graphSize = gui.GRID_ROWS * gui.GRID_COLS;
        int[][] d = new int[graphSize][graphSize];
        int[][] n = new int[graphSize][graphSize];

        // initialization
        for (int i = 0; i < graphSize; i++) {
            int upIndex = i - gui.GRID_COLS;
            int downIndex = i + gui.GRID_COLS;
            int leftIndex = i - 1;
            int rightIndex = i + 1;
            for (int j = 0; j < graphSize; j++) {
                d[i][j] = Integer.MAX_VALUE;
                if (i == j) {
                    d[i][j] = 0;
                }
                n[i][j] = -1;
            }
            // up
            if (upIndex >= 0) {
                d[i][upIndex] = adjacencyMatrix[i][0];
                n[i][upIndex] = upIndex;
            }
            // down
            if (downIndex < graphSize) {
                d[i][downIndex] = adjacencyMatrix[i][1];
                n[i][downIndex] = downIndex;
            }
            // left
            if (leftIndex >= 0) {
                d[i][leftIndex] = adjacencyMatrix[i][2];
                n[i][leftIndex] = leftIndex;
            }
            // right
            if (rightIndex < graphSize) {
                d[i][rightIndex] = adjacencyMatrix[i][3];
                n[i][rightIndex] = rightIndex;
            }

        }

        // dynamic programming
        for (int k = 0; k < graphSize; k++) {
            for (int i = 0; i < graphSize; i++) {
                for (int j = 0; j < graphSize; j++) {
                    // avoid Integer Overflow
                    if (d[i][k] != Integer.MAX_VALUE && d[k][j] != Integer.MAX_VALUE) {
                        if (d[i][k] + d[k][j] < d[i][j]) {
                            d[i][j] = d[i][k] + d[k][j];
                            n[i][j] = n[i][k];
                        }
                    }
                }
            }
        }

        return new int[][][] {d, n};
    }


    private int[][] dijkstra(APP gui) {
        int graphSize = GRID_ROWS * GRID_COLS;
        int animalIndex = gui.animalLocation[0] * gui.GRID_COLS + gui.animalLocation[1];

        // initialize distances to infinity
        int[] d = new int[graphSize];
        for (int i = 0; i < graphSize; i++) {
            d[i] = Integer.MAX_VALUE;
        }
        d[animalIndex] = 0;

        // initialize parents to null
        Node[] p = new Node[graphSize];
        for (int i = 0; i < graphSize; i++) {
            p[i] = null;
        }
        p[animalIndex] = new Node(0, -1,-1);

        // create a new priority queue
        PriorityQueue q = new PriorityQueue(graphSize);
        q.addToQueue(vertices[animalIndex]);

        // while queue is not empty
        while (q.getCurrSize() != 0) {
            // get the minimum node
            Node curr = q.deleteMin();
            int index = curr.getRow() * GRID_COLS + curr.getCol();

            Node currAdj = adjacencyList[index];
            int currAdjIndex;
            while (currAdj != null) {
                currAdjIndex = currAdj.getRow() * GRID_COLS + currAdj.getCol();
                int newD = d[index] + currAdj.getWeight();
                if (newD < 0) {
                    newD = Integer.MAX_VALUE;
                }
                if (newD < d[currAdjIndex]) {
                    d[currAdjIndex] = newD;
                    p[currAdjIndex] = vertices[index];
                    if (vertices[currAdjIndex].getInQueue()) {
                        q.updateQueue(vertices[currAdjIndex], newD);
                    }
                    else {
                        vertices[currAdjIndex].setWeight(newD);
                        q.addToQueue(vertices[currAdjIndex]);
                    }
                }
                currAdj = currAdj.getNext();
            }
        }
        // find the closest food
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < gui.foodPlaced; i++) {
            int index = gui.foodLocations[i][0] * GRID_COLS + gui.foodLocations[i][1];
            if (d[index] < min) {
                min = d[index];
                minIndex = index;
           }
        }
        closestFood = minIndex;

        // recover path
        int source = gui.animalLocation[0] * GRID_COLS + gui.animalLocation[1];
        int curr_index = 0;
        if (p[closestFood] == null) {
            return null;
        }
        int curr = closestFood;
        while (curr != source) {
            curr_index++;
            curr = p[curr].getRow() * GRID_COLS + p[curr].getCol();
        }
        int[][] path = new int[curr_index + 1][2];
        for (int i = 0; i < curr_index + 1; i++) {
            path[i][0] = -1;
            path[i][1] = -1;
        }
        curr = closestFood;
        curr_index = 0;
        while (curr != source) {
            path[curr_index][0] = vertices[curr].getRow();
            path[curr_index][1] = vertices[curr].getCol();
            curr_index++;
            curr = p[curr].getRow() * gui.GRID_COLS + p[curr].getCol();
        }
        path[curr_index][0] = vertices[source].getRow();
        path[curr_index][1] = vertices[source].getCol();
        return path;
    }

    private List<Integer> heldKarp(Double[][] dist, int n) {
        final int END_STATE = (1 << n) - 1;
        Double[][] memo = new Double[n][1 << n];

        // add all outgoing edges from starting node to memo table
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                continue;
            }
            memo[i][(1) | (1 << i)] = dist[0][i];
        }

        // dynamic programming part
        for (int i = 3; i <= n; i++) {
            for (int subset : combinations(i, n)) {
                if (notIn(0, subset)) {
                    continue;
                }
                for (int next = 0; next < n; next++) {
                    if (next == 0 || notIn(next, subset)) {
                        continue;
                    }
                    int subsetWithoutNext = subset ^ (1 << next);
                    double minDist = Double.POSITIVE_INFINITY;
                    for (int end = 0; end < n; end++) {
                        if (end == 0 || end == next || notIn(end, subset)) {
                            continue;
                        }
                        double newDist = memo[end][subsetWithoutNext] + dist[end][next];
                        if (newDist < minDist) {
                            minDist = newDist;
                        }
                    }
                    memo[next][subset] = minDist;
                }
            }
        }

        // Connect tour back to starting node and minimize cost
        double minTourCost = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                continue;
            }
            double tourCost = memo[i][END_STATE] + dist[i][0];
            if (tourCost < minTourCost) {
                minTourCost = tourCost;
            }
        }
        int lastIndex = 0;
        int state = END_STATE;

        List<Integer> tour = new ArrayList<>();
        tour.add(0);

        // reconstruct path from memo table
        for (int i = 1; i < n; i++) {
            int bestIndex = -1;
            double bestDist = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                if (j == 0 || notIn(j, state)) {
                    continue;
                }
                double newDist = memo[j][state] + dist[j][lastIndex];
                if (newDist < bestDist) {
                    bestIndex = j;
                    bestDist = newDist;
                }
            }
            tour.add(bestIndex);
            state = state ^ (1 << bestIndex);
            lastIndex = bestIndex;
        }

        tour.add(0);
        Collections.reverse(tour);
        return tour;
    }

    private int[][] reconstructMinCostTour(List<Integer> ordering, CondensedGraph condensedGraph) {
        int prev = -1;
        List<Integer> finalPath = new ArrayList<>();
        finalPath.add(condensedGraph.getStart());
        for (Integer node : ordering) {
            if (node == 0 && prev == -1) {
                prev = 0;
                continue;
            }
            finalPath.addAll(condensedGraph.getPath(prev, node));
            prev = node;
        }
        return convertMinCostTour(finalPath);
    }

    private int[][] convertMinCostTour(List<Integer> path) {
        int n = path.size();
        int[][] finalPath = new int[n][2];
        for (int i = 0; i < n; i++) {
            finalPath[i][0] = path.get(i) / GRID_COLS;
            finalPath[i][1] = path.get(i) % GRID_COLS;
        }
        return finalPath;
    }

    public int[][] getHeldKarpTSP() {
        return heldKarpTSP;
    }

    private List<Integer> combinations(int i, int n) {
        List<Integer> subsets = new ArrayList<>();
        combinationsHelper(0, 0, i, n, subsets);
        return subsets;
    }

    private void combinationsHelper(int set, int at, int i, int n, List<Integer> subsets) {
        // return early if there are more elements left to select than what is available
        int elementsLeftToPick = n - at;
        if (elementsLeftToPick < i) {
            return;
        }

        if (i == 0) {
            subsets.add(set);
        }
        else {
            for (int j = at; j < n; j++) {
                // try including this element
                set ^= 1 << j;

                combinationsHelper(set, j + 1, i - 1, n, subsets);

                // Backtrack and try the instance where we did not include this element
                set ^= (1 << j);
            }
        }
    }

    private boolean notIn(int elem, int subset) {
        return ((1 << elem) & subset) == 0;
    }

    public int[][] getDijkstraShortestPath() {
        return dijkstraShortestPath;
    }

    public int getClosestFood() {
        return closestFood;
    }

}
