package mayonez.io.text;

import kotlin.io.TextStreamsKt;
import mayonez.io.*;

import java.io.*;

/**
 * Reads, writes, and appends text to files as single strings.
 *
 * @author SlavSquatSuperstar
 */
public class TextIOManager implements AssetReader<String>, AssetWriter<String> {

    // Source: Apache Commons IO > IOUtils.readLines(InputStream, Charset).
    @Override
    public String read(InputStream input) throws IOException {
        if (input == null) throw new FileNotFoundException("File does not exist");
        try (var reader = new BufferedReader(new InputStreamReader(input))) {
            return TextStreamsKt.readText(reader);
        } catch (IOException e) {
            throw new IOException("Error while reading file");
        }
    }

    // Source: Apache Commons IO > IOUtils.write(String, OutputStream, Charset)
    @Override
    public void write(OutputStream output, String text) throws IOException {
        if (output == null) throw new FileNotFoundException("File does not exist");
        if (text == null) return;
        try (var writer = new BufferedWriter(new OutputStreamWriter(output))) {
//            output.write(text.getBytes(StandardCharsets.UTF_8));
            writer.write(text);
        } catch (IOException e) {
            throw new IOException("Error while saving file");
        }
    }

}
