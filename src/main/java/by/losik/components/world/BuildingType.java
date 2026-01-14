package by.losik.components.world;

public enum BuildingType {
    WALL("wall"),
    DOOR("door"),
    WINDOW("window"),
    FURNACE("furnace"),
    STORAGE("storage");

    private final String buildingType;

    BuildingType(String buildingType) {
        this.buildingType = buildingType;
    }


    public String getBuildingType() {
        return buildingType;
    }
}
