package by.losik.systems.bounds;

import by.losik.components.core.Bounds;
import by.losik.components.core.Jump;
import by.losik.components.core.Position;
import by.losik.components.core.Velocity;
import by.losik.components.core.BounceState;
import by.losik.systems.time.TimeSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.google.inject.Singleton;

@Singleton
@All({Position.class, Bounds.class, Velocity.class})
public class BoundsSystem extends IteratingSystem {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BoundsSystem.class);
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Bounds> mBounds;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Jump> mJump;
    protected ComponentMapper<BounceState> mBounceState;

    private TimeSystem timeSystem;

    private float worldMinX = -1f;
    private float worldMaxX = 1f;
    private float worldMinZ = -1f;
    private float worldMaxZ = 1f;
    private float worldMinY = -10f;
    private float worldMaxY = 20f;

    private boolean enforceWorldBounds = true;
    private boolean enforceVerticalBounds = true;

    private float wallBounceFactor = 0.7f;
    private float ceilingBounceFactor = 0.6f;
    private float floorBounceFactor = 0.5f;
    private float minBounceVelocity = 0.5f;

    @Override
    protected void initialize() {
        super.initialize();
        timeSystem = world.getSystem(TimeSystem.class);
    }

    @Override
    protected void process(int entityId) {
        Position position = mPosition.get(entityId);
        Bounds bounds = mBounds.get(entityId);
        Velocity velocity = mVelocity.get(entityId);

        if (!enforceWorldBounds) {
            return;
        }

        float deltaTime = world.getDelta();
        float halfWidth = bounds.getHalfWidth();
        float halfDepth = bounds.getHalfDepth() > 0 ? bounds.getHalfDepth() : halfWidth;
        float objectHeight = bounds.getHeight() > 0 ? bounds.getHeight() : 1.0f;

        float futureY = position.value.y + velocity.value.y * deltaTime;
        float futureX = position.value.x + velocity.value.x * deltaTime;
        float futureZ = position.value.z + velocity.value.z * deltaTime;

        float currentTime = timeSystem.getGameTime();
        boolean canBounce = true;

        if (mBounceState.has(entityId)) {
            BounceState bounce = mBounceState.get(entityId);
            canBounce = bounce.canBounce(currentTime);
        }

        if (futureX - halfWidth < worldMinX) {
            position.value.x = worldMinX + halfWidth;
            if (velocity.value.x < 0 && Math.abs(velocity.value.x) > minBounceVelocity) {
                velocity.value.x = -velocity.value.x * wallBounceFactor;
                logger.debug("Entity {} hit left wall, new X velocity: {}", entityId, velocity.value.x);
            } else {
                velocity.value.x = 0;
            }
        } else if (futureX + halfWidth > worldMaxX) {
            position.value.x = worldMaxX - halfWidth;
            if (velocity.value.x > 0 && Math.abs(velocity.value.x) > minBounceVelocity) {
                velocity.value.x = -velocity.value.x * wallBounceFactor;
                logger.debug("Entity {} hit right wall, new X velocity: {}", entityId, velocity.value.x);
            } else {
                velocity.value.x = 0;
            }
        } else {
            position.value.x = futureX;
        }

        if (futureZ - halfDepth < worldMinZ) {
            position.value.z = worldMinZ + halfDepth;
            if (velocity.value.z < 0 && Math.abs(velocity.value.z) > minBounceVelocity) {
                velocity.value.z = -velocity.value.z * wallBounceFactor;
                logger.debug("Entity {} hit back wall, new Z velocity: {}", entityId, velocity.value.z);
            } else {
                velocity.value.z = 0;
            }
        } else if (futureZ + halfDepth > worldMaxZ) {
            position.value.z = worldMaxZ - halfDepth;
            if (velocity.value.z > 0 && Math.abs(velocity.value.z) > minBounceVelocity) {
                velocity.value.z = -velocity.value.z * wallBounceFactor;
                logger.debug("Entity {} hit front wall, new Z velocity: {}", entityId, velocity.value.z);
            } else {
                velocity.value.z = 0;
            }
        } else {
            position.value.z = futureZ;
        }

        if (enforceVerticalBounds) {
            if (futureY + objectHeight > worldMaxY) {
                position.value.y = worldMaxY - objectHeight;
                if (velocity.value.y > 0 && Math.abs(velocity.value.y) > minBounceVelocity && canBounce) {
                    float bounceVelocity = -velocity.value.y * ceilingBounceFactor;
                    velocity.value.y = bounceVelocity;
                    BounceState bounce = getOrCreateBounceState(entityId);
                    bounce.setBounced(currentTime);

                    if (mJump.has(entityId)) {
                        Jump jump = mJump.get(entityId);
                        if (jump.isJumping) {
                            jump.verticalVelocity = bounceVelocity;
                        }
                    }

                    logger.info("Entity {} hit ceiling at y={}, bounce velocity: {}",
                            entityId, worldMaxY, bounceVelocity);
                } else {
                    velocity.value.y = 0;
                    if (mJump.has(entityId)) {
                        Jump jump = mJump.get(entityId);
                        if (jump.isJumping) {
                            jump.verticalVelocity = 0;
                            jump.isJumping = false;
                        }
                    }
                }
            }
            else if (futureY < worldMinY) {
                position.value.y = worldMinY;
                if (velocity.value.y < 0 && Math.abs(velocity.value.y) > minBounceVelocity && canBounce) {
                    float bounceVelocity = -velocity.value.y * floorBounceFactor;
                    velocity.value.y = bounceVelocity;

                    BounceState bounce = getOrCreateBounceState(entityId);
                    bounce.setBounced(currentTime);

                    logger.info("Entity {} hit floor at y={}, bounce velocity: {}",
                            entityId, worldMinY, bounceVelocity);
                } else {
                    velocity.value.y = 0;
                }
            }
            else {
                position.value.y = futureY;
            }
        } else {
            position.value.y = futureY;
        }
    }

    private BounceState getOrCreateBounceState(int entityId) {
        if (mBounceState.has(entityId)) {
            return mBounceState.get(entityId);
        } else {
            BounceState bounce = new BounceState();
            world.edit(entityId).add(bounce);
            return bounce;
        }
    }

    public void setWorldBounds(float minX, float maxX, float minZ, float maxZ) {
        this.worldMinX = minX;
        this.worldMaxX = maxX;
        this.worldMinZ = minZ;
        this.worldMaxZ = maxZ;
    }

    public void setVerticalBounds(float minY, float maxY) {
        this.worldMinY = minY;
        this.worldMaxY = maxY;
    }

    public void setAllBounds(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.worldMinX = minX;
        this.worldMaxX = maxX;
        this.worldMinY = minY;
        this.worldMaxY = maxY;
        this.worldMinZ = minZ;
        this.worldMaxZ = maxZ;
    }

    public void setEnforceWorldBounds(boolean enforce) {
        this.enforceWorldBounds = enforce;
    }

    public void setEnforceVerticalBounds(boolean enforce) {
        this.enforceVerticalBounds = enforce;
    }

    public void setWallBounceFactor(float factor) {
        this.wallBounceFactor = Math.max(0, Math.min(1, factor));
    }

    public void setCeilingBounceFactor(float factor) {
        this.ceilingBounceFactor = Math.max(0, Math.min(1, factor));
    }

    public void setFloorBounceFactor(float factor) {
        this.floorBounceFactor = Math.max(0, Math.min(1, factor));
    }

    public void setMinBounceVelocity(float minVelocity) {
        this.minBounceVelocity = Math.max(0, minVelocity);
    }
}