package by.losik.components.world;

import com.artemis.Component;

public class OreComponent extends Component {
    public OreType type;
    public float purity; // 0-1

    public OreComponent() {}
    public OreComponent(OreType type, float purity) {
        this.type = type;
        this.purity = purity;
    }

    public enum OreType {
        IRON, COPPER, GOLD, SILVER, COAL
    }
}