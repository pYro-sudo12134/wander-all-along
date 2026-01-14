package by.losik.components.world;

import com.artemis.Component;

public class WaterSource extends Component {
    public float purity = 100f;
    public float capacity = 1000f;
    public boolean isDrinkable() {
        return purity > 60f;
    }

    public WaterSource() {}
    public WaterSource(float purity, float capacity) {
        this.purity = purity;
        this.capacity = capacity;
    }
}