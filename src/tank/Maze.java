package tank;

import javafx.scene.Group;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.Random;

class Maze {
    private Group group;

    private Cell[][] grid;

    // TODO we export this because we want to place the tank not in the grid, instead we should randomly place the tank in a cell.
    protected final static double THICKNESS = Tank.HEAD_HEIGHT;
    protected final static int ROWS = 16;
    protected final static int COLUMNS = 20;

    private void makeGrid() {
        grid = new Cell[ROWS][COLUMNS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Cell cell = new Cell();
                grid[i][j] = cell;

                // If we are not in the first column, then the left of the current cell is the right of the one left.
                if (j != 0) {
                    cell.left = grid[i][j - 1].right;
                }

                // If we are not in the first row, then the up of the current cell is the down of the one above.
                if (i != 0) {
                    cell.up = grid[i - 1][j].down;
                }
            }
        }
    }

    private ArrayList<Cell> getYummyCells() {
        ArrayList<Cell> yummyCells = new ArrayList<>(ROWS * COLUMNS);
        for (Cell[] cells : grid) {
            for (Cell cell : cells) {
                if (cell.yummyness() >= 2) {
                    yummyCells.add(cell);
                }
            }
        }
        return yummyCells;
    }

    private void eatGrid() {
        Random rand = new Random();
        while (true) {
            ArrayList<Cell> yummyCells = getYummyCells();
            if (yummyCells.isEmpty()) {
                return;
            }
            Cell cell = yummyCells.get(rand.nextInt(yummyCells.size()));


        }
    }

    protected Maze(Group root, Scene scene) {
        makeGrid();

        eatGrid();

        group = new Group();
        root.getChildren().add(group);
    }
}

class MutableBoolean {
    protected boolean value;
}

class Cell {
    protected final static int LENGTH = 40;

    private int row;
    private int column;

    protected Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.up = new MutableBoolean();
        this.right = new MutableBoolean();
        this.down = new MutableBoolean();
        this.left = new MutableBoolean();
    }

    protected int yummyness() {
        int yummyness = 0;
        if (up.value) {
            yummyness++;
        }
        if (right.value) {
            yummyness++;
        }
        if (down.value) {
            yummyness++;
        }
        if (left.value) {
            yummyness++;
        }
        return yummyness;
    }

    protected MutableBoolean up;
    protected MutableBoolean right;
    protected MutableBoolean down;
    protected MutableBoolean left;
}