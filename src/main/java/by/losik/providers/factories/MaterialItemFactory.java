package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.MaterialInfo;
import by.losik.components.items.StackData;
import by.losik.components.ui.Description;
import by.losik.providers.config.ItemConfig;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MaterialItemFactory extends EntityFactory {

    private final ItemConfig itemConfig;

    @Inject
    public MaterialItemFactory(World world, ItemConfig itemConfig) {
        super(world);
        this.itemConfig = itemConfig;
    }

    public int createMaterial(String materialId) {
        ItemConfig.ItemTemplate template = itemConfig.getTemplate(materialId);

        int entity = createBaseEntity();
        ID id = new ID(template.itemId, template.name);

        world.edit(entity)
                .add(id)
                .add(new Item(id, template.name, ItemType.MATERIAL, (int)template.value))
                .add(new Weight(template.weight))
                .add(new MaterialInfo(
                        id,
                        template.weight,
                        getFloatProperty(template, "durability", 1.0f),
                        template.value
                ))
                .add(new StackData(template.maxStackSize, 1))
                .add(new Description("Crafting material: " + template.name));

        return entity;
    }

    public int createIronIngot() {
        int entity = createBaseEntity();
        ID id = new ID("iron_ingot", "Iron Ingot");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Iron Ingot", ItemType.MATERIAL, 15))
                .add(new Weight(2.0f))
                .add(new MaterialInfo(id, 2.0f, 0.9f, 15f))
                .add(new StackData(32, 1))
                .add(new Description("Refined iron ingot for crafting"));

        return entity;
    }

    public int createLeather() {
        int entity = createBaseEntity();
        ID id = new ID("leather", "Leather");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Leather", ItemType.MATERIAL, 8))
                .add(new Weight(1.5f))
                .add(new MaterialInfo(id, 1.5f, 0.6f, 8f))
                .add(new StackData(24, 1))
                .add(new Description("Tanned leather hide"));

        return entity;
    }

    public int createCloth() {
        int entity = createBaseEntity();
        ID id = new ID("cloth", "Cloth");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Cloth", ItemType.MATERIAL, 3))
                .add(new Weight(0.3f))
                .add(new MaterialInfo(id, 0.3f, 0.3f, 3f))
                .add(new StackData(48, 1))
                .add(new Description("Woven cloth fabric"));

        return entity;
    }

    private float getFloatProperty(ItemConfig.ItemTemplate template, String key, float defaultValue) {
        Object value = template.properties.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }
}