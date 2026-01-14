package by.losik.components.world;

import com.artemis.Component;

public class WoodComponent extends Component {
    public WoodType type;
    public float hardness; // 0-1

    public WoodComponent() {}
    public WoodComponent(WoodType type) {
        this.type = type;
        this.hardness = type.getStrength();
    }

    public enum WoodType {
        OAK(0.7f), PINE(0.5f), BIRCH(0.6f), MAPLE(0.8f);
        private final float hardness;
        WoodType(float hardness) { this.hardness = hardness; }
        public float getStrength() { return hardness; }
    }
}