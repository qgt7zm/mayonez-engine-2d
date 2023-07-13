package mayonez.graphics

import mayonez.graphics.renderer.*
import mayonez.math.shapes.*
import mayonez.math.shapes.Polygon
import mayonez.util.*
import java.awt.*

/**
 * Passes shape and color information to a
 * [mayonez.graphics.renderer.DebugRenderer].
 *
 * @author SlavSquatSuperstar
 */
internal data class DebugShape constructor(
    var shape: MShape,
    internal val color: MColor,
    internal val fill: Boolean,
    private val zIndex: Int,
    internal val strokeSize: Float
) : JRenderable, GLRenderable {

    constructor(shape: MShape, brush: ShapeBrush) :
            this(shape, brush.color, brush.fill, brush.zIndex, brush.strokeSize)

    // AWT Renderer Methods

    override fun render(g2: Graphics2D?) {
        if (g2 == null) return
        g2.setBrushProperties(color, strokeSize)
        g2.drawShape(shape, fill)
    }

    private fun Graphics2D.setBrushProperties(color: MColor, strokeSize: Float) {
        this.color = color.toAWT()
        this.stroke = BasicStroke(strokeSize)
    }

    private fun Graphics2D.drawShape(shape: MShape, fill: Boolean) {
        val awtShape = shape.toAWTShape()
        if (fill) fill(awtShape)
        else draw(awtShape)
    }

    // GL Renderer Methods

    /**
     * Pushes a shape's vertices and texture to a render batch.
     *
     * @param batch the batch
     */
    override fun pushToBatch(batch: RenderBatch) {
        val color = color.toGL()
        when (val shape = this.shape) {
            is Edge -> batch.pushLine(shape, color)
            is Triangle -> batch.pushTriangle(shape, color)
        }
    }

    private fun RenderBatch.pushLine(line: Edge, color: GLColor) {
        pushVec2(line.start)
        pushVec4(color)
        pushVec2(line.end)
        pushVec4(color)
    }

    private fun RenderBatch.pushTriangle(tri: Triangle, color: GLColor) {
        for (v in tri.vertices) {
            pushVec2(v)
            pushVec4(color)
        }
    }

    // GL Shape Conversion Methods

    /**
     * Break down this shape into its simplest components (lines or triangles).
     *
     * @return an array of primitive shapes
     */
    fun splitIntoParts(): Array<out MShape> {
        return when (val shape = this.shape) {
            is Edge -> arrayOf(shape) // add line directly
            is Polygon -> shape.splitIntoParts(this.fill) // else break into lines or triangles
            is Ellipse -> shape.toPolygon().splitIntoParts(this.fill)
            else -> emptyArray()
        }
    }

    private fun MPolygon.splitIntoParts(fill: Boolean): Array<out MShape> {
        return if (fill) this.triangles else this.edges
    }

    // Renderable Methods

    override fun getBatchSize(): Int {
        return if (fill) RenderBatch.MAX_TRIANGLES
        else RenderBatch.MAX_LINES
    }

    override fun getPrimitive(): DrawPrimitive {
        return if (fill) DrawPrimitive.TRIANGLE
        else DrawPrimitive.LINE
    }

    override fun getZIndex(): Int = zIndex

    override fun isEnabled(): Boolean = true

    override fun toString(): String {
        return "${shape.javaClass.simpleName} (color = $color, fill = $fill, z = $zIndex)"
    }

}
