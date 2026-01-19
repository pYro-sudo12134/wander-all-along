package by.losik.systems.movement;

import by.losik.components.core.Position;
import by.losik.components.core.Gravity;
import by.losik.components.core.Jump;
import by.losik.components.core.Velocity;
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

    private static final float GROUND_LEVEL = 0.0f;
    private static final float GROUND_THRESHOLD = 0.1f;

    @Override
    protected void process(int entityId) {
        Position position = mPosition.get(entityId);
        Gravity gravity = mGravity.get(entityId);

        boolean hasJump = mJump.has(entityId);

        if (hasJump) {
            Jump jump = mJump.get(entityId);

            if (jump.isJumping) {
                jump.verticalVelocity += gravity.gravity * world.getDelta();
                position.value.y += jump.verticalVelocity * world.getDelta();

                if (position.value.y <= GROUND_LEVEL) {
                    position.value.y = GROUND_LEVEL;
                    jump.isJumping = false;
                    jump.verticalVelocity = 0;
                    gravity.isGrounded = true;
                }
            } else {
                gravity.isGrounded = position.value.y <= GROUND_LEVEL + GROUND_THRESHOLD;
            }
        } else {
            if (mVelocity.has(entityId)) {
                Velocity velocity = mVelocity.get(entityId);
                velocity.value.y += gravity.gravity * world.getDelta();
            }

            gravity.isGrounded = position.value.y <= GROUND_LEVEL + GROUND_THRESHOLD;

            if (gravity.isGrounded && position.value.y < GROUND_LEVEL) {
                position.value.y = GROUND_LEVEL;
            }
        }
    }
}