package by.losik.systems;

import by.losik.components.core.Position;
import by.losik.components.core.Velocity;
import by.losik.components.core.State;
import by.losik.components.core.EntityState;
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
            mPosition.get(entityId);

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
}