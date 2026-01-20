package by.losik.components.core;

import com.artemis.Component;
import org.joml.Vector3f;

public class Interpolation extends Component {
    public final Vector3f previousPosition;
    public final Vector3f previousRotation;
    public final Vector3f previousScale;

    public float interpolationAlpha = 0f; // 0-1
    public final Vector3f velocity;

    public Interpolation() {
        this.previousPosition = new Vector3f();
        this.previousRotation = new Vector3f();
        this.previousScale = new Vector3f(1, 1, 1);
        this.velocity = new Vector3f();
    }

    public void updatePrevious(Vector3f position, Vector3f rotation, Vector3f scale) {
        previousPosition.set(position);
        previousRotation.set(rotation);
        previousScale.set(scale);
    }

    public void calculateVelocity(Vector3f currentPosition, float deltaTime) {
        if (deltaTime > 0) {
            velocity.set(currentPosition)
                    .sub(previousPosition)
                    .div(deltaTime);
        }
    }

    public boolean isMovingFast(float threshold) {
        return velocity.lengthSquared() > threshold * threshold;
    }
}