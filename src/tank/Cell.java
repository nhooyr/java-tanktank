package tank;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

class Cell {
    protected final static double LENGTH = 3.5 * Tank.BODY_HEIGHT;

    // Used in the grid constructor.
    protected int row;
    protected int column;

    // True means the side is opaque.
    // Protected so that the grid constructor can access.
    protected MutableBoolean up;
    protected MutableBoolean left;
    protected MutableBoolean right;
    protected MutableBoolean down;

    // Protected so that the grid constructor can access.
    protected ArrayList<MutableBoolean> yummySides;
    private double x;
    private double y;

    private static final Color COLOR = Color.BLACK;

    protected Cell(int column, int row, MutableBoolean up, MutableBoolean left) {
        this.column = column;
        this.row = row;
        this.x = column * Cell.LENGTH;
        this.y = row * Cell.LENGTH;

        this.up = up;
        this.left = left;
        this.right = new MutableBoolean();
        this.down = new MutableBoolean();

        makeYummySides();
    }

    private void makeYummySides() {
        yummySides = new ArrayList<>();

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

    protected boolean isYummy() {
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

    protected Rectangle getSideUp() {
        return getSide(this.up, x, y, Cell.LENGTH, Maze.THICKNESS);
    }

    protected Rectangle getSideLeft() {
        return getSide(this.left, x, y, Maze.THICKNESS, Cell.LENGTH);
    }

    // We add maze thickness to the length's in the down side and right side to prevent gaping squares from appearing
    // where a invisible side up or side left would be.
    protected Rectangle getSideDown() {
        return getSide(this.down, x, y + Cell.LENGTH, Cell.LENGTH + Maze.THICKNESS, Maze.THICKNESS);
    }

    protected Rectangle getSideRight() {
        return getSide(this.right, x + Cell.LENGTH, y, Maze.THICKNESS, Cell.LENGTH + Maze.THICKNESS);
    }

    private Rectangle getSide(MutableBoolean visibility, double x, double y, double width, double height) {
        Rectangle rect = null;
        if (visibility.value) {
            rect = new Rectangle(x, y, width, height);
            rect.setFill(COLOR);
        }
        return rect;
    }

    static class MutableBoolean {

        public MutableBoolean() {
            this.value = true;
        }

        protected boolean value;
    }
}
