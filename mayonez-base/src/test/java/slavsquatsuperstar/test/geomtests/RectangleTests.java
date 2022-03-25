package slavsquatsuperstar.test.geomtests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slavsquatsuperstar.math.MathUtils;
import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.math.geom.Rectangle;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Rectangle} class.
 *
 * @author SlavSquatSuperstar
 */
public class RectangleTests {

    private static Rectangle rect;

    @BeforeAll
    public static void getRect() {
        rect = new Rectangle(new Vec2(3, 1.5f), new Vec2(4, 3)); // center (3, 1.5f), size(4, 3)
        // vertices (1, 1), (5, 1), (1, 3), (5, 3)
    }

    @Test
    public void areaCorrect() {
        assertEquals(rect.area(), rect.getWidth() * rect.getHeight(), MathUtils.EPSILON);
    }

    @Test
    public void perimeterCorrect() {
        assertEquals(rect.perimeter(), 2 * (rect.getWidth() + rect.getHeight()), MathUtils.EPSILON);
    }

    @Test
    public void interiorPointInRect() {
        assertTrue(rect.contains(new Vec2(2, 2)));
        assertTrue(rect.contains(new Vec2(4.9f, 2.9f)));
    }

    @Test
    public void boundaryPointInRect() {
        assertTrue(rect.contains(new Vec2(1, 1)));
        assertTrue(rect.contains(new Vec2(2, 3)));
        assertTrue(rect.contains(new Vec2(2, 1)));
    }

    @Test
    public void exteriorPointNotInRect() {
        assertFalse(rect.contains(new Vec2(0, 5)));
        assertFalse(rect.contains(new Vec2(-1, 2)));
    }

}
