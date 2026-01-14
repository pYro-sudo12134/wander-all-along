package by.losik.components.survival;

public enum SurvivalSkillType {
    CARPENTRY("carpentry"),
    COOKING("cooking"),
    FARMING("farming"),
    FORAGING("foraging"),
    FISHING("fishing"),
    HEALING("healing"),
    MECHANICS("mechanics"),
    TAILORING("tailoring"),
    MAGIC("magic"),
    ATTUNEMENT("attunement"),
    NONE("none");

    private final String skillType;

    SurvivalSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getSkillType() {
        return skillType;
    }
}
