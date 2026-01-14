package by.losik.components.combat;

public enum ArmorSlot {
    HELMET("head"),
    TORSO("torso"),
    LEGGINGS("legs"),
    BOOTS("feet"),
    NECK("neck"),
    WAIST("waist");

    private final String bodyPart;

    ArmorSlot(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public String getBodyPart() {
        return bodyPart;
    }
}