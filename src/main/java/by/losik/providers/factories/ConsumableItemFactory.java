package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.StackData;
import by.losik.components.markers.items.Consumable;
import by.losik.components.ui.Description;
import by.losik.providers.config.ItemConfig;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ConsumableItemFactory extends EntityFactory {

    private final ItemConfig itemConfig;

    @Inject
    public ConsumableItemFactory(World world, ItemConfig itemConfig) {
        super(world);
        this.itemConfig = itemConfig;
    }

    public int createConsumable(String consumableId) {
        ItemConfig.ItemTemplate template = itemConfig.getTemplate(consumableId);

        int entity = createBaseEntity();
        ID id = new ID(template.itemId, template.name);

        world.edit(entity)
                .add(id)
                .add(new Item(id, template.name, ItemType.CONSUMABLE, (int)template.value))
                .add(new Weight(template.weight))
                .add(new StackData(template.maxStackSize, 1))
                .add(new Consumable())
                .add(new Description(getDescriptionFromTemplate(template)));

        return entity;
    }

    public int createHealthPotion() {
        int entity = createBaseEntity();
        ID id = new ID("health_potion", "Health Potion");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Health Potion", ItemType.CONSUMABLE, 25))
                .add(new Weight(0.5f))
                .add(new StackData(4, 1))
                .add(new Consumable())
                .add(new Description("Restores 50 health points"));

        return entity;
    }

    public int createManaPotion() {
        int entity = createBaseEntity();
        ID id = new ID("mana_potion", "Mana Potion");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Mana Potion", ItemType.CONSUMABLE, 30))
                .add(new Weight(0.5f))
                .add(new StackData(4, 1))
                .add(new Consumable())
                .add(new Description("Restores 100 mana points"));

        return entity;
    }

    public int createStaminaPotion() {
        int entity = createBaseEntity();
        ID id = new ID("stamina_potion", "Stamina Potion");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Stamina Potion", ItemType.CONSUMABLE, 15))
                .add(new Weight(0.3f))
                .add(new StackData(8, 1))
                .add(new Consumable())
                .add(new Description("Boosts stamina regeneration"));

        return entity;
    }

    private String getDescriptionFromTemplate(ItemConfig.ItemTemplate template) {
        Object effect = template.properties.get("effect");
        if (effect != null) {
            return "Has " + effect + " effect";
        }
        return template.name;
    }
}