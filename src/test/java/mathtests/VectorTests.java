package mathtests;

import org.junit.Test;
import slavsquatsuperstar.mayonez.Vector2;
import slavsquatsuperstar.util.MathUtils;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class VectorTests {

    @Test
    public void unitVectorLengthIsOne() {
        assertEquals(1f, new Vector2(500, 500).unit().lengthSquared(), MathUtils.EPSILON);
        assertEquals(1f, new Vector2(-500, -500).unit().lengthSquared(), MathUtils.EPSILON);
    }

    @Test
    public void unitVectorOfZeroIsZero() {
        Vector2 v = new Vector2();
        assertEquals(v.unit(), v);
    }

    @Test
    public void vectorDivideByZeroIsZero() {
        Vector2 v = new Vector2();
        assertEquals(v.div(0), v);
    }

    @Test
    public void perpendicularDotProductIsZero() {
        Vector2 v1 = new Vector2(1, 0);
        Vector2 v2 = new Vector2(0, 1);
        assertEquals(90f, v1.angle(v2), MathUtils.EPSILON);
        assertEquals(0f, v1.dot(v2));
        assertEquals(0f, v2.dot(v1));
    }

    @Test
    public void acuteDotProductIsPositive() {
        Vector2 v1 = new Vector2(3, 4);
        Vector2 v2 = new Vector2(4, 3);
        assertTrue(v1.angle(v2) < 90f);
        assertTrue(v1.dot(v2) > 0);
    }

    @Test
    public void obtuseDotProductIsPositive() {
        Vector2 v1 = new Vector2(5, 1);
        Vector2 v2 = new Vector2(-5, 1);
        assertTrue(v1.angle(v2) > 90f);
        assertTrue(v1.dot(v2) < 0);
    }

    @Test
    public void acuteProjectionIsPositive() {
        Vector2 v1 = new Vector2(6, 4);
        Vector2 v2 = new Vector2(4, 0);
        assertEquals(new Vector2(6, 0), v1.project(v2));
    }

    @Test
    public void obtuseProjectionIsNegative() {
        Vector2 v1 = new Vector2(-6, 4);
        Vector2 v2 = new Vector2(4, 0);
        assertEquals(new Vector2(-6, 0), v1.project(v2));
    }

    @Test
    public void perpendicularProjectionIsZero() {
        Vector2 v1 = new Vector2(0, 1);
        Vector2 v2 = new Vector2(1, 0);
        assertEquals(0f, v1.project(v2).magnitude(), MathUtils.EPSILON);
    }

    @Test
    public void parallelProjectionIsSelf() {
        Vector2 v1 = new Vector2(2, 2);
        Vector2 v2 = new Vector2(4, 4);
        assertEquals(v1, v1.project(v2));
    }

}
