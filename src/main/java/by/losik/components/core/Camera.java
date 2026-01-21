package by.losik.components.core;

import com.artemis.Component;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class Camera extends Component {
    public boolean isIsometric = true;
    public Vector3 position = new Vector3();
    public Vector3 target = new Vector3();
    public transient PerspectiveCamera perspectiveCamera;
    public float angleXZ = 45f;
    public float targetAngleXZ = 45f;
    public float rotationSpeed = 90f;
    public boolean isRotating = false;
    public float cameraHeight = 20f;
    public float cameraDistance = 20f;
    public float verticalAngle = 30f;
    public static final float[] CAMERA_ANGLES = {45f, 135f, 225f, 315f};
    public int currentAngleIndex = 0;

    public void lookAt(float x, float y, float z) {
        target.set(x, y, z);
    }

    public void rotateLeft() {
        currentAngleIndex = (currentAngleIndex + 1) % CAMERA_ANGLES.length;
        targetAngleXZ = CAMERA_ANGLES[currentAngleIndex];
        isRotating = true;
    }

    public void rotateRight() {
        currentAngleIndex = (currentAngleIndex - 1 + CAMERA_ANGLES.length) % CAMERA_ANGLES.length;
        targetAngleXZ = CAMERA_ANGLES[currentAngleIndex];
        isRotating = true;
    }
}