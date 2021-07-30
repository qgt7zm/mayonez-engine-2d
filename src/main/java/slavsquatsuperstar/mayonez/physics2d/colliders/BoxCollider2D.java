package slavsquatsuperstar.mayonez.physics2d.colliders;

import slavsquatsuperstar.math.Vec2;
import slavsquatsuperstar.math.MathUtils;

/**
 * An oriented bounding box, a rectangle that can be rotated. The sides will align with the object's rotation angle.
 *
 * @author SlavSquatSuperstar
 */
public class BoxCollider2D extends AbstractBoxCollider2D {

    public BoxCollider2D(Vec2 size) {
        super(size);
    }

    // Properties

    public Vec2[] vertices() {
        Vec2 min = min();
        Vec2 max = max();
        Vec2[] vertices = new Vec2[]{
                new Vec2(min), new Vec2(min.x, max.y), new Vec2(max), new Vec2(max.x, min.y)
        };

        if (!MathUtils.equals(getRotation(), 0)) {
            for (int i = 0; i < vertices.length; i++)
                // Rotate a point about the center by a rotation
                vertices[i] = vertices[i].rotate(getRotation(), center());
        }
        return vertices;
    }

    @Override
    public AlignedBoxCollider2D getMinBounds() {
        Vec2 aabbSize;

        if (MathUtils.equals(getRotation() % 360f, 0)) {
            aabbSize = localSize(); // If not rotated return a copy
        } else {
            Vec2[] vertices = vertices();
            float[] verticesX = new float[vertices.length];
            float[] verticesY = new float[vertices.length];

            for (int i = 0; i < vertices.length; i++) {
                verticesX[i] = vertices[i].x;
                verticesY[i] = vertices[i].y;
            }

            Vec2 newMin = new Vec2(MathUtils.min(verticesX), MathUtils.min(verticesY));
            Vec2 newMax = new Vec2(MathUtils.max(verticesX), MathUtils.max(verticesY));
            aabbSize = newMax.sub(newMin).div(transform.scale);
        }

        return new AlignedBoxCollider2D(aabbSize).setTransform(transform).setRigidBody(rb);
    }

    // Collision Methods

    @Override
    public boolean contains(Vec2 point) {
        // Translate the point into the box's local space
        Vec2 pointLocal = point.rotate(-getRotation(), center());
        return MathUtils.inRange(pointLocal.x, min().x, max().x) && MathUtils.inRange(pointLocal.y, min().y, max().y);
    }

    @Override
    public boolean intersects(Edge2D edge) {
        float rot = -getRotation();

        // rotate the line into the AABB's local space
        Vec2 localStart = edge.start.rotate(rot, center());
        Vec2 localEnd = edge.end.rotate(rot, center());
        Edge2D localEdge = new Edge2D(localStart, localEnd);

        // Create AABB with same size
        AlignedBoxCollider2D aabb = new AlignedBoxCollider2D(size());
        aabb.rb = this.rb;
        aabb.transform = this.transform;
        return aabb.intersects(localEdge);
    }

    @Override
    public boolean detectCollision(Collider2D collider) {
        if (collider == this)
            return false;

        if (collider instanceof CircleCollider)
            return collider.detectCollision(this);
        else if (collider instanceof AlignedBoxCollider2D)
            return collider.detectCollision(this);
        else if (collider instanceof BoxCollider2D)
            return intersects((BoxCollider2D) collider);

        return false;
    }

    boolean intersects(BoxCollider2D box) {
        // rotate around box center, or origin?
        Vec2[] axes = {
                new Vec2(0, 1).rotate(this.getRotation(), new Vec2()),
                new Vec2(1, 0).rotate(this.getRotation(), new Vec2()),
                new Vec2(0, 1).rotate(box.getRotation(), new Vec2()),
                new Vec2(1, 0).rotate(box.getRotation(), new Vec2())
        };

        // top right - min, bottom left - min
        for (Vec2 axis : axes)
            if (!overlapOnAxis(box, axis))
                return false;

        return true;
    }

    @Override
    public Vec2 nearestPoint(Vec2 position) {
        if (contains(position))
            return position;
        // rotate the point into lcoal space
        return position.rotate(-getRotation(), center()).clampInbounds(min(), max());
    }

    @Override
    public boolean raycast(Ray2D ray, RaycastResult result, float limit) {
        float rot = -getRotation();

        // Rotate the edge into the AABB's local space
        Vec2 localOrigin = ray.origin.rotate(rot, center());
        Vec2 localDir = ray.direction.rotate(rot, center());
        Ray2D localRay = new Ray2D(localOrigin, localDir);

        // Create AABB with same size
        AlignedBoxCollider2D aabb = new AlignedBoxCollider2D(size());
        aabb.rb = this.rb;
        return aabb.raycast(ray, result, limit);

        // Gabe's Method
//        Vector2 xAxis = new Vector2(1, 0).rotate(rot, new Vector2());
//        Vector2 yAxis = new Vector2(0, 1).rotate(rot, new Vector2());
//
//        Vector2 rayToCenter = center().sub(ray.origin);
//        Vector2 f = new Vector2(xAxis.dot(ray.direction), yAxis.dot(ray.direction));
//        Vector2 e = new Vector2(xAxis.dot(rayToCenter), yAxis.dot(rayToCenter));
//
//        Vector2 halfSize = size.div(2);
//        float[] tArr = new float[4];
//        for (int i = 0; i < 2; i++) {
//            if (MathUtils.equals(i, 0f)) { // if perpendicular and not inside box
//                if (e.components()[i] - halfSize.components()[i] > 0 || -e.components()[i] + halfSize.components()[i] < 0)
//                    return false;
//            }
//            tArr[i * 2] = (e.components()[i] + size.components()[i]) / f.components()[i]; // tmax
//            tArr[i * 2 + 1] = (e.components()[i] - size.components()[i]) / f.components()[i]; // tmin
//        }
//
//        float tmin = Math.max(Math.min(tArr[0], tArr[1]), Math.min(tArr[2], tArr[3]));
//        float tmax = Math.min(Math.max(tArr[0], tArr[1]), Math.max(tArr[2], tArr[3]));
//
//        float dist = (tmin < 0f) ? tmax : tmin;
//        boolean hit = dist > 0f; //&& t * t < ray.getMaximum();
//
//        if (result != null) {
//            Vector2 point = ray.origin.add(ray.direction.mul(dist));
//            Vector2 normal = point.sub(center()).unit();
//            result.set(point, normal, dist, hit);
//        }
//
//        return hit;
    }

    public float getRotation() {
        return transform.rotation;
    }
}