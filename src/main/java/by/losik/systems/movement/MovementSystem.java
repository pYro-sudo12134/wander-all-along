package by.losik.systems.movement;

import by.losik.components.core.Position;
import by.losik.components.core.Velocity;
import by.losik.components.core.State;
import by.losik.components.core.EntityState;
import by.losik.components.core.Rotation;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.google.inject.Singleton;

@Singleton
@All({Position.class, Velocity.class})
public class MovementSystem extends IteratingSystem {
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<Rotation> mRotation;

    private float deltaTime;

    @Override
    protected void begin() {
        deltaTime = world.getDelta();
    }
    @Override
    protected void process(int entityId) {
        Position position = mPosition.get(entityId);
        Velocity velocity = mVelocity.get(entityId);
        position.value.x += velocity.value.x * deltaTime;
        position.value.z += velocity.value.z * deltaTime;

        updateRotation(entityId);
        updateEntityState(entityId, velocity);
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
}