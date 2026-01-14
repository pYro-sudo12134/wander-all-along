package by.losik.providers.config;

import by.losik.components.combat.ArmorSlot;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
public class ArmorConfig extends BaseConfig<ArmorConfig.ArmorTemplate, String> {

    public static class ArmorTemplate {
        public final String id;
        public final String name;
        public final ArmorSlot slot;
        public final String material;
        public final int defense;
        public final float weight;
        public final float durability;
        public final float speedModifier;
        public final int value;
        public final List<String> requiredSkills;

        @JsonCreator
        public ArmorTemplate(
                @JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("slot") String slotStr,
                @JsonProperty("material") String material,
                @JsonProperty("defense") int defense,
                @JsonProperty("weight") float weight,
                @JsonProperty("durability") float durability,
                @JsonProperty("speed_modifier") float speedModifier,
                @JsonProperty("value") int value,
                @JsonProperty("required_skills") List<String> requiredSkills
        ) {
            this.id = id;
            this.name = name;
            this.slot = ArmorSlot.valueOf(slotStr.toUpperCase());
            this.material = material;
            this.defense = defense;
            this.weight = weight;
            this.durability = durability;
            this.speedModifier = speedModifier;
            this.value = value;
            this.requiredSkills = requiredSkills != null ? requiredSkills : List.of();
        }
    }

    @Inject
    public ArmorConfig(ConfigManager configManager) {
        super(configManager, ArmorTemplate.class);
    }

    @Override
    protected String getConfigPath() {
        return "configs/armor/templates.json";
    }

    @Override
    protected String getTemplateId(ArmorTemplate template) {
        return template.id;
    }

    @Override
    protected String getConfigName() {
        return "armor";
    }

    @Override
    protected void initializeDefaultTemplates() {
        templates.put("leather_helmet", new ArmorTemplate(
                "leather_helmet", "Leather Helmet", "HELMET", "leather",
                2, 1.5f, 100f, 0.95f, 50, List.of("tailoring")
        ));

        templates.put("iron_helmet", new ArmorTemplate(
                "iron_helmet", "Iron Helmet", "HELMET", "iron",
                5, 3.0f, 200f, 0.85f, 150, List.of("smithing")
        ));

        logger.info("Initialized {} default armor templates", templates.size());
    }

    public List<ArmorTemplate> getTemplatesForSlot(ArmorSlot slot) {
        return templates.values().stream()
                .filter(t -> t.slot == slot)
                .toList();
    }
}