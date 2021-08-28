package slavsquatsuperstar.fileio

import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import slavsquatsuperstar.mayonez.Logger.trace
import slavsquatsuperstar.mayonez.Logger.warn
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Path
import java.util.regex.Pattern

/**
 * A utility class for file I/O and managing the application's resources safely.
 *
 * @author SlavSquatSuperstar
 */
object Assets {

    // TODO reload, create if absent

    private val ASSETS = HashMap<String, Asset?>()

    /**
     * Recursively searches a resource directory inside the JAR and adds all assets.
     *
     * @param directory a folder inside the jar
     */
    @JvmStatic
    fun scanResources(directory: String) {
        val reflections = Reflections(
            ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(directory)).setScanners(ResourcesScanner())
        )
        val resources = reflections.getResources(Pattern.compile(".*\\.*"))
        resources.forEach { createAsset(it, AssetType.CLASSPATH) } // Create an asset from each path
        trace("Assets: Created ${resources.size} resources inside $directory")
    }

    /**
     * Recursively searches a file directory outside the JAR and adds all assets.
     *
     * @param directory a folder outside the jar
     */
    @JvmStatic
    fun scanFiles(directory: String) {
        val files = searchFiles(directory)
        files.forEach { createAsset(it, AssetType.LOCAL) }
        trace("Assets: Created ${files.size} files inside $directory")
    }

    @JvmStatic
    fun searchFiles(directory: String): MutableList<String> {
        val folder = File(directory)
        val files: MutableList<String> = ArrayList()
        if (folder.listFiles() == null) return files // If file return empty list

        for (f in folder.listFiles()!!) {
            if (f.name == ".DS_Store") continue
            val filename = Path.of(directory, f.name).toString()
            if (f.isFile) files.add(filename) else {
                files.addAll(searchFiles(filename))
            }
        }
        return files
    }

    // Asset Accessors / Mutators

    /**
     * Indicates whether the [Asset] stored under the given location exists.
     *
     * @param filename    the location of the asset
     * @return if a file exists at the given path
     */
    @JvmStatic
    fun hasAsset(filename: String): Boolean = filename in ASSETS

    /**
     * Retrieves the [Asset] at the given location, if it exists.
     *
     * @param filename the path to the file
     */
    @JvmStatic
    fun getAsset(filename: String): Asset? = ASSETS[filename]

    /**
     * Creates a new [Asset], if it does not exist already, and stores it for future use.
     *
     * @param filename  the location of the asset
     * @param type      what kind of asset to create
     * @return the file, if successfully created
     */
    @JvmStatic
    fun createAsset(filename: String, type: AssetType): Asset {
        if (hasAsset(filename))
            trace("Assets: Resource \"%s\" already exists", filename)
        else
            ASSETS[filename] = Asset(filename, type)
        return getAsset(filename)!!
    }

    // I/O Stream Methods

    @JvmStatic
    @Throws(IOException::class)
    fun openInputStream(filename: String): InputStream =
        getAsset(filename)?.inputStream() ?: throw FileNotFoundException("Assets: File \"$filename\" not found")

    @JvmStatic
    @Throws(IOException::class)
    fun openOutputStream(filename: String, append: Boolean): OutputStream =
        getAsset(filename)?.outputStream(append) ?: throw FileNotFoundException("Assets: File \"$filename\" not found")


    @JvmStatic
    fun readContents(filename: String): ByteArray? = openInputStream(filename).readAllBytes()

    // Asset URL Methods

    /**
     * Accesses a classpath resource from within the .jar executable.
     *
     * @param path The location of the file inside the root resource directory.
     * @return The file's URL.
     */
    @JvmStatic
    fun getClasspathURL(path: String): URL? = ClassLoader.getSystemResource(path)

    /**
     * Accesses an external file from outside the .jar executable.
     *
     * @param filename the location of the file inside the computer's local storage
     * @return the file's URL
     */
    @JvmStatic
    fun getFileURL(filename: String): URL? {
        return try {
            File(filename).toURI().toURL()
        } catch (e: MalformedURLException) {
            warn("Assets: Invalid file path \"%s\"", filename)
            null
        }
    }

}