package com.slavsquatsuperstar.mayonez.physics2d;

import com.slavsquatsuperstar.mayonez.Vector2;

// Axis Aligned Bounding Box (is never rotated)
public class AlignedBoxCollider2D extends Collider2D {

    private Vector2 size;

    public AlignedBoxCollider2D(Vector2 size) {
        this.size = size;
    }

    public Vector2 min() {
        return transform.position;
    }

    public Vector2 max() {
        return min().add(size);
    }

    public Vector2 size() {
        return size;
    }

    public Vector2 center() {
        return min().add(size().div(2));
    }

    @Override
    public boolean contains(Vector2 point) {
        return point.x >= min().x && point.x <= max().x && point.y >= min().y && point.y <= max().y;
    }

    @Override
    // Raycast
    // Source: https://www.youtube.com/watch?v=eo_hrg6kVA8
    public boolean intersects(Line2D line) {
        if (contains(line.start()) || contains(line.end()))
            return true;

        Vector2 unit = line.toVector().unit();
        Vector2 min = min().sub(line.start());
        min.x /= unit.x;
        min.y /= unit.y;
        Vector2 max = max().sub(line.start());
        max.x /= unit.x;
        max.y /= unit.y;

        // If we extend the line segment forever
        // Is the distance from start to intersection shorter than the line segment
        // itself?
        float tmin = Math.max(Math.min(min.x, max.x), Math.min(min.y, max.y));
        float tmax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));
        if (tmax < 0f || tmin > tmax)
            return false;
        float t = (tmin < 0f) ? tmax : tmin;
        return t > 0f && t * t < line.toVector().lengthSquared();
    }

    public boolean intersects(CircleCollider circle) {
        return circle.intersects(this);
    }
}
