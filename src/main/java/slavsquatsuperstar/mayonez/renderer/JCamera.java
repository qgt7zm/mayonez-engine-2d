package slavsquatsuperstar.mayonez.renderer;

import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.mayonez.GameObject;
import slavsquatsuperstar.mayonez.Logger;
import slavsquatsuperstar.mayonez.Preferences;
import slavsquatsuperstar.mayonez.Script;
import slavsquatsuperstar.mayonez.input.MouseInput;
import slavsquatsuperstar.mayonez.physics2d.colliders.AlignedBoxCollider2D;
import slavsquatsuperstar.mayonez.scripts.DragAndDrop;
import slavsquatsuperstar.mayonez.scripts.KeepInScene;

/**
 * A scene camera for the Java engine.
 *
 * @author SlavSquatSuperstar
 */
public final class JCamera extends Script implements Camera {

    private final float width, height; // In world units
    private final float minX, minY, maxX, maxY;
    private GameObject subject;
    private Script keepInScene, dragAndDrop; // Reference to parent scripts

    public JCamera(float sceneWidth, float sceneHeight, float cellSize) {
        width = (float) Preferences.SCREEN_WIDTH / cellSize;
        height = (float) Preferences.SCREEN_HEIGHT / cellSize;
        minX = 0;
        minY = 0; //(int) (-28f / cellSize); // account for the bar on top of the window
        maxX = sceneWidth;
        maxY = sceneHeight;
    }

    // Static (Factory) Methods

    /**
     * Creates a container {@link GameObject} to hold a Camera object
     *
     * @param camera the camera instance
     * @return the object
     */
    public static GameObject createCameraObject(JCamera camera) {
        return new GameObject("Camera", camera.getHalfSize()) {
            @Override
            protected void init() {
                addComponent(camera);
                // Allow camera to be moved with mouse
                addComponent(camera.dragAndDrop = new DragAndDrop("right mouse", true) {
                    // Reset camera position with double click
                    @Override
                    public void onMouseDown() {
                        if (MouseInput.getClicks() == 2) {
                            camera.setOffset(new Vec2(0, 0));
                        }
                    }
                }.setEnabled(false));
                // Keep camera inside scene and add camera collider
                addComponent(new AlignedBoxCollider2D(new Vec2(camera.width, camera.height)).setTrigger(true));
                addComponent(camera.keepInScene = new KeepInScene(camera.minX, camera.minY, camera.maxX, camera.maxY, KeepInScene.Mode.STOP));
            }

            // Don't want to get rid of the camera!
            @Override
            public void destroy() {
            }

            @Override
            public boolean isDestroyed() {
                return false;
            }
        };
    }

    @Override
    public void update(float dt) {
        // Follow subject (Set position to subject position)
        if (subject != null) transform.position.set(subject.transform.position);
    }

    // Getters and setters

    @Override
    public Vec2 getOffset() {
        return transform.position.sub(getHalfSize());
    }

    @Override
    public void setOffset(Vec2 offset) {
        transform.position.set(offset.add(getHalfSize()));
    }

    public Vec2 getHalfSize() {
        return new Vec2(width, height).div(2);
    }

    /**
     * Toggles the camera's ability to be dragged with the mouse.
     *
     * @param enabled If mouse should move the camera. Set to true to disable subject following.
     * @return this object
     */
    public JCamera enableFreeMovement(boolean enabled) {
        dragAndDrop.setEnabled(enabled);
        if (enabled && subject != null) {
            subject = null;
            Logger.log("Subject following for %s has been disabled because freecam was enabled.", this, dragAndDrop);
        }
        return this;
    }

    /**
     * Toggles the camera's ability to stay within the scene bounds.
     *
     * @param enabled if the camera should stay inside the scene
     * @return this object
     */
    public JCamera enableKeepInScene(boolean enabled) {
        keepInScene.setEnabled(enabled);
        return this;
    }

    /**
     * Sets a subject for this Camera to follow, disabling free camera movement.
     *
     * @param subject A {@link GameObject} in the scene. Set to null to disable subject following.
     * @return this object
     */
    public JCamera setSubject(GameObject subject) {
        this.subject = subject;
        if (subject != null) {
//            enableKeepInScene(subject.getComponent(KeepInScene.class) != null);
            if (dragAndDrop.isEnabled()) {
                dragAndDrop.setEnabled(false);
                Logger.log("Freecam for %s has been disabled because a subject was set.", this, dragAndDrop);
            }
        }
        return this;
    }
}