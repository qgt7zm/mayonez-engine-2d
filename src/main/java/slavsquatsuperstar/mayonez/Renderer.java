package slavsquatsuperstar.mayonez;

import slavsquatsuperstar.mayonez.components.Camera;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Renderer {

    private ArrayList<GameObject> objects;
    private Camera camera;

    public Renderer(Camera camera) {
        this.camera = camera;
        objects = new ArrayList<>();
    }

    /**
     * Submit a {@link GameObject} for rendering.
     */
    public void add(GameObject obj) {
        objects.add(obj);
    }

    public void remove(GameObject obj) {
        objects.remove(obj);
    }

    public void render(Graphics2D g2) {
        // Save a copy of the unmodified transform
        AffineTransform transform = g2.getTransform();

        // Move the screen and render everything at the offset position
        float camX = camera.parent.getX();
        float camY = camera.parent.getY();
        g2.translate(-camX, -camY);
        // TODO only render if in screen
//		Rectangle screen = new Rectangle((int) camX, (int) camY, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        objects.forEach(o -> o.render(g2));

        // Reset the screen's transform to its unmodified state
        g2.setTransform(transform);
    }

}