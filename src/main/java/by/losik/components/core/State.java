package by.losik.components.core;

import com.artemis.Component;

public class State extends Component {

    public EntityState current = EntityState.IDLE;
    public EntityState previous = EntityState.IDLE;
    public float stateTime = 0f;

    public State() {}

    public State(EntityState previous, EntityState current, float stateTime) {
        this.current = current;
        this.previous = previous;
        this.stateTime = stateTime;
    }

    public State(float stateTime) {
        this.stateTime = stateTime;
    }
}