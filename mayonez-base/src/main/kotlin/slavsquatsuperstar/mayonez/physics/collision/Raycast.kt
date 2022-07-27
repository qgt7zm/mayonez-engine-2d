package slavsquatsuperstar.mayonez.physics.collision

import slavsquatsuperstar.math.Vec2

/**
 * Provides additional information about a raycast against a shape.
 *
 * @author SlavSquatSuperstar
 */
// TODO save all intersections?
// TODO restricted constructor
class Raycast(
    @JvmField
    val contact: Vec2, // contact point
    normal: Vec2, // contact normal
    @JvmField
    val distance: Float // unit lengths along ray to intersection
) {
    @JvmField
    val normal = normal.unit()
}