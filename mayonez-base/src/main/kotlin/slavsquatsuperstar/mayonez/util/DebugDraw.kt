package slavsquatsuperstar.mayonez.util

import slavsquatsuperstar.mayonez.Mayonez
import slavsquatsuperstar.mayonez.annotations.EngineType
import slavsquatsuperstar.mayonez.annotations.UsesEngine
import slavsquatsuperstar.mayonez.graphics.renderer.DebugRenderer
import slavsquatsuperstar.mayonez.graphics.renderer.DebugShape
import slavsquatsuperstar.mayonez.math.Vec2
import slavsquatsuperstar.mayonez.physics.shapes.*

/**
 * Draws colliders and shapes onto the screen. All shapes are specified using world coordinates, and colliders are
 * centered around an object's position.
 *
 * @author SlavSquatSuperstar
 */
@UsesEngine(EngineType.AWT)
object DebugDraw {

    const val STROKE_SIZE = 2f
    private val debugRenderer: DebugRenderer
        get() = Mayonez.scene.debugRenderer

    // Public Draw Methods
    /**
     * Draws a point onto the screen.
     *
     * @param position where the point is located, in world coordinates
     * @param color    the color to use
     */
    @JvmStatic
    fun drawPoint(position: Vec2, color: JColor?) {
        if (!Mayonez.started) return
        // Fill a circle with radius "STROKE_SIZE" in pixels
        addShape(Circle(position.toScreen(), STROKE_SIZE), color, true, DebugShape.Priority.POINT)
    }

    /**
     * Draws a line segment onto the screen.
     *
     * @param start the segment's starting point, in world coordinates
     * @param end   the segment's ending point in, world coordinates
     * @param color the color to use
     */
    @JvmStatic
    fun drawLine(start: Vec2, end: Vec2, color: JColor?) {
        if (!Mayonez.started) return
        addShape(Edge(start.toScreen(), end.toScreen()), color, false, DebugShape.Priority.LINE)
    }

    /**
     * Draws a vector onto the screen.
     *
     * @param origin    the vector's starting point, in world coordinates
     * @param direction how far away the vector's end point is, in world coordinates
     * @param color     the color to use
     */
    @JvmStatic
    fun drawVector(origin: Vec2, direction: Vec2, color: JColor?) = drawLine(origin, origin.add(direction), color)

    // Draw Shapes
    /**
     * Draws the outline of a shape onto the screen.
     *
     * @param shape a [Shape]
     * @param color the color to use
     */
    @JvmStatic
    fun drawShape(shape: Shape?, color: JColor?) {
        if (!Mayonez.started) return
        when (shape) {
            is Edge -> drawLine(shape.start, shape.end, color)
            is Circle -> drawCircle(shape, color, false)
            is Ellipse -> drawEllipse(shape, color, false)
            is Polygon -> drawPolygon(shape, color, false)
        }
    }

    /**
     * Draws a shape onto the screen and fills in the interior.
     *
     * @param shape a [Shape]
     * @param color the color to use
     */
    @JvmStatic
    fun fillShape(shape: Shape?, color: JColor?) {
        if (!Mayonez.started) return
        val drawColor = color ?: Colors.BLACK
        when (shape) {
            is Edge -> drawLine(shape.start, shape.end, drawColor)
            is Circle -> drawCircle(shape, drawColor, true)
            is Ellipse -> drawEllipse(shape, drawColor, true)
            is Polygon -> drawPolygon(shape, drawColor, true)
        }
    }

    // Internal Draw methods

    private fun drawCircle(circle: Circle, color: JColor?, fill: Boolean) {
        addShape(Circle(circle.center().toScreen(), circle.radius.toScreen()), color, fill, DebugShape.Priority.SHAPE)
    }

    private fun drawEllipse(ellipse: Ellipse, color: JColor?, fill: Boolean) {
        if (ellipse.isCircle) return drawCircle(ellipse.boundingCircle(), color, fill)
        addShape(ellipse.toScreen(), color, fill, DebugShape.Priority.SHAPE)
    }

    private fun drawPolygon(polygon: Polygon, color: JColor?, fill: Boolean) {
        addShape(polygon.toScreen(), color, fill, if (fill) DebugShape.Priority.FILL else DebugShape.Priority.SHAPE)
    }

    // Helper Methods/Classes

    private fun addShape(shape: Shape, color: JColor?, fill: Boolean, priority: DebugShape.Priority) {
        debugRenderer.addShape(DebugShape(shape, Color(color ?: Colors.BLACK), fill, priority))
    }

    /**
     * Converts a coordinate from world to screen units.
     *
     * @return the corresponding screen pixel
     */
    private fun Float.toScreen(): Float = this * Mayonez.scene.scale

    /**
     * Converts a pair of coordinates from world to screen units.
     *
     * @return the corresponding screen pixels
     */
    private fun Vec2.toScreen(): Vec2 = this * Mayonez.scene.scale

    /**
     * Converts all points on a shape to screen units.
     *
     * @return the corresponding screen pixels
     */
    private fun Shape.toScreen(): Shape = this.scale(Vec2(Mayonez.scene.scale), Vec2(0f))

}