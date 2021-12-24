package slavsquatsuperstar.mayonezgl.renderer;

import org.lwjgl.BufferUtils;
import slavsquatsuperstar.mayonez.Logger;
import slavsquatsuperstar.mayonez.fileio.Asset;
import slavsquatsuperstar.mayonez.fileio.AssetType;
import slavsquatsuperstar.mayonez.fileio.Assets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memSlice;

/**
 * A LWJGL image file used by this program.<br> Sources:
 * <ul>
 *    <li>https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Image.java</li>
 *    <li>https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/util/IOUtil.java</li>
 *    <li>https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/glfw/GLFWUtil.java</li>
 * </ul>
 *
 * @author SlavSquatSuperstar
 */
public class TextureGL extends Asset {

    private ByteBuffer image;
    private int width, height, channels;
    private int texID;

    public TextureGL(String filename, AssetType type) {
        super(filename, type);
        Assets.setAsset(filename, this);
        readImage();
        createTexture();
    }

    public TextureGL(String filename) {
        this(filename, AssetType.CLASSPATH);
    }

    private void readImage() {
        try {
            byte[] imageData = Assets.readContents(getFilename());
            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
            imageBuffer = memSlice(imageBuffer.put(imageData).flip());

            IntBuffer w = BufferUtils.createIntBuffer(1);
            IntBuffer h = BufferUtils.createIntBuffer(1);
            IntBuffer comp = BufferUtils.createIntBuffer(1);

            if (!stbi_info_from_memory(imageBuffer, w, h, comp))
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            else
                Logger.log("Texture: Successfully loaded image \"%s\"", getFilename());

            // Decode the image
            stbi_set_flip_vertically_on_load(true); // GL uses (0,0) as bottom left, unlike AWT
            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (image == null) {
                Logger.warn("Texture: Could not load image \"%s\"", getFilename());
                Logger.warn("GL: " + stbi_failure_reason());
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }

            width = w.get(0);
            height = h.get(0);
            channels = comp.get(0);
        } catch (IOException | NullPointerException e) {
            Logger.log("Could not read image \"%s\"", getFilename());
        }
    }

    private void createTexture() {
        // Generate texture on CPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // wrap if too big
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // pixelate when stretching
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // pixelate when shrinking

        // Upload image to GPU
        int format;
        if (channels == 3) { // jpg image, no transparency
//            if ((width & 3) != 0)
//                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            format = GL_RGB;
        } else { // png image, has transparency
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            format = GL_RGBA;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, image);
        Logger.log("Texture: Loaded image \"%s\"", getFilename());
        stbi_image_free(image);
    }

    public void bind(int texSlot) {
        glActiveTexture(GL_TEXTURE0 + texSlot + 1);
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}