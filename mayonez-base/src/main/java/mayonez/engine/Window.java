package mayonez.engine;

import mayonez.input.keyboard.*;
import mayonez.input.mouse.*;

/**
 * The main application window that renders the game to the screen and detects
 * input events.
 *
 * @author SlavSquatSuperstar
 */
public sealed interface Window permits JWindow, GLWindow {

    // Property Getters

    String getTitle();

    int getWidth();

    int getHeight();

    // Resource Management Methods

    /**
     * Setup system resources and show the window.
     */
    void start();

    /**
     * Free system resources and destroy the window.
     */
    void stop();

    // Game Loop Methods

    /**
     * Whether the window is still open or has been closed (x-ed out) by the user.
     *
     * @return if the window is not closed
     */
    boolean notClosedByUser();

    /**
     * Poll any input or window events.
     */
    void beginFrame();

    /**
     * Redraw the game to the screen.
     */
    void render();

    /**
     * Reset events and input listeners.
     */
    void endFrame();

    // Input Methods

    /**
     * Set the keyboard listener for this window and mark it as the active
     * instance for the input manager.
     *
     * @param keyboard a {@link mayonez.input.keyboard.KeyManager} instance
     */
    void setKeyInput(KeyManager keyboard);

    /**
     * Set the mouse listener for this window and mark it as the active
     * instance for the input manager.
     *
     * @param mouse a {@link mayonez.input.mouse.MouseManager} instance
     */
    void setMouseInput(MouseManager mouse);

}
