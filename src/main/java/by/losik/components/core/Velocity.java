package by.losik.components.core;

import com.artemis.Component;
import org.joml.Vector3f;

public class Velocity extends Component {
    public final Vector3f value = new Vector3f();
    public float jumpSpeed = 15f;
    public float crouchSpeed = 5f;
    public float movementSpeed = 10.0f;

    public Velocity() {}

    public Velocity(float x, float y) {
        this.value.set(x, y, 0);
    }

    public Velocity(float x, float y, float z) {
        this.value.set(x, y, z);
    }
}