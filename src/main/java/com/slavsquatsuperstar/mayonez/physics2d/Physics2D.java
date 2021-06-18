package com.slavsquatsuperstar.mayonez.physics2d;

import com.slavsquatsuperstar.mayonez.GameObject;
import com.slavsquatsuperstar.mayonez.Vector2;

import java.util.ArrayList;

// World simulation
public class Physics2D {

    public Vector2 gravity;
    private ArrayList<RigidBody2D> rigidBodies;

    public Physics2D(Vector2 gravity) {
        this.gravity = gravity;
        rigidBodies = new ArrayList<>();
    }

    public void add(GameObject o) {
        RigidBody2D rb = o.getComponent(RigidBody2D.class);
        if (rb != null)
            rigidBodies.add(rb);
    }

    public void remove(GameObject o) {
        RigidBody2D rb = o.getComponent(RigidBody2D.class);
        if (rb != null)
            rigidBodies.remove(rb);
    }

    public void update(float dt) {
        // apply forces
        rigidBodies.forEach(r -> {
            if (r.followsGravity)
                r.addForce(gravity.mul(r.mass));
        });
    }

    // handle collisions
    // collider.onCollision

}
