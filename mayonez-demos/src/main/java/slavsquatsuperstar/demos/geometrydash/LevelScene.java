package slavsquatsuperstar.demos.geometrydash;

import slavsquatsuperstar.mayonez.math.Vec2;
import slavsquatsuperstar.mayonez.*;
import slavsquatsuperstar.mayonez.physics.Rigidbody;
import slavsquatsuperstar.mayonez.physics.colliders.BoxCollider;
import slavsquatsuperstar.mayonez.physics.colliders.Collider;
import slavsquatsuperstar.mayonez.util.Colors;

import java.awt.*;

public class LevelScene extends Scene {

    public LevelScene() {
        super("Level", (int) (Preferences.getScreenWidth() * 1.5f), (int) (Preferences.getScreenHeight() * 1f), 42);
        setBackground(Colors.LIGHT_GRAY);
    }

    @Override
    protected void init() {
        addObject(new GameObject("Ground", new Vec2(0, getHeight() * -0.5f)) {
            @Override
            protected void init() {
                addComponent(new Rigidbody(0f).setFixedRotation(true));
                addComponent(new BoxCollider(new Vec2(getWidth() + 2f, 2f)));
            }

            @Override
            public void onUserRender(Graphics2D g2) {
                getComponent(Collider.class).setDebugDraw(Colors.BLACK, true);
            }
        });

        addObject(new Player("Player", new Vec2(0, -5)));
    }

    public static void main(String[] args) {
        Mayonez.setUseGL(false);
        Mayonez.start(new LevelScene());
    }

}
