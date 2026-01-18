package by.losik.components.core;

import com.artemis.Component;

public class Jump extends Component {
    public float jumpForce = 10.0f;
    public boolean isJumping = true;
    public float verticalVelocity = 0;

    public Jump() {}

    public Jump(float jumpForce) {
        this.jumpForce = jumpForce;
    }
}