package slavsquatsuperstar.demos.geometrydash;

import slavsquatsuperstar.demos.geometrydash.components.Grid;
import slavsquatsuperstar.demos.geometrydash.components.SnapToGrid;
import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.mayonez.*;
import slavsquatsuperstar.mayonez.graphics.DebugDraw;
import slavsquatsuperstar.mayonez.graphics.JSpriteSheet;
import slavsquatsuperstar.mayonez.physics.Rigidbody;
import slavsquatsuperstar.mayonez.physics.colliders.BoxCollider;
import slavsquatsuperstar.mayonez.physics.colliders.Collider;

import java.awt.*;

public class LevelEditor extends Scene {

    public LevelEditor() {
        super("Level Editor", Preferences.getScreenWidth(), Preferences.getScreenHeight(), 42);
        setGravity(new Vec2());
    }

    @Override
    protected void init() {
        JSpriteSheet blocks = new JSpriteSheet("assets/textures/blocks.png", 42, 42, 2, 12);

        addObject(new GameObject("Ground", new Vec2(getWidth() * 0.5f, 0)) {
            @Override
            protected void init() {
                addComponent(new Rigidbody(0f).setFixedRotation(true));
                addComponent(new BoxCollider(new Vec2(getWidth() + 2f, 2f)));
            }

            @Override
            public void onUserRender(Graphics2D g2) {
                DebugDraw.fillShape(getComponent(Collider.class), Colors.BLACK);
            }
        });

        addObject(new Player("Player", new Vec2(5, 5)));

        addObject(new GameObject("Grid") {
            @Override
            protected void init() {
                addComponent(new Grid(new Vec2(getCellSize())));
            }
        });

        addObject(new GameObject("Mouse Cursor") {
            @Override
            protected void init() {
                addComponent(new SnapToGrid(new Vec2(getCellSize())));
                addComponent(blocks.getSprite(0));
            }
        });
    }

    public static void main(String[] args) {
        Mayonez.setUseGL(false);
        Mayonez.setScene(new LevelEditor());
        Mayonez.start();
    }

}