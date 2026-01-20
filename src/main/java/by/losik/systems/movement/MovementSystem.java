package by.losik.systems.movement;

import by.losik.components.core.Position;
import by.losik.components.core.Velocity;
import by.losik.components.core.State;
import by.losik.components.core.EntityState;
import by.losik.components.core.Rotation;
import by.losik.components.core.Weight;
import by.losik.components.core.Gravity;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.google.inject.Singleton;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@Singleton
@All({Position.class, Velocity.class})
public class MovementSystem extends IteratingSystem {
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<Rotation> mRotation;
    protected ComponentMapper<Weight> mWeight;
    protected ComponentMapper<Gravity> mGravity;
    private float deltaTime;
    private float acceleration = 50.0f;
    private float deceleration = 30.0f;
    private float maxSpeed = 10.0f;
    private float groundFriction = 5.0f;
    private final float airResistance = 1.0f;
    private boolean enablePhysics = true;
    private final Map<Integer, Vector3f> targetVelocities = new HashMap<>();

    @Override
    protected void begin() {
        deltaTime = world.getDelta();
    }

    @Override
    protected void process(int entityId) {
        Position position = mPosition.get(entityId);
        Velocity velocity = mVelocity.get(entityId);

        boolean hasGravity = mGravity.has(entityId);
        boolean isGrounded = hasGravity && mGravity.get(entityId).isGrounded;

        Vector3f targetVelocity = targetVelocities.get(entityId);
        if (targetVelocity == null) {
            targetVelocity = new Vector3f(0, velocity.value.y, 0);
        }

        if (enablePhysics) {
            applyPhysics(entityId, velocity, targetVelocity, isGrounded);
        }

        position.value.x += velocity.value.x * deltaTime;
        position.value.z += velocity.value.z * deltaTime;

        applyHorizontalFriction(entityId, velocity, isGrounded);

        updateRotation(entityId);
        updateEntityState(entityId, velocity);
    }

    @Override
    protected void end() {
        targetVelocities.clear();
    }

    private void applyPhysics(int entityId, Velocity velocity,
                              Vector3f targetVelocity, boolean isGrounded) {
        float currentSpeedXZ = (float) Math.sqrt(
                velocity.value.x * velocity.value.x +
                        velocity.value.z * velocity.value.z
        );

        float targetSpeedXZ = (float) Math.sqrt(
                targetVelocity.x * targetVelocity.x +
                        targetVelocity.z * targetVelocity.z
        );

        float massFactor = getMassFactor(entityId);

        if (targetSpeedXZ > 0.01f) {

            Vector3f targetDir = new Vector3f(targetVelocity.x, 0, targetVelocity.z);
            targetDir.normalize();

            Vector3f currentDir = new Vector3f(velocity.value.x, 0, velocity.value.z);
            if (currentSpeedXZ > 0.01f) {
                currentDir.normalize();
            }

            float dot = targetDir.dot(currentDir);

            if (dot > 0.7f) {
                float speedDiff = Math.max(0, targetSpeedXZ - currentSpeedXZ);
                float effectiveAcceleration = isGrounded ? acceleration : acceleration * 0.3f;
                effectiveAcceleration *= massFactor * deltaTime;

                float accelAmount = Math.min(effectiveAcceleration, speedDiff);

                velocity.value.x += targetDir.x * accelAmount;
                velocity.value.z += targetDir.z * accelAmount;

            } else if (dot < -0.7f) {
                float effectiveDeceleration = deceleration * 2.0f;
                effectiveDeceleration *= massFactor * deltaTime;

                if (currentSpeedXZ <= effectiveDeceleration) {
                    velocity.value.x = 0;
                    velocity.value.z = 0;
                } else {
                    float ratio = (currentSpeedXZ - effectiveDeceleration) / currentSpeedXZ;
                    velocity.value.x *= ratio;
                    velocity.value.z *= ratio;
                }

            } else {
                float turnRate = isGrounded ? 0.3f : 0.1f;
                Vector3f newDir = new Vector3f(currentDir).lerp(targetDir, turnRate);
                newDir.normalize();

                if (currentSpeedXZ > 0.01f) {
                    velocity.value.x = newDir.x * currentSpeedXZ;
                    velocity.value.z = newDir.z * currentSpeedXZ;
                }

                float speedDiff = Math.max(0, targetSpeedXZ - currentSpeedXZ);
                float effectiveAcceleration = isGrounded ? acceleration * 0.5f : acceleration * 0.1f;
                effectiveAcceleration *= massFactor * deltaTime;

                float accelAmount = Math.min(effectiveAcceleration, speedDiff);
                velocity.value.x += targetDir.x * accelAmount;
                velocity.value.z += targetDir.z * accelAmount;
            }

        } else if (currentSpeedXZ > 0.01f) {
            float effectiveDeceleration = isGrounded ?
                    deceleration * 0.5f :
                    deceleration * 0.1f;

            effectiveDeceleration *= massFactor * deltaTime;

            if (currentSpeedXZ <= effectiveDeceleration) {
                velocity.value.x = 0;
                velocity.value.z = 0;
            } else {
                float ratio = (currentSpeedXZ - effectiveDeceleration) / currentSpeedXZ;
                velocity.value.x *= ratio;
                velocity.value.z *= ratio;
            }
        }

        float newSpeedXZ = (float) Math.sqrt(
                velocity.value.x * velocity.value.x +
                        velocity.value.z * velocity.value.z
        );
        if (newSpeedXZ > maxSpeed) {
            float ratio = maxSpeed / newSpeedXZ;
            velocity.value.x *= ratio;
            velocity.value.z *= ratio;
        }
    }

    private void applyHorizontalFriction(int entityId, Velocity velocity, boolean isGrounded) {
        float speedXZ = (float) Math.sqrt(
                velocity.value.x * velocity.value.x + velocity.value.z * velocity.value.z
        );

        if (speedXZ > 0.01f) {
            float effectiveFriction = isGrounded ? groundFriction : airResistance;
            float massFactor = getMassFactor(entityId);
            effectiveFriction *= massFactor;

            float frictionAmount = effectiveFriction * deltaTime;

            if (speedXZ <= frictionAmount) {
                velocity.value.x = 0;
                velocity.value.z = 0;
            } else {
                float ratio = (speedXZ - frictionAmount) / speedXZ;
                velocity.value.x *= ratio;
                velocity.value.z *= ratio;
            }
        }
    }

    private float getMassFactor(int entityId) {
        if (mWeight.has(entityId)) {
            float weight = mWeight.get(entityId).value;
            return 1.0f / (1.0f + weight / 100.0f);
        }
        return 1.0f;
    }

    private void updateRotation(int entityId) {
        if (!mRotation.has(entityId)) {
            return;
        }

        Rotation rotation = mRotation.get(entityId);

        if (rotation.isRotating) {
            float angleDifference = rotation.target - rotation.current;

            while (angleDifference > Math.PI) {
                angleDifference -= 2 * Math.PI;
            }
            while (angleDifference < -Math.PI) {
                angleDifference += 2 * Math.PI;
            }

            float maxRotation = rotation.speed * deltaTime;
            if (Math.abs(angleDifference) <= maxRotation) {
                rotation.current = rotation.target;
                rotation.isRotating = false;
            } else {
                rotation.current += Math.signum(angleDifference) * maxRotation;

                if (rotation.current > 2 * Math.PI) {
                    rotation.current -= 2 * Math.PI;
                } else if (rotation.current < 0) {
                    rotation.current += 2 * Math.PI;
                }
            }

            Position position = mPosition.get(entityId);
            position.rotation = rotation.current;
        }
    }

    private void updateEntityState(int entityId, Velocity velocity) {
        if (mState.has(entityId)) {
            State state = mState.get(entityId);

            boolean isMoving = velocity.value.length() > 0.1f;

            if (isMoving) {
                if (state.current != EntityState.MOVING) {
                    state.previous = state.current;
                    state.current = EntityState.MOVING;
                    state.stateTime = 0f;
                }
            } else {
                if (state.current == EntityState.MOVING) {
                    state.previous = state.current;
                    state.current = EntityState.IDLE;
                    state.stateTime = 0f;
                    state.rotationChanged = false;
                }
            }

            state.stateTime += deltaTime;
        }
    }

    public void setTargetVelocity(int entityId, Vector3f targetVelocity) {
        targetVelocities.put(entityId, new Vector3f(targetVelocity));
    }

    public void setTargetVelocity(int entityId, float targetX, float targetZ) {
        Velocity velocity = mVelocity.get(entityId);
        Vector3f target = new Vector3f(targetX, velocity.value.y, targetZ);
        setTargetVelocity(entityId, target);
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setFriction(float friction) {
        this.groundFriction = friction;
    }

    public void setEnablePhysics(boolean enable) {
        this.enablePhysics = enable;
    }

    public void setVelocity(int entityId, float x, float y, float z) {
        if (mVelocity.has(entityId)) {
            Velocity velocity = mVelocity.get(entityId);
            velocity.value.set(x, y, z);
        }
    }

    public void setVelocity(int entityId, float x, float z) {
        setVelocity(entityId, x, 0, z);
    }

    public void stop(int entityId) {
        if (mVelocity.has(entityId)) {
            Velocity velocity = mVelocity.get(entityId);
            velocity.value.set(0, 0, 0);
        }
        targetVelocities.remove(entityId);
    }

    public void setTargetRotation(int entityId, float targetRotation, float speed) {
        if (!mRotation.has(entityId)) {
            world.edit(entityId).add(new Rotation(speed));
        }

        Rotation rotation = mRotation.get(entityId);
        rotation.target = targetRotation;
        rotation.isRotating = true;
        rotation.speed = speed;
    }

    public void applyImpulse(int entityId, Vector3f impulse) {
        if (mVelocity.has(entityId)) {
            Velocity velocity = mVelocity.get(entityId);

            float massFactor = getMassFactor(entityId);
            velocity.value.x += impulse.x * massFactor;
            velocity.value.y += impulse.y * massFactor;
            velocity.value.z += impulse.z * massFactor;
        }
    }

    public void dampenVelocity(int entityId, float dampingFactor) {
        if (mVelocity.has(entityId)) {
            Velocity velocity = mVelocity.get(entityId);
            velocity.value.x *= dampingFactor;
            velocity.value.y *= dampingFactor;
            velocity.value.z *= dampingFactor;
        }
    }

    public void applyExtraFriction(int entityId, float extraFriction, float duration) {

    }
}