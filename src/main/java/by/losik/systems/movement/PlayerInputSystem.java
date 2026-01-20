package by.losik.systems.movement;

import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.components.core.Gravity;
import by.losik.components.core.Jump;
import by.losik.components.core.Velocity;
import by.losik.components.core.Position;
import by.losik.components.core.Rotation;
import by.losik.systems.camera.CameraSystem;
import by.losik.systems.inventory.InventorySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@All({Creature.class, Velocity.class, Position.class})
public class PlayerInputSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(PlayerInputSystem.class);
    protected ComponentMapper<Creature> mCreature;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Gravity> mGravity;
    protected ComponentMapper<Jump> mJump;
    protected ComponentMapper<Rotation> mRotation;
    private CameraSystem cameraSystem;
    private MovementSystem movementSystem;

    private float movementSpeed = 10.0f;
    private boolean spaceKeyWasPressed = false;

    @Override
    protected void initialize() {
        cameraSystem = world.getSystem(CameraSystem.class);
        movementSystem = world.getSystem(MovementSystem.class);
    }

    @Override
    protected void process(int entityId) {
        Creature creature = mCreature.get(entityId);
        Position position = mPosition.get(entityId);

        if (creature.type == CreatureType.PLAYER) {
            InventorySystem inventorySystem = world.getSystem(InventorySystem.class);
            if (inventorySystem != null && inventorySystem.isInventoryOpen(entityId)) {
                if (movementSystem != null) {
                    movementSystem.setTargetVelocity(entityId, 0, 0);
                    movementSystem.stop(entityId);
                }
                return;
            }

            Velocity velocity = mVelocity.get(entityId);
            float cameraAngle = cameraSystem.getCurrentCameraAngle();
            float cameraAngleRad = (float) Math.toRadians(cameraAngle);

            Vector3 forward = new Vector3();
            Vector3 right = new Vector3();

            forward.x = (float) Math.sin(cameraAngleRad);
            forward.z = (float) Math.cos(cameraAngleRad);
            forward.y = 0;

            right.x = forward.z;
            right.z = -forward.x;
            right.y = 0;

            Vector3 moveDirection = new Vector3(0, 0, 0);

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                Vector3 wDir = new Vector3(forward).scl(-1);
                moveDirection.add(wDir);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                moveDirection.add(forward);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                moveDirection.add(right);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                Vector3 aDir = new Vector3(right).scl(-1);
                moveDirection.add(aDir);
            }

            if (moveDirection.len() > 0.01f) {
                moveDirection.nor().scl(movementSpeed * 0.75f); //just for now to have a lower speed

                if (movementSystem != null) {
                    movementSystem.setTargetVelocity(entityId, moveDirection.x, moveDirection.z);
                } else {
                    velocity.value.x = moveDirection.x;
                    velocity.value.z = moveDirection.z;
                }

                if (moveDirection.len() > 0.1f) {
                    float moveX = -moveDirection.x;
                    float moveZ = -moveDirection.z;
                    float targetRotation = (float) Math.atan2(moveX, moveZ);

                    Rotation rotation = getOrCreateRotation(entityId);
                    rotation.target = targetRotation;
                    rotation.isRotating = true;
                    position.rotation = rotation.current;

                    logger.debug("Camera angle: {}, Move dir=({}, {}), Look dir=({}, {}), target rotation={}",
                            cameraAngle,
                            moveDirection.x, moveDirection.z,
                            moveX, moveZ,
                            Math.toDegrees(targetRotation));
                }
            } else {
                if (movementSystem != null) {
                    movementSystem.setTargetVelocity(entityId, 0, 0);
                } else {
                    velocity.value.x = 0;
                    velocity.value.z = 0;
                }
            }

            boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

            if (spacePressed && !spaceKeyWasPressed) {
                boolean hasGravity = mGravity.has(entityId);
                boolean hasJump = mJump.has(entityId);

                if (hasGravity && hasJump) {
                    Gravity gravity = mGravity.get(entityId);
                    Jump jump = mJump.get(entityId);

                    if (gravity.isGrounded && !jump.isJumping) {
                        jump.isJumping = true;
                        jump.verticalVelocity = jump.jumpForce;
                        gravity.isGrounded = false;

                        logger.info("Player jumping! Jump force: {}, Vertical velocity: {}",
                                jump.jumpForce, jump.verticalVelocity);
                    } else if (!gravity.isGrounded) {
                        logger.debug("Cannot jump: not grounded");
                    } else {
                        logger.debug("Cannot jump: already jumping");
                    }
                } else {
                    logger.debug("No gravity/jump components for player, using old jump logic");
                    velocity.value.y = velocity.jumpSpeed;
                }
            }

            spaceKeyWasPressed = spacePressed;

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                if (mGravity.has(entityId)) {
                    Gravity gravity = mGravity.get(entityId);
                    if (gravity.isGrounded) {
                        movementSpeed = velocity.crouchSpeed;
                        logger.debug("Player crouching, speed: {}", movementSpeed);
                    }
                } else {
                    velocity.value.y = -velocity.crouchSpeed;
                }
            } else {
                movementSpeed = 10.0f;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                float horizontalSpeed = (float) Math.sqrt(
                        velocity.value.x * velocity.value.x + velocity.value.z * velocity.value.z
                );
                logger.info("Player velocity: x={}, z={}, speed={}",
                        String.format("%.2f", velocity.value.x),
                        String.format("%.2f", velocity.value.z),
                        String.format("%.2f", horizontalSpeed));
            }
        }
    }

    private Rotation getOrCreateRotation(int entityId) {
        if (mRotation.has(entityId)) {
            return mRotation.get(entityId);
        } else {
            Rotation rotation = new Rotation();
            rotation.current = mPosition.get(entityId).rotation;
            rotation.target = rotation.current;
            world.edit(entityId).add(rotation);
            return rotation;
        }
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }
}