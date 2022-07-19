package slavsquatsuperstar.mayonez

import slavsquatsuperstar.mayonez.fileio.AssetType
import slavsquatsuperstar.mayonez.fileio.Assets
import slavsquatsuperstar.mayonez.fileio.JSONFile

/**
 * A storage class for game parameters and statistics.
 *
 * @author SlavSquatSuperstar
 */
// TODO create default compile tile constants
object Preferences : GameConfig() {

    init {
        copyFrom(Defaults)
    }

    internal fun readPreferences() {
        if (!Mayonez.INIT_ASSETS) Mayonez.init()
        if (Mayonez.INIT_PREFERENCES) return

        // Don't set preferences until after file has been read to prevent default values and nulls
        val preferenceFile = Assets.createAsset("preferences.json", AssetType.CLASSPATH, JSONFile::class.java)!!

        // Read preferences file and update game configuration
        loadFrom(preferenceFile.json)
    }

    /* Window */
    @JvmStatic
    val title: String
        get() = getString("title")

    @JvmStatic
    val version: String
        get() = getString("version")

    @JvmStatic
    val screenWidth: Int
        get() = getInt("screen_width")

    @JvmStatic
    val screenHeight: Int
        get() = getInt("screen_height")

    /* Logging */
    @JvmStatic
    val logLevel: Int
        get() = getInt("log_level")

    @JvmStatic
    val saveLogs: Boolean
        get() = getBoolean("save_logs")

    @JvmStatic
    val logDirectory: String
        get() = getString("log_directory")

    /* File I/O */
    @JvmStatic
    val fileCharset: String = getString("file_charset")

    /* Renderer */
    @JvmStatic
    val bufferCount: Int
        get() = getInt("buffers")

    @JvmStatic
    val FPS: Int
        get() = getInt("fps")

    @JvmStatic
    val maxBatchSize: Int
        get() = getInt("max_batch_size")

    @JvmStatic
    val maxTextureSlots: Int
        get() = getInt("max_texture_slots")

    /* Physics */
    @JvmStatic
    val impulseIterations: Int
        get() = getInt("physics_iterations")

}