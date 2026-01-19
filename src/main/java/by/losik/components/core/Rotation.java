package by.losik.components.core;

import com.artemis.Component;

public class Rotation extends Component {
    public float current = 0f;
    public float target = 0f;
    public float speed = 10f;
    public boolean isRotating = false;

    public Rotation() {}

    public Rotation(float speed) {
        this.speed = speed;
    }
}