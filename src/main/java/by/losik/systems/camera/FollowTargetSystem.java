package by.losik.systems.camera;

import by.losik.components.core.Velocity;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import by.losik.components.core.FollowTarget;
import by.losik.components.core.Position;
import com.google.inject.Singleton;

@Singleton
@All({FollowTarget.class})
public class FollowTargetSystem extends IteratingSystem {
    protected ComponentMapper<FollowTarget> mFollowTarget;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;

    @Override
    protected void process(int entityId) {
        FollowTarget follow = mFollowTarget.get(entityId);

        if (follow.targetEntityId >= 0 && mPosition.has(follow.targetEntityId)) {
            Position targetPos = mPosition.get(follow.targetEntityId);

            float targetSpeed = 0f;
            if (mVelocity.has(follow.targetEntityId)) {
                Velocity targetVel = mVelocity.get(follow.targetEntityId);
                targetSpeed = (float) Math.sqrt(
                        targetVel.value.x * targetVel.value.x +
                                targetVel.value.z * targetVel.value.z
                );
            }

            float baseAlpha = follow.followSpeed;
            float speedMultiplier = 1.0f;

            if (targetSpeed > 0.5f) {
                speedMultiplier = Math.min(1.0f + targetSpeed * 0.3f, 2.0f);
            } else if (targetSpeed < 0.1f) {
                speedMultiplier = 0.5f;
            }

            float alpha = Math.min(baseAlpha * speedMultiplier * world.getDelta() * 30f, 0.25f);

            follow.targetX += (targetPos.value.x - follow.targetX) * alpha;
            follow.targetY += (targetPos.value.y - follow.targetY) * alpha;
            follow.targetZ += (targetPos.value.z - follow.targetZ) * alpha;
        }
    }
}