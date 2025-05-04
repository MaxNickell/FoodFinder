import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class APP extends JFrame {
    public ColorButtonPanel colorButtonPanel;
    public PathFindingButtonPanel pathFindingButtonPanel;
    public DrawingGrid drawingGrid;
    public final int GRID_ROWS = 30;
    public final int GRID_COLS = 60;
    private final int GRID_WIDTH = 1200;
    private final int GRID_HEIGHT = 600;
    private final int TOP_HEIGHT = 100;
    private final int BOTTOM_HEIGHT = 100;

    private final Color[][] gridColors = new Color[GRID_ROWS][GRID_COLS];
    private final Color[][] maskedColors = new Color[GRID_ROWS][GRID_COLS];

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color PATH_GREY = new Color(211, 211, 211);
    public static final Color GRASS_GREEN = new Color(102, 204, 0);
    public static final Color FOREST_GREEN = new Color(0, 100, 0);
    public static final Color SAND_BROWN = new Color(245, 222, 179);
    public static final Color MUD_BROWN = new Color(139, 69, 19);
    public static final Color WATER_BLUE = new Color(30, 144, 255);
    public static final Color FOOD_RED = new Color(255, 0, 0);
    public static final Color ANIMAL_BROWN = new Color(54,34,4);
    private Color currColor = GRASS_GREEN;
    private int brushSize = 1;
    private final int DELAY = 1000;
    private int speed = 1;
    private boolean animalPlaced = false;
    public int[] animalLocation = new int[] {-1, -1};
    public int[][] foodLocations;
    public int foodPlaced = 0;

    private boolean allowDrawing = true;



    public APP() {
        // Initializations
        setTitle("Mackee Terrain");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        clearGrid();

        // initialize panels
        drawingGrid = new DrawingGrid();
        colorButtonPanel = new ColorButtonPanel(drawingGrid);
        pathFindingButtonPanel = new PathFindingButtonPanel(this);

        // add panels
        add(colorButtonPanel, BorderLayout.NORTH);
        add(drawingGrid, BorderLayout.CENTER);
        add(pathFindingButtonPanel, BorderLayout.SOUTH);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void clearGrid() {
        // reset grid back to GRASS_GREEN
        animalPlaced = false;
        animalLocation = new int[] {-1, -1};
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLS; j++) {
                gridColors[i][j] = GRASS_GREEN;
                maskedColors[i][j] = GRASS_GREEN;
            }
        }
        updateFood();
    }

    private void updateFood() {
        // store food coordinates
        foodPlaced = 0;
        foodLocations = new int[GRID_ROWS * GRID_COLS][2];
        for (int i = 0; i < GRID_ROWS * GRID_COLS; i++) {
            foodLocations[i][0] = -1;
            foodLocations[i][1] = -1;
        }
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLS; j++) {
                if (gridColors[i][j] == FOOD_RED) {
                    foodLocations[foodPlaced][0] = i;
                    foodLocations[foodPlaced][1] = j;
                    foodPlaced++;
                }
            }
        }
    }

    private void updateColor(MouseEvent e) {
        // update color array and grid panel
        int cellWidth = GRID_WIDTH / GRID_COLS;
        int cellHeight = GRID_HEIGHT / GRID_ROWS;

        int col = e.getX() / cellWidth;
        int row = e.getY() / cellHeight;

        if (row >= 0 && row < GRID_ROWS && col >= 0 && col < GRID_COLS) {
            changeCellColor(row, col);
            repaint();
        }
    }

    private void paintPath(int row, int col) {
        // set brush size to 1 and paint a single cell
        brushSize = 1;
        changeCellColor(row, col);
        repaint();
    }

    private void randomizeGrid() {
        // generate a random grid
        Random random = new Random();
        Color[] colors = new Color[] {PATH_GREY, GRASS_GREEN, FOREST_GREEN, SAND_BROWN, MUD_BROWN, WATER_BLUE};
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLS; j++) {
                int rand = random.nextInt(6);
                currColor = colors[rand];
                brushSize = 1;
                changeCellColor(i, j);
            }
        }
        repaint();
    }

    private void changeCellColor(int row, int col) {
        // check if placing an animal
        updateAnimalPlaced();
        if (currColor == ANIMAL_BROWN && animalPlaced) {
            return;
        }
        if (currColor == ANIMAL_BROWN) {
            if (gridColors[row][col] == ANIMAL_BROWN || gridColors[row][col] == FOOD_RED) {
                // replacing a food or an animal with an animal -> keep the same mask
                maskedColors[row][col] = maskedColors[row][col];
            }
            else {
                if (gridColors[row][col] != WHITE) {
                    maskedColors[row][col] = gridColors[row][col];
                }
            }
            gridColors[row][col] = currColor;
            animalLocation = new int[] {row, col};
            animalPlaced = true;
        }
        else if (currColor == FOOD_RED) {
            if (gridColors[row][col] == ANIMAL_BROWN || gridColors[row][col] == FOOD_RED) {
                // replacing a food or an animal with a food -> keep the same mask
                maskedColors[row][col] = maskedColors[row][col];
            }
            else {
                maskedColors[row][col] = gridColors[row][col];
            }
            gridColors[row][col] = currColor;
        }
        else {
            // paint based on brush size
            for (int i = 0; i < brushSize; i++) {
                for (int j = 0; j < brushSize; j++) {
                    if (i + j < brushSize) {
                        try {
                            gridColors[row + i][col + j] = currColor;
                            gridColors[row + i][col - j] = currColor;
                            gridColors[row - i][col - j] = currColor;
                            gridColors[row - i][col + j] = currColor;
                            if (currColor != WHITE) {
                                maskedColors[row + i][col + j] = currColor;
                                maskedColors[row + i][col - j] = currColor;
                                maskedColors[row - i][col - j] = currColor;
                                maskedColors[row - i][col + j] = currColor;
                            }
                        } catch (IndexOutOfBoundsException ignore) {
                        }
                    }
                }
            }
        }
    }

    private boolean validateGrid() {
        if (!animalPlaced) {
            JOptionPane.showMessageDialog(null, "No animal placed!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        updateFood();
        if (foodPlaced == 0) {
            JOptionPane.showMessageDialog(null, "No food placed!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLS; j++) {
                if (gridColors[i][j] == WHITE) {
                    JOptionPane.showMessageDialog(null, "Not a valid map (grid cannot have white)!", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    private void updateAnimalPlaced() {
        // check if animal placed
        for (int i = 0; i < GRID_ROWS; i++) {
            for (int j = 0; j < GRID_COLS; j++) {
                if (gridColors[i][j] == ANIMAL_BROWN) {
                    animalPlaced = true;
                    return;
                }
            }
        }
        animalPlaced = false;
    }

    public Color[][] getMaskedColors() {
        return maskedColors;
    }

    private void eat(APP app) {
        if (!validateGrid()) {
            return;
        }
        app.pathFindingButtonPanel.eat.setEnabled(false);
        app.pathFindingButtonPanel.food.setEnabled(false);
        app.pathFindingButtonPanel.animal.setEnabled(false);
        app.colorButtonPanel.eraser.setEnabled(false);
        app.colorButtonPanel.randomize.setEnabled(false);
        app.colorButtonPanel.reset.setEnabled(false);
        app.colorButtonPanel.brushSizeSlider.setEnabled(false);
        app.colorButtonPanel.grassButton.setEnabled(false);
        app.colorButtonPanel.forestButton.setEnabled(false);
        app.colorButtonPanel.pathButton.setEnabled(false);
        app.colorButtonPanel.waterButton.setEnabled(false);
        app.colorButtonPanel.mudButton.setEnabled(false);
        app.colorButtonPanel.sandButton.setEnabled(false);
        allowDrawing = false;

        PathFinding pathFinding = new PathFinding(app, false);
        int[][] dsp = pathFinding.getDijkstraShortestPath();

        // save the colors of the old path
        Color[] oldPath = new Color[dsp.length];
        for (int i = 0; i < dsp.length; i++) {
            oldPath[i] = gridColors[dsp[i][0]][dsp[i][1]];
            if (gridColors[dsp[i][0]][dsp[i][1]] == ANIMAL_BROWN) {
                oldPath[i] = maskedColors[dsp[i][0]][dsp[i][1]];
            }
        }

        // set up a worker thread to do GUI updates
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int i = dsp.length - 1;
            @Override
            protected Void doInBackground() throws Exception {
                currColor = WHITE;
                while (i >= 0) {
                    publish();
                    int wait = DELAY / (2 * speed);
                    TimeUnit.MILLISECONDS.sleep(wait);
                }
                return null;
            }

            @Override
            protected void process(List<Void> chunks) {
                super.process(chunks);
                if (i >= 0) {
                    paintPath(dsp[i][0], dsp[i][1]);
                }
                i--;
            }

            @Override
            protected void done() {
                for (int i = dsp.length - 1; i >= 0; i--) {
                    currColor = oldPath[i];
                    if (currColor == FOOD_RED) {
                        if (pathFinding.getClosestFood() == dsp[i][0] * GRID_COLS + dsp[i][1]) {
                            currColor = ANIMAL_BROWN;
                        }
                    }
                    paintPath(dsp[i][0], dsp[i][1]);
                }
                app.pathFindingButtonPanel.eat.setEnabled(true);
                app.pathFindingButtonPanel.food.setEnabled(true);
                app.pathFindingButtonPanel.animal.setEnabled(true);

                app.colorButtonPanel.eraser.setEnabled(true);
                app.colorButtonPanel.randomize.setEnabled(true);
                app.colorButtonPanel.reset.setEnabled(true);
                app.colorButtonPanel.brushSizeSlider.setEnabled(true);
                app.colorButtonPanel.grassButton.setEnabled(true);
                app.colorButtonPanel.forestButton.setEnabled(true);
                app.colorButtonPanel.pathButton.setEnabled(true);
                app.colorButtonPanel.waterButton.setEnabled(true);
                app.colorButtonPanel.mudButton.setEnabled(true);
                app.colorButtonPanel.sandButton.setEnabled(true);

                allowDrawing = true;
            }
        };
        worker.execute();
    }

    private void eatAll(APP app) {
        if (!validateGrid()) {
            return;
        }

        // Show loading label before computation starts
        app.pathFindingButtonPanel.loadingLabel.setVisible(true);
        
        // Disable all buttons
        app.pathFindingButtonPanel.eat.setEnabled(false);
        app.pathFindingButtonPanel.eatAll.setEnabled(false);
        app.pathFindingButtonPanel.food.setEnabled(false);
        app.pathFindingButtonPanel.animal.setEnabled(false);
        app.colorButtonPanel.eraser.setEnabled(false);
        app.colorButtonPanel.randomize.setEnabled(false);
        app.colorButtonPanel.reset.setEnabled(false);
        app.colorButtonPanel.brushSizeSlider.setEnabled(false);
        app.colorButtonPanel.grassButton.setEnabled(false);
        app.colorButtonPanel.forestButton.setEnabled(false);
        app.colorButtonPanel.pathButton.setEnabled(false);
        app.colorButtonPanel.waterButton.setEnabled(false);
        app.colorButtonPanel.mudButton.setEnabled(false);
        app.colorButtonPanel.sandButton.setEnabled(false);
        allowDrawing = false;

        // Use SwingWorker to compute path in background
        SwingWorker<int[][], Void> pathWorker = new SwingWorker<>() {
            @Override
            protected int[][] doInBackground() throws Exception {
                PathFinding pathFinding = new PathFinding(app, true);
                return pathFinding.getHeldKarpTSP();
            }
            
            @Override
            protected void done() {
                try {
                    int[][] tour = get();
                    // Hide loading label once computation is complete
                    app.pathFindingButtonPanel.loadingLabel.setVisible(false);
                    // Start animation with the computed path
                    animateEatAll(app, tour);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Hide loading and re-enable UI in case of error
                    app.pathFindingButtonPanel.loadingLabel.setVisible(false);
                    enableAllControls(app);
                    allowDrawing = true;
                }
            }
        };
        pathWorker.execute();
    }
    
    private void animateEatAll(APP app, int[][] tour) {
        // This method contains the animation code that was previously in eatAll
        
        // Get PathFinding to access the closest food
        PathFinding pathFinding = new PathFinding(app, true);
        
        // save the colors of the old path
        Color[] oldPath = new Color[tour.length];
        for (int i = 0; i < tour.length; i++) {
            oldPath[i] = gridColors[tour[i][0]][tour[i][1]];
            if (gridColors[tour[i][0]][tour[i][1]] == FOOD_RED) {
                oldPath[i] = maskedColors[tour[i][0]][tour[i][1]];
            }
        }

        // set up a worker thread to do GUI updates
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int i = tour.length - 1;
            @Override
            protected Void doInBackground() throws Exception {
                currColor = WHITE;
                while (i >= 0) {
                    publish();
                    int wait = DELAY / (2 * speed);
                    TimeUnit.MILLISECONDS.sleep(wait);
                }
                return null;
            }

            @Override
            protected void process(List<Void> chunks) {
                super.process(chunks);
                if (i >= 0) {
                    paintPath(tour[i][0], tour[i][1]);
                }
                i--;
            }

            @Override
            protected void done() {
                for (int i = tour.length - 1; i >= 0; i--) {
                    currColor = oldPath[i];
                    if (currColor == FOOD_RED) {
                        if (pathFinding.getClosestFood() == tour[i][0] * GRID_COLS + tour[i][1]) {
                            currColor = ANIMAL_BROWN;
                        }
                    }
                    paintPath(tour[i][0], tour[i][1]);
                }
                enableAllControls(app);
                allowDrawing = true;
            }
        };
        worker.execute();
    }
    
    private void enableAllControls(APP app) {
        // Re-enable all buttons
        app.pathFindingButtonPanel.eat.setEnabled(true);
        app.pathFindingButtonPanel.eatAll.setEnabled(true);
        app.pathFindingButtonPanel.food.setEnabled(true);
        app.pathFindingButtonPanel.animal.setEnabled(true);

        app.colorButtonPanel.eraser.setEnabled(true);
        app.colorButtonPanel.randomize.setEnabled(true);
        app.colorButtonPanel.reset.setEnabled(true);
        app.colorButtonPanel.brushSizeSlider.setEnabled(true);
        app.colorButtonPanel.grassButton.setEnabled(true);
        app.colorButtonPanel.forestButton.setEnabled(true);
        app.colorButtonPanel.pathButton.setEnabled(true);
        app.colorButtonPanel.waterButton.setEnabled(true);
        app.colorButtonPanel.mudButton.setEnabled(true);
        app.colorButtonPanel.sandButton.setEnabled(true);
    }

    private class DrawingGrid extends JPanel {
        public DrawingGrid() {
            setPreferredSize(new Dimension(GRID_WIDTH, GRID_HEIGHT));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (allowDrawing) {
                        updateColor(e);
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (allowDrawing) {
                        updateColor(e);
                    }
                }
            });
        }

        // override the paint component to make a custom JPanel
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int cellWidth = this.getWidth() / GRID_COLS;
            int cellHeight = this.getHeight() / GRID_ROWS;

            for (int i = 0; i < GRID_ROWS; i++) {
                for (int j = 0; j < GRID_COLS; j++) {
                    // set the color to the color of the current cell
                    g.setColor(gridColors[i][j]);
                    // draw the grid
                    g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                    // set the color to black for the grid lines
                    g.setColor(BLACK);
                    // add gridlines
                    g.drawRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                }
            }
        }
    }

    private class ColorButtonPanel extends JPanel {
        public JButton pathButton;
        public JButton grassButton;
        public JButton forestButton;
        public JButton mudButton;
        public JButton sandButton;
        public JButton waterButton;
        public JButton eraser;
        public JButton reset;
        public JLabel brushSizeLabel;
        public JSlider brushSizeSlider;
        public JButton randomize;

        public ColorButtonPanel(DrawingGrid drawingGrid) {
            // set the layout
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(GRID_WIDTH, TOP_HEIGHT));

            // create components
            pathButton = new JButton("Path");
            pathButton.addActionListener(e -> currColor = PATH_GREY);
            grassButton = new JButton("Grass");
            grassButton.addActionListener(e -> currColor = GRASS_GREEN);
            forestButton = new JButton("Forest");
            forestButton.addActionListener(e -> currColor = FOREST_GREEN);
            mudButton = new JButton("Mud");
            mudButton.addActionListener(e -> currColor = MUD_BROWN);
            sandButton = new JButton("Sand");
            sandButton.addActionListener(e -> currColor = SAND_BROWN);
            waterButton = new JButton("Water");
            waterButton.addActionListener(e -> currColor = WATER_BLUE);
            eraser = new JButton("Eraser");
            eraser.addActionListener(e -> currColor = GRASS_GREEN);
            reset = new JButton("Reset");
            reset.addActionListener(e -> {
                clearGrid();
                drawingGrid.repaint();
            });
            brushSizeLabel = new JLabel("Brush Size: " + brushSize);
            brushSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, brushSize);
            brushSizeSlider.setMajorTickSpacing(1);
            brushSizeSlider.setPaintTicks(true);
            brushSizeSlider.setPaintLabels(true);
            brushSizeSlider.addChangeListener(e -> {
                brushSize = brushSizeSlider.getValue();
                brushSizeLabel.setText("Brush Size: " + brushSize);
            });
            randomize = new JButton("Randomize");
            randomize.addActionListener(e -> randomizeGrid());

            // Add the components to the panel
            add(pathButton);
            add(grassButton);
            add(forestButton);
            add(mudButton);
            add(sandButton);
            add(waterButton);
            add(eraser);
            add(reset);
            add(brushSizeLabel);
            add(brushSizeSlider);
            add(randomize);
        }
    }

    private class PathFindingButtonPanel extends JPanel {
        public JButton food;
        public JButton animal;
        public JButton eat;
        public JButton eatAll;
        public JLabel speedSliderLabel;
        public JSlider speedSlider;
        public JLabel loadingLabel;
        public PathFindingButtonPanel(APP app) {
            // set the layout
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(GRID_WIDTH, BOTTOM_HEIGHT));

            // create components
            food = new JButton("Food");
            food.addActionListener(e -> currColor = FOOD_RED);
            animal = new JButton("Animal");
            animal.addActionListener(e -> currColor = ANIMAL_BROWN);
            eat = new JButton("Eat");
            eat.addActionListener(e -> {
                eat(app);
            });
            eatAll = new JButton("Eat All");
            eatAll.addActionListener(e -> {
                eatAll(app);
            });
            speedSliderLabel = new JLabel("Speed: " + speed);
            speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, brushSize);
            speedSlider.setMajorTickSpacing(1);
            speedSlider.setPaintTicks(true);
            speedSlider.setPaintLabels(true);
            speedSlider.addChangeListener(e -> {
                speed = speedSlider.getValue() * speedSlider.getValue();
                speedSliderLabel.setText("Speed: " + speed);
            });
            
            // Create loading label
            loadingLabel = new JLabel("Computing optimal path...");
            loadingLabel.setForeground(Color.BLACK);
            loadingLabel.setFont(new Font("Arial", Font.BOLD, 14));
            loadingLabel.setVisible(false);

            // Add the components to the panel
            add(food);
            add(animal);
            add(eat);
            add(eatAll);
            add(speedSliderLabel);
            add(speedSlider);
            add(loadingLabel);
        }
    }

}
