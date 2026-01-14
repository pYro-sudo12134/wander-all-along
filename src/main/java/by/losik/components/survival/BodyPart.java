package by.losik.components.survival;

public enum BodyPart {
    HEAD(2f, true),
    TORSO(1.0f, true),
    LEFT_ARM(0.7f, false),
    RIGHT_ARM(0.7f, false),
    LEFT_LEG(0.8f, false),
    RIGHT_LEG(0.8f, false);

    private final float damageMultiplier;
    private final boolean isVital;

    BodyPart(float multiplier, boolean vital) {
        this.damageMultiplier = multiplier;
        this.isVital = vital;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public boolean isVital() {
        return isVital;
    }
}
