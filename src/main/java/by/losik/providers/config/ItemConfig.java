package by.losik.providers.config;

import by.losik.components.core.ItemType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class ItemConfig extends BaseConfig<ItemConfig.ItemTemplate, String> {

    public static class ItemTemplate {
        public final String itemId;
        public final String name;
        public final ItemType type;
        public final float weight;
        public final float value;
        public final int maxStackSize;
        public final Map<String, Object> properties;

        @JsonCreator
        public ItemTemplate(
                @JsonProperty("item_id") String itemId,
                @JsonProperty("name") String name,
                @JsonProperty("type") String type,
                @JsonProperty("weight") float weight,
                @JsonProperty("value") float value,
                @JsonProperty("max_stack_size") int maxStackSize,
                @JsonProperty("properties") Map<String, Object> properties
        ) {
            this.itemId = itemId;
            this.name = name;
            this.type = ItemType.valueOf(type.toUpperCase());
            this.weight = weight;
            this.value = value;
            this.maxStackSize = maxStackSize;
            this.properties = properties != null ? properties : Map.of();
        }
    }

    @Inject
    public ItemConfig(ConfigManager configManager) {
        super(configManager, ItemTemplate.class);
    }

    @Override
    protected String getConfigPath() {
        return "configs/items/templates.json";
    }

    @Override
    protected String getTemplateId(ItemTemplate template) {
        return template.itemId;
    }

    @Override
    protected String getConfigName() {
        return "item";
    }

    @Override
    protected void initializeDefaultTemplates() {
        templates.put("stone", new ItemTemplate(
                "stone", "Stone", "RESOURCE", 2.5f, 1f, 64,
                Map.of("hardness", 0.7f, "material", "stone")
        ));

        templates.put("wood_log", new ItemTemplate(
                "wood_log", "Wood Log", "RESOURCE", 5.0f, 3f, 32,
                Map.of("wood_type", "oak", "flammable", true)
        ));

        templates.put("apple", new ItemTemplate(
                "apple", "Apple", "FOOD", 0.3f, 2f, 16,
                Map.of("nutrition", 20f, "hydration", 5f, "spoil_time", 24f)
        ));

        templates.put("torch", new ItemTemplate(
                "torch", "Torch", "TOOL", 1.0f, 5f, 8,
                Map.of("light_radius", 10f, "burn_time", 3600f)
        ));

        logger.info("Initialized {} default item templates", templates.size());
    }
}