package mayonez.graphics.sprites

import mayonez.*
import mayonez.graphics.*
import mayonez.graphics.textures.*
import mayonez.renderer.*

/**
 * Draws a [Texture] at a [GameObject]'s position. To instantiate a sprite,
 * use [Sprites.createSprite]. See [Texture] for more information.
 *
 * @author SlavSquatSuperstar
 */
sealed class Sprite : Component(UpdateOrder.RENDER), Renderable {

    companion object {
        @JvmStatic
        protected val DEFAULT_COLOR: MColor = Colors.WHITE
    }

    // Sprite Properties

    private var spriteXf: Transform? = null

    /**
     * Get the width of this sprite's stored texture in pixels.
     *
     * @return the image width, or 0 if drawing a color
     */
    protected abstract val imageWidth: Int

    /**
     * Get the height of this sprite's stored texture in pixels.
     *
     * @return the image height, or 0 if drawing a color
     */
    protected abstract val imageHeight: Int

    // Getters and Setters

    /**
     * Get the color of this sprite, white by default if drawing a texture.
     *
     * @return the color
     */
    abstract fun getColor(): MColor

    /**
     * Set the color of this sprite, or recolor the current texture.
     *
     * @param color the color
     */
    abstract fun setColor(color: MColor?)

    /**
     * Get the sprite's transform in the parent object's local space.
     *
     * @return the sprite transform
     */
    fun getSpriteTransform(): Transform? = spriteXf

    /**
     * Set additional position, rotation, and size modifiers for the sprite.
     *
     * @param spriteXf the transform
     * @return this sprite
     */
    fun setSpriteTransform(spriteXf: Transform?): Sprite {
        this.spriteXf = spriteXf
        return this
    }

    /**
     * Get the texture this sprite draws.
     *
     * @return the texture, or null if drawing a color
     */
    abstract fun getTexture(): Texture?

    /**
     * Set the texture this sprite draws.
     *
     * @param texture the new texture
     */
    abstract fun setTexture(texture: Texture?)

    // Renderable Methods

    final override fun getZIndex(): Int = gameObject.zIndex

    final override fun isInUI(): Boolean = false

    // Copy Methods

    /**
     * Construct a new sprite with the same image but not attached to any
     * [mayonez.GameObject].
     *
     * @return a copy of this image
     */
    abstract fun copy(): Sprite?

}