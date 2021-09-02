package slavsquatsuperstar.fileio

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

/**
 * A resource file used by this program. Stores a path and opens streams to that location.
 *
 * @author SlavSquatSuperstar
 */
open class Asset(val filename: String, internal val type: AssetType) {

    private val isClasspath: Boolean = type == AssetType.CLASSPATH

    @JvmField
    val path: URL? =
        if (isClasspath) Assets.getClasspathURL(filename)
        else Assets.getFileURL(filename)

    // I/O Methods

    @Throws(IOException::class)
    fun inputStream(): InputStream? {
        return if (isClasspath) ClassLoader.getSystemResourceAsStream(filename)
        else Files.newInputStream(Paths.get(filename))
    }


    @Throws(IOException::class)
    fun outputStream(append: Boolean): OutputStream? {
        return if (isClasspath) throw UnsupportedOperationException("Cannot write to classpath resource $filename")
        else FileUtils.openOutputStream(toFile(), append)
    }

    // Helper Methods

    private fun toFile(): File = File(path!!.path)

    fun isValid(): Boolean {
        return when (type) {
            AssetType.CLASSPATH -> path != null
            AssetType.LOCAL -> toFile().exists()
        }
    }

    override fun toString(): String = "$type ${javaClass.simpleName} \"$filename\""

}