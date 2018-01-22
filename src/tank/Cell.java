package tank;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

class Cell {
    final static double LENGTH = 3.5 * Tank.BODY_HEIGHT;

    private final int row;
    private final int column;

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    // True means the side is opaque.
    // Protected so that the grid constructor can access.
    private final MutableBoolean up;
    private final MutableBoolean left;
    private final MutableBoolean right = new MutableBoolean();
    private final MutableBoolean down = new MutableBoolean();

    MutableBoolean getUp() {
        return up;
    }

    MutableBoolean getLeft() {
        return left;
    }

    MutableBoolean getRight() {
        return right;
    }

    MutableBoolean getDown() {
        return down;
    }


    private final ArrayList<MutableBoolean> yummySides = new ArrayList<>();

    ArrayList<MutableBoolean> getYummySides() {
        return yummySides;
    }

    private final double x;
    private final double y;

    private static final Color COLOR = Color.BLACK;

    Cell(final int column, final int row, final MutableBoolean up, final MutableBoolean left) {
        this.column = column;
        this.row = row;
        this.x = column * Cell.LENGTH;
        this.y = row * Cell.LENGTH;

        this.up = up;
        this.left = left;

        makeYummySides();
    }

    private void makeYummySides() {
        // If up is true and this cell is not at the top row then it is yummy.
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

    boolean isYummy() {
        int yummyThreshold = 2;
        if (column == 0 ||
                row == 0 ||
                column == Maze.COLUMNS - 1 ||
                row == Maze.ROWS - 1) {
            // We make bordering cells more yummy to ensure we do not any encircled areas and to allow for a open outer
            // area. An interesting map element.
            // No proof that this ensures no encircling areas but it is my intuition.
            yummyThreshold = 1;
        }
        return yummySides.size() > yummyThreshold;
    }

    Rectangle getSideUp() {
        return getSide(this.up, x, y, Cell.LENGTH, Maze.THICKNESS);
    }

    Rectangle getSideLeft() {
        return getSide(this.left, x, y, Maze.THICKNESS, Cell.LENGTH);
    }

    // We add maze thickness to the length's in the down side and right side to prevent gaping squares from appearing
    // where a invisible side up or side left would be.
    Rectangle getSideDown() {
        return getSide(this.down, x, y + Cell.LENGTH, Cell.LENGTH + Maze.THICKNESS, Maze.THICKNESS);
    }

    Rectangle getSideRight() {
        return getSide(this.right, x + Cell.LENGTH, y, Maze.THICKNESS, Cell.LENGTH + Maze.THICKNESS);
    }

    private Rectangle getSide(final MutableBoolean visibility, final double x, final double y, final double width, final double height) {
        Rectangle rect = null;
        if (visibility.value) {
            rect = new Rectangle(x, y, width, height);
            rect.setFill(COLOR);
        }
        return rect;
    }

    static class MutableBoolean {

        MutableBoolean() {
            this.value = true;
        }

        boolean value;
    }
}
