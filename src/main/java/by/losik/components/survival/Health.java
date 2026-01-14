package by.losik.components.survival;

import com.artemis.Component;

public class Health extends Component {
    public float current;
    public float max;
    public float regenerationRate;

    public float getCurrent() {
        return current;
    }

    public float getMax() {
        return max;
    }

    public Health setCurrent(float value) {
        this.current = Math.max(0, Math.min(value, max));
        return this;
    }

    public Health setMax(float max) {
        this.max = Math.max(1, max);
        this.current = Math.min(this.current, this.max);
        return this;
    }

    public Health() {}
    public Health(float current, float max,
                  float regenerationRate) {
        this.current = current;
        this.max = max;
        this.regenerationRate = regenerationRate;
    }
}