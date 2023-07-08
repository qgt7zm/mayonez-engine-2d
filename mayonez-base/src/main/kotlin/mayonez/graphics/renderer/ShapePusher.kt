package mayonez.graphics.renderer

import mayonez.*
import mayonez.graphics.*
import mayonez.graphics.sprites.*
import mayonez.math.*
import mayonez.math.shapes.*
import mayonez.util.*
import kotlin.math.*

// GL Add Shape Methods

internal fun MutableList<DebugShape>.addLine(edge: Edge, shape: DebugShape, lineStyle: LineStyle) {
    when (lineStyle) {
        LineStyle.SINGLE -> this.addLineAsSingle(shape)
        LineStyle.MULTIPLE -> this.addLineAsMultiple(edge, shape)
        LineStyle.QUADS -> this.addLineAsQuads(edge, shape)
    }
}

private fun MutableList<DebugShape>.addLineAsSingle(shape: DebugShape) {
    this.add(shape)
}

private fun MutableList<DebugShape>.addLineAsMultiple(line: Edge, shape: DebugShape) {
    val len = line.length
    val stroke = DebugDraw.DEFAULT_STROKE_SIZE
    val stretched = line.scale(Vec2((len + stroke - 1f) / len), null)

    val norm = line.unitNormal()
    val start = (1 - stroke) * 0.5f // -(stroke - 1) / 2

    for (i in 0 until stroke.roundToInt()) {
        val lineElem = stretched.translate(norm * (start + i))
        this.addShapeAndCopyBrush(lineElem, shape)
    }
}

private fun MutableList<DebugShape>.addLineAsQuads(edge: Edge, shape: DebugShape) {
    val len = edge.length
    val stroke = DebugDraw.DEFAULT_STROKE_SIZE

    val stretchedLen = len + stroke - 1f
    val rect = Rectangle(edge.center(), Vec2(stretchedLen, stroke), edge.toVector().angle())
    for (tri in rect.triangles) {
        // Change brush fill to "true" since using quads
        val brush = ShapeBrush(shape.color, LineStyle.QUADS)
        this.add(DebugShape(tri, brush))
    }
}

// Debug Shape Helper Methods

/**
 * Add a [mayonez.math.shapes.Shape] and copy the brush property of another
 * [mayonez.graphics.DebugShape].
 */
internal fun MutableList<DebugShape>.addShapeAndCopyBrush(newShape: MShape, debugShape: DebugShape) {
    val copy = DebugShape(newShape, debugShape.color, debugShape.fill, debugShape.zIndex)
    this.add(copy)
}

internal fun ShapeSprite.toDebugShape(): DebugShape {
    return DebugShape(this.colliderShape, this.color, this.fill, this.zIndex)
}