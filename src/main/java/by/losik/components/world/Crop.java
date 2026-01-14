package by.losik.components.world;

import com.artemis.Component;

public class Crop extends Component {

    public GrowthStage stage = GrowthStage.SEED;
    public float growthProgress = 0f;
    public float waterLevel = 50f;

    public Crop() {}

    public Crop(GrowthStage stage, float waterLevel) {
        this.stage = stage != null ? stage : GrowthStage.SEED;
        this.waterLevel = waterLevel;
    }
}