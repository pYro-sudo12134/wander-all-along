package by.losik.components.items;

import com.artemis.Component;

public class FoodStats extends Component {
    public float nutrition = 100f;
    public float hydration = 0f;
    public float freshness = 100f;
    public float spoilTime = 72f;
    public boolean isCooked = false;

    public FoodStats() {}
    public FoodStats(float nutrition, float hydration, float spoilTime) {
        this.nutrition = nutrition;
        this.hydration = hydration;
        this.spoilTime = spoilTime;
    }
}