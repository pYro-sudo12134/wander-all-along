package by.losik.components.items;

import com.artemis.Component;

public class Durability extends Component {
    public float current;
    public float max;

    public Durability() {}

    public Durability(float max) {
        this.current = max;
        this.max = max;
    }
}