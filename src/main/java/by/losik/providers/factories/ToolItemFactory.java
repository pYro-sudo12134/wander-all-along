package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.Durability;
import by.losik.components.items.StackData;
import by.losik.components.items.ToolStats;
import by.losik.components.items.ToolType;
import by.losik.components.markers.items.Equippable;
import by.losik.components.markers.items.Repairable;
import by.losik.components.ui.Description;
import by.losik.providers.config.ItemConfig;
import com.artemis.Component;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class ToolItemFactory extends EntityFactory {

    private final ItemConfig itemConfig;

    @Inject
    public ToolItemFactory(World world, ItemConfig itemConfig) {
        super(world);
        this.itemConfig = itemConfig;
    }

    public int createTool(String toolId) {
        ItemConfig.ItemTemplate template = itemConfig.getTemplate(toolId);

        int entity = createBaseEntity();
        ID id = new ID(template.itemId, template.name);

        world.edit(entity)
                .add(id)
                .add(new Item(id, template.name, ItemType.TOOL, (int)template.value))
                .add(new Weight(template.weight))
                .add(new Durability(getFloatProperty(template, "durability", 100f)))
                .add(new StackData(template.maxStackSize, 1))
                .add(new Equippable())
                .add(new Repairable())
                .add(new Description(template.name + " tool"));

        return entity;
    }

    public int createPickaxe(String material) {
        String toolName = material + "_pickaxe";
        int entity = createBaseEntity();
        ID id = new ID(toolName, material.substring(0, 1).toUpperCase() + material.substring(1) + " Pickaxe");

        float durability = switch (material) {
            case "wood" -> 60f;
            case "stone" -> 100f;
            case "iron" -> 200f;
            default -> 100f;
        };

        world.edit(entity)
                .add(id)
                .add(new Item(id, id.name, ItemType.TOOL, (int)(durability / 10)))
                .add(new Weight(getWeightByMaterial(material)))
                .add(new Durability(durability))
                .add(createToolStats(ToolType.PICKAXE, material))
                .add(new StackData(1, 1))
                .add(new Equippable())
                .add(new Repairable())
                .add(new Description("Used for mining ore and stone"));

        return entity;
    }

    public int createAxe(String material) {
        String toolName = material + "_axe";
        int entity = createBaseEntity();
        ID id = new ID(toolName, material.substring(0, 1).toUpperCase() + material.substring(1) + " Axe");

        float durability = switch (material) {
            case "wood" -> 80f;
            case "stone" -> 120f;
            case "iron" -> 250f;
            default -> 120f;
        };

        world.edit(entity)
                .add(id)
                .add(new Item(id, id.name, ItemType.TOOL, (int)(durability / 10)))
                .add(new Weight(getWeightByMaterial(material)))
                .add(new Durability(durability))
                .add(createToolStats(ToolType.AXE, material))
                .add(new StackData(1, 1))
                .add(new Equippable())
                .add(new Repairable())
                .add(new Description("Used for chopping wood"));

        return entity;
    }

    public int createShovel(String material) {
        String toolName = material + "_shovel";
        int entity = createBaseEntity();
        ID id = new ID(toolName, material.substring(0, 1).toUpperCase() + material.substring(1) + " Shovel");

        world.edit(entity)
                .add(id)
                .add(new Item(id, id.name, ItemType.TOOL, (int)(100f / 10)))
                .add(new Weight(getWeightByMaterial(material)))
                .add(new Durability(100f))
                .add(createToolStats(ToolType.SHOVEL, material))
                .add(new StackData(1, 1))
                .add(new Equippable())
                .add(new Repairable())
                .add(new Description("Used for digging soil and sand"));

        return entity;
    }

    private ToolStats createToolStats(ToolType toolType, String material) {
        float efficiency = switch (material) {
            case "wood" -> 0.7f;
            case "stone" -> 1.0f;
            case "iron" -> 1.5f;
            default -> 1.0f;
        };

        Set<Class<? extends Component>> harvestableTypes = new HashSet<>();

        return new ToolStats(
                efficiency,
                0.01f,
                harvestableTypes,
                1.0f
        );
    }

    private float getWeightByMaterial(String material) {
        return switch (material) {
            case "wood" -> 2.5f;
            case "stone" -> 5.0f;
            case "iron" -> 8.0f;
            default -> 3.0f;
        };
    }

    private float getFloatProperty(ItemConfig.ItemTemplate template, String key, float defaultValue) {
        Object value = template.properties.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }
}