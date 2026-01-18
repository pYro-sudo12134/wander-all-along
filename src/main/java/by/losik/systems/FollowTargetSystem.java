package by.losik.systems;

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

    @Override
    protected void process(int entityId) {
        FollowTarget follow = mFollowTarget.get(entityId);

        if (follow.targetEntityId >= 0 && mPosition.has(follow.targetEntityId)) {
            Position targetPos = mPosition.get(follow.targetEntityId);

            float alpha = Math.min(follow.followSpeed * world.getDelta() * 60f, 1f);
            follow.targetX += (targetPos.value.x - follow.targetX) * alpha;
            follow.targetY += (targetPos.value.y - follow.targetY) * alpha;
            follow.targetZ += (targetPos.value.z - follow.targetZ) * alpha;
        }
    }
}