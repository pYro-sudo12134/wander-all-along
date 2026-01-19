package by.losik.providers.factories;

import by.losik.components.combat.Equipment;
import by.losik.components.combat.Resistances;
import by.losik.components.core.Bounds;
import by.losik.components.core.CarryingCapacity;
import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.components.core.Gravity;
import by.losik.components.core.ID;
import by.losik.components.core.Inventory;
import by.losik.components.core.Jump;
import by.losik.components.core.Model3D;
import by.losik.components.core.Position;
import by.losik.components.core.Rotation;
import by.losik.components.core.State;
import by.losik.components.core.Velocity;
import by.losik.components.core.Weight;
import by.losik.components.survival.BodyStatus;
import by.losik.components.survival.Health;
import by.losik.components.survival.MentalState;
import by.losik.components.survival.Needs;
import by.losik.components.survival.Rest;
import by.losik.components.survival.SurvivalSkillType;
import by.losik.components.survival.SurvivalSkills;
import by.losik.components.ui.Description;
import by.losik.components.ui.Tag;
import by.losik.providers.config.CreatureConfig;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class CreatureFactory extends EntityFactory {
    private final CreatureConfig config;

    @Inject
    public CreatureFactory(World world, CreatureConfig config) {
        super(world);
        this.config = config;
    }

    public int createCreature(String name, float x, float y, float z, CreatureType creatureType) {
        int entity = createBaseEntity();
        ID id = new ID(generateCreatureId(name, creatureType), name);

        world.edit(entity)
                .add(id)
                .add(new Position(x, y, z))
                .add(new Velocity())
                .add(new Bounds(0.9f, 1.7f))
                .add(new State())
                .add(new Gravity())
                .add(new Jump())
                .add(new Rotation())
                .add(new Tag(creatureType.getCreatureType()))
                .add(new Description("A " + creatureType.getCreatureType() + " named " + name));

        addCreatureSpecificComponents(entity, id, creatureType);
        addDefaultSkills(entity);

        return entity;
    }

    private void addCreatureSpecificComponents(int entity, ID id, CreatureType creatureType) {
        CreatureConfig.CreatureStats stats = config.getStatsForType(creatureType);

        world.edit(entity)
                .add(new Creature(id, creatureType))
                .add(new Health(stats.maxHealth, stats.maxHealth, stats.healthRegen))
                .add(new Needs())
                .add(new Inventory(stats.inventorySlots))
                .add(new CarryingCapacity(stats.maxCarryWeight, 0f))
                .add(new Weight(stats.weight))
                .add(new Equipment())
                .add(new Resistances())
                .add(new BodyStatus())
                .add(new MentalState())
                .add(new Rest());

        addModelComponent(entity, stats);
    }

    private void addModelComponent(int entity, CreatureConfig.CreatureStats stats) {
        Model3D model3D = new Model3D();
        model3D.modelPath = stats.modelPath;
        model3D.scale = stats.modelScale;

        world.edit(entity).add(model3D);
    }

    private String generateCreatureId(String name, CreatureType creatureType) {
        if (name == null || name.trim().isEmpty()) {
            return creatureType.getCreatureType() + "_" + UUID.randomUUID();
        }
        return creatureType.getCreatureType() + "_" + name.toLowerCase().replace(" ", "_");
    }

    private void addDefaultSkills(int entity) {
        for (SurvivalSkillType skillType : SurvivalSkillType.values()) {
            if (skillType != SurvivalSkillType.NONE) {
                addSkill(entity, skillType, 0f);
            }
        }
    }

    private void addSkill(int entity, SurvivalSkillType skillType, float progress) {
        world.edit(entity).add(new SurvivalSkills(skillType, progress));
    }

    public int createPlayer(String name, float x, float y) {
        return createCreature(name, x, y, 0, CreatureType.PLAYER);
    }

    public int createPlayer(String name, Position position) {
        return createCreature(name, position.value.x, position.value.y, position.value.z, CreatureType.PLAYER);
    }

    public int createNPC(String name, float x, float y) {
        return createCreature(name, x, y, 0, CreatureType.OTHER);
    }

    public int createCreatureWithSkill(String name, float x, float y, float z,
                                       CreatureType creatureType,
                                       SurvivalSkillType skillType,
                                       float skillProgress) {
        int entity = createCreature(name, x, y, z, creatureType);
        addSkill(entity, skillType, skillProgress);
        return entity;
    }
}