package slavsquatsuperstar.test.shapetests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slavsquatsuperstar.math.MathUtils;
import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.mayonez.physics.shapes.Circle;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Circle} class.
 *
 * @author SlavSquatSuperstar
 */
public class CircleTests {

    private static Circle circle;

    @BeforeAll
    public static void getCircle() {
        circle = new Circle(new Vec2(2, 2), 2); // center (2, 2), radius 2
    }

    @Test
    public void areaCorrect() {
        assertEquals(circle.area(), 4 * MathUtils.PI, MathUtils.EPSILON);
    }

    @Test
    public void interiorPointInCircle() {
        assertTrue(circle.contains(new Vec2(2, 2)));
        assertTrue(circle.contains(new Vec2(1, 3)));
    }

    @Test
    public void boundaryPointInCircle() {
        assertTrue(circle.contains(new Vec2(2, 0)));
        assertTrue(circle.contains(new Vec2(4, 2)));
    }

    @Test
    public void exteriorPointNotInCircle() {
        assertFalse(circle.contains(new Vec2(0, 0)));
        assertFalse(circle.contains(new Vec2(4, 4)));
    }

    @Test
    public void circleTransformedProperly() {
        Circle newCircle = circle.scale(new Vec2(2f), null).translate(new Vec2(1, 1));
        assertEquals(newCircle.center(), new Vec2(3, 3));
        assertEquals(circle.area() * 4f, newCircle.area());
    }


}