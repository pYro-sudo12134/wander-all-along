package by.losik.systems.time;

public class TimeEvent {
    public final String type;
    public final float triggerTime;
    public final Runnable action;
    public boolean executed = false;

    public TimeEvent(String type, float triggerTime, Runnable action) {
        this.type = type;
        this.triggerTime = triggerTime;
        this.action = action;
    }
}