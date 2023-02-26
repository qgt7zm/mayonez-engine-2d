package mayonez.physics

import mayonez.GameObject
import mayonez.Scene
import mayonez.math.Vec2
import mayonez.physics.colliders.Collider
import mayonez.physics.resolution.CollisionSolver

/**
 * A simulation containing bodies that approximate real-world physics.
 * <br></br>
 * Thanks to GamesWithGabe's [ Coding a 2D
 * Physics Engine playlist](https://youtube.com/playlist?list=PLtrSb4XxIVbpZpV65kk73OoUcIrBzoSiO)for explaining the math and logic.
 *
 * @author SlavSquatSuperstar
 */
class PhysicsWorld {

    companion object {
        const val GRAVITY_CONSTANT = 9.8f
    }

    // World Properties
    private var gravity: Vec2 = Vec2() // acceleration due to gravity

    // Bodies and Collisions
    private val bodies: MutableList<Rigidbody> // physical objects in the world
    private val colliders: MutableList<Collider> // shapes in the world
    private val listeners: MutableSet<CollisionListener> // all collision listeners
    private val collisions: MutableList<CollisionSolver> // confirmed narrowphase collisions

    init {
        bodies = ArrayList()
        colliders = ArrayList()
        listeners = HashSet()
        collisions = ArrayList()
        gravity = Vec2(0f, -GRAVITY_CONSTANT)
    }

    fun start() {
        bodies.clear()
        colliders.clear()
        listeners.clear()
        collisions.clear()
    }

    /**
     * Updates all objects in the physics simulation by the given time step.
     *
     * @param dt seconds since the last frame
     */
    /*
     * Pre-collision optimizations
     * Detect collisions x1
     * Resolve static collisions x1
     * Send collision events x2
     * Resolve dynamic collisions x2
     *
     * Integrate forces and velocities
     */
    fun step(dt: Float) {
        collisions.clear()

        // Apply Gravity and Update Bodies
        bodies.forEach { rb: Rigidbody ->
            if (rb.followsGravity) rb.applyForce(gravity * rb.mass)
            rb.integrateForce(dt)
            rb.integrateVelocity(dt)
        }

        // Detect Collisions and Create Collision Events
        // TODO Pre-collision optimizations and spatial partitioning
        detectBroadPhase()
        detectNarrowPhase()

        // Resolve Collisions
        collisions.forEach { col: CollisionSolver -> col.solve() }
    }

    // Collision Helper Methods
    /**
     * Get the collision listener that matches the with two colliders
     *
     * @param c1 the first collider
     * @param c2 the second collider
     * @return an existing listener or a new listener
     */
    private fun getListener(c1: Collider, c2: Collider): CollisionListener {
        for (lis in listeners) {
            if (lis.match(c1, c2)) return lis
        }
        return CollisionListener(c1, c2)
    }

    /**
     * Detect potential collisions between bounding boxes while avoiding expensive contact calculations.
     */
    private fun detectBroadPhase() {
        for (i in colliders.indices) {
            // Avoid duplicate collisions between two objects and checking against self
            for (j in i + 1 until colliders.size) {
                val c1 = colliders[i]
                val c2 = colliders[j]

                // Reset collision flags
                c1.collisionResolved = false
                c2.collisionResolved = false

                // Check if collidable
                val disabled = !(c1.isEnabled && c2.isEnabled)
                val ignore = c1.gameObject.hasTag("Ignore Collisions") || c2.gameObject.hasTag("Ignore Collisions")
                val static = c1.isStatic() && c2.isStatic() // Don't check for collision if both are static
                if (disabled || ignore || static) continue

                val lis = getListener(c1, c2)
                if (lis.checkBroadphase()) listeners.add(lis)
            }
        }
    }

    /**
     * Check broadphase pairs for collisions and calculate contact points.
     */
    private fun detectNarrowPhase() {
        for (lis in listeners) {
            val collision = lis.checkNarrowphase() ?: continue // Get contacts
            val c1 = lis.c1
            val c2 = lis.c2

            // Don't resolve if either object called ignore collision
            if (c1.ignoreCurrentCollision || c2.ignoreCurrentCollision) {
                c1.ignoreCurrentCollision = false
                c2.ignoreCurrentCollision = false
                continue
            }
            collisions.add(CollisionSolver(c1, c2, collision)) // Resolve collisions
        }
    }

    // Game Object Methods
    fun addObject(obj: GameObject) {
        val rb = obj.getComponent(Rigidbody::class.java)
        if (rb != null) bodies.add(rb)
        val comp = obj.getComponent(Collider::class.java)
        if (comp != null) colliders.add(comp)
    }

    fun removeObject(obj: GameObject) {
        bodies.remove(obj.getComponent(Rigidbody::class.java))
        val comp = obj.getComponent(Collider::class.java)
        colliders.remove(comp)
        // remove all listeners with this collider
//        listeners.stream().filter { lis: CollisionListener -> lis.match(c) }.toList()
//            .forEach { lis2: CollisionListener -> listeners.remove(lis2) }
        listeners.stream().filter { lis: CollisionListener -> lis.match(comp) }.toList().forEach(listeners::remove)
    }

    fun setScene(newScene: Scene) {
        bodies.clear()
        colliders.clear()
        newScene.objects.forEach { o: GameObject -> addObject(o) }
    }

    fun stop() {
        bodies.clear()
        colliders.clear()
        listeners.clear()
        collisions.clear()
    }

    // Getter and Setters

    fun setGravity(gravity: Vec2?) {
        this.gravity = gravity ?: Vec2()
    }

}