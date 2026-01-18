package by.losik.components.core;

import com.artemis.Component;

public class Gravity extends Component {
    public float gravity = -9.8f;
    public boolean isGrounded = true;

    public Gravity() {}

    public Gravity(float gravity) {
        this.gravity = gravity;
    }
}