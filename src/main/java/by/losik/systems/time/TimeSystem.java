package by.losik.systems.time;

import com.artemis.BaseSystem;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TimeSystem extends BaseSystem {
    private static final Logger logger = LoggerFactory.getLogger(TimeSystem.class);
    private float gameTime = 0f;
    private float deltaTime = 0f;

    @Override
    protected void processSystem() {
        deltaTime = world.getDelta();
        gameTime += deltaTime;
        logger.info("Game time is: {} sec", gameTime);
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