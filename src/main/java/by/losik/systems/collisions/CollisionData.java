package by.losik.systems.collisions;

public class CollisionData {
    public final int entityId1;
    public final int entityId2;
    public float startTime;
    public float endTime;
    public float duration;
    public float totalDuration;
    public int collisionCount;

    public CollisionData(int entityId1, int entityId2) {
        this.entityId1 = entityId1;
        this.entityId2 = entityId2;
    }

    public void recordCollision(float currentTime) {
        if (collisionCount == 0 || currentTime - endTime > 0.1f) {
            startTime = currentTime;
            collisionCount++;
        }
    }

    public void updateDuration(float currentTime) {
        duration = currentTime - startTime;
        endTime = currentTime;
    }

    public boolean lastCollisionWasBrief() {
        return duration < 0.05f;
    }
}