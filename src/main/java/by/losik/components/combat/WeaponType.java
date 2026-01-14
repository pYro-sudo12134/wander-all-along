package by.losik.components.combat;

import by.losik.components.core.ItemType;

public enum WeaponType {
    SWORD("sword", "Sword", ItemType.WEAPON),
    AXE("axe", "Axe", ItemType.WEAPON),
    MACE("mace", "Mace", ItemType.WEAPON),
    DAGGER("dagger", "Dagger", ItemType.WEAPON),
    SPEAR("spear", "Spear", ItemType.WEAPON),
    BOW("bow", "Bow", ItemType.WEAPON),
    CROSSBOW("crossbow", "Crossbow", ItemType.WEAPON),
    STAFF("staff", "Staff", ItemType.WEAPON),
    WAND("wand", "Wand", ItemType.WEAPON),
    SHIELD("shield", "Shield", ItemType.WEAPON);

    private final String id;
    private final String displayName;
    private final ItemType itemType;

    WeaponType(String id, String displayName, ItemType itemType) {
        this.id = id;
        this.displayName = displayName;
        this.itemType = itemType;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemType getItemType() {
        return itemType;
    }
}