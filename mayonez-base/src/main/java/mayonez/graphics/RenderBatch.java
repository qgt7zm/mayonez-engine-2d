package mayonez.graphics;

import mayonez.Preferences;
import mayonez.annotations.EngineType;
import mayonez.annotations.UsesEngine;
import mayonez.io.image.GLTexture;
import mayonez.math.Vec2;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

/**
 * A structure containing vertex information of many similar objects and combining them into one large mesh,
 * allowing many sprites and shapes to be drawn in fewer GPU calls. Roughly analog to the {@link java.awt.Graphics2D}
 * from the AWT engine.
 *
 * @author SlavSquatSuperstar
 */
@UsesEngine(EngineType.GL)
public final class RenderBatch {

    // Batch Constants
    public static final int MAX_SPRITES = Preferences.getMaxBatchSprites();
    public static final int MAX_LINES = Preferences.getMaxBatchLines();
    public static final int MAX_TRIANGLES = Preferences.getMaxBatchTriangles();

    // Batch Characteristics
    private final int maxTextureSlots = Preferences.getMaxTextureSlots();
    private final int maxBatchSize;
    private final int zIndex;

    // Vertex Parameters
    private final DrawPrimitive primitive;
    private final int vertexCount; // vertices per primitive
    private final int elementCount; // element per primitive (if dividing into triangles)
    private final int vertexSize; // attributes per vertex
    private final List<GLTexture> textures;

    // GPU Resources
    private int vao; // Vertex array object (VAO) ID for this batch
    private int vbo; // Vertex buffer object (VBO) ID for this batch
    private int ebo; // Element buffer object (EBO)/index buffer ID for this batch
    private final float[] vertices; // Quad vertex data for this batch, containing position, colors, and texture (UV) coordinates
    private int vertexOffset; // Current vertex index

    public RenderBatch(int maxBatchSize, int zIndex, DrawPrimitive primitive) {
        this.maxBatchSize = maxBatchSize;
        this.zIndex = zIndex;

        this.primitive = primitive;
        this.vertexCount = primitive.getVertexCount();
        this.elementCount = primitive.getElementCount();
        this.vertexSize = primitive.getVertexSize();

        // Renderer Fields
        textures = new ArrayList<>(maxTextureSlots);
        vertices = new float[maxBatchSize * vertexCount * vertexSize];

        createBatch();
    }

    // Initialization Methods

    /**
     * Allocates GPU resources to this batch and generates the vertex buffers upon starting the scene.
     */
    private void createBatch() {
        generateVAOs();
        generateVBOs();
        generateEBOs();
        generateVertexIndexBuffer();
    }

    private void generateVAOs() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
    }

    private void generateVBOs() {
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
    }

    private void generateEBOs() {
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, generateIndices(), GL_STATIC_DRAW);
    }

    private void generateVertexIndexBuffer() {
        var offset = 0;
        var attributeSizes = primitive.getAttributeSizes();
        for (var i = 0; i < attributeSizes.length; i++) {
            glVertexAttribPointer(i, attributeSizes[i], GL_FLOAT, false, vertexSize * Float.BYTES, offset);
            glEnableVertexAttribArray(i);
            offset += attributeSizes[i] * Float.BYTES;
        }
    }

    /**
     * Load element indices and define shapes for OpenGL to draw.
     *
     * @return an index array (int buffer)
     */
    private IntBuffer generateIndices() {
        var elements = BufferUtils.createIntBuffer(elementCount * maxBatchSize);
        for (var i = 0; i < maxBatchSize; i++) primitive.addIndices(elements, i);
        elements.flip(); // need to flip an int buffer
        return elements;
    }

    // Renderer Methods

    /**
     * Empties all sprite vertices and textures from the batch and readies it for buffering.
     */
    public void clear() {
        vertexOffset = 0;
        Arrays.fill(vertices, 0);
        textures.clear();
    }

    /**
     * Upload all vertex data to the GPU after buffering.
     */
    public void uploadVertices() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }

    public void drawBatch() {
        bindVertices();
        drawVertices();
        unbindVertices();
    }

    private void bindVertices() {
        for (var i = 0; i < textures.size(); i++) textures.get(i).bind(i);
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    private void drawVertices() {
        var numVertices = (vertexOffset * elementCount) / (vertexSize * vertexCount);
        glDrawElements(primitive.getPrimitiveType(), numVertices, GL_UNSIGNED_INT, 0);
    }

    private void unbindVertices() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        textures.forEach(GLTexture::unbind);
    }

    /**
     * Free GPU resources upon stopping the scene.
     */
    public void deleteBatch() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }

    // Helper Methods

    /**
     * Adds a texture to this render batch if the texture is not present and returns the texture ID.
     *
     * @param tex the texture
     * @return the texture ID
     */
    public int addTexture(GLTexture tex) {
        if (tex == null) return 0; // if color return 0
        if (!hasTexture(tex)) textures.add(tex); // add if don't have texture
        return getTexID(tex);
    }

    private int getTexID(GLTexture tex) {
        return textures.indexOf(tex) + 1;
    }

    // Push Vertex Methods

    public void pushInt(int i) {
        vertices[vertexOffset++] = i;
    }

    public void pushFloat(float f) {
        vertices[vertexOffset++] = f;
    }

    public void pushVec2(Vec2 v) {
        pushFloat(v.x);
        pushFloat(v.y);
    }

    public void pushVec4(Vector4f v) {
        pushFloat(v.x);
        pushFloat(v.y);
        pushFloat(v.z);
        pushFloat(v.w);
    }

    // Getter Methods

    public DrawPrimitive getPrimitive() {
        return primitive;
    }

    public int getZIndex() {
        return zIndex;
    }

    /**
     * If the render batch contains this texture (can always render colors)
     *
     * @param tex the texture, or null if drawing a color
     * @return if this texture is used by the batch
     */
    public boolean hasTexture(GLTexture tex) {
        return tex == null || textures.contains(tex);
    }

    /**
     * If the render batch has capacity for another texture.
     *
     * @return if there are unused texture slots
     */
    public boolean hasTextureRoom() {
        return textures.size() < maxTextureSlots;
    }

    /**
     * If the render batch has capacity for another primitive object.
     *
     * @return if there are still unused vertices
     */
    public boolean hasVertexRoom() {
        return vertexOffset < vertices.length;
    }

    @Override
    public String toString() {
        return String.format("Render Batch (Type: %s, Capacity: %d/%d, Z-Index: %d)",
                primitive, vertexOffset / (vertexCount * vertexSize), maxBatchSize, zIndex);
    }
}
