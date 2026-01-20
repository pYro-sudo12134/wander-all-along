package by.losik.systems.movement;

import by.losik.components.core.BounceState;
import by.losik.components.core.Position;
import by.losik.components.core.Gravity;
import by.losik.components.core.Jump;
import by.losik.components.core.Velocity;
import by.losik.systems.time.TimeSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.google.inject.Singleton;

@Singleton
@All({Position.class, Gravity.class})
public class GravitySystem extends IteratingSystem {
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Gravity> mGravity;
    protected ComponentMapper<Jump> mJump;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<BounceState> mBounceState;

    private TimeSystem timeSystem;

    private static final float GROUND_LEVEL = 0.0f;
    private static final float GROUND_THRESHOLD = 0.1f;
    private float gravityMultiplier = 1.0f;

    @Override
    protected void initialize() {
        timeSystem = world.getSystem(TimeSystem.class);
    }

    @Override
    protected void process(int entityId) {
        Position position = mPosition.get(entityId);
        Gravity gravity = mGravity.get(entityId);

        boolean hasJump = mJump.has(entityId);
        boolean hasVelocity = mVelocity.has(entityId);
        boolean hasBounceState = mBounceState.has(entityId);

        float currentTime = timeSystem.getGameTime();

        if (hasJump) {
            Jump jump = mJump.get(entityId);

            if (jump.isJumping) {
                boolean canApplyGravity = !hasBounceState || mBounceState.get(entityId).canBounce(currentTime);

                if (canApplyGravity) {
                    jump.verticalVelocity += gravity.gravity * world.getDelta() * gravityMultiplier;
                }

                position.value.y += jump.verticalVelocity * world.getDelta();

                if (hasVelocity) {
                    Velocity velocity = mVelocity.get(entityId);
                    velocity.value.y = jump.verticalVelocity;
                }

                if (position.value.y <= GROUND_LEVEL) {
                    position.value.y = GROUND_LEVEL;
                    jump.isJumping = false;
                    jump.verticalVelocity = 0;
                    gravity.isGrounded = true;

                    if (hasVelocity) {
                        Velocity velocity = mVelocity.get(entityId);
                        velocity.value.y = 0;
                    }
                }
            } else {
                gravity.isGrounded = position.value.y <= GROUND_LEVEL + GROUND_THRESHOLD;

                if (hasVelocity) {
                    Velocity velocity = mVelocity.get(entityId);
                    if (gravity.isGrounded && velocity.value.y < 0) {
                        velocity.value.y = 0;
                    }
                }
            }
        } else if (hasVelocity) {
            Velocity velocity = mVelocity.get(entityId);

            boolean canApplyGravity = (!gravity.isGrounded || velocity.value.y > 0);
            if (hasBounceState) {
                canApplyGravity = canApplyGravity && mBounceState.get(entityId).canBounce(currentTime);
            }

            if (canApplyGravity) {
                velocity.value.y += gravity.gravity * world.getDelta() * gravityMultiplier;
            }

            if (Math.abs(velocity.value.y) < 0.1f && position.value.y <= GROUND_LEVEL + GROUND_THRESHOLD) {
                velocity.value.y = 0;
            }

            gravity.isGrounded = position.value.y <= GROUND_LEVEL + GROUND_THRESHOLD;

            if (gravity.isGrounded && position.value.y < GROUND_LEVEL) {
                position.value.y = GROUND_LEVEL;
                velocity.value.y = 0;
            }
        }

        if (hasVelocity) {
            Velocity velocity = mVelocity.get(entityId);
            position.value.y += velocity.value.y * world.getDelta();
        }
    }

    public void setGravityMultiplier(float multiplier) {
        this.gravityMultiplier = multiplier;
    }
}