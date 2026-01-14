package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.StackData;
import by.losik.components.markers.items.Interactable;
import by.losik.components.ui.Description;
import by.losik.components.world.Container;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContainerItemFactory extends EntityFactory {

    @Inject
    public ContainerItemFactory(World world) {
        super(world);
    }

    public int createContainer(String containerType) {
        return switch (containerType) {
            case "chest" -> createChest();
            case "backpack" -> createBackpack();
            case "bag" -> createBag();
            case "crate" -> createCrate();
            default -> createChest();
        };
    }

    public int createChest() {
        int entity = createBaseEntity();
        ID id = new ID("wooden_chest", "Wooden Chest");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Wooden Chest", ItemType.CONTAINER, 50))
                .add(new Weight(15f))
                .add(new Container(id, 20, ""))
                .add(new StackData(1, 1))
                .add(new Interactable())
                .add(new Description("A sturdy wooden chest for storage"));

        return entity;
    }

    public int createBackpack() {
        int entity = createBaseEntity();
        ID id = new ID("backpack", "Backpack");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Backpack", ItemType.CONTAINER, 30))
                .add(new Weight(3f))
                .add(new Container(id, 12, ""))
                .add(new StackData(1, 1))
                .add(new Interactable())
                .add(new Description("Leather backpack with multiple pockets"));

        return entity;
    }

    public int createBag() {
        int entity = createBaseEntity();
        ID id = new ID("cloth_bag", "Cloth Bag");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Cloth Bag", ItemType.CONTAINER, 10))
                .add(new Weight(1f))
                .add(new Container(id, 8, ""))
                .add(new StackData(1, 1))
                .add(new Interactable())
                .add(new Description("Simple cloth bag for carrying items"));

        return entity;
    }

    public int createCrate() {
        int entity = createBaseEntity();
        ID id = new ID("wooden_crate", "Wooden Crate");

        world.edit(entity)
                .add(id)
                .add(new Item(id, "Wooden Crate", ItemType.CONTAINER, 25))
                .add(new Weight(8f))
                .add(new Container(id, 16, ""))
                .add(new StackData(1, 1))
                .add(new Interactable())
                .add(new Description("Reinforced wooden crate"));

        return entity;
    }
}