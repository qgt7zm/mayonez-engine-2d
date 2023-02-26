package mayonez.test.javatests;

import mayonez.io.Asset;
import mayonez.io.text.TextFile;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link java.lang.reflect.Constructor} class.
 *
 * @author SlavSquatSuperstar
 */
public class ConstructorTests {

    @Test
    public void newSuperclassInstanceSuccess() throws Exception {
        Asset inst = instantiateAsset(Asset.class, "preferences.json");
        assertEquals("preferences.json", inst.getFilename());
        assertTrue(inst.isClasspath());
    }

    @Test
    public void newSubclassInstanceSuccess() throws Exception {
        TextFile inst = instantiateAsset(TextFile.class, "preferences.json");
        assertEquals("preferences.json", inst.getFilename());
        assertTrue(inst.isClasspath());
        assertNotEquals(0, inst.readLines().length);
    }

    @Test
    public void newInstanceFail() {
        assertThrows(Exception.class, () -> instantiateAsset(Asset.class));
        assertThrows(Exception.class, () -> instantiateAsset(Asset.class, "foo", "bar"));
    }

    public static <T> T instantiateAsset(Class<T> cls, Object... args) throws Exception {
        Constructor<?> ctor = cls.getDeclaredConstructor(String.class);
        return cls.cast(ctor.newInstance(args));
    }

}

