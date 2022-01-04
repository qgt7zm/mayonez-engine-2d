package slavsquatsuperstar.demos;

import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.mayonez.*;
import slavsquatsuperstar.mayonez.physics2d.Rigidbody2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.AlignedBoxCollider2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.Collider2D;
import slavsquatsuperstar.mayonez.graphics.DebugDraw;

import java.awt.*;

public class LevelScene extends Scene {

    public LevelScene() {
        super("Level", (int) (Preferences.SCREEN_WIDTH * 1.5), (int) (Preferences.SCREEN_HEIGHT * 1.0), 42);
        setBackground(Colors.LIGHT_GRAY);
    }

    @Override
    protected void init() {
        addObject(new GameObject("Ground", new Vec2(getWidth() * 0.5f, 0)) {
            @Override
            protected void init() {
                addComponent(new Rigidbody2D(0f));
                addComponent(new AlignedBoxCollider2D(new Vec2(getWidth() + 2f, 2f)));
                System.out.println(transform.position);
            }

            @Override
            public void onUserRender(Graphics2D g2) {
                DebugDraw.fillShape(getComponent(Collider2D.class), Colors.BLACK);
            }
        });

        addObject(new Player("Player", new Vec2(5, 5)));
    }

    public static void main(String[] args) {
        Mayonez.setUseGL(false);
        Mayonez.setScene(new LevelScene());
        Mayonez.start();
    }

}