package by.losik.components.core;

import com.artemis.Component;

public class Bounds extends Component {
    private float width = 10f;
    private float height = 10f;
    private float depth = 10f;

    public Bounds() {}

    public Bounds(float width, float height) {
        this.width = width;
        this.height = height;
        this.depth = width;
    }

    public Bounds(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public float getHalfWidth() {
        return width / 2f;
    }

    public float getHalfHeight() {
        return height / 2f;
    }

    public float getHalfDepth() {
        return depth / 2f;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }
}