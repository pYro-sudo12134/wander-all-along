package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.MaterialInfo;
import by.losik.components.items.StackData;
import by.losik.components.markers.items.Collectable;
import by.losik.components.ui.Description;
import by.losik.providers.config.ItemConfig;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ResourceItemFactory extends EntityFactory {

    private final ItemConfig itemConfig;

    @Inject
    public ResourceItemFactory(World world, ItemConfig itemConfig) {
        super(world);
        this.itemConfig = itemConfig;
    }

    public int createResource(String resourceId) {
        ItemConfig.ItemTemplate template = itemConfig.getTemplate(resourceId);

        int entity = createBaseEntity();
        ID id = new ID(template.itemId, template.name);

        world.edit(entity)
                .add(id)
                .add(new Item(id, template.name, ItemType.RESOURCE, (int)template.value))
                .add(new Weight(template.weight))
                .add(new MaterialInfo(
                        id,
                        template.weight,
                        getFloatProperty(template, "durability", 1.0f),
                        template.value
                ))
                .add(new StackData(template.maxStackSize, 1))
                .add(new Collectable())
                .add(new Description(template.name));

        return entity;
    }

    public int createStone() {
        return createResource("stone");
    }

    public int createWoodLog() {
        return createResource("wood_log");
    }

    public int createIronOre() {
        return createResource("iron_ore");
    }

    private float getFloatProperty(ItemConfig.ItemTemplate template, String key, float defaultValue) {
        Object value = template.properties.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }
}