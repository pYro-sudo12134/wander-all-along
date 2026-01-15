package by.losik.providers.builders.creature;

import by.losik.components.combat.Equipment;
import by.losik.components.combat.Resistances;
import by.losik.components.core.CarryingCapacity;
import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.components.core.ID;
import by.losik.components.core.State;
import by.losik.components.survival.BodyPart;
import by.losik.components.survival.BodyParts;
import by.losik.components.survival.BodyStatus;
import by.losik.components.survival.MentalState;
import by.losik.components.survival.Needs;
import by.losik.components.survival.Rest;
import by.losik.components.survival.SurvivalSkillType;
import by.losik.components.survival.SurvivalSkills;
import by.losik.providers.builders.base.EntityBuilder;
import com.artemis.World;

public class CreatureBuilder extends EntityBuilder {
    private static final float DEFAULT_HEALTH = 100f;
    private static final int DEFAULT_CAPACITY = 30;
    private static final float DEFAULT_WEIGHT = 100f;
    private static final float DEFAULT_REGEN = 1f;
    private static final float DEFAULT_PROGRESS = 0f;
    private static final float DEFAULT_CURRENT_WEIGHT = 0f;
    public CreatureBuilder(World world) {
        super(world);
    }

    public CreatureBuilder asCreature(String name, CreatureType creatureType) {
        String id = creatureType
                .getCreatureType()
                .concat("_")
                .concat(name.toLowerCase());

        return (CreatureBuilder) withId(id, name)
                .withComponent(new Creature(ID.of(id, name), creatureType))
                .withHealth(DEFAULT_HEALTH, DEFAULT_HEALTH, DEFAULT_REGEN)
                .withComponent(new Needs())
                .withComponent(new BodyStatus())
                .withComponent(new MentalState())
                .withComponent(new Rest())
                .withInventory(DEFAULT_CAPACITY)
                .withComponent(new CarryingCapacity(DEFAULT_WEIGHT, DEFAULT_CURRENT_WEIGHT))
                .withComponent(new Equipment())
                .withComponent(new Resistances())
                .withComponent(new State());
    }

    public CreatureBuilder withDefaultSkills() {
        addSkill(SurvivalSkillType.CARPENTRY, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.COOKING, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.FISHING, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.FORAGING, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.FARMING, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.HEALING, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.MECHANICS, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.TAILORING, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.MAGIC, DEFAULT_PROGRESS)
                .addSkill(SurvivalSkillType.ATTUNEMENT, DEFAULT_PROGRESS);
        return this;
    }

    public CreatureBuilder addSkill(SurvivalSkillType skill, float progress) {
        withComponent(new SurvivalSkills(skill, progress));
        return this;
    }

    public CreatureBuilder withBodyParts() {
        withComponent(new BodyParts(BodyPart.HEAD, DEFAULT_HEALTH));
        withComponent(new BodyParts(BodyPart.TORSO, DEFAULT_HEALTH));
        withComponent(new BodyParts(BodyPart.LEFT_ARM, DEFAULT_HEALTH));
        withComponent(new BodyParts(BodyPart.RIGHT_ARM, DEFAULT_HEALTH));
        withComponent(new BodyParts(BodyPart.LEFT_LEG, DEFAULT_HEALTH));
        withComponent(new BodyParts(BodyPart.RIGHT_LEG, DEFAULT_HEALTH));
        return this;
    }

    public CreatureBuilder withEquipment(Equipment equipment) {
        withComponent(equipment);
        return this;
    }
}