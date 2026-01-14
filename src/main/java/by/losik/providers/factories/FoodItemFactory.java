package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.FoodStats;
import by.losik.components.items.StackData;
import by.losik.components.markers.items.Consumable;
import by.losik.components.markers.items.Perishable;
import by.losik.components.ui.Description;
import by.losik.providers.config.ItemConfig;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FoodItemFactory extends EntityFactory {

    private final ItemConfig itemConfig;

    @Inject
    public FoodItemFactory(World world, ItemConfig itemConfig) {
        super(world);
        this.itemConfig = itemConfig;
    }

    public int createFood(String foodId) {
        ItemConfig.ItemTemplate template = itemConfig.getTemplate(foodId);

        int entity = createBaseEntity();
        ID id = new ID(template.itemId, template.name);

        world.edit(entity)
                .add(id)
                .add(new Item(id, template.name, ItemType.FOOD, (int)template.value))
                .add(new Weight(template.weight))
                .add(new FoodStats(
                        getFloatProperty(template, "nutrition", 50f),
                        getFloatProperty(template, "hydration", 0f),
                        getFloatProperty(template, "spoil_time", 72f)
                ))
                .add(new StackData(template.maxStackSize, 1))
                .add(new Consumable())
                .add(new Perishable())
                .add(new Description("Edible " + template.name.toLowerCase()));

        return entity;
    }

    public int createApple() {
        return createFood("apple");
    }

    public int createCookedMeat() {
        int entity = createBaseEntity();
        ID id = new ID("cooked_meat", "Cooked Meat");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Cooked Meat", ItemType.FOOD, 10))
                .add(new Weight(1.5f))
                .add(new FoodStats(80f, 0f, 48f))
                .add(new StackData(8, 1))
                .add(new Consumable())
                .add(new Perishable())
                .add(new Description("Juicy cooked meat"));

        return entity;
    }

    public int createBread() {
        int entity = createBaseEntity();
        ID id = new ID("bread", "Bread");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Bread", ItemType.FOOD, 5))
                .add(new Weight(0.5f))
                .add(new FoodStats(40f, 5f, 24f))
                .add(new StackData(12, 1))
                .add(new Consumable())
                .add(new Perishable())
                .add(new Description("Fresh baked bread"));

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