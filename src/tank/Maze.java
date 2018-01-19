package tank;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

class Maze {
    private Group group;

    // TODO maybe have a grid class?
    private Cell[][] grid;

    // TODO we export this because we want to place the tank not in the grid, instead we should randomly place the tank in a cell.
    protected final static int THICKNESS = Tank.HEAD_HEIGHT;
    protected final static int ROWS = 8;
    protected final static int COLUMNS = 10;

    private void makeGrid() {
        grid = new Cell[ROWS][COLUMNS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                MutableBoolean left = new MutableBoolean();
                // If we are not in the first column, then the left of the current cell is the right of the one left.
                if (j != 0) {
                    left = grid[i][j - 1].right;
                }

                MutableBoolean up = new MutableBoolean();
                // If we are not in the first row, then the up of the current cell is the down of the one above.
                if (i != 0) {
                    up = grid[i - 1][j].down;
                }

                grid[i][j] = new Cell(i, j, up, left);
            }
        }
    }

    private ArrayList<Cell> getYummyCells() {
        ArrayList<Cell> yummyCells = new ArrayList<>(ROWS * COLUMNS);
        for (Cell[] cells : grid) {
            for (Cell cell : cells) {
                if (cell.yummySides.size() > 2) {
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

            int i = rand.nextInt(cell.yummySides.size());
            MutableBoolean side = cell.yummySides.get(i);
            side.value = false;
            cell.yummySides.remove(i);
        }
    }

    protected void drawGrid() {
        group = new Group();
        for (Cell[] cells : grid) {
            for (Cell cell : cells) {
                group.getChildren().add(cell.group());
            }
        }
    }

    protected Maze(Group root) {
        makeGrid();
        eatGrid();
        drawGrid();
        root.getChildren().add(group);
    }
}

class MutableBoolean {

    public MutableBoolean() {
        this.value = true;
    }

    protected boolean value;
}

class Cell {
    protected final static int LENGTH = 3 * Tank.HEIGHT;

    // TODO back to private
    protected int row;
    protected int column;

    // True means the side is opaque.
    private MutableBoolean up;
    private MutableBoolean left;
    // Protected so that the grid constructor can access.
    protected MutableBoolean right;
    protected MutableBoolean down;

    protected ArrayList<MutableBoolean> yummySides;

    protected Cell(int row, int column, MutableBoolean up, MutableBoolean left) {
        this.row = row;
        this.column = column;

        this.up = up;
        this.left = left;
        this.right = new MutableBoolean();
        this.down = new MutableBoolean();

        makeYummySides();
    }

    private void makeYummySides() {
        yummySides = new ArrayList<>();

        // If up is true this cell is not at the top row then it is yummy.
        if (up.value && row != 0) {
            yummySides.add(up);
        }

        // If right is true and this cell is not at the right edge then it is yummy.
        if (right.value && column != Maze.COLUMNS - 1) {
            yummySides.add(right);
        }

        // If down is true and this cell is not at the bottom row then it is yummy.
        if (down.value && row != Maze.ROWS - 1) {
            yummySides.add(down);
        }

        // If left is true and this cell is not at the left edge then it is yummy.
        if (left.value && column != 0) {
            yummySides.add(left);
        }
    }

    // The length of the rectangles in the group is extended by the thickness because we want overlap in case
    // one of the sides of a vertex is false, then the middle part won't disappear.
    protected Group group() {
        Group group = new Group();

        int x = column * Cell.LENGTH;
        int y = row * Cell.LENGTH;

        if (up.value) {
            Rectangle up = new Rectangle(x, y, Cell.LENGTH + Maze.THICKNESS, Maze.THICKNESS);
            up.setFill(Color.BLACK);
            group.getChildren().add(up);
        }

        if (left.value) {
            Rectangle left = new Rectangle(x, y, Maze.THICKNESS, Cell.LENGTH + Maze.THICKNESS);
            left.setFill(Color.BLACK);
            group.getChildren().add(left);
        }

        if (row == Maze.ROWS - 1) {
            Rectangle down = new Rectangle(x, y + Cell.LENGTH, Cell.LENGTH + Maze.THICKNESS, Maze.THICKNESS);
            down.setFill(Color.BLACK);
            group.getChildren().add(down);
        }

        if (column == Maze.COLUMNS - 1) {
            Rectangle right = new Rectangle(x + Cell.LENGTH, y, Maze.THICKNESS, Cell.LENGTH + Maze.THICKNESS);
            right.setFill(Color.BLACK);
            group.getChildren().add(right);
        }

        return group;
    }
}