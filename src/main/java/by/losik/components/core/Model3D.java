package by.losik.components.core;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.Model;

public class Model3D extends Component {
    public String modelPath;
    public transient Model model;
    public float scale = 1.0f;

    public Model3D() {}

    public Model3D(String modelPath) {
        this.modelPath = modelPath;
    }
}