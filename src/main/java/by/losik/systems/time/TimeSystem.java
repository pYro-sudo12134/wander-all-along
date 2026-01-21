package by.losik.systems.time;

import com.artemis.BaseSystem;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class TimeSystem extends BaseSystem {
    private static final Logger logger = LoggerFactory.getLogger(TimeSystem.class);
    private float gameTime = 0f;
    private float deltaTime = 0f;
    private final List<TimeEvent> scheduledEvents = new ArrayList<>();
    private final List<TimeListener> listeners = new ArrayList<>();
    private int lastLoggedSecond = -1;

    @Override
    protected void processSystem() {
        deltaTime = world.getDelta();
        gameTime += deltaTime;

        for (int i = scheduledEvents.size() - 1; i >= 0; i--) {
            TimeEvent event = scheduledEvents.get(i);
            if (!event.executed && gameTime >= event.triggerTime) {
                event.action.run();
                event.executed = true;
                notifyListeners(event.type, gameTime);
                scheduledEvents.remove(i);
            }
        }

        int currentSecond = (int) gameTime;
        if (currentSecond != lastLoggedSecond) {
            lastLoggedSecond = currentSecond;

            if (currentSecond % 60 == 0 && deltaTime > 0) {
                logger.debug("Game time: {} min, scheduled events: {}",
                        currentSecond / 60,
                        scheduledEvents.size());
            }
        }
    }

    public void scheduleEvent(String type, float delaySeconds, Runnable action) {
        float triggerTime = gameTime + delaySeconds;
        scheduledEvents.add(new TimeEvent(type, triggerTime, action));
        logger.debug("Scheduled event '{}' in {} sec at time {}",
                type, delaySeconds, triggerTime);
    }

    public void addListener(TimeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TimeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String eventType, float time) {
        for (TimeListener listener : listeners) {
            try {
                listener.onTimeEvent(eventType, time);
            } catch (Exception e) {
                logger.error("Error in time listener: {}", e.getMessage());
            }
        }
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
        scheduledEvents.clear();
        listeners.clear();
        lastLoggedSecond = -1;
    }

    public int getScheduledEventsCount() {
        return scheduledEvents.size();
    }
}