package by.losik.components.core;

public enum EntityState {
    IDLE("idle"),
    MOVING("moving"),
    CROUCHING("crouching"),
    COLLIDING("colliding"),
    COMBAT("combat"),
    ATTACKING("attacking"),
    CASTING("casting"),
    DEAD("dead"),
    NONE("none");

    private final String state;
    EntityState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}