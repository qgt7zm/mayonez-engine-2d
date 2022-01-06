package slavsquatsuperstar.demos.physics;

import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.mayonez.Colors;
import slavsquatsuperstar.mayonez.GameObject;
import slavsquatsuperstar.mayonez.Preferences;
import slavsquatsuperstar.mayonez.Scene;
import slavsquatsuperstar.mayonez.input.KeyInput;
import slavsquatsuperstar.mayonez.physics2d.PhysicsMaterial;
import slavsquatsuperstar.mayonez.physics2d.Rigidbody2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.AlignedBoxCollider2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.BoxCollider2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.CircleCollider;
import slavsquatsuperstar.mayonez.physics2d.colliders.Collider2D;
import slavsquatsuperstar.mayonez.graphics.DebugDraw;
import slavsquatsuperstar.mayonez.scripts.DragAndDrop;
import slavsquatsuperstar.mayonez.scripts.KeepInScene;
import slavsquatsuperstar.mayonez.scripts.MouseFlick;
import slavsquatsuperstar.mayonez.scripts.MoveMode;

import java.awt.*;

public abstract class PhysicsTestScene extends Scene {

    static final PhysicsMaterial TEST_MATERIAL = new PhysicsMaterial(0.4f, 0.4f, 0.3f);
    static final PhysicsMaterial BOUNCY_MATERIAL = new PhysicsMaterial(0f, 0f, 1f);
    static final PhysicsMaterial STICKY_MATERIAL = new PhysicsMaterial(1f, 1f, 0f);
    final int NUM_SHAPES;

    public PhysicsTestScene(String name, int numShapes) {
        super(name, Preferences.SCREEN_WIDTH, Preferences.SCREEN_HEIGHT, 10);
        setBackground(Colors.WHITE);
        NUM_SHAPES = numShapes;
        setGravity(new Vec2());
    }

    @Override
    public void onUserRender(Graphics2D g2) {
        for (GameObject o : objects) {
            if (o.name.equals("Camera")) continue;

            Collider2D col = o.getComponent(Collider2D.class);
            if (col != null) {
                Color color = Colors.BLACK;
                if (!col.isStatic()) {
                    if (col instanceof CircleCollider) color = Colors.BLUE;
                    else if (col instanceof AlignedBoxCollider2D) color = Colors.LIGHT_GREEN;
                    else if (col instanceof BoxCollider2D) color = Colors.ORANGE;
                }

                // Draw velocity and direction vector
                if (!col.isStatic()) {
                    DebugDraw.drawVector(col.center(), col.getRigidbody().getVelocity().mul(0.1f), color);
                    DebugDraw.drawVector(col.getRigidbody().getPosition(), col.getRigidbody().getTransform().getDirection(), Colors.BLACK);
                }
                DebugDraw.drawShape(col, color); // Draw Shape
            }
        }
    }

    @Override
    protected void onUserUpdate(float dt) {
        if (KeyInput.keyDown("up")) setGravity(new Vec2());
        else if (KeyInput.keyDown("down")) setGravity(new Vec2(0, -18));
    }

    protected final GameObject createCircle(float radius, Vec2 position, PhysicsMaterial material) {
        return new GameObject("Circle", position) {
            @Override
            protected void init() {
                addComponent(new CircleCollider(radius).setMaterial(material));
                addComponent(new Rigidbody2D(radius));
                addComponent(new KeepInScene(getScene(), KeepInScene.Mode.BOUNCE));
                addComponent(new DragAndDrop("left mouse"));
                addComponent(new MouseFlick(MoveMode.VELOCITY, "right mouse", 15, false));
            }
        };
    }

    protected final GameObject createAABB(float width, float height, Vec2 position, PhysicsMaterial material) {
        return new GameObject("AABB Rectangle", position) {
            @Override
            protected void init() {
                addComponent(new AlignedBoxCollider2D(new Vec2(width, height)).setMaterial(material));
                addComponent(new Rigidbody2D(width * height / 4f));
                addComponent(new KeepInScene(getScene(), KeepInScene.Mode.BOUNCE));
                addComponent(new DragAndDrop("left mouse"));
                addComponent(new MouseFlick(MoveMode.VELOCITY, "right mouse", 15, false));
            }
        };
    }

    protected final GameObject createOBB(float width, float height, Vec2 position, float rotation, PhysicsMaterial material) {
        return new GameObject("OBB Rectangle", position) {
            @Override
            protected void init() {
                transform.rotate(rotation);
                addComponent(new BoxCollider2D(new Vec2(width, height)).setMaterial(material));
                addComponent(new Rigidbody2D(width * height / 4f));
                addComponent(new KeepInScene(getScene(), KeepInScene.Mode.BOUNCE));
                addComponent(new DragAndDrop("left mouse"));
                addComponent(new MouseFlick(MoveMode.VELOCITY, "right mouse", 15, false));
            }
        };
    }
}
