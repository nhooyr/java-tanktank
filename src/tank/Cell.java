package tank;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

class Cell {
    protected final static int LENGTH = 3 * Tank.BODY_HEIGHT;

    private int row;
    private int column;

    // True means the side is opaque.
    private MutableBoolean up;
    private MutableBoolean left;
    // Protected so that the grid constructor can access.
    protected MutableBoolean right;
    protected MutableBoolean down;

    // Protected so that the grid constructor can access.
    protected ArrayList<MutableBoolean> yummySides;
    private double x;
    private double y;

    private static final Color COLOR = Color.BLACK;

    protected Cell(int row, int column, MutableBoolean up, MutableBoolean left) {
        this.row = row;
        this.column = column;
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

    // The length of the rectangles in the group is extended by the thickness because we want overlap in case
    // one of the sides of a vertex is false, then the middle part won't disappear.
    // TODO make up above comment more clear.
    protected Rectangle getSideUp() {
        return getSide(this.up, x, y, Cell.LENGTH + Maze.THICKNESS, Maze.THICKNESS);
    }

    protected Rectangle getSideLeft() {
        return getSide(this.left, x, y, Maze.THICKNESS, Cell.LENGTH + Maze.THICKNESS);
    }

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
