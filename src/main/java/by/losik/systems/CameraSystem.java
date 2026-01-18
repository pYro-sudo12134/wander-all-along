package by.losik.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import by.losik.components.core.Camera;
import by.losik.components.core.FollowTarget;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@All({Camera.class, FollowTarget.class})
public class CameraSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(CameraSystem.class);
    protected ComponentMapper<Camera> mCamera;
    protected ComponentMapper<FollowTarget> mFollowTarget;
    private static final float CAMERA_HEIGHT = 15f;
    private static final float CAMERA_DISTANCE = 20f;
    private static final float ANGLE_XZ = 45f;
    private static final float ANGLE_VERTICAL = 30f;

    private PerspectiveCamera perspectiveCamera;
    private boolean cameraInitialized = false;

    @Override
    protected void initialize() {
        mCamera = world.getMapper(Camera.class);
        mFollowTarget = world.getMapper(FollowTarget.class);

        logger.info("CameraSystem initialized (camera creation deferred)");
    }

    private void initPerspectiveCamera() {
        if (cameraInitialized) return;

        try {
            logger.info("Attempting to create PerspectiveCamera...");
            perspectiveCamera = new PerspectiveCamera(67, 800, 600);
            setupIsometricProjection();
            cameraInitialized = true;

            logger.info("Perspective camera initialized successfully");

        } catch (UnsatisfiedLinkError e) {
            logger.error("Failed to create PerspectiveCamera (native libraries not loaded): {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create PerspectiveCamera: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize camera", e);
        }
    }

    private void setupIsometricProjection() {
        float radXZ = (float) Math.toRadians(ANGLE_XZ);
        float radVertical = (float) Math.toRadians(ANGLE_VERTICAL);
        float horizontalDistance = CAMERA_DISTANCE * (float)Math.cos(radVertical);
        float x = horizontalDistance * (float)Math.sin(radXZ);
        float y = CAMERA_HEIGHT;
        float z = horizontalDistance * (float)Math.cos(radXZ);

        perspectiveCamera.position.set(x, y, z);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.up.set(0, 1, 0);
        perspectiveCamera.near = 0.1f;
        perspectiveCamera.far = 100f;
        perspectiveCamera.fieldOfView = 60f;

        logger.info("Position: ({}, {}, {})", x, y, z);
        logger.info("Looking at: (0, 0, 0)");
        logger.info("Distance from target: {}", Math.sqrt(x*x + y*y + z*z));
        logger.info("Height: {}, Horizontal distance: {}", CAMERA_HEIGHT, horizontalDistance);
        logger.info("Angles: XZ={}°, Vertical={}°", ANGLE_XZ, ANGLE_VERTICAL);
        logger.info("FOV: {}, Near: {}, Far: {}", perspectiveCamera.fieldOfView, perspectiveCamera.near, perspectiveCamera.far);
    }

    @Override
    protected void process(int entityId) {
        if (!cameraInitialized) {
            initPerspectiveCamera();
        }

        Camera camera = mCamera.get(entityId);
        FollowTarget follow = mFollowTarget.get(entityId);

        if (camera == null || follow == null) return;

        camera.target.set(follow.targetX, follow.targetY, follow.targetZ);

        updateCameraPosition(camera, follow);

        if (perspectiveCamera != null) {
            perspectiveCamera.position.set(camera.position);
            perspectiveCamera.lookAt(camera.target);
            perspectiveCamera.up.set(0, 1, 0);

            try {
                perspectiveCamera.update();
            } catch (UnsatisfiedLinkError e) {
                logger.error("Failed to update camera (native error): {}", e.getMessage());
                throw e;
            }

            camera.perspectiveCamera = perspectiveCamera;
        }
    }

    private void updateCameraPosition(Camera camera, FollowTarget follow) {
        float radXZ = (float) Math.toRadians(ANGLE_XZ);
        float radVertical = (float) Math.toRadians(ANGLE_VERTICAL);
        float horizontalDistance = CAMERA_DISTANCE * (float)Math.cos(radVertical);
        float offsetX = horizontalDistance * (float)Math.sin(radXZ);
        float offsetY = CAMERA_HEIGHT;
        float offsetZ = horizontalDistance * (float)Math.cos(radXZ);
        float alpha = Math.min(2.0f * world.getDelta() * 60f, 1f);

        Vector3 desiredPosition = new Vector3(
                follow.targetX + offsetX,
                follow.targetY + offsetY,
                follow.targetZ + offsetZ
        );

        if (camera.position.isZero()) {
            camera.position.set(desiredPosition);
        } else {
            camera.position.lerp(desiredPosition, alpha);
        }

        logger.debug("Camera position updated: target=({}, {}, {}), camera=({}, {}, {})",
                follow.targetX, follow.targetY, follow.targetZ,
                camera.position.x, camera.position.y, camera.position.z);
    }

    public void resize(int width, int height) {
        if (perspectiveCamera != null) {
            perspectiveCamera.viewportWidth = width;
            perspectiveCamera.viewportHeight = height;

            float aspectRatio = (float)width / height;
            perspectiveCamera.fieldOfView = 60f;

            perspectiveCamera.update();
            logger.info("Camera resized to {}x{} (aspect: {})", width, height, aspectRatio);
        }
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return perspectiveCamera;
    }

    public boolean isCameraInitialized() {
        return cameraInitialized && perspectiveCamera != null;
    }
}