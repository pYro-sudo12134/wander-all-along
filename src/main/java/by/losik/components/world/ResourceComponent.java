package by.losik.components.world;

import com.artemis.Component;

public class ResourceComponent extends Component {
    public ResourceType type;

    public ResourceComponent() {}
    public ResourceComponent(ResourceType type) {
        this.type = type;
    }

    public enum ResourceType {
        WOOD, STONE, ORE, SOIL, PLANT
    }
}