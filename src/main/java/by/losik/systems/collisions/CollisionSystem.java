package by.losik.systems.collisions;

import by.losik.components.core.Bounds;
import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.components.core.EntityState;
import by.losik.components.core.Position;
import by.losik.components.core.State;
import by.losik.components.core.Velocity;
import by.losik.components.core.Weight;
import by.losik.systems.time.TimeSystem;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Singleton
@All({Position.class, Bounds.class})
public class CollisionSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(CollisionSystem.class);
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Bounds> mBounds;
    protected ComponentMapper<Creature> mCreature;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<Weight> mWeight;
    private TimeSystem timeSystem;
    private EntitySubscription allEntitiesWithBounds;
    private final Map<Integer, Map<Integer, CollisionData>> collisionTimestamps = new HashMap<>();
    private final Map<Integer, Set<Integer>> currentCollisions = new HashMap<>();
    private final Map<Integer, Set<Integer>> lastFrameCollisions = new HashMap<>();

    private final SpatialGrid spatialGrid;
    private boolean useSpatialGrid = true;
    private float spatialGridCellSize = 5.0f;
    private float collisionTolerance = 0.01f;
    private float minCollisionDuration = 0.1f;
    private float collisionCooldown = 0.3f;
    private float persistentCollisionThreshold = 1.0f;
    private float raycastTimeout = 2.0f;
    private boolean detectPlayerCollisions = true;
    private boolean detectNPCCollisions = true;
    private boolean detectCreatureCollisions = true;
    private boolean enableCollisionResponse = true;
    private float lastStatLogTime = 0f;
    private float statLogInterval = 5.0f;
    private final Vector3f tempVec1 = new Vector3f();
    private final Vector3f tempVec3 = new Vector3f();

    @Inject
    public CollisionSystem() {
        this.spatialGrid = new SpatialGrid(spatialGridCellSize);
    }

    @Override
    protected void initialize() {
        super.initialize();

        timeSystem = world.getSystem(TimeSystem.class);
        if (timeSystem == null) {
            logger.warn("TimeSystem not found in world! Collision timing features will be disabled.");
        }

        allEntitiesWithBounds = world.getAspectSubscriptionManager()
                .get(Aspect.all(Position.class, Bounds.class));

        logger.info("CollisionSystem initialized with TimeSystem integration: {}", timeSystem != null);
    }

    @Override
    protected void begin() {
        float currentTime = getCurrentTime();

        lastFrameCollisions.clear();
        lastFrameCollisions.putAll(currentCollisions);
        currentCollisions.clear();
        cleanupOldCollisionData(currentTime);

        if (useSpatialGrid) {
            spatialGrid.clear();
            updateSpatialGrid();
        }
    }

    @Override
    protected void process(int entityId) {
        if (!shouldProcessEntity(entityId)) {
            return;
        }

        Position pos = mPosition.get(entityId);
        Bounds bounds = mBounds.get(entityId);

        IntBag potentialCollisions = getPotentialCollisions(entityId, pos, bounds);

        for (int i = 0; i < potentialCollisions.size(); i++) {
            int otherEntityId = potentialCollisions.get(i);

            if (entityId == otherEntityId) {
                continue;
            }

            if (!canCollide(entityId, otherEntityId)) {
                continue;
            }

            if (checkCollision(entityId, otherEntityId)) {
                handleCollision(entityId, otherEntityId);
            }
        }
    }

    @Override
    protected void end() {
        float currentTime = getCurrentTime();

        detectCollisionEnds();
        updateCollisionDurations(currentTime);

        if (shouldLogStats(currentTime)) {
            logCollisionStats(currentTime);
        }
    }

    private float getCurrentTime() {
        return timeSystem != null ? timeSystem.getGameTime() : 0f;
    }

    private boolean shouldLogStats(float currentTime) {
        if (currentTime - lastStatLogTime >= statLogInterval) {
            lastStatLogTime = currentTime;
            return true;
        }
        return false;
    }

    private void cleanupOldCollisionData(float currentTime) {
        Iterator<Map.Entry<Integer, Map<Integer, CollisionData>>> outerIt = collisionTimestamps.entrySet().iterator();

        while (outerIt.hasNext()) {
            Map.Entry<Integer, Map<Integer, CollisionData>> outerEntry = outerIt.next();
            Map<Integer, CollisionData> innerMap = outerEntry.getValue();

            Iterator<Map.Entry<Integer, CollisionData>> innerIt = innerMap.entrySet().iterator();
            while (innerIt.hasNext()) {
                Map.Entry<Integer, CollisionData> innerEntry = innerIt.next();
                CollisionData data = innerEntry.getValue();

                if (currentTime - data.endTime > collisionCooldown * 2) {
                    innerIt.remove();
                }
            }

            if (innerMap.isEmpty()) {
                outerIt.remove();
            }
        }
    }

    private void updateCollisionDurations(float currentTime) {
        for (Map.Entry<Integer, Set<Integer>> entry : currentCollisions.entrySet()) {
            int entityId1 = entry.getKey();

            for (int entityId2 : entry.getValue()) {
                if (entityId1 < entityId2) {
                    CollisionData data = getOrCreateCollisionData(entityId1, entityId2);
                    data.updateDuration(currentTime);

                    if (data.duration > persistentCollisionThreshold) {
                        logger.warn("Persistent collision between {} and {} for {} seconds",
                                entityId1, entityId2, data.duration);
                    }
                }
            }
        }
    }

    private boolean canCollide(int entityId1, int entityId2) {
        if (timeSystem == null) {
            return true;
        }

        CollisionData data = getCollisionData(entityId1, entityId2);
        if (data != null) {
            float currentTime = getCurrentTime();

            if (currentTime - data.endTime < collisionCooldown) {
                return false;
            }

            return !data.lastCollisionWasBrief() || !(currentTime - data.startTime < minCollisionDuration);
        }

        return true;
    }

    private CollisionData getCollisionData(int entityId1, int entityId2) {
        int firstId = Math.min(entityId1, entityId2);
        int secondId = Math.max(entityId1, entityId2);

        Map<Integer, CollisionData> innerMap = collisionTimestamps.get(firstId);
        if (innerMap != null) {
            return innerMap.get(secondId);
        }
        return null;
    }

    private CollisionData getOrCreateCollisionData(int entityId1, int entityId2) {
        int firstId = Math.min(entityId1, entityId2);
        int secondId = Math.max(entityId1, entityId2);

        Map<Integer, CollisionData> innerMap = collisionTimestamps.computeIfAbsent(
                firstId, k -> new HashMap<>());

        return innerMap.computeIfAbsent(secondId, k -> new CollisionData(firstId, secondId));
    }

    private boolean shouldProcessEntity(int entityId) {
        return true;
    }

    private IntBag getPotentialCollisions(int entityId, Position pos, Bounds bounds) {
        if (useSpatialGrid) {
            return spatialGrid.getPotentialCollisions(entityId, pos.value, bounds);
        } else {
            return allEntitiesWithBounds.getEntities();
        }
    }

    private void updateSpatialGrid() {
        IntBag entities = allEntitiesWithBounds.getEntities();

        for (int i = 0; i < entities.size(); i++) {
            int entityId = entities.get(i);
            Position pos = mPosition.get(entityId);
            Bounds bounds = mBounds.get(entityId);

            spatialGrid.insert(entityId, pos.value,
                    bounds.getHalfWidth() * 2,
                    bounds.getHeight(),
                    bounds.getHalfDepth() * 2);
        }
    }

    public boolean checkCollision(int entityId1, int entityId2) {
        Position pos1 = mPosition.get(entityId1);
        Bounds bounds1 = mBounds.get(entityId1);
        Position pos2 = mPosition.get(entityId2);
        Bounds bounds2 = mBounds.get(entityId2);

        return checkAABBCollision3D(pos1, bounds1, pos2, bounds2);
    }

    private boolean checkAABBCollision3D(Position pos1, Bounds bounds1,
                                         Position pos2, Bounds bounds2) {
        float minX1 = pos1.value.x - bounds1.getHalfWidth();
        float maxX1 = pos1.value.x + bounds1.getHalfWidth();
        float minY1 = pos1.value.y;
        float maxY1 = pos1.value.y + bounds1.getHeight();
        float minZ1 = pos1.value.z - bounds1.getHalfDepth();
        float maxZ1 = pos1.value.z + bounds1.getHalfDepth();

        float minX2 = pos2.value.x - bounds2.getHalfWidth();
        float maxX2 = pos2.value.x + bounds2.getHalfWidth();
        float minY2 = pos2.value.y;
        float maxY2 = pos2.value.y + bounds2.getHeight();
        float minZ2 = pos2.value.z - bounds2.getHalfDepth();
        float maxZ2 = pos2.value.z + bounds2.getHalfDepth();

        boolean xOverlap = (minX1 <= maxX2 + collisionTolerance) &&
                (maxX1 >= minX2 - collisionTolerance);
        boolean yOverlap = (minY1 <= maxY2 + collisionTolerance) &&
                (maxY1 >= minY2 - collisionTolerance);
        boolean zOverlap = (minZ1 <= maxZ2 + collisionTolerance) &&
                (maxZ1 >= minZ2 - collisionTolerance);

        return xOverlap && yOverlap && zOverlap;
    }

    public Vector3f getCollisionNormal(int entityId1, int entityId2) {
        Position pos1 = mPosition.get(entityId1);
        Bounds bounds1 = mBounds.get(entityId1);
        Position pos2 = mPosition.get(entityId2);
        Bounds bounds2 = mBounds.get(entityId2);

        tempVec1.set(pos2.value.x - pos1.value.x,
                pos2.value.y - pos1.value.y,
                pos2.value.z - pos1.value.z);

        float overlapX = (bounds1.getHalfWidth() + bounds2.getHalfWidth()) - Math.abs(tempVec1.x);
        float overlapY = (bounds1.getHeight() + bounds2.getHeight()) - Math.abs(tempVec1.y);
        float overlapZ = (bounds1.getHalfDepth() + bounds2.getHalfDepth()) - Math.abs(tempVec1.z);

        if (overlapX < overlapY && overlapX < overlapZ) {
            return new Vector3f(Math.signum(tempVec1.x), 0, 0);
        } else if (overlapZ < overlapY && overlapZ < overlapX) {
            return new Vector3f(0, 0, Math.signum(tempVec1.z));
        } else {
            return new Vector3f(0, Math.signum(tempVec1.y), 0);
        }
    }

    public float getPenetrationDepth(int entityId1, int entityId2) {
        Position pos1 = mPosition.get(entityId1);
        Bounds bounds1 = mBounds.get(entityId1);
        Position pos2 = mPosition.get(entityId2);
        Bounds bounds2 = mBounds.get(entityId2);

        tempVec1.set(pos2.value.x - pos1.value.x,
                pos2.value.y - pos1.value.y,
                pos2.value.z - pos1.value.z);

        float overlapX = (bounds1.getHalfWidth() + bounds2.getHalfWidth()) - Math.abs(tempVec1.x);
        float overlapY = (bounds1.getHeight() + bounds2.getHeight()) - Math.abs(tempVec1.y);
        float overlapZ = (bounds1.getHalfDepth() + bounds2.getHalfDepth()) - Math.abs(tempVec1.z);

        return Math.max(0, Math.min(Math.min(overlapX, overlapY), overlapZ));
    }

    private void handleCollision(int entityId1, int entityId2) {
        float currentTime = getCurrentTime();

        registerCollision(entityId1, entityId2);

        CollisionData data = getOrCreateCollisionData(entityId1, entityId2);
        data.recordCollision(currentTime);

        boolean isCreature1 = mCreature.has(entityId1);
        boolean isCreature2 = mCreature.has(entityId2);

        CreatureType type1 = isCreature1 ? mCreature.get(entityId1).type : null;
        CreatureType type2 = isCreature2 ? mCreature.get(entityId2).type : null;

        if (isCreature1 && isCreature2) {
            handleCreatureCreatureCollision(entityId1, entityId2, type1, type2, data);
        } else if (isCreature1) {
            handleCreatureObjectCollision(entityId1, entityId2, type1, data);
        } else if (isCreature2) {
            handleCreatureObjectCollision(entityId2, entityId1, type2, data);
        } else {
            handleObjectObjectCollision(entityId1, entityId2, data);
        }

        if (enableCollisionResponse) {
            resolveCollision(entityId1, entityId2);
        }
    }

    private void handleCreatureCreatureCollision(int entityId1, int entityId2,
                                                 CreatureType type1, CreatureType type2,
                                                 CollisionData data) {
        if (!detectCreatureCollisions) {
            return;
        }

        if (type1 == CreatureType.PLAYER || type2 == CreatureType.PLAYER) {
            if (!detectPlayerCollisions) {
                return;
            }

            int playerId = (type1 == CreatureType.PLAYER) ? entityId1 : entityId2;
            int otherId = (type1 == CreatureType.PLAYER) ? entityId2 : entityId1;

            handlePlayerCreatureCollision(playerId, otherId,
                    (type1 == CreatureType.PLAYER) ? type2 : type1, data);
        } else if (type1 == CreatureType.OTHER || type2 == CreatureType.OTHER) {
            if (!detectNPCCollisions) {
                return;
            }

            handleNPCCollision(entityId1, entityId2, data);
        }

        updateCreatureState(entityId1, entityId2);
        updateCreatureState(entityId2, entityId1);
    }

    private void handlePlayerCreatureCollision(int playerId, int creatureId,
                                               CreatureType creatureType, CollisionData data) {
        if (timeSystem != null) {
            logger.debug("Player {} collided with {} creature {} at time {} (duration: {})",
                    playerId, creatureType, creatureId, data.startTime, data.duration);
        } else {
            logger.debug("Player {} collided with {} creature {}",
                    playerId, creatureType, creatureId);
        }

        if (mState.has(playerId)) {
            State state = mState.get(playerId);
            state.current = EntityState.COLLIDING;
            state.stateTime = 0f;
        }
    }

    private void handleNPCCollision(int npcId1, int npcId2, CollisionData data) {
        if (timeSystem != null && data.collisionCount > 1) {
            logger.debug("NPC {} and {} have collided {} times (last: {} sec ago)",
                    npcId1, npcId2, data.collisionCount,
                    getCurrentTime() - data.endTime);
        }

        if (mState.has(npcId1)) {
            State state = mState.get(npcId1);
            if (state.current != EntityState.COMBAT && state.current != EntityState.DEAD) {
                state.current = EntityState.COLLIDING;
                state.stateTime = 0f;
            }
        }
    }

    private void handleCreatureObjectCollision(int creatureId, int objectId,
                                               CreatureType creatureType, CollisionData data) {
        if (timeSystem != null && data.duration > 0.5f) {
            logger.debug("{} creature {} stuck on object {} for {} seconds",
                    creatureType, creatureId, objectId, data.duration);
        }

        if (mState.has(creatureId)) {
            State state = mState.get(creatureId);
            if (state.current != EntityState.COMBAT && state.current != EntityState.DEAD) {
                state.current = EntityState.COLLIDING;
                state.stateTime = 0f;
            }
        }
    }

    private void handleObjectObjectCollision(int objectId1, int objectId2, CollisionData data) {
        if (timeSystem != null && data.duration > 2.0f) {
            logger.info("Objects {} and {} have been colliding for {} seconds",
                    objectId1, objectId2, data.duration);
        }
    }

    private void updateCreatureState(int creatureId, int collidedWithId) {
        if (mState.has(creatureId)) {
            State state = mState.get(creatureId);

            if (state.current == EntityState.COMBAT || state.current == EntityState.DEAD) {
                return;
            }

            state.current = EntityState.COLLIDING;
            state.stateTime = 0f;
        }
    }

    private void resolveCollision(int entityId1, int entityId2) {
        Velocity vel1 = mVelocity.has(entityId1) ? mVelocity.get(entityId1) : null;
        Velocity vel2 = mVelocity.has(entityId2) ? mVelocity.get(entityId2) : null;
        Vector3f normal = getCollisionNormal(entityId1, entityId2);

        Vector3f relativeVelocity = new Vector3f();
        if (vel1 != null) relativeVelocity.add(vel1.value);
        if (vel2 != null) relativeVelocity.sub(vel2.value);

        float velocityAlongNormal = relativeVelocity.dot(normal);

        if (velocityAlongNormal > 0) {
            return;
        }

        float mass1 = mWeight.get(entityId1).value;
        float mass2 = mWeight.get(entityId2).value;

        if (vel1 == null) mass1 = Float.POSITIVE_INFINITY;
        if (vel2 == null) mass2 = Float.POSITIVE_INFINITY;

        float restitution = getRestitution(entityId1, entityId2);
        float j = -(1 + restitution) * velocityAlongNormal;
        j /= (1/mass1 + 1/mass2);

        Vector3f impulse = new Vector3f(normal).mul(j);

        if (vel1 != null && mass1 != Float.POSITIVE_INFINITY) {
            vel1.value.sub(new Vector3f(impulse).div(mass1));
            applyPostCollisionDamping(entityId1);
        }

        if (vel2 != null && mass2 != Float.POSITIVE_INFINITY) {
            vel2.value.add(new Vector3f(impulse).div(mass2));
            applyPostCollisionDamping(entityId2);
        }
        applyFriction(entityId1, entityId2, normal);
        applyPositionalCorrection(entityId1, entityId2, normal);
    }

    private void applyPostCollisionDamping(int entityId) {
        Velocity velocity = mVelocity.get(entityId);
        if (velocity == null) return;
        float dampingFactor = 0.7f;

        Vector3f horizontalVel = new Vector3f(velocity.value);
        horizontalVel.y = 0;

        if (horizontalVel.length() > 0.1f) {
            horizontalVel.mul(dampingFactor);
            velocity.value.x = horizontalVel.x;
            velocity.value.z = horizontalVel.z;
        }
    }

    private void applyFriction(int entityId1, int entityId2, Vector3f normal) {
        Velocity vel1 = mVelocity.has(entityId1) ? mVelocity.get(entityId1) : null;
        Velocity vel2 = mVelocity.has(entityId2) ? mVelocity.get(entityId2) : null;

        if (vel1 == null && vel2 == null) return;

        Vector3f relativeVel = new Vector3f();

        if (vel1 != null) relativeVel.add(vel1.value);
        if (vel2 != null) relativeVel.sub(vel2.value);
        Vector3f tangent = new Vector3f(relativeVel);
        float normalVelocity = relativeVel.dot(normal);
        tangent.sub(new Vector3f(normal).mul(normalVelocity));

        if (tangent.length() > 0.001f) {
            tangent.normalize();

            float friction = 0.5f;

            if (vel1 != null) {
                Vector3f tangentVelocity = new Vector3f(vel1.value);
                tangentVelocity.sub(new Vector3f(normal).mul(tangentVelocity.dot(normal)));
                tangentVelocity.mul(1.0f - friction * 0.5f);
                Vector3f normalVelocityComp = new Vector3f(normal).mul(vel1.value.dot(normal));
                vel1.value.set(normalVelocityComp).add(tangentVelocity);
            }

            if (vel2 != null) {
                Vector3f tangentVelocity = new Vector3f(vel2.value);
                tangentVelocity.sub(new Vector3f(normal).mul(tangentVelocity.dot(normal)));
                tangentVelocity.mul(1.0f - friction * 0.5f);
                Vector3f normalVelocityComp = new Vector3f(normal).mul(vel2.value.dot(normal));
                vel2.value.set(normalVelocityComp).add(tangentVelocity);
            }
        }
    }

    private float getRestitution(int entityId1, int entityId2) {
        boolean isCreature1 = mCreature.has(entityId1);
        boolean isCreature2 = mCreature.has(entityId2);

        if (isCreature1 && isCreature2) {
            return 0.3f;
        } else if (isCreature1 || isCreature2) {
            return 0.5f;
        } else {
            return 0.7f;
        }
    }

    private void applyPositionalCorrection(int entityId1, int entityId2, Vector3f normal) {
        float penetration = getPenetrationDepth(entityId1, entityId2);
        if (penetration > 0) {
            Position pos1 = mPosition.get(entityId1);
            Position pos2 = mPosition.get(entityId2);

            float mass1 = mWeight.get(entityId1).value;
            float mass2 = mWeight.get(entityId2).value;
            float totalMass = mass1 + mass2;

            float pushFactor1 = (totalMass > 0) ? mass2 / totalMass : 0.5f;
            float pushFactor2 = (totalMass > 0) ? mass1 / totalMass : 0.5f;

            float correctionPercent = 0.2f;
            float slop = 0.01f;
            float totalCorrection = Math.max(penetration - slop, 0.0f) * correctionPercent;

            tempVec3.set(normal).mul(totalCorrection * pushFactor1);
            pos1.value.sub(tempVec3);

            tempVec3.set(normal).mul(totalCorrection * pushFactor2);
            pos2.value.add(tempVec3);
        }
    }

    private void registerCollision(int entityId1, int entityId2) {
        currentCollisions.computeIfAbsent(entityId1, k -> new HashSet<>()).add(entityId2);
        currentCollisions.computeIfAbsent(entityId2, k -> new HashSet<>()).add(entityId1);
    }

    private void detectCollisionEnds() {
        float currentTime = getCurrentTime();

        for (Map.Entry<Integer, Set<Integer>> entry : lastFrameCollisions.entrySet()) {
            int entityId = entry.getKey();
            Set<Integer> lastFrame = entry.getValue();
            Set<Integer> currentFrame = currentCollisions.getOrDefault(entityId, new HashSet<>());

            for (int otherId : lastFrame) {
                if (!currentFrame.contains(otherId)) {
                    handleCollisionEnd(entityId, otherId, currentTime);
                }
            }
        }
    }

    private void handleCollisionEnd(int entityId1, int entityId2, float currentTime) {
        CollisionData data = getCollisionData(entityId1, entityId2);
        if (data != null) {
            data.endTime = currentTime;
        }

        if (mCreature.has(entityId1) && mState.has(entityId1)) {
            Set<Integer> currentCollisionsForEntity = currentCollisions.get(entityId1);
            if (currentCollisionsForEntity == null || currentCollisionsForEntity.isEmpty()) {
                State state = mState.get(entityId1);
                if (state.current == EntityState.COLLIDING) {
                    boolean isMoving = mVelocity.has(entityId1) &&
                            mVelocity.get(entityId1).value.length() > 0.1f;

                    state.current = isMoving ? EntityState.MOVING : EntityState.IDLE;
                    state.stateTime = 0f;
                }
            }
        }
    }

    private void logCollisionStats(float currentTime) {
        int totalCollisions = 0;
        int uniquePairs = 0;
        float totalDuration = 0f;
        int persistentCollisions = 0;

        for (Map<Integer, CollisionData> innerMap : collisionTimestamps.values()) {
            for (CollisionData data : innerMap.values()) {
                totalCollisions += data.collisionCount;
                uniquePairs++;
                totalDuration += data.totalDuration;

                if (data.duration > persistentCollisionThreshold) {
                    persistentCollisions++;
                }
            }
        }

        if (uniquePairs > 0) {
            float avgDuration = totalDuration / uniquePairs;

            logger.info("Collision Stats at {} sec:", currentTime);
            logger.info("  Active collisions: {}", currentCollisions.size());
            logger.info("  Unique collision pairs: {}", uniquePairs);
            logger.info("  Total collisions registered: {}", totalCollisions);
            logger.info("  Average collision duration: {} sec", avgDuration);
            logger.info("  Persistent collisions (>{} sec): {}",
                    persistentCollisionThreshold, persistentCollisions);

            if (timeSystem != null) {
                logger.info("  FPS: {}", 1.0f / timeSystem.getDeltaTime());
            }
        }
    }

    public boolean isColliding(int entityId) {
        return currentCollisions.containsKey(entityId) &&
                !currentCollisions.get(entityId).isEmpty();
    }

    public float getCollisionDuration(int entityId1, int entityId2) {
        CollisionData data = getCollisionData(entityId1, entityId2);
        return data != null ? data.duration : 0f;
    }

    public int getCollisionCount(int entityId1, int entityId2) {
        CollisionData data = getCollisionData(entityId1, entityId2);
        return data != null ? data.collisionCount : 0;
    }

    public Set<Integer> getCollidingEntities(int entityId) {
        return currentCollisions.getOrDefault(entityId, new HashSet<>());
    }

    public boolean areColliding(int entityId1, int entityId2) {
        Set<Integer> collisions1 = currentCollisions.get(entityId1);
        return collisions1 != null && collisions1.contains(entityId2);
    }

    public void raycast(Vector3f origin, Vector3f direction, float maxDistance,
                        RaycastCallback callback) {
        raycast(origin, direction, maxDistance, callback, raycastTimeout);
    }

    public void raycast(Vector3f origin, Vector3f direction, float maxDistance,
                        RaycastCallback callback, float timeout) {
        float startTime = getCurrentTime();

        IntBag entities = allEntitiesWithBounds.getEntities();

        for (int i = 0; i < entities.size(); i++) {
            int entityId = entities.get(i);
            Position pos = mPosition.get(entityId);
            Bounds bounds = mBounds.get(entityId);

            if (rayIntersectsAABB3D(origin, direction, pos.value, bounds, maxDistance)) {
                if (!callback.onHit(entityId, getCurrentTime() - startTime)) {
                    break;
                }
            }

            if (getCurrentTime() - startTime > timeout) {
                logger.warn("Raycast timed out after {} seconds", timeout);
                break;
            }
        }
    }

    public boolean rayIntersectsAABB3D(Vector3f origin, Vector3f direction,
                                       Vector3f center, Bounds bounds, float maxDistance) {
        float minX = center.x - bounds.getHalfWidth();
        float maxX = center.x + bounds.getHalfWidth();
        float minY = center.y;
        float maxY = center.y + bounds.getHeight();
        float minZ = center.z - bounds.getHalfDepth();
        float maxZ = center.z + bounds.getHalfDepth();

        float t1 = (minX - origin.x) / direction.x;
        float t2 = (maxX - origin.x) / direction.x;
        float t3 = (minY - origin.y) / direction.y;
        float t4 = (maxY - origin.y) / direction.y;
        float t5 = (minZ - origin.z) / direction.z;
        float t6 = (maxZ - origin.z) / direction.z;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if (tmax < 0 || tmin > tmax) {
            return false;
        }

        float t = (tmin < 0) ? tmax : tmin;
        return t <= maxDistance;
    }

    public void setMinCollisionDuration(float duration) {
        this.minCollisionDuration = Math.max(0, duration);
    }

    public void setCollisionCooldown(float cooldown) {
        this.collisionCooldown = Math.max(0, cooldown);
    }

    public void setPersistentCollisionThreshold(float threshold) {
        this.persistentCollisionThreshold = Math.max(0, threshold);
    }

    public void setRaycastTimeout(float timeout) {
        this.raycastTimeout = Math.max(0, timeout);
    }

    public void setStatLogInterval(float interval) {
        this.statLogInterval = Math.max(1, interval);
    }

    public void setUseSpatialGrid(boolean useSpatialGrid) {
        this.useSpatialGrid = useSpatialGrid;
        logger.info("Spatial grid optimization: {}", useSpatialGrid);
    }

    public void setSpatialGridCellSize(float cellSize) {
        this.spatialGridCellSize = cellSize;
        spatialGrid.setCellSize(cellSize);
    }

    public void setCollisionTolerance(float tolerance) {
        this.collisionTolerance = tolerance;
    }

    public void setDetectPlayerCollisions(boolean detect) {
        this.detectPlayerCollisions = detect;
    }

    public void setDetectNPCCollisions(boolean detect) {
        this.detectNPCCollisions = detect;
    }

    public void setDetectCreatureCollisions(boolean detect) {
        this.detectCreatureCollisions = detect;
    }

    public void setEnableCollisionResponse(boolean enable) {
        this.enableCollisionResponse = enable;
    }
}