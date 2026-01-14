package by.losik.components.world;

import com.artemis.Component;

public class StoneComponent extends Component {
    public StoneType type;

    public StoneComponent() {}
    public StoneComponent(StoneType type) {
        this.type = type;
    }

    public enum StoneType {
        IRON, COPPER, GOLD, SILVER, COAL
    }
}