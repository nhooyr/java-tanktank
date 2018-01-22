package tank;

import javafx.scene.paint.Color;

import java.util.ArrayList;

// Cell represents a cell within the Maze.
class Cell {
    static final double LENGTH = 3.5 * Tank.BODY_HEIGHT;

    private final int row;
    private final int column;
    private final ArrayList<MutableBoolean> yummySegments = new ArrayList<>();
    private final double x;
    private final double y;

    // True means the segment is opaque.
    private final MutableBoolean up;
    private final MutableBoolean left;
    private final MutableBoolean right = new MutableBoolean();
    private final MutableBoolean down = new MutableBoolean();

    Cell(final int column, final int row, final MutableBoolean up, final MutableBoolean left) {
        this.column = column;
        this.row = row;
        this.x = column * Cell.LENGTH;
        this.y = row * Cell.LENGTH;

        this.up = up;
        this.left = left;

        setYummySides();
    }

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

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

    ArrayList<MutableBoolean> getYummySegments() {
        return yummySegments;
    }

    private void setYummySides() {
        // If up is true and this cell is not at the top row then it is yummy.
        if (up.value && row != 0) {
            yummySegments.add(up);
        }

        // If right is true and this cell is not at the right edge then it is yummy.
        if (right.value && column != Maze.COLUMNS - 1) {
            yummySegments.add(right);
        }

        // If down is true and this cell is not at the bottom row then it is yummy.
        if (down.value && row != Maze.ROWS - 1) {
            yummySegments.add(down);
        }

        // If left is true and this cell is not at the left edge then it is yummy.
        if (left.value && column != 0) {
            yummySegments.add(left);
        }
    }

    // isYummy tells the maze generation algorithm whether this cell can have more segments removed, or "eaten".
    boolean isYummy() {
        int yummyThreshold = 2;
        if (column == 0 ||
                row == 0 ||
                column == Maze.COLUMNS - 1 ||
                row == Maze.ROWS - 1) {
            // We make bordering cells more yummy to ensure we do not any encircled areas and to allow for a open outer
            // area. An interesting map element.
            // No proof that this ensures no encircling areas but it is my intuition and seems to work in practice.
            yummyThreshold = 1;
        }
        return yummySegments.size() > yummyThreshold;
    }

    Rectangle getUpSeg() {
        return getSeg(this.up, x, y, Cell.LENGTH, Maze.THICKNESS);
    }

    Rectangle getLeftSeg() {
        return getSeg(this.left, x, y, Maze.THICKNESS, Cell.LENGTH);
    }

    // We add maze thickness to the lengths in the down segment and right segment to prevent gaping squares from appearing
    // where an invisible up segment or left segment would be.
    Rectangle getDownSeg() {
        return getSeg(this.down, x, y + Cell.LENGTH, Cell.LENGTH + Maze.THICKNESS, Maze.THICKNESS);
    }

    Rectangle getRightSeg() {
        return getSeg(this.right, x + Cell.LENGTH, y, Maze.THICKNESS, Cell.LENGTH + Maze.THICKNESS);
    }

    private Rectangle getSeg(final MutableBoolean visibility, final double x, final double y, final double width, final double height) {
        Rectangle rect = null;
        if (visibility.value) {
            rect = new Rectangle(x, y, width, height);
        }
        return rect;
    }

    static class MutableBoolean {

        boolean value;

        MutableBoolean() {
            this.value = true;
        }
    }
}
