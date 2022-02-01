package slavsquatsuperstar.demos.physics;

import slavsquatsuperstar.math.MathUtils;
import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.mayonez.GameObject;
import slavsquatsuperstar.mayonez.Mayonez;
import slavsquatsuperstar.mayonez.physics2d.Rigidbody2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.BoundingBoxCollider2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.PolygonCollider2D;
import slavsquatsuperstar.mayonez.scripts.DragAndDrop;
import slavsquatsuperstar.mayonez.scripts.KeepInScene;
import slavsquatsuperstar.mayonez.scripts.MouseFlick;
import slavsquatsuperstar.mayonez.scripts.MoveMode;

public class AngularResolutionTest extends PhysicsTestScene {

    public AngularResolutionTest(String name) {
        super(name, 8);
    }

    @Override
    protected void init() {
        addObject(new GameObject("Floor", new Vec2(getWidth() / 2f, 1)) {
            @Override
            protected void init() {
                addComponent(new Rigidbody2D(0));
                addComponent(new BoundingBoxCollider2D(new Vec2(getWidth(), 2)));
            }
        });

        for (int i = 0; i < NUM_SHAPES; i++) {
            if (i % 3 == 0) {
                addObject(createCircle(MathUtils.random(3f, 6f), new Vec2(MathUtils.random(0, getWidth()),
                        MathUtils.random(0, getHeight())), NORMAL_MATERIAL));
            } else {
            addObject(createOBB(MathUtils.random(5f, 12f), MathUtils.random(5f, 12f),
                    new Vec2(MathUtils.random(0, getWidth()), MathUtils.random(0, getHeight())), MathUtils.random(0, 90), NORMAL_MATERIAL));
            }
        }

        addObject(new GameObject("Triangle", new Vec2(40, 40)) {
            @Override
            protected void init() {
                addComponent(new PolygonCollider2D(3, 5));
                addComponent(new Rigidbody2D(10));
                addComponent(new KeepInScene(getScene(), KeepInScene.Mode.BOUNCE));
                addComponent(new DragAndDrop("left mouse"));
                addComponent(new MouseFlick(MoveMode.VELOCITY, "right mouse", 15, false));
            }
        });

    }

    public static void main(String[] args) {
        Mayonez.setUseGL(false);
        Mayonez.setScene(new AngularResolutionTest("Angular Impulse Resolution Test"));
        Mayonez.start();
    }

}
