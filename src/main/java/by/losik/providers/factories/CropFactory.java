package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Position;
import by.losik.components.ui.Description;
import by.losik.components.world.Crop;
import by.losik.components.world.GrowthStage;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CropFactory extends EntityFactory {
    @Inject
    public CropFactory(World world) {
        super(world);
    }

    public int createCrop(float x, float y, GrowthStage stage, float waterLevel, String name) {
        int entity = createBaseEntity();

        world.edit(entity)
                .add(new ID("crop", name))
                .add(new Position(x, y, 0))
                .add(new Crop(stage, waterLevel))
                .add(new Description("A growing crop"));

        return entity;
    }
}
