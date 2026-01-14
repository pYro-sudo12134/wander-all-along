package by.losik.components.core;

import com.artemis.Component;
import org.joml.Vector3f;

public class Position extends Component {
    public final Vector3f value = new Vector3f();
    public float rotation = 0f;
    public float scale = 1f;

    public Position() {}

    public Position(float x, float y) {
        this.value.set(x, y, 0);
    }

    public Position(float x, float y, float z) {
        this.value.set(x, y, z);
    }

    public Position set(float x, float y) {
        this.value.set(x, y, 0);
        return this;
    }

    public Position set(float x, float y, float z) {
        this.value.set(x, y, z);
        return this;
    }
}