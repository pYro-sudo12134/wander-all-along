package by.losik.components.core;

import com.artemis.Component;

public class Bounds extends Component {
    public float width = 1f;
    public float height = 1f;
    public float depth = 1f;

    public Bounds() {}

    public Bounds(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Bounds(float width, float height, float depth) {
        this(width, height);
        this.depth = depth;
    }

    public float getHalfWidth() { return width / 2f; }
    public float getHalfHeight() { return height / 2f; }
    public float getHalfDepth() { return depth / 2f; }
}