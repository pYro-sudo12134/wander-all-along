package by.losik.components.world;

import com.artemis.Component;

public class Fire extends Component {
    public float intensity = 1.0f; // 0-10
    public float spreadRate = 0.1f;
    public float fuelRemaining = 100f;
    public float temperature = 200f;

    public boolean isBurning() { return intensity > 0; }
    public boolean canCook() { return temperature > 100f && intensity > 2f; }

    public Fire() {}
    public Fire(float intensity, float fuel) {
        this.intensity = intensity;
        this.fuelRemaining = fuel;
        this.temperature = intensity * 100f;
    }
}