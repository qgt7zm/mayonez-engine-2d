package slavsquatsuperstar.mayonez.physics2d.colliders

import slavsquatsuperstar.math.MathUtils.clamp
import slavsquatsuperstar.math.Vec2
import slavsquatsuperstar.mayonez.Component
import slavsquatsuperstar.mayonez.GameObject
import slavsquatsuperstar.mayonez.Logger
import slavsquatsuperstar.mayonez.Transform
import slavsquatsuperstar.mayonez.physics2d.CollisionManifold
import slavsquatsuperstar.mayonez.physics2d.RaycastResult
import slavsquatsuperstar.mayonez.physics2d.Rigidbody2D

/**
 * A shape that takes up space and can detect collisions. Requires a [Rigidbody2D] to respond to collisions
 * properly.
 *
 * @author SlavSquatSuperstar
 */
abstract class Collider2D : Component() {

    // Object References

    /**
     * A reference to the parent object's [Transform].
     */
    @JvmField
    protected var transform: Transform? = null

    @SuppressWarnings("unchecked")
    fun <T : Collider2D?> setTransform(transform: Transform?): T {
        this.transform = transform
        return this as T
    }

    /**
     * A reference to the parent object's [Rigidbody2D]
     */
    @JvmField
    protected var rb: Rigidbody2D? = null

    /**
     * Returns the parent object's [Rigidbody2D]. A collider should have rigidbody to react to collisions.
     *
     * @return the attached rigidbody
     */
    fun getRigidbody(): Rigidbody2D? = rb

    @SuppressWarnings("unchecked")
    fun <T : Collider2D?> setRigidbody(rb: Rigidbody2D?): T {
        this.rb = rb
        return this as T
    }

    // Physics Properties

    /**
     * What percentage of energy is conserved after a collision (0-1).
     */
    var bounce = 0.25f
        private set

    fun setBounce(bounce: Float): Collider2D {
        this.bounce = clamp(bounce, 0f, 1f)
        return this
    }

    /**
     * Whether this collider is non-physical and should not react to collisions.
     */
    private var isTrigger = false

    fun isTrigger(): Boolean = isTrigger

    fun setTrigger(trigger: Boolean): Collider2D {
        isTrigger = trigger
        return this
    }

    // Game Loop Methods

    override fun start() {
        rb = parent.getComponent(Rigidbody2D::class.java)
        if (rb == null) // TODO what to do if object is static
            Logger.warn("%s needs a Rigidbody to function properly!", this)
    }

    // Shape Properties

    fun center(): Vec2 = if (transform != null) transform!!.position else Vec2()

    abstract fun getMinBounds(): AlignedBoxCollider2D

    // Shape vs Point Collisions
    /**
     * Calculates whether the given point is on or inside this shape.
     *
     * @param point a vector
     * @return if the shape contains the point
     */
    abstract operator fun contains(point: Vec2): Boolean

    /**
     * Returns the point on or inside the collider nearest to the given position.
     *
     * @param position a 2D vector
     * @return the point
     */
    abstract fun nearestPoint(position: Vec2): Vec2?

    // Shape vs Line Collisions

    /**
     * Calculates whether the given [Edge2D] touches or passes through this collider.
     *
     * @param edge a line segment
     * @return if the edge intersects this shape
     */
    open fun intersects(edge: Edge2D): Boolean {
        return if (edge.start in this || edge.end in this) true
        else raycast(Ray2D(edge), edge.length()) != null
    }

    /**
     * Casts a ray onto this collider and calculates where the ray intersects the collider.
     *
     * @param ray    the [Ray2D]
     * @param limit  The maximum distance the ray is allowed to travel before hitting an object. Set to 0 to allow the
     * ray to travel infinitely. Should be positive otherwise.
     * @return if the ray intersects this shape
     */
    abstract fun raycast(ray: Ray2D?, limit: Float): RaycastResult?

    // Shape vs Shape Collisions

    /**
     * Detects whether this collider is touching or overlapping another.
     *
     * @param collider another collider
     * @return if there is a collision
     */
    open fun detectCollision(collider: Collider2D?): Boolean = getCollisionInfo(collider) != null

    /**
     * Calculates the contact points, normal, and overlap between this collider and another if they are intersecting.
     *
     * @param collider another collider
     * @return the collision info, or no if there is no intersection
     */
    abstract fun getCollisionInfo(collider: Collider2D?): CollisionManifold?

    // Transform Methods
    open fun toLocal(world: Vec2): Vec2 = if (transform != null) transform!!.toLocal(world) else world

    open fun toWorld(local: Vec2): Vec2 = if (transform != null) transform!!.toWorld(local) else local

    // Field Getters and Setters

    override fun setParent(parent: GameObject): Collider2D {
        super.setParent(parent)
        transform = parent.transform
        return this
    }

    /**
     * Returns whether this collider has a null or static rigidbody, and thus does not respond to collisions.
     *
     * @return if this collider is not affected by collisions.
     */
    fun isStatic(): Boolean = (rb == null) || rb!!.hasInfiniteMass()

}