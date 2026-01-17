package by.losik.systems;

import by.losik.components.core.Position;
import by.losik.components.core.Velocity;
import by.losik.components.core.State;
import by.losik.components.core.EntityState;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All({Position.class, Velocity.class})
public class MovementSystem extends IteratingSystem {
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<State> mState;

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
        position.value.y += velocity.value.y * deltaTime;
        position.value.z += velocity.value.z * deltaTime;

        updateEntityState(entityId, velocity);
    }

    private void updateEntityState(int entityId, Velocity velocity) {
        if (mState.has(entityId)) {
            State state = mState.get(entityId);

            boolean isMoving = velocity.value.lengthSquared() > 0.001f;

            if (isMoving) {
                if (state.current != EntityState.MOVING) {
                    state.previous = state.current;
                    state.current = EntityState.MOVING;
                    state.stateTime = 0f;

                    if (velocity.value.x != 0 || velocity.value.y != 0) {
                        Position position = mPosition.get(entityId);
                        position.rotation = (float) Math.atan2(velocity.value.y, velocity.value.x);
                    }
                }
            } else {
                if (state.current == EntityState.MOVING) {
                    state.previous = state.current;
                    state.current = EntityState.IDLE;
                    state.stateTime = 0f;
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

    public void setVelocity(int entityId, float x, float y) {
        setVelocity(entityId, x, y, 0);
    }

    public void stop(int entityId) {
        if (mVelocity.has(entityId)) {
            Velocity velocity = mVelocity.get(entityId);
            velocity.value.set(0, 0, 0);
        }
    }

    public void moveTo(int entityId, float targetX, float targetY, float speed) {
        if (mPosition.has(entityId)) {
            Position position = mPosition.get(entityId);

            float dx = targetX - position.value.x;
            float dy = targetY - position.value.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > 0.1f) {
                float directionX = dx / distance;
                float directionY = dy / distance;

                setVelocity(entityId, directionX * speed, directionY * speed);
            } else {
                stop(entityId);
            }
        }
    }
}