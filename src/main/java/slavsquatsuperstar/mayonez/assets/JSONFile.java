package slavsquatsuperstar.mayonez.assets;

import slavsquatsuperstar.mayonez.Logger;
import slavsquatsuperstar.mayonez.Preferences;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Stores and manipulates a JSON object from a .json file.
 *
 * @author SlavSquatSuperstar
 */
public class JSONFile extends Asset {

    private JSONObject json;

    public JSONFile(String filename, boolean isClasspath) {
        super(filename, isClasspath);
        read();
    }

    // File I/O Methods

    /**
     * Parses the JSON object stored in this file. Called automatically upon object creation.
     */
    public void read() {
        try (InputStream in = inputStream()) {
            json = new JSONObject(new JSONTokener(IOUtils.toString(in, Preferences.CHARSET)));
        } catch (FileNotFoundException e) {
            Logger.warn("JSONFile: File \"%s\" not found", path);
        } catch (IOException e) {
            Logger.warn("TextFile: Could not read file");
        } catch (JSONException e) {
            Logger.warn("JSONUtil: Could not parse JSON file");
        }
    }

    // JSON Methods

    /**
     * Saves JSON data to this file.
     */
    public void save() {
        try (OutputStream out = outputStream(false)) {
            IOUtils.write(json.toString(4).getBytes(StandardCharsets.UTF_8), out);
        } catch (FileNotFoundException e) {
            Logger.warn("TextFile: File \"%s\" not found\n", path);
        } catch (IOException e) {
            Logger.warn(ExceptionUtils.getStackTrace(e));
            Logger.warn("TextFile: Could not save to file");
        }
    }

    // JSON Getters and Setters

    public JSONObject getObj(String key) {
        try {
            return json.getJSONObject(key);
        } catch (JSONException e) {
            logError(key, "an object");
        }
        return null;
    }

    public JSONArray getArray(String key) {
        try {
            return json.getJSONArray(key);
        } catch (JSONException e) {
            logError(key, "an array");
        }
        return null;
    }

    public String getStr(String key) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            logError(key, "a string");
        }
        return null;
    }

    public boolean getBool(String key) {
        try {
            return json.getBoolean(key);
        } catch (JSONException e) {
            logError(key, "a boolean");
        }
        return false;
    }

    public int getInt(String key) {
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            logError(key, "an integer");
        }
        return -1;
    }

    public double getFloat(String key) {
        try {
            return json.getFloat(key);
        } catch (NumberFormatException e) {
            logError(key, "a float");
        }
        return -1f;
    }

    public void setProperty(String key, Object value) {
        json.put(key, value);
    }

    private static void logError(String key, String type) {
        Logger.warn("JSONFile: Value at \"%s\" is not %s.", key, type);
    }

}
