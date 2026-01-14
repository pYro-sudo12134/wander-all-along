package by.losik.components.core;

public enum CreatureType {
    PLAYER("player"),
    OTHER("other"),
    WOLF("wolf"),
    BEAR("bear"),
    RABBIT("rabbit"),
    DEER("deer"),
    GOBLIN("goblin"),
    ORC("orc");

    private final String creatureType;

    CreatureType(String creatureType) {
        this.creatureType = creatureType;
    }

    public String getCreatureType() {
        return creatureType;
    }

    public static CreatureType fromString(String text) {
        for (CreatureType type : CreatureType.values()) {
            if (type.creatureType.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return OTHER;
    }
}