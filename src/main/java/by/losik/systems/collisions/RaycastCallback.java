package by.losik.systems.collisions;

public interface RaycastCallback {
    boolean onHit(int entityId, float hitTime);
}