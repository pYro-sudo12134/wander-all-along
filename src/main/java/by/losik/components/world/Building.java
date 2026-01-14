package by.losik.components.world;

import com.artemis.Component;

public class Building extends Component {
    public BuildingType type;
    public float integrity = 100f;
    public boolean isLocked = false;
    public boolean canPassThrough = false;
    public float insulation = 0f;

    public Building() {}
    public Building(BuildingType type, float integrity) {
        this.type = type;
        this.integrity = integrity;
        this.canPassThrough = type == BuildingType.DOOR || type == BuildingType.WINDOW;
    }
}