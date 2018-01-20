package tank;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

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

    protected Maze.CollisionStatus checkBulletCollision(Bullet bullet) {
        for (Node node : group.getChildren()) {
            Rectangle side = (Rectangle) node;
            Maze.CollisionStatus collisionStatus = bullet.checkCollision(side);
            if (collisionStatus != Maze.CollisionStatus.OK) {
                return collisionStatus;
            }
        }
        return Maze.CollisionStatus.OK;
    }

    protected Group group;

    // The length of the rectangles in the group is extended by the thickness because we want overlap in case
    // one of the sides of a vertex is false, then the middle part won't disappear.
    protected Group group() {
        if (group != null) {
            return group;
        }
        group = new Group();

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

    static class MutableBoolean {

        public MutableBoolean() {
            this.value = true;
        }

        protected boolean value;
    }
}
