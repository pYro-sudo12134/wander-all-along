package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Position;
import by.losik.components.ui.Description;
import by.losik.components.world.WoodComponent;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TreeFactory extends EntityFactory{
    @Inject
    public TreeFactory(World world) {
        super(world);
    }

    public int createTree(float x, float y, WoodComponent.WoodType woodType) {
        int entity = createBaseEntity();
        String woodName = woodType.name().toLowerCase();

        world.edit(entity)
                .add(new ID(woodName + "_tree", woodName + " Tree"))
                .add(new Position(x, y, 0))
                .add(new WoodComponent(woodType))
                .add(new Description("A " + woodName + " tree"));

        return entity;
    }
}
