package by.losik.components.core;

import com.artemis.Component;
import org.joml.Vector3f;

public class SmoothPhysics extends Component {
    public final Vector3f renderPosition;
    public final Vector3f renderVelocity;
    public final Vector3f predictedPosition;
    public final Vector3f previousPosition;

    public SmoothPhysics() {
        this.renderPosition = new Vector3f();
        this.renderVelocity = new Vector3f();
        this.predictedPosition = new Vector3f();
        this.previousPosition = new Vector3f();
    }

    public void initialize(Vector3f startPosition) {
        renderPosition.set(startPosition);
        predictedPosition.set(startPosition);
        previousPosition.set(startPosition);
    }

    public void savePreviousState(Vector3f position) {
        previousPosition.set(position);
    }
}