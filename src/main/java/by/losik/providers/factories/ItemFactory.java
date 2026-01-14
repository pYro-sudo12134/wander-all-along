package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.*;
import by.losik.components.ui.Description;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ItemFactory extends EntityFactory {

    @Inject
    public ItemFactory(World world) {
        super(world);
    }

    public int createBasicItem(String id, String name, ItemType type, float weight) {
        int entity = createBaseEntity();

        world.edit(entity)
                .add(new ID(id, name))
                .add(new Item(ID.of(id), name, type, 1))
                .add(new Weight(weight))
                .add(new Description("Some " + type.getDisplayName().toLowerCase()));

        return entity;
    }

    protected int createItemWithComponents(String id, String name, ItemType type, float weight) {
        int entity = createBaseEntity();
        ID itemId = new ID(id, name);

        world.edit(entity)
                .add(itemId)
                .add(new Item(itemId, name, type, 1))
                .add(new Weight(weight))
                .add(new Description(name));

        return entity;
    }

    public int createStackableItem(String id, String name, ItemType type,
                                   float weight, int maxStackSize) {
        int entity = createItemWithComponents(id, name, type, weight);
        world.edit(entity).add(new StackData(maxStackSize, 1));
        return entity;
    }

    public int createDurableItem(String id, String name, ItemType type,
                                 float weight, float maxDurability) {
        int entity = createItemWithComponents(id, name, type, weight);
        world.edit(entity).add(new Durability(maxDurability));
        return entity;
    }
}