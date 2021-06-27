package physicstests;

import org.junit.Before;
import slavsquatsuperstar.mayonez.Transform;
import slavsquatsuperstar.mayonez.Vector2;
import slavsquatsuperstar.mayonez.physics2d.AlignedBoxCollider2D;
import slavsquatsuperstar.mayonez.physics2d.Line2D;
import slavsquatsuperstar.mayonez.physics2d.RigidBody2D;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class BoxTests {

    AlignedBoxCollider2D aabb;

    // Create box centered at (0, 0) with dimensions 4x4
    @Before
    public void getAABB() {
        aabb = new AlignedBoxCollider2D(new Vector2(4, 4));
        aabb.setTransform(new Transform(new Vector2(0, 0)));
        aabb.setRigidBody(new RigidBody2D());
    }

    @Test
    public void aabbIsAtObjectCenter() {
        assertEquals(new Vector2(0, 0), aabb.center());
    }

    @Test
    public void cornerPointIsInAABB() {
        assertTrue(aabb.contains(new Vector2(-2, -2)));
        assertTrue(aabb.contains(new Vector2(-2, 2)));
        assertTrue(aabb.contains(new Vector2(2, 2)));
        assertTrue(aabb.contains(new Vector2(2, 2)));
    }

    @Test
    public void outsidePointNotInAABB() {
        assertFalse(aabb.contains(new Vector2(3, -3)));
        assertFalse(aabb.contains(new Vector2(-3, 3)));
        assertFalse(aabb.contains(new Vector2(-3, -3)));
        assertFalse(aabb.contains(new Vector2(3, 3)));
    }

    @Test
    public void tangentLineIsInAABB() {
        assertTrue(aabb.intersects(new Line2D(new Vector2(1, 3), new Vector2(3, 1))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(-1, -3), new Vector2(-3, -1))));
    }

    @Test
    public void bisectLineIsInAABB() {
        assertTrue(aabb.intersects(new Line2D(new Vector2(-2, -2), new Vector2(2, 2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(-2, 2), new Vector2(2, -2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(0, -2), new Vector2(0, 2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(-2, 0), new Vector2(2, 0))));
    }

    @Test
    public void edgeLineIsInAABB() {
        assertTrue(aabb.intersects(new Line2D(new Vector2(-2, -2), new Vector2(-2, 2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(2, -2), new Vector2(2, 2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(-2, -2), new Vector2(2, -2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(-2, 2), new Vector2(2, 2))));
    }

    @Test
    public void lineIsInAABB() {
        assertTrue(aabb.intersects(new Line2D(new Vector2(0, 0), new Vector2(-2, -2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(0, 0), new Vector2(2, 2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(1, 2), new Vector2(-1, 2))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(1, 1), new Vector2(3, 3))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(2, 2), new Vector2(3, 3))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(2, 2), new Vector2(2, 3))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(2, 2), new Vector2(3, 1))));
        assertTrue(aabb.intersects(new Line2D(new Vector2(-1, -1), new Vector2(-3, -3))));
    }

    @Test
    public void lineNotInAABB() {
        assertFalse(aabb.intersects(new Line2D(new Vector2(3, 3), new Vector2(4, 4))));
        assertFalse(aabb.intersects(new Line2D(new Vector2(3, 1), new Vector2(3, -1))));
    }

}