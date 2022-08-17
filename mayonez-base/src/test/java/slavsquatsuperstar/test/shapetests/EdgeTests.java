package slavsquatsuperstar.test.shapetests;

import org.junit.jupiter.api.Test;
import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.mayonez.physics.collision.Collisions;
import slavsquatsuperstar.mayonez.physics.shapes.Circle;
import slavsquatsuperstar.mayonez.physics.shapes.Edge;
import slavsquatsuperstar.mayonez.physics.shapes.Polygon;
import slavsquatsuperstar.mayonez.physics.shapes.Triangle;

import static org.junit.jupiter.api.Assertions.*;
import static slavsquatsuperstar.test.TestUtils.assertShapeCollision;
import static slavsquatsuperstar.test.TestUtils.assertNoShapeCollision;

/**
 * Unit tests {@link Edge} class and line vs line/shape detection in {@link Collisions} class.
 *
 * @author SlavSquatSuperstar
 */
public class EdgeTests {

    // Point vs Line

    @Test
    public void pointVsEdgeHitInterior() {
        Edge e1 = new Edge(new Vec2(0, 0), new Vec2(2, 2));
        assertTrue(e1.contains(new Vec2(1, 1))); // oblique

        Edge e2 = new Edge(new Vec2(0, 0), new Vec2(2, 0));
        assertTrue(e2.contains(new Vec2(1, 0))); // horizontal

        Edge e3 = new Edge(new Vec2(0, 0), new Vec2(0, 2));
        assertTrue(e3.contains(new Vec2(0, 1))); // vertical
    }

    @Test
    public void pointVsEdgeHitEndpoint() {
        Edge e = new Edge(new Vec2(0, 0), new Vec2(2, 2));
        assertTrue(e.contains(new Vec2(2, 2)));
    }

    @Test
    public void pointNotInEdge() {
        Edge e = new Edge(new Vec2(0, 0), new Vec2(2, 2));
        assertFalse(e.contains(new Vec2(3, 3))); // outside
        assertFalse(e.contains(new Vec2(1, 0))); // above
    }

    // Unit Normals
    @Test
    public void unitNormalsCorrect() { // always to the left of line vector
        Edge e1 = new Edge(new Vec2(0, 0), new Vec2(4, 0));
        assertEquals(new Vec2(0, 1), e1.unitNormal());

        Edge e2 = new Edge(new Vec2(4, 0), new Vec2(0, 0));
        assertEquals(new Vec2(0, -1), e2.unitNormal());
    }

    @Test
    public void unitNormalsDirectionalCorrect() { // always to the left of line vector
        Edge e = new Edge(new Vec2(0, 0), new Vec2(4, 0));
        assertEquals(new Vec2(0, 1), e.unitNormal(new Vec2(1, 1)));
        assertEquals(new Vec2(0, -1), e.unitNormal(new Vec2(1, -1)));
        assertEquals(new Vec2(0, 0), e.unitNormal(new Vec2(1, 0)));
    }

    // Line vs Line

    @Test
    public void edgeVsEdgeHitOblique() {
        Edge e1 = new Edge(new Vec2(0, 0), new Vec2(4, 3));
        Edge e2 = new Edge(new Vec2(4, 0), new Vec2(0, 3));
        assertShapeCollision(e1, e2); // x-shape

        Edge e3 = new Edge(new Vec2(0, 0), new Vec2(2, 2));
        Edge e4 = new Edge(new Vec2(1, 1), new Vec2(-1, 3));
        assertShapeCollision(e3, e4); // t-shape
    }

    @Test
    public void edgeVsEdgeHitParallel() {
        Edge e1 = new Edge(new Vec2(0, 0), new Vec2(2, 2));
        Edge e2 = new Edge(new Vec2(1, 1), new Vec2(3, 3));
        assertShapeCollision(e1, e2);
    }

    @Test
    public void edgeVsEdgeMissOblique() {
        Edge e1 = new Edge(new Vec2(0, 0), new Vec2(4, 3));
        Edge e2 = new Edge(new Vec2(9, 1), new Vec2(5, 4));
        assertNoShapeCollision(e1, e2);
    }

    @Test
    public void edgeVsEdgeMissParallel() {
        Edge e1 = new Edge(new Vec2(0, 0), new Vec2(2, 2));
        Edge e2 = new Edge(new Vec2(0, 1), new Vec2(2, 3));
        assertNoShapeCollision(e1, e2);
    }

    // Line vs Shape

    @Test
    public void boxVsLineHitBisect() {
        Polygon rect = Polygon.rectangle(new Vec2(0, 0), new Vec2(4, 4)); // 4x4 rect at (0, 0)
        Edge e1 = new Edge(new Vec2(-1, 3), new Vec2(3, -1));
        assertShapeCollision(rect, e1); // adjacent edges

        Edge e2 = new Edge(new Vec2(-1, 3), new Vec2(1, -3));
        assertShapeCollision(rect, e2); // opposite edges

        Edge e3 = new Edge(new Vec2(0, 3), new Vec2(0, -3));
        assertShapeCollision(rect, e3); // perpendicular
    }

    @Test
    public void boxVsLineHitBoundary() {
        Polygon rect = Polygon.rectangle(new Vec2(0, 0), new Vec2(4, 4)); // 4x4 rect at (0, 0)
        Edge e1 = new Edge(new Vec2(-1, 2), new Vec2(3, 2));
        assertShapeCollision(rect, e1); // parallel to edge

        Edge e2 = new Edge(new Vec2(1, 3), new Vec2(3, 1));
        assertShapeCollision(rect, e2); // hits corner
    }

    @Test
    public void boxVsLineMiss() {
        Polygon rect = Polygon.rectangle(new Vec2(0, 0), new Vec2(4, 4)); // 4x4 rect at (0, 0)
        Edge e = new Edge(new Vec2(1, 8), new Vec2(3, 1));
        assertNoShapeCollision(rect, e); // parallel to edge
    }

    @Test
    public void circleVsLineHit() {
        Circle c = new Circle(new Vec2(0, 0), 2); // 4x4 circle at (0, 0)
        Edge e1 = new Edge(new Vec2(1, 3), new Vec2(1, -3));
        assertShapeCollision(c, e1); // secant

        Edge e2 = new Edge(new Vec2(2, 3), new Vec2(2, -3));
        assertShapeCollision(c, e2); // tangent
    }

    @Test
    public void circleVsLineMiss() {
        Circle c = new Circle(new Vec2(0, 0), 2); // 4x4 circle at (0, 0)
        Edge e = new Edge(new Vec2(3, 3), new Vec2(3, -3));
        assertNoShapeCollision(c, e); // secant
    }

    @Test
    public void triVsLineHit() {
        Triangle tri = new Triangle(new Vec2(0, 0), new Vec2(1, 3), new Vec2(2, 0)); // 2x3 triangle from (0, 0)
        Edge e = new Edge(new Vec2(0, 2), new Vec2(2, 2));
        assertShapeCollision(tri, e);
    }

    @Test
    public void triVsLineMiss() {
        Triangle tri = new Triangle(new Vec2(0, 0), new Vec2(1, 3), new Vec2(2, 0)); // 2x3 triangle from (0, 0)
        Edge e = new Edge(new Vec2(0, 4), new Vec2(2, 4));
        assertNoShapeCollision(tri, e);
    }

}