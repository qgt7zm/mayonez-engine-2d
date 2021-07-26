package slavsquatsuperstar.mayonez.scripts;

import slavsquatsuperstar.mayonez.Game;
import slavsquatsuperstar.mayonez.MouseInput;
import slavsquatsuperstar.mayonez.Vector2;

/**
 * Allows objects to be picked up using the mouse.
 *
 * @author SlavSquatSuperstar
 */
// Issue: activating for multiple objects
// Solution: use static boolean active?
public class DragAndDrop extends MouseScript {

    public DragAndDrop(String button, boolean inverted) {
        super(MoveMode.POSITION, 0);
        this.inverted = inverted;
        this.button = button;
    }

    @Override
    public void onMouseMove() {
        if (isMouseHeld())
            transform.move(getRawInput());
    }

    @Override
    protected Vector2 getRawInput() {
        MouseInput mouse = Game.mouse();
        return new Vector2(mouse.getX() - lastMx + mouse.getDx(), mouse.getY() - lastMy + mouse.getDy()).mul(inverted ? -1 : 1);
    }
}
