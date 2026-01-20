package by.losik.systems.time;

import com.artemis.BaseSystem;
import com.google.inject.Singleton;

@Singleton
public class TimeSystem extends BaseSystem {
    private float gameTime = 0f;
    private float deltaTime = 0f;

    @Override
    protected void processSystem() {
        deltaTime = world.getDelta();
        gameTime += deltaTime;
    }

    public float getGameTime() {
        return gameTime;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public void reset() {
        gameTime = 0f;
        deltaTime = 0f;
    }
}