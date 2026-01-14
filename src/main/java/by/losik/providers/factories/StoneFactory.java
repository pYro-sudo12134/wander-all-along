package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Position;
import by.losik.components.ui.Description;
import by.losik.components.world.StoneComponent;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StoneFactory extends EntityFactory {
    @Inject
    public StoneFactory(World world) {
        super(world);
    }

    public int createStone(float x, float y, StoneComponent.StoneType stoneType) {
        int entity = createBaseEntity();
        String stoneName = stoneType.name().toLowerCase();

        world.edit(entity)
                .add(new ID(stoneName + "_rock", stoneName + " Rock"))
                .add(new Position(x, y, 0))
                .add(new StoneComponent(stoneType))
                .add(new Description("A " + stoneName + " rock"));

        return entity;
    }
}
