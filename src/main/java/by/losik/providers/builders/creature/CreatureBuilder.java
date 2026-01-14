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
                .withHealth(100, 100, 0.1f)
                .withComponent(new Needs())
                .withComponent(new BodyStatus())
                .withComponent(new MentalState())
                .withComponent(new Rest())
                .withInventory(30)
                .withComponent(new CarryingCapacity(100f, 0f))
                .withComponent(new Equipment())
                .withComponent(new Resistances())
                .withComponent(new State());
    }

    public CreatureBuilder withDefaultSkills() {
        addSkill(SurvivalSkillType.CARPENTRY, 0f)
                .addSkill(SurvivalSkillType.COOKING, 0f)
                .addSkill(SurvivalSkillType.FISHING, 0f)
                .addSkill(SurvivalSkillType.FORAGING, 0f)
                .addSkill(SurvivalSkillType.FARMING, 0f)
                .addSkill(SurvivalSkillType.HEALING, 0f)
                .addSkill(SurvivalSkillType.MECHANICS, 0f)
                .addSkill(SurvivalSkillType.TAILORING, 0f)
                .addSkill(SurvivalSkillType.MAGIC, 0f)
                .addSkill(SurvivalSkillType.ATTUNEMENT, 0f);
        return this;
    }

    public CreatureBuilder addSkill(SurvivalSkillType skill, float progress) {
        withComponent(new SurvivalSkills(skill, progress));
        return this;
    }

    public CreatureBuilder withBodyParts() {
        withComponent(new BodyParts(BodyPart.HEAD, 100f));
        withComponent(new BodyParts(BodyPart.TORSO, 100f));
        withComponent(new BodyParts(BodyPart.LEFT_ARM, 100f));
        withComponent(new BodyParts(BodyPart.RIGHT_ARM, 100f));
        withComponent(new BodyParts(BodyPart.LEFT_LEG, 100f));
        withComponent(new BodyParts(BodyPart.RIGHT_LEG, 100f));
        return this;
    }

    public CreatureBuilder withEquipment(Equipment equipment) {
        withComponent(equipment);
        return this;
    }
}