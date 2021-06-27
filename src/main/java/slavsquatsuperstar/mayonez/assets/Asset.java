package slavsquatsuperstar.mayonez.assets;

import slavsquatsuperstar.mayonez.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * A resource file used by this program.
 */
public class Asset {

    public final URL path;
    public final boolean isClasspath;

    public Asset(String filename, boolean isClasspath) {
        this.isClasspath = isClasspath;
        this.path = isClasspath ? Assets.getClasspathURL(filename) : Assets.getFileURL(filename);
    }

    // Getters
    public InputStream inputStream() throws IOException {
        return path.openStream();
    }

    public OutputStream outputStream(boolean append) throws IOException {
        if (isClasspath) {
            Logger.log("%s: Cannot write to classpath resource", getClass().getSimpleName());
            throw new IOException();
        }
        return FileUtils.openOutputStream(toFile(), append);
    }

    public File toFile() {
        return new File(path.getPath());
    }

    /**
     * Verify that a resource exists at the given URL.
     *
     * @return Whether this {@link Asset} exists.
     */
    public boolean exists() {
        if (isClasspath) {
            try {
                inputStream();
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return toFile().exists();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", getClass().getSimpleName(), path.getPath(), (isClasspath ? "Classpath" : "External"));
    }
}