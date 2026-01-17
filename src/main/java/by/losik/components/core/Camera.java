package by.losik.components.core;

import com.artemis.Component;
import org.joml.Vector3f;

public class Camera extends Component {
    public final Vector3f position = new Vector3f();
    public final Vector3f target = new Vector3f();
    public final Vector3f up = new Vector3f(0, 1, 0);

    public static final float ISO_ANGLE_X = (float) Math.toRadians(30);
    public static final float ISO_ANGLE_Y = (float) Math.toRadians(45);
    public static final float ISO_DISTANCE = 50f;
    public static final float ISO_HEIGHT = 30f;

    public Camera() {
        updateIsometricView();
    }

    public Camera(float x, float y, float z) {
        position.set(x, y, z);
        updateIsometricView();
    }

    public void updateIsometricView() {
        float dx = (float) (ISO_DISTANCE * Math.cos(ISO_ANGLE_Y));
        float dz = (float) (ISO_DISTANCE * Math.sin(ISO_ANGLE_Y));
        float dy = ISO_HEIGHT;
        position.set(target).add(dx, dy, dz);
    }

    public void lookAt(float x, float y, float z) {
        target.set(x, y, z);
        updateIsometricView();
    }
}