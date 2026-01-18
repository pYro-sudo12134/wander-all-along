package by.losik.systems;

import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.components.core.Gravity;
import by.losik.components.core.Jump;
import by.losik.components.core.Velocity;
import by.losik.components.core.Position;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@All({Creature.class, Velocity.class, Position.class})
public class PlayerInputSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(PlayerInputSystem.class);
    protected ComponentMapper<Creature> mCreature;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Gravity> mGravity;
    protected ComponentMapper<Jump> mJump;

    private float movementSpeed = 10.0f;
    private final float jumpSpeed = 15.0f;
    private final float crouchSpeed = 5.0f;
    private boolean spaceKeyWasPressed = false;


    private final Vector3[] isoDirections = {
            new Vector3(0.707f, 0, -0.707f),   // (W+D)
            new Vector3(0.707f, 0, 0.707f),    // (S+D)
            new Vector3(-0.707f, 0, -0.707f),  // (W+A)
            new Vector3(-0.707f, 0, 0.707f)    // (S+A)
    };

    @Override
    protected void process(int entityId) {
        Creature creature = mCreature.get(entityId);
        Position position = mPosition.get(entityId);

        if (creature.type == CreatureType.PLAYER) {
            Velocity velocity = mVelocity.get(entityId);

            velocity.value.x = 0;
            velocity.value.z = 0;

            Vector3 moveDirection = new Vector3(0, 0, 0);
            boolean diagonalMove = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    moveDirection.add(isoDirections[0]);
                    diagonalMove = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    moveDirection.add(isoDirections[2]);
                    diagonalMove = true;
                } else {
                    moveDirection.add(new Vector3(0, 0, -1));
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    moveDirection.add(isoDirections[1]);
                    diagonalMove = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    moveDirection.add(isoDirections[3]);
                    diagonalMove = true;
                } else {
                    moveDirection.add(new Vector3(0, 0, 1));
                }
            }

            if (!diagonalMove) {
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    moveDirection.add(new Vector3(1, 0, 0));
                }
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    moveDirection.add(new Vector3(-1, 0, 0));
                }
            }

            if (moveDirection.len() > 0.01f) {
                moveDirection.nor().scl(movementSpeed);
                velocity.value.x = moveDirection.x;
                velocity.value.z = moveDirection.z;

                position.rotation = (float) Math.atan2(moveDirection.x, moveDirection.z);

                logger.debug("Player moving: direction=({}, {}), rotation={} radians ({} degrees)",
                        moveDirection.x, moveDirection.z,
                        position.rotation, Math.toDegrees(position.rotation));
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
                    velocity.value.y = jumpSpeed;
                }
            }

            spaceKeyWasPressed = spacePressed;

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                if (mGravity.has(entityId)) {
                    Gravity gravity = mGravity.get(entityId);
                    if (gravity.isGrounded) {
                        movementSpeed = crouchSpeed;
                        logger.debug("Player crouching, speed: {}", movementSpeed);
                    }
                } else {
                    velocity.value.y = -crouchSpeed;
                }
            } else {
                movementSpeed = 10.0f;
            }
        }
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }
}