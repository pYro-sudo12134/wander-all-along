package by.losik.systems.camera;

import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.systems.inventory.InventorySystem;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import by.losik.components.core.Camera;
import by.losik.components.core.FollowTarget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    protected ComponentMapper<Creature> mCreature;
    private PerspectiveCamera perspectiveCamera;
    private boolean cameraInitialized = false;
    private boolean leftKeyWasPressed = false;
    private boolean rightKeyWasPressed = false;
    private final float originalFOV = 45f;

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
            perspectiveCamera = new PerspectiveCamera(45, 800, 600);
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
        perspectiveCamera.position.set(15, 15, 15);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.up.set(0, 1, 0);
        perspectiveCamera.near = 0.1f;
        perspectiveCamera.far = 100f;
        perspectiveCamera.fieldOfView = originalFOV;
        perspectiveCamera.update();
    }
    @Override
    protected void process(int entityId) {
        if (!cameraInitialized) {
            initPerspectiveCamera();
        }

        Camera camera = mCamera.get(entityId);
        FollowTarget follow = mFollowTarget.get(entityId);

        if (camera == null || follow == null) return;
        handleCameraRotationInput(camera);
        updateCameraRotation(camera);
        updateCameraPosition(camera, follow);

        camera.target.set(follow.targetX, follow.targetY, follow.targetZ);

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

    private void handleCameraRotationInput(Camera camera) {
        InventorySystem inventorySystem = world.getSystem(InventorySystem.class);
        if (inventorySystem != null) {
            com.artemis.utils.IntBag players = world.getAspectSubscriptionManager()
                    .get(Aspect.all(Creature.class))
                    .getEntities();

            for (int i = 0; i < players.size(); i++) {
                int playerId = players.get(i);
                Creature creature = mCreature.get(playerId);
                if (creature != null && creature.type == CreatureType.PLAYER &&
                        inventorySystem.isInventoryOpen(playerId)) {
                    leftKeyWasPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
                    rightKeyWasPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
                    return;
                }
            }
        }

        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (leftPressed && !leftKeyWasPressed && !camera.isRotating) {
            camera.rotateLeft();
            logger.info("Rotating camera left to angle: {}", camera.targetAngleXZ);
        }

        if (rightPressed && !rightKeyWasPressed && !camera.isRotating) {
            camera.rotateRight();
            logger.info("Rotating camera right to angle: {}", camera.targetAngleXZ);
        }

        leftKeyWasPressed = leftPressed;
        rightKeyWasPressed = rightPressed;
    }

    private void updateCameraRotation(Camera camera) {
        if (camera.isRotating) {
            float angleDifference = camera.targetAngleXZ - camera.angleXZ;

            if (angleDifference > 180f) {
                angleDifference -= 360f;
            } else if (angleDifference < -180f) {
                angleDifference += 360f;
            }

            float maxRotation = camera.rotationSpeed * world.getDelta();
            if (Math.abs(angleDifference) <= maxRotation) {
                camera.angleXZ = camera.targetAngleXZ;
                camera.isRotating = false;
                logger.debug("Camera rotation completed at angle: {}", camera.angleXZ);
            } else {
                camera.angleXZ += Math.signum(angleDifference) * maxRotation;

                if (camera.angleXZ >= 360f) {
                    camera.angleXZ -= 360f;
                } else if (camera.angleXZ < 0f) {
                    camera.angleXZ += 360f;
                }
            }
        }
    }

    private void updateCameraPosition(Camera camera, FollowTarget follow) {
        float radXZ = (float) Math.toRadians(camera.angleXZ);
        float radVertical = (float) Math.toRadians(camera.verticalAngle);
        float horizontalDistance = camera.cameraDistance * (float) Math.cos(radVertical);
        float offsetX = horizontalDistance * (float) Math.sin(radXZ);
        float offsetY = camera.cameraHeight;
        float offsetZ = horizontalDistance * (float) Math.cos(radXZ);
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

        logger.debug("Camera position updated: target=({}, {}, {}), camera=({}, {}, {}), angle={}",
                follow.targetX, follow.targetY, follow.targetZ,
                camera.position.x, camera.position.y, camera.position.z,
                camera.angleXZ);
    }

    public void resize(int width, int height) {
        if (perspectiveCamera != null) {
            Vector3 savedPosition = new Vector3(perspectiveCamera.position);
            Vector3 savedDirection = new Vector3(perspectiveCamera.direction);
            Vector3 savedUp = new Vector3(perspectiveCamera.up);

            perspectiveCamera.viewportWidth = width;
            perspectiveCamera.viewportHeight = height;

            perspectiveCamera.fieldOfView = originalFOV;

            perspectiveCamera.update();

            perspectiveCamera.position.set(savedPosition);
            perspectiveCamera.direction.set(savedDirection);
            perspectiveCamera.up.set(savedUp);

            perspectiveCamera.update();

            logger.info("Camera resized to {}x{} (FOV: {})", width, height, originalFOV);
        }
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return perspectiveCamera;
    }

    public boolean isCameraInitialized() {
        return cameraInitialized && perspectiveCamera != null;
    }

    public float getCurrentCameraAngle() {
        com.artemis.utils.IntBag entities = world.getAspectSubscriptionManager()
                .get(Aspect.all(Camera.class, FollowTarget.class))
                .getEntities();

        if (entities.size() > 0) {
            int entityId = entities.get(0);
            Camera camera = mCamera.get(entityId);
            if (camera != null) {
                return camera.angleXZ;
            }
        }

        return 45f;
    }

    public void setPerspectiveCamera(PerspectiveCamera perspectiveCamera) {
        this.perspectiveCamera = perspectiveCamera;
    }
}