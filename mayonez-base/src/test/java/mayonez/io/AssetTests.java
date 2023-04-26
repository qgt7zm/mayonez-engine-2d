package mayonez.io;

import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link mayonez.io.Asset} class.
 *
 * @author SlavSquatSuperstar
 */
public class AssetTests {

    private final String classpathImg = "testassets/images/mario.png";
    private final String externalImg = "src/test/resources/testassets/images/mario.png";

    @Test
    public void assetIsClasspath() {
        var asset = new Asset(classpathImg);
        assertEquals(LocationType.CLASSPATH, asset.getLocationType());
    }

    @Test
    public void assetIsExternal() {
        var asset = new Asset(externalImg);
        assertEquals(LocationType.EXTERNAL, asset.getLocationType());
    }

    @Test
    public void classpathAssetIsReadOnly() {
        var asset = new Asset(classpathImg);
        assertDoesNotThrow(() -> {
            InputStream in = asset.inputStream();
            in.close();
        });
        assertThrows(IOException.class, () -> {
            OutputStream out = asset.outputStream(true);
            out.close();
        });
    }

    @Test
    public void externalAssetCanReadAndWrite() {
        var asset = new Asset(externalImg);
        assertDoesNotThrow(() -> {
            InputStream in = asset.inputStream();
            in.close();
        });
        assertDoesNotThrow(() -> {
            OutputStream out = asset.outputStream(true);
            out.close();
        });
    }

    @Test
    public void nonExistingAssetIsWriteOnly() {
        var outputFilename = "src/test/resources/testassets/out/output.txt";
        var asset = new Asset(outputFilename);

        var file = new File(outputFilename);
        if (file.exists()) file.delete(); // Delete so the test works

        assertThrows(IOException.class, () -> {
            InputStream in = asset.inputStream();
            in.close();
        });
	    assertDoesNotThrow(() -> {
            // Requires parent directory to exist but not file
            OutputStream out = asset.outputStream(true);
            out.close();
        });

        file.delete(); // Delete so the test still works next time
    }


}
