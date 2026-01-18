package by.losik.components.core;

import com.artemis.Component;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class Camera extends Component {
    public boolean isIsometric = true;
    public Vector3 position = new Vector3();
    public Vector3 target = new Vector3();
    public transient PerspectiveCamera perspectiveCamera;

    public void lookAt(float x, float y, float z) {
        target.set(x, y, z);
    }
}