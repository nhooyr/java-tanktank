package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

class Maze {
    private Group group = new Group();
    private Rectangle[][] horizontalSides = new Rectangle[COLUMNS][ROWS + 1];
    private Rectangle[][] verticalSides = new Rectangle[COLUMNS + 1][ROWS];

    // TODO maybe have a grid class?
    private Cell[][] grid;

    // TODO we export this because we want to place the tank not in the grid, instead we should randomly place the tank in a cell.
    protected final static double THICKNESS = Bullet.VELOCITY * 2;
    protected final static int ROWS = 8;
    protected final static int COLUMNS = 10;

    protected Maze(Group root) {
        makeGrid();
        eatGrid();
        drawGrid();
        root.getChildren().add(group);
    }

    private void makeGrid() {
        // TODO should be columns first because column is x value.
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
                if (cell.yummySides.size() > 1) {
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
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Cell cell = grid[i][j];
                Rectangle sideRight = cell.getSideRight();
                verticalSides[j + 1][i] = sideRight;

                Rectangle sideDown = cell.getSideDown();
                horizontalSides[j][i + 1] = sideDown;

                if (j == 0) {
                    Rectangle sideLeft = cell.getSideLeft();
                    verticalSides[j][i] = sideLeft;
                }
                if (i == 0) {
                    Rectangle sideUp = cell.getSideUp();
                    horizontalSides[j][i] = sideUp;
                }
            }
        }

        for (Rectangle[] sides : horizontalSides) {
            for (Rectangle side : sides) {
                if (side != null) {
                    group.getChildren().add(side);
                }
            }
        }
        for (Rectangle[] sides : verticalSides) {
            for (Rectangle side : sides) {
                if (side != null) {
                    group.getChildren().add(side);
                }
            }
        }
    }

    // Dirty hack because collision detection is expensive. See the Physics class.
    // This could probably be optimized even further based on the overlaps in sides but its more than
    // fast enough as it is and it is really difficult to understand. It is hard to explain
    // but what we are doing is finding the closest two horizontal and vertical sides and checking for
    // collisions against those.
    // This assumes that the object can only touch two horizontal or two vertical sides maximum at once.
    // We do not return early if a collision is detected because it is possible for multiple collisions to occur. TODO reevalute that assumption
    protected void handleBulletCollision(CollisionHandler obj) {
        Point2D objCenter = obj.getCenter();
        // Coordinates if the units were cells.
        double cellX = objCenter.getX() / Cell.LENGTH;
        double cellY = objCenter.getY() / Cell.LENGTH;

        // Closest column.
        int column = (int) Math.round(cellX);
        // Closest row.
        int row = (int) Math.round(cellY);

        ArrayList<Rectangle> sides = new ArrayList<>(2);

        if (column < COLUMNS) {
            Rectangle side = horizontalSides[column][row];
            if (side != null) {
                sides.add(side);
            }
        }

        // Let's try the horizontal side in the previous column.
        column--;
        if (column >= 0) {
            Rectangle side = horizontalSides[column][row];
            if (side != null) {
                sides.add(side);
            }
        }
        column++;

        if (row < ROWS) {
            Rectangle side = verticalSides[column][row];
            if (side != null) {
                sides.add(side);
            }
        }

        // Let's try the vertical side in the previous row.
        row--;
        if (row >= 0) {
            Rectangle side = verticalSides[column][row];
            if (side != null) {
                sides.add(side);
            }
        }
        row++;

        obj.handleCollision(sides);
    }

    protected interface CollisionHandler {
        Point2D getCenter();

        void handleCollision(ArrayList<Rectangle> sides);
    }
}