import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class CondensedGraph {
    private Double[][] adjacencyMatrix;
    // used to recover paths;
    private PathBlock[][] paths;
    // no need for adjacency list because completely connected
    private Node[] vertices;

    private final int N;

    public CondensedGraph(int[][] next, int[][] costs, APP app) {
        N = app.foodPlaced + 1;
        initAdjacencyMatrix(costs, app);
        initPaths(next, app);

    }

    private void initAdjacencyMatrix(int[][] costs, APP app) {
        adjacencyMatrix = new Double[app.foodPlaced + 1][app.foodPlaced + 1];
        adjacencyMatrix[0][0] = (double) 0;
        for (int i = 1; i < app.foodPlaced + 1; i++) {
            int index = app.animalLocation[0] * app.GRID_COLS + app.animalLocation[1];
            int foodIndex = app.foodLocations[i - 1][0] * app.GRID_COLS + app.foodLocations[i - 1][1];
            adjacencyMatrix[0][i] = (double) costs[index][foodIndex];
            adjacencyMatrix[i][0] = (double) costs[index][foodIndex];
        }
        for (int i = 1; i < app.foodPlaced + 1; i++) {
            int index = app.foodLocations[i - 1][0] * app.GRID_COLS + app.foodLocations[i - 1][1];
            for (int j = 1; j < app.foodPlaced + 1; j++) {
                int foodIndex = app.foodLocations[j - 1][0] * app.GRID_COLS + app.foodLocations[j - 1][1];
                adjacencyMatrix[i][j] = (double) costs[index][foodIndex];
            }
        }
    }

    private void initPaths(int[][] next, APP app) {
        int GRID_COLS = app.GRID_COLS;
        paths = new PathBlock[app.foodPlaced + 1][app.foodPlaced + 1];

        // handle animal base case
        PathBlock animalStart = new PathBlock(app.animalLocation[0] * app.GRID_COLS + app.animalLocation[1], GRID_COLS);
        paths[0][0] = animalStart;
        for (int i = 1; i < app.foodPlaced + 1; i++) {
            int index = app.animalLocation[0] * app.GRID_COLS + app.animalLocation[1];
            int temp = index;
            int foodIndex = app.foodLocations[i - 1][0] * app.GRID_COLS + app.foodLocations[i - 1][1];
            paths[0][i] = new PathBlock(app.animalLocation[0] * app.GRID_COLS + app.animalLocation[1], GRID_COLS);
            PathBlock curr = paths[0][i];
            while (index != foodIndex) {
                index = next[index][foodIndex];
                PathBlock newPathBlock = new PathBlock(index, GRID_COLS);
                curr.next = newPathBlock;
                curr = newPathBlock;
            }
            index = temp;
            curr = new PathBlock(foodIndex, GRID_COLS);
            paths[i][0] = curr;
            while (foodIndex != index){
                foodIndex = next[foodIndex][index];
                PathBlock newPathBlock = new PathBlock(foodIndex, GRID_COLS);
                curr.next = newPathBlock;
                curr = newPathBlock;
            }

        }

        for (int i = 1; i < app.foodPlaced + 1; i++) {
            int start = app.foodLocations[i - 1][0] * app.GRID_COLS + app.foodLocations[i - 1][1];
            int temp = start;
            for (int j = 1; j < app.foodPlaced + 1; j++) {
                start = temp;
                int dest = app.foodLocations[j - 1][0] * app.GRID_COLS + app.foodLocations[j - 1][1];
                paths[i][j] = new PathBlock(start, GRID_COLS);
                PathBlock curr = paths[i][j];
                while (start != dest) {
                    start = next[start][dest];
                    PathBlock newPathBlock = new PathBlock(start, GRID_COLS);
                    curr.next = newPathBlock;
                    curr = newPathBlock;
                }
            }
        }
    }

    public Double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public int getN() {
        return N;
    }

    public List<Integer> getPath(int start, int dest) {
        List<Integer> path = new ArrayList<Integer>();
        PathBlock p = paths[start][dest];
        p = p.next;
        while (p != null) {
            path.add(p.index);
            p = p.next;
        }
        return path;
    }

    public int getStart() {
        return paths[0][0].index;
    }

    private static class PathBlock {
        PathBlock next;
        int row;
        int col;
        int index;

        public PathBlock(int index, int GRID_COLS) {
            next = null;
            this.index = index;
            row = index / GRID_COLS;
            col = index % GRID_COLS;
        }
    }
}
