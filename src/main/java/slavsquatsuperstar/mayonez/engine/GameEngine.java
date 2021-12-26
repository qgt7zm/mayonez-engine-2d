package slavsquatsuperstar.mayonez.engine;

import org.apache.commons.lang3.exception.ExceptionUtils;
import slavsquatsuperstar.mayonez.Logger;
import slavsquatsuperstar.mayonez.Mayonez;
import slavsquatsuperstar.mayonez.Scene;
import slavsquatsuperstar.mayonez.physics2d.Physics2D;
import slavsquatsuperstar.mayonez.renderer.Renderer;

/**
 * The application that contains the engine's core loop.
 *
 * @author SlavSquatSuperstar
 */
// TODO probably better off with enums of companion objects
// TODO sealed, core package
public sealed abstract class GameEngine permits JGame, GLGame{

    private boolean running = false;

    // Game Layers
    protected Physics2D physics;
    protected Renderer renderer;
    protected Scene scene;
    protected Window window;

    protected GameEngine() {}

    // Resource Management Methods

    /**
     * Set up system resources and initialize the application
     */
    public final void start() {
        if (!running) {
            running = true;
            window.start();
            startScene();
            run();
        }
    }

    /**
     * Free system resources and quit the application.
     */
    public final void stop() {
        if (running) {
            running = false;
            window.stop();
        }
    }

    // Game Loop Methods

    public void beginFrame() {
        window.beginFrame();
    }

    public final void run() {
        // All time values are in seconds
        float lastTime = 0f; // Last time the game loop iterated
        float currentTime; // Time of current frame
        float deltaTime = 0f; // Unprocessed time since last frame

        // For Debugging
        float timer = 0f;
        int frames = 0;

        try {
            // Render to the screen until the user closes the window or presses the ESCAPE key
            while (running && window.notClosedByUser()) {
                boolean ticked = false; // Has the engine actually updated?

                currentTime = getCurrentTime();
                float passedTime = currentTime - lastTime;
                deltaTime += passedTime;
                timer += passedTime;
                lastTime = currentTime;  // Reset lastTime

                beginFrame();
                // Update the game as many times as possible even if the screen freezes
                while (deltaTime >= Mayonez.TIME_STEP) {
                    update(deltaTime);
                    deltaTime -= Mayonez.TIME_STEP;
                    ticked = true;
                }
                // Only render if the game has updated to save resources
                if (ticked) {
                    render();
                    frames++;
                }
                // Print FPS count each second
                if (timer >= 1) {
                    Logger.trace("Frames per Second: %d", frames);
                    frames = 0;
                    timer = 0;
                }

                endFrame();
            }
        } catch (Exception e) {
            Logger.warn(ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
            Mayonez.stop(1);
        }

        Mayonez.stop(0);
    }

    /**
     * Refreshes all objects in the current scene.
     *
     * @param dt seconds since the last frame
     */
    public abstract void update(float dt) throws Exception;

    /**
     * Redraws all objects in the current scene.
     */
    public abstract void render() throws Exception;

    public void endFrame() {
        window.endFrame();
    }

    // Getters and Setters

    public Window getWindow() {
        return window;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        startScene();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Physics2D getPhysics() {
        return physics;
    }


    // Helper Methods

    public abstract float getCurrentTime();

    /**
     * Starts the current scene, if not null
     */
    private void startScene() {
        if (scene != null && running) {
            scene.start();
            physics.setScene(scene);
            renderer.setScene(scene);
            Logger.trace("Game: Loaded scene \"%s\"", scene.getName());
        }
    }

}