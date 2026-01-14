package by.losik.components.combat;

public enum DamageType {
    SLASHING("slashing", "Slashing"),
    PIERCING("piercing", "Piercing"),
    BLUDGEONING("bludgeoning", "Bludgeoning"),
    FIRE("fire", "Fire"),
    FROST("frost", "Frost"),
    LIGHTNING("lightning", "Lightning"),
    ARCANE("arcane", "Arcane"),
    HOLY("holy", "Holy"),
    SHADOW("shadow", "Shadow"),
    POISON("poison", "Poison");

    private final String id;
    private final String displayName;

    DamageType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}