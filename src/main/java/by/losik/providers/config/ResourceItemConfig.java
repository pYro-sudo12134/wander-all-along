package by.losik.providers.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class ResourceItemConfig extends BaseConfig<ResourceItemConfig.ResourceTemplate, String> {

    public record ResourceTemplate(String resourceId, String name, String resourceType, float weight, float hardness,
                                   boolean flammable, Map<String, Float> yields) {
            @JsonCreator
            public ResourceTemplate(
                    @JsonProperty("resource_id") String resourceId,
                    @JsonProperty("name") String name,
                    @JsonProperty("resource_type") String resourceType,
                    @JsonProperty("weight") float weight,
                    @JsonProperty("hardness") float hardness,
                    @JsonProperty("flammable") boolean flammable,
                    @JsonProperty("yields") Map<String, Float> yields
            ) {
                this.resourceId = resourceId;
                this.name = name;
                this.resourceType = resourceType;
                this.weight = weight;
                this.hardness = hardness;
                this.flammable = flammable;
                this.yields = yields != null ? yields : Map.of();
            }
        }

    @Inject
    public ResourceItemConfig(ConfigManager configManager) {
        super(configManager, ResourceTemplate.class);
    }

    @Override
    protected String getConfigPath() {
        return "configs/items/resources.json";
    }

    @Override
    protected String getTemplateId(ResourceTemplate template) {
        return template.resourceId;
    }

    @Override
    protected String getConfigName() {
        return "resource";
    }

    @Override
    protected void initializeDefaultTemplates() {
        templates.put("oak_log", new ResourceTemplate(
                "oak_log", "Oak Log", "WOOD", 8.0f, 0.7f, true,
                Map.of("plank", 4f, "stick", 8f)
        ));

        templates.put("iron_ore", new ResourceTemplate(
                "iron_ore", "Iron Ore", "ORE", 3.0f, 0.9f, false,
                Map.of("iron_ingot", 0.5f)
        ));

        templates.put("stone_chunk", new ResourceTemplate(
                "stone_chunk", "Stone Chunk", "STONE", 4.0f, 0.8f, false,
                Map.of("stone_brick", 1f, "gravel", 2f)
        ));

        logger.info("Initialized {} default resource templates", templates.size());
    }
}