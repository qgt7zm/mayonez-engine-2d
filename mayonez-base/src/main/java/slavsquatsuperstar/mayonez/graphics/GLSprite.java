package slavsquatsuperstar.mayonez.graphics;

import org.joml.Vector2f;
import org.joml.Vector4f;
import slavsquatsuperstar.mayonez.GameObject;
import slavsquatsuperstar.mayonez.Transform;
import slavsquatsuperstar.mayonez.fileio.GLTexture;
import slavsquatsuperstar.mayonez.annotations.EngineType;
import slavsquatsuperstar.mayonez.annotations.UsesEngine;

/**
 * A component that draws an image at a {@link GameObject}'s position. For use the GL engine.
 *
 * @author SlavSquatSuperstar
 */
@UsesEngine(EngineType.GL)
public final class GLSprite extends Sprite { // = Gabe's SpriteRenderer

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private GLTexture texture = null; // Rendering just a color

    private Transform lastTransform;
    private boolean dirty = true; // "Dirty" flag, object state changed since last frame

    private Vector2f[] texCoords = new Vector2f[]{
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    public GLSprite(Vector4f color) {
        this.color = color;
    }

    public GLSprite(GLTexture texture) {
        this.texture = texture;
    }

    public GLSprite(GLTexture texture, Vector2f[] texCoords) {
        this.texture = texture;
        this.texCoords = texCoords;
    }

    // Game Loop Methods

    @Override
    public void start() {
        lastTransform = parent.transform.copy();
    }

    @Override
    public void update(float dt) { // Have we moved?
        if (!lastTransform.equals(parent.transform)) {
            lastTransform.set(parent.transform);
            dirty = true;
        }
    }

    // Getters and Setters

    public Vector4f getColor() {
        return this.color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
        dirty = true;
    }

    public GLTexture getTexture() {
        return texture;
    }

    public void setTexture(GLTexture texture) {
        this.texture = texture;
        dirty = true;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setClean() {
        dirty = false;
    }

    @Override
    public GLSprite copy() {
        return new GLSprite(texture, texCoords);
    }

}