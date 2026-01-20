package by.losik.systems.collisions;

import org.joml.Vector3f;

public class CollisionCorrection {
    public final Vector3f correction;
    public final int otherEntityId;
    public final float penetration;

    public CollisionCorrection(Vector3f correction, int otherEntityId, float penetration) {
        this.correction = correction;
        this.otherEntityId = otherEntityId;
        this.penetration = penetration;
    }
}