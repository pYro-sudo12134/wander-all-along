package by.losik.components.core;

public enum ItemType {
    WEAPON("weapon"),
    ARMOR("configs/armor"),
    TOOL("tool"),
    FOOD("food"),
    RESOURCE("resource"),
    CONSUMABLE("consumable"),
    MATERIAL("material"),
    CONTAINER("container");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}