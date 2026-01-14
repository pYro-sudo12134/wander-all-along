package by.losik.components.items;

public enum ToolType {
    PICKAXE("pickaxe", "Pickaxe"),
    AXE("axe", "Axe"),
    SHOVEL("shovel", "Shovel"),
    HOE("hoe", "Hoe"),
    FISHING_ROD("fishing_rod", "Fishing Rod");

    private final String id;
    private final String displayName;

    ToolType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
}