package physicstests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slavsquatsuperstar.mayonez.Transform;
import slavsquatsuperstar.mayonez.Vector2;
import slavsquatsuperstar.mayonez.physics2d.Rigidbody2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.AlignedBoxCollider2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.BoxCollider2D;
import slavsquatsuperstar.mayonez.physics2d.colliders.CircleCollider;
import slavsquatsuperstar.mayonez.physics2d.colliders.Edge2D;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BoxCollider2D} class.
 *
 * @author SlavSquatSuperstar
 */
public class OBBTests {

    static BoxCollider2D obb;

    // Create box centered at (0, 0) with dimensions 4x4 with a rotation of 30 degrees
    @BeforeAll
    public static void getAABB() {
        obb = new BoxCollider2D(new Vector2(4, 4));
        Transform t = new Transform();
        t.rotate(30);
        obb.setTransform(t);
        obb.setRigidBody(new Rigidbody2D(0f));
    }

    @Test
    public void vertexPointIsInOBB() {
        for (Vector2 v : obb.getVertices())
            assertTrue(obb.contains(v));
    }

    @Test
    public void edgeLineIsInOBB() {
        Vector2[] vertices = obb.getVertices();
        for (int i = 0; i < vertices.length; i++)
            assertTrue(obb.intersects(new Edge2D(vertices[i], vertices[(i + 1) / 4])));
    }

    @Test
    public void obbIntersectsCircle() {
        CircleCollider c = new CircleCollider(4);
        c.setTransform(new Transform(new Vector2(2, 2)));
        c.setRigidBody(new Rigidbody2D(0f));
        assertTrue(obb.detectCollision(c));
    }

    @Test
    public void obbIntersectsAABB() {
        AlignedBoxCollider2D box = new AlignedBoxCollider2D(new Vector2(4, 4));
        box.setTransform(new Transform(new Vector2(2, 2)));
        box.setRigidBody(new Rigidbody2D(0f));
        assertTrue(obb.detectCollision(box));
    }

}
