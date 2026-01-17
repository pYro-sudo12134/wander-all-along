package by.losik.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import by.losik.components.core.Camera;
import by.losik.components.core.FollowTarget;

@All({Camera.class, FollowTarget.class})
public class CameraSystem extends IteratingSystem {
    protected ComponentMapper<Camera> mCamera;
    protected ComponentMapper<FollowTarget> mFollowTarget;

    @Override
    protected void process(int entityId) {
        Camera camera = mCamera.get(entityId);
        FollowTarget follow = mFollowTarget.get(entityId);
        camera.lookAt(follow.targetX, follow.targetY, follow.targetZ);
    }
}