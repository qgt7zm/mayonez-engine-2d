package mayonez;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit tests for the {@link mayonez.Component} class.
 *
 * @author SlavSquatSuperstar
 */
public class ComponentTests {

    @Test
    public void componentIDsAreUnique() {
        var comp1 = new Component() {
        };
        var comp2 = new Component() {
        };
        assertNotEquals(comp1.componentID, comp2.componentID);
    }

}