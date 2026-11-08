package eng.cad.auto.axiology.client.render;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BezierCurve {
    List<Vector3f> points = new ArrayList<>();

    public Vector3f calculateBezier(float t) {
        List<Vector3f> pointSet = new ArrayList<>();
        for (Vector3f point : points) {
            pointSet.add(new Vector3f(point.x, point.y, point.z));
        }

        for (int j = 1; j < pointSet.size(); j++) {
            for (int i = 0; i < pointSet.size() - j; i++) {
                Vector3f vec1 = pointSet.get(i);
                Vector3f vec2 = pointSet.get(i + 1);
                Vector3f lerpedVec = new Vector3f(vec1).add(new Vector3f(vec2).sub(vec1).mul(t));
                pointSet.set(i, lerpedVec);
            }
        }
        return pointSet.get(0);
    }

    public BezierCurve add(Vector3f vec) {
        points.add(vec);
        return this;
    }

    public BezierCurve add(Vec3 vec) {
        return add(vec.toVector3f());
    }
}
