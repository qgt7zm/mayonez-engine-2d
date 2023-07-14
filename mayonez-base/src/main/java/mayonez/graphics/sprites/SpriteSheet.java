package mayonez.graphics.sprites;

import mayonez.graphics.textures.*;
import mayonez.math.*;

/**
 * Stores multiple textures and creates multiple sprites from a larger image.
 *
 * @author SlavSquatSuperstar
 */
public abstract sealed class SpriteSheet permits JSpriteSheet, GLSpriteSheet {

    // Create Sprite Methods

    /**
     * Create individual sprites from the sprite sheet, reading from left to right
     * then top to bottom.
     *
     * @param numSprites the number of sprites on the sheet
     * @param spacing    the space between each sprite in pixels
     */
    protected abstract void createSprites(int numSprites, int spacing);

    /**
     * Move to the next sprite on the sprite sheet.
     *
     * @param imgOrigin the origin of the current sprite on the sprite sheet in pixels
     * @param spacing   the space between each sprite in pixels
     */
    protected abstract void moveToNextSprite(Vec2 imgOrigin, int spacing);

    // Sprite Sheet Getters

    /**
     * The dimensions of the sprite sheet texture in pixels.
     *
     * @return the sheet size
     */
    protected abstract Vec2 getSheetSize();

    // Sprite/Texture Getters

    public abstract Sprite getSprite(int index);

    public abstract Texture getTexture(int index);

    public abstract int numSprites();

    public Sprite[] getSprites() {
        var sprites = new Sprite[numSprites()];
        for (var i = 0; i < sprites.length; i++) sprites[i] = getSprite(i);
        return sprites;
    }

    public Texture[] getTextures() {
        var textures = new Texture[numSprites()];
        for (var i = 0; i < textures.length; i++) textures[i] = getTexture(i);
        return textures;
    }

}
