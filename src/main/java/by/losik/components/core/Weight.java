package by.losik.components.core;

import com.artemis.Component;

public class Weight extends Component {
    public float value;

    public Weight() {}
    public Weight(float value) {
        this.value = Math.max(0, value);
    }
}