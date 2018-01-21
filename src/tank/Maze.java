package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

class Maze {
    private Group group = new Group();
    private Rectangle[][] horizontalSides = new Rectangle[COLUMNS][ROWS + 1];
    private Rectangle[][] verticalSides = new Rectangle[COLUMNS + 1][ROWS];

    private Cell[][] grid = new Cell[COLUMNS][ROWS];

    // These three are used in various places.
    protected final static double THICKNESS = Bullet.VELOCITY * 2;
    protected final static int ROWS = 8;
    protected final static int COLUMNS = 10;

    protected Maze() {
        makeGrid();
        eatGrid();
        drawGrid();
    }

    protected Node getNode() {
        return group;
    }

    private void makeGrid() {
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Cell.MutableBoolean left = new Cell.MutableBoolean();
                // If we are not in the first column, then the left of the current cell is the right of the one left.
                if (i > 0) {
                    left = grid[i - 1][j].getRight();
                }

                Cell.MutableBoolean up = new Cell.MutableBoolean();
                // If we are not in the first row, then the up of the current cell is the down of the one above.
                if (j > 0) {
                    up = grid[i][j - 1].getDown();
                }

                grid[i][j] = new Cell(i, j, up, left);
            }
        }
    }

    private ArrayList<Cell> getYummyCells() {
        ArrayList<Cell> yummyCells = new ArrayList<>();
        for (Cell[] cells : grid) {
            for (Cell cell : cells) {
                if (cell.isYummy()) {
                    yummyCells.add(cell);
                }
            }
        }
        return yummyCells;
    }

    // The way we generate the maze is by eating the grid. We select a random cell that is "yummy", meaning it has enough
    // removable sides. We remove one of its sides randomly and then based on which side we removed, we move into the adjacent
    // cell and repeat. Once the cell we move into is not yummy, we select another random cell from the rest of the grid that is
    // yummy and then repeat.
    // A cell is yummy if it has more than two eatable sides. If the cell lies on the outer ring of cells, then it is yummy
    // if it has more than one eatable side. See isYummy() on the Cell class.
    // Why all of this? I am not sure if any of it is meaningful. It was just intuition and I like the mazes it generates.
    // The mazes are very open and allow for diverse strategy.
    private void eatGrid() {
        Random rand = new Random();
        ArrayList<Cell> yummyCells = getYummyCells();
        Cell cell = yummyCells.get(rand.nextInt(yummyCells.size()));
        while (true) {
            int i = rand.nextInt(cell.getYummySides().size());
            Cell.MutableBoolean side = cell.getYummySides().get(i);

            side.value = false;
            cell.getYummySides().remove(i);

            if (cell.getUp() == side) {
                cell = grid[cell.getColumn()][cell.getRow() - 1];
            } else if (cell.getRight() == side) {
                cell = grid[cell.getColumn() + 1][cell.getRow()];
            } else if (cell.getDown() == side) {
                cell = grid[cell.getColumn()][cell.getRow() + 1];
            } else if (cell.getLeft() == side) {
                cell = grid[cell.getColumn() - 1][cell.getRow()];
            }

            if (!cell.isYummy()) {
                yummyCells = getYummyCells();
                if (yummyCells.size() == 0) {
                    return;
                }
                cell = yummyCells.get(rand.nextInt(yummyCells.size()));
            }
        }
    }

    private void drawGrid() {
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Cell cell = grid[i][j];

                Rectangle sideRight = cell.getSideRight();
                verticalSides[i + 1][j] = sideRight;

                Rectangle sideDown = cell.getSideDown();
                horizontalSides[i][j + 1] = sideDown;

                // We are in the first column and so we need to grab the left sides too.
                if (i == 0) {
                    Rectangle sideLeft = cell.getSideLeft();
                    verticalSides[i][j] = sideLeft;
                }

                // We are in the first row and so we need to grab the up sides too.
                if (j == 0) {
                    Rectangle sideUp = cell.getSideUp();
                    horizontalSides[i][j] = sideUp;
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
    protected void handleCollision(CollisionHandler obj) {
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