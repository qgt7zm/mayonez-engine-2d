package mayonez;

import mayonez.graphics.*;
import mayonez.graphics.camera.*;
import mayonez.graphics.debug.*;
import mayonez.graphics.renderer.*;
import mayonez.graphics.sprites.*;
import mayonez.graphics.textures.*;
import mayonez.math.Random;
import mayonez.math.*;
import mayonez.physics.*;
import mayonez.physics.colliders.*;
import mayonez.physics.dynamics.*;
import mayonez.util.*;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * An in-game world or level that holds multiple {@link mayonez.GameObject}s. Each scene
 * can  be given a background image or color.
 * <p>
 * Usage: Create a scene by instantiating a subclass or anonymous instance of
 * {@link mayonez.Scene}. Add objects to the scene by calling {@link #addObject}
 * inside the {@link #init()} method. Scenes may also define custom game logic
 * and graphics inside {@link #onUserUpdate} and {@link #onUserRender}.
 * <p>
 * See {@link mayonez.GameObject} and {@link mayonez.SceneManager} for more information.
 *
 * @author SlavSquatSuperstar
 */
// TODO current cursor object
// TODO maybe don't spam "add/remove object" in log
public abstract class Scene {

    private static int sceneCounter = 0; // total number of scenes created

    // Scene Information
    final int sceneID; // UUID for this scene
    private final String name;
    private final float scale; // scene scale, or how many pixels correspond to a world unit
    private final Vec2 size; // scene size, or zero if unbounded

    // Scene Objects
    private final List<GameObject> objects;
    private final SceneLayer[] layers;

    // Renderers
    private Camera camera;
    protected final Sprite background;
    private final RenderLayer renderLayer;

    // Physics
    private final PhysicsWorld physics;

    // Scene State
    private final Queue<Runnable> changesToScene; // Use separate add/remove buffer to avoid concurrent exceptions
    private SceneState state; // if paused or running

    /**
     * Creates an empty scene with size of 0x0 and a scale of 1.
     */
    public Scene(String name) {
        this(name, 0, 0, 1);
    }

    /**
     * Creates an empty scene and sets the bounds and scale.
     *
     * @param name   the name of the scene
     * @param width  the width of the scene, in pixels
     * @param height the height of the scene, in pixels
     * @param scale  the scale of the scene
     */
    public Scene(String name, int width, int height, float scale) {
        sceneID = sceneCounter++;
        this.name = name;
        this.size = new Vec2(width, height).div(scale);
        this.scale = scale;
        background = Sprites.createSprite(Colors.WHITE);

        // Scene state
        state = SceneState.STOPPED;

        // Initialize layers
        objects = new ArrayList<>();
        layers = new SceneLayer[SceneLayer.NUM_LAYERS];

        renderLayer = new RenderLayer(background, size, scale);
        physics = new DefaultPhysicsWorld();

        // Scene changes
        changesToScene = new LinkedList<>();
    }

    // Initialization Methods

    /**
     * Initialize all objects and begin updating the scene. Calls {@link GameObject#start()}
     * for all objects added on start.
     */
    final void start() {
        // Set up layers
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new SceneLayer(i);
        }

        clearSceneLayers();
        addCameraToScene();
        state = SceneState.RUNNING;
        init();
        objects.forEach(obj -> changesToScene.offer(() -> this.startGameObject(obj) ));
        processSceneChanges();
    }

    private void addCameraToScene() {
        camera = CameraFactory.createCamera(scale);
        addObject(CameraFactory.createCameraObject(camera));
    }

    /**
     * Add game objects to this scene before the scene starts or initialize fields
     * after this scene has been loaded.
     * The method {@link #getCamera()} is accessible here.
     * <p>
     * Usage: Subclasses may override this method and can also call {@code super.init()}.
     * <p>
     * Warning: Calling {@code init()} at any other point in time may lead to unintended errors
     * and should be avoided!
     */
    protected void init() {
    }

    // Update Methods

    /**
     * Moves everything in the scene forward in time by a small increment, including physics, scripts, and UI.
     *
     * @param dt seconds since the last frame
     */
    // TODO Make hidden and call from SceneManager
    final void update(float dt) {
        onUserUpdate(dt);
        if (isRunning()) updateSceneObjects(dt);
        processSceneChanges();
    }

    /**
     * Provide user-defined update behavior for this scene.
     *
     * @param dt seconds since the last frame
     */
    protected void onUserUpdate(float dt) {
    }

    private void updateSceneObjects(float dt) {
        objects.forEach(obj -> {
            obj.update(dt);
            if (obj.isDestroyed()) removeObject(obj);
        });
        physics.step(dt);
        camera.gameObject.update(dt); // Update camera last
    }

    private void processSceneChanges() {
        // Remove destroyed objects or add new
        while (!changesToScene.isEmpty()) {
            changesToScene.poll().run();
        }
    }

    // Render Methods

    /**
     * Redraws everything in the current scene, including backgrounds, sprites, and UI.
     *
     * @param g2 the window's graphics object
     */
    final void render(Graphics2D g2) {
        onUserRender();
        objects.forEach(GameObject::debugRender);
        renderLayer.render(g2);
    }

    /**
     * Provide user-defined draw behavior for this scene.
     */
    protected void onUserRender() {
    }

    // Stop Methods

    /**
     * Destroys all objects and stop updating the scene.
     */
    final void stop() {
        destroySceneObjects();
        clearSceneLayers();
        state = SceneState.STOPPED;
    }

    private void destroySceneObjects() {
        camera.setSubject(null);
        objects.forEach(GameObject::onDestroy);
        objects.clear();
    }

    private void clearSceneLayers() {
        renderLayer.clear();
        physics.clear();
    }

    // Object Methods

    /**
     * Adds an object to this scene and initializes the object if the
     * scene is running.
     *
     * @param obj a {@link GameObject}
     */
    public final void addObject(GameObject obj) {
        if (obj == null) return;
        if (isStopped()) {
            // Static add: when not loaded
            addObjectToScene(obj);
        } else {
            // Dynamic add: when loaded (running or paused)
            changesToScene.offer(() -> this.addObjectToScene(obj));
        }
    }

    private void addObjectToScene(GameObject obj) {
        objects.add(obj);
        obj.setScene(this);
        if (!isStopped()) startGameObject(obj);
        Logger.debug("Added object \"%s\" to scene \"%s\"",
                obj.getNameAndID(), this.name);
    }

    private void startGameObject(GameObject obj) {
        obj.start(); // Add components first so renderer and physics can access it
        addObjectToLayers(obj);
    }

    private void addObjectToLayers(GameObject obj) {
        for (var comp : obj.getComponents()) {
            if (comp instanceof Renderable r) renderLayer.addRenderable(r);
            if (comp instanceof PhysicsBody b) physics.addPhysicsBody(b);
            if (comp instanceof CollisionBody b) physics.addCollisionBody(b);
        }
    }

    /**
     * Removes an object from this scene and destroys it.
     *
     * @param obj a {@link GameObject}
     */
    final void removeObject(GameObject obj) {
        if (obj == null) return;
        changesToScene.offer(() -> this.removeObjectFromScene(obj));
    }

    private void removeObjectFromScene(GameObject obj) {
        objects.remove(obj);
        for (var comp : obj.getComponents()) {
            if (comp instanceof Renderable r) renderLayer.removeRenderable(r);
            if (comp instanceof PhysicsBody b) physics.removePhysicsBody(b);
            if (comp instanceof CollisionBody b) physics.removeCollisionBody(b);
        }
        obj.onDestroy();
        Logger.debug("Removed object \"%s\" from scene \"%s\"",
                obj.getNameAndID(), this.name);
    }

    /**
     * Finds the first {@link GameObject} with the given name, or null if none exists.
     *
     * @param name the object name
     * @return the object
     */
    public GameObject getObject(String name) {
        for (var obj : objects) {
            if (obj.getName().equals(name)) return obj;
        }
        return null;
    }

    /**
     * Returns a copy of the all objects in this scene.
     *
     * @return the list of objects
     */
    public List<GameObject> getObjects() {
        return new ArrayList<>(objects);
    }

    /**
     * Counts the number of objects in the scene.
     *
     * @return the amount of objects
     */
    public int numObjects() {
        return objects.size();
    }

    // Scene Layer Methods

    /**
     * Get the {@link mayonez.SceneLayer} by its numerical index.
     *
     * @param index the layer index
     * @return the layer, or null if the index is invalid
     */
    public SceneLayer getLayer(int index) {
        if (index >= 0 && index < layers.length) return layers[index];
        else return null;
    }

    /**
     * Get the {@link mayonez.SceneLayer} by its name.
     *
     * @param name the layer name
     * @return the layer, or null if the name is invalid
     */
    public SceneLayer getLayer(String name) {
        return Arrays.stream(layers)
                .filter(layer -> layer.getName().equals(name))
                .findFirst().orElse(null);
    }

    // Scene Properties

    /**
     * Returns the name of this scene.
     *
     * @return the scene name
     */
    public String getName() {
        return name;
    }

    public Vec2 getSize() {
        return size;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    /**
     * Returns a random vector within the scene's bounds.
     *
     * @return a random position
     */
    public Vec2 getRandomPosition() {
        return Random.randomVector(getSize().mul(-0.5f), getSize().mul(0.5f));
    }

    /**
     * Returns the number pixels on the screen for every unit in the world. Defaults to a 1:1 scale.
     *
     * @return the scene scale
     */
    public float getScale() {
        return scale;
    }

    // Getters and Setters

    /**
     * Sets the background color of this scene, white by default.
     *
     * @param background a color
     */
    public void setBackground(Color background) {
        this.background.setColor(background);
        this.background.setTexture(null);
    }

    /**
     * Sets the background image of this scene, none by default.
     *
     * @param background a texture
     */
    public void setBackground(Texture background) {
        this.background.setColor(null);
        this.background.setTexture(background);
    }

    /**
     * Get the scene's {@link Camera} instance. The camera is initialized before
     * {@link GameObject#start()} is called for all other objects.
     *
     * @return the scene camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Get the scene's {@link  mayonez.graphics.debug.DebugDraw} instance.
     *
     * @return the scene debug draw
     */
    public final DebugDraw getDebugDraw() {
        return renderLayer.getDebugDraw();
    }

    public void setGravity(Vec2 gravity) {
        physics.setGravity(gravity);
    }

    boolean isRunning() {
        return state == SceneState.RUNNING;
    }

    boolean isPaused() {
        return state == SceneState.PAUSED;
    }

    boolean isStopped() {
        return state == SceneState.STOPPED;
    }

    /**
     * Pauses the scene but does not destroy any game objects. While paused,
     * objects do not move or update but key inputs can still be polled through onUserUpdate().
     */
    final void pause() {
        state = SceneState.PAUSED;
    }

    /**
     * Resumes the scene but does not reinitialize any game objects.
     */
    final void resume() {
        state = SceneState.RUNNING;
    }

    // Object Overrides

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Scene s) && (s.sceneID == this.sceneID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sceneID, name);
    }

    @Override
    public String toString() {
        // Use Scene for class name if anonymous instance
        return String.format(
                "%s [ID = %d] (%s)",
                name, sceneID,
                StringUtils.getObjectClassName(this)
        );
    }

}
