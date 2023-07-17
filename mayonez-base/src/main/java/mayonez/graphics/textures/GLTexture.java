package mayonez.graphics.textures;

import mayonez.*;
import mayonez.annotations.*;
import mayonez.io.image.*;
import mayonez.math.*;
import mayonez.math.shapes.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memSlice;

/**
 * An image file used by the GL engine.
 * <p>
 * Sources:
 * <ul>
 *    <li><a href="https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Image.java">org.lwjgl.demo.stb.Image</a></li>
 *    <li><a href="https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/util/IOUtil.java">org.lwjgl.demo.stb.IOUtil</a></li>
 *    <li><a href="https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/glfw/GLFWUtil.java">org.lwjgl.demo.glfw.GLFWUtil</a></li>
 * </ul>
 *
 * @author SlavSquatSuperstar
 */
@UsesEngine(EngineType.GL)
public final class GLTexture extends Texture {

    // Image Data Fields
    private ByteBuffer image;
    private int width, height, channels;
    private boolean imageFreed;

    // GPU Fields
    private int texID;
    private final Vec2[] texCoords;
    public static final Vec2[] DEFAULT_TEX_COORDS = Rectangle.rectangleVertices(new Vec2(0.5f), new Vec2(1f), 0f);

    private GLTexture(String filename, Vec2[] texCoords, ByteBuffer image) {
        super(filename);
        this.texCoords = texCoords;
        this.image = image;
        imageFreed = false;
    }

    /**
     * Create a brand-new GLTexture with the given filename.
     *
     * @param filename the file location
     */
    public GLTexture(String filename) {
        this(filename, DEFAULT_TEX_COORDS, null);
        readImage();
        createTexture();
    }

    /**
     * Create a GLTexture from a portion of another texture.
     *
     * @param texture   another texture
     * @param texCoords the image coordinates, between 0-1
     */
    public GLTexture(GLTexture texture, Vec2[] texCoords) {
        this(texture.getFilename(), texCoords, texture.image);
        this.texID = texture.texID;

//        readImage();  // this fixes wrong textures, but uses a lot of memory
//        createTexture();

        var size = texCoords[2].sub(texCoords[0]); // relative size
        this.width = (int) (texture.width * size.x); // get new image size
        this.height = (int) (texture.height * size.y);
        this.channels = texture.channels;
    }

    // Read Image File Methods

    @Override
    protected void readImage() {
        try {
            var imageBuffer = readImageBytes();
            var info = getImageInfo(imageBuffer);
            image = loadImage(imageBuffer, info);
        } catch (Exception e) {
            Logger.error("Could not read image file \"%s\"", getFilename());
        }
    }

    private ByteBuffer readImageBytes() throws TextureException, IOException {
        var imageBytes = new ImageIOManager().read(openInputStream());
        if (imageBytes == null) {
            throw new TextureException("Image data is null");
        }

        var imageBuffer = BufferUtils.createByteBuffer(imageBytes.length);
        return memSlice(imageBuffer.put(imageBytes).flip());
    }

    private ImageInfo getImageInfo(ByteBuffer imageBuffer)
            throws TextureException {
        var width = BufferUtils.createIntBuffer(1);
        var height = BufferUtils.createIntBuffer(1);
        var channels = BufferUtils.createIntBuffer(1);

        if (!stbi_info_from_memory(imageBuffer, width, height, channels)) {
            throwExceptionOnFailure();
        } else {
            Logger.debug("OpenGL: Loaded image \"%s\"", getFilename());
        }

        this.width = width.get(0);
        this.height = height.get(0);
        this.channels = channels.get(0);
        return new ImageInfo(width, height, channels);
    }

    private ByteBuffer loadImage(ByteBuffer imageBuffer, ImageInfo info)
            throws TextureException {
        stbi_set_flip_vertically_on_load(true); // GL uses (0,0) as bottom left, unlike AWT
        var image = stbi_load_from_memory(imageBuffer,
                info.width(), info.height(), info.channels(), 0);

        if (image == null) {
            Logger.error("OpenGL: Could not load image file \"%s\"", getFilename());
            throwExceptionOnFailure();
        }
        return image;
    }

    private static void throwExceptionOnFailure() throws TextureException {
        String msg = "Reason for failure: " + stbi_failure_reason();
        Logger.error(msg);
        throw new TextureException(msg);
    }

    // Create Texture Methods

    private void createTexture() {
        generateTexID();
        setTextureParameters();
        uploadImage();
    }

    private void generateTexID() {
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    private static void setTextureParameters() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // wrap if too big
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // pixelate when stretching
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // pixelate when shrinking
    }

    private void uploadImage() {
        if (!imageFreed) {
            int format = getImageFormat();
            glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, image);
            stbi_image_free(image); // free from memory
            image = null;
            imageFreed = true;
        }
    }

    private int getImageFormat() {
        if (channels == 3) { // jpg image, no transparency
            return GL_RGB;
        } else { // png image, has transparency
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            return GL_RGBA;
        }
    }

    // Render Batch Methods

    public void bind(int texSlot) {
        glActiveTexture(GL_TEXTURE0 + texSlot + 1); // count from 1
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    // Image Getters

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public Vec2[] getTexCoords() {
        return texCoords;
    }

}

