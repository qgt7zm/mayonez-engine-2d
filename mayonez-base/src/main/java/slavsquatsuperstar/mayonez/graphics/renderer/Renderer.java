package slavsquatsuperstar.mayonez.graphics.renderer;

import slavsquatsuperstar.mayonez.SceneManager;
import slavsquatsuperstar.mayonez.engine.GameLayer;
import slavsquatsuperstar.mayonez.graphics.Camera;

import java.awt.*;

/**
 * Draws objects or other information in the program to the screen.
 *
 * @author SlavSquatSuperstar
 */
public interface Renderer extends GameLayer {

    // Game Loop Methods

    /**
     * Prepares the renderer for allocating data.
     */
    void start();

    /**
     * Redraws everything to the screen.
     *
     * @param g2 a {@link Graphics2D} object for the AWT engine
     */
    void render(Graphics2D g2);

    /**
     * Removes all objects and frees resources upon quitting the program.
     */
    void stop();

    // Helper Methods

    /**
     * The current scene's camera.
     *
     * @return the camera
     */
    default Camera getCamera() {
        return SceneManager.getCurrentScene().getCamera();
    }

}
