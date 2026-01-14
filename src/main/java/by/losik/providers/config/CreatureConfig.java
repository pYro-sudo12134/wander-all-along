package by.losik.providers.config;

import by.losik.components.core.CreatureType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
public class CreatureConfig extends BaseConfig<CreatureConfig.CreatureTemplate, CreatureType> {

    public static class CreatureTemplate {
        public final CreatureType creatureType;
        public final float maxHealth;
        public final float healthRegen;
        public final int inventorySlots;
        public final float maxCarryWeight;
        public final float weight;
        public final float baseSpeed;
        public final float baseDamage;
        public final List<String> defaultSkills;

        @JsonCreator
        public CreatureTemplate(
                @JsonProperty("creature_type") String creatureTypeStr,
                @JsonProperty("max_health") float maxHealth,
                @JsonProperty("health_regen") float healthRegen,
                @JsonProperty("inventory_slots") int inventorySlots,
                @JsonProperty("max_carry_weight") float maxCarryWeight,
                @JsonProperty("weight") float weight,
                @JsonProperty("base_speed") float baseSpeed,
                @JsonProperty("base_damage") float baseDamage,
                @JsonProperty("default_skills") List<String> defaultSkills
        ) {
            this.creatureType = CreatureType.valueOf(creatureTypeStr.toUpperCase());
            this.maxHealth = maxHealth;
            this.healthRegen = healthRegen;
            this.inventorySlots = inventorySlots;
            this.maxCarryWeight = maxCarryWeight;
            this.weight = weight;
            this.baseSpeed = baseSpeed;
            this.baseDamage = baseDamage;
            this.defaultSkills = defaultSkills != null ? defaultSkills : List.of();
        }
    }

    @Inject
    public CreatureConfig(ConfigManager configManager) {
        super(configManager, CreatureTemplate.class);
    }

    @Override
    protected String getConfigPath() {
        return "configs/creatures/templates.json";
    }

    @Override
    protected CreatureType getTemplateId(CreatureTemplate template) {
        return template.creatureType;
    }

    @Override
    protected String getConfigName() {
        return "creature";
    }

    @Override
    protected void initializeDefaultTemplates() {
        templates.put(CreatureType.PLAYER, new CreatureTemplate(
                "PLAYER", 100f, 1f, 30, 50f, 60f, 5f, 10f,
                List.of("foraging", "cooking", "healing")
        ));

        templates.put(CreatureType.OTHER, new CreatureTemplate(
                "OTHER", 50f, 0.5f, 10, 20f, 40f, 4f, 5f,
                List.of()
        ));

        logger.info("Initialized {} default creature templates", templates.size());
    }

    public CreatureStats getStatsForType(CreatureType type) {
        CreatureTemplate template = getTemplate(type);
        return new CreatureStats(template);
    }

    public static class CreatureStats {
        public final float maxHealth;
        public final float healthRegen;
        public final int inventorySlots;
        public final float maxCarryWeight;
        public final float weight;
        public final float baseSpeed;
        public final float baseDamage;
        public final List<String> defaultSkills;

        public CreatureStats(CreatureTemplate template) {
            this.maxHealth = template.maxHealth;
            this.healthRegen = template.healthRegen;
            this.inventorySlots = template.inventorySlots;
            this.maxCarryWeight = template.maxCarryWeight;
            this.weight = template.weight;
            this.baseSpeed = template.baseSpeed;
            this.baseDamage = template.baseDamage;
            this.defaultSkills = template.defaultSkills;
        }
    }
}