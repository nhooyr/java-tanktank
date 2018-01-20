package tank;

import javafx.scene.Group;

import java.util.ArrayList;
import java.util.Random;

class Maze {
    private Group group;

    // TODO maybe have a grid class?
    private Cell[][] grid;

    // TODO we export this because we want to place the tank not in the grid, instead we should randomly place the tank in a cell.
    protected final static int THICKNESS = (int) (Bullet.VELOCITY) * 4;
    protected final static int ROWS = 8;
    protected final static int COLUMNS = 10;

    protected Maze(Group root) {
        makeGrid();
        eatGrid();
        drawGrid();
        root.getChildren().add(group);
    }

    private void makeGrid() {
        grid = new Cell[ROWS][COLUMNS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Cell.MutableBoolean left = new Cell.MutableBoolean();
                // If we are not in the first column, then the left of the current cell is the right of the one left.
                if (j != 0) {
                    left = grid[i][j - 1].right;
                }

                Cell.MutableBoolean up = new Cell.MutableBoolean();
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
            Cell.MutableBoolean side = cell.yummySides.get(i);
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

    protected enum CollisionStatus {
        OK,
        VERTICAL,
        HORIZONTAL,
    }

    protected CollisionStatus checkBulletCollision(Bullet bullet) {
        // A clever way to optimize this potentially in the future would be to calculate the cell that object being
        // checked for collision against is in and then only check for collision against its sides.
        for (Cell[] cells : grid) {
            for (Cell cell : cells) {
                CollisionStatus result = cell.checkBulletCollision(bullet);
                if (result != CollisionStatus.OK) {
                    return result;
                }
            }
        }
        return CollisionStatus.OK;
    }
}

