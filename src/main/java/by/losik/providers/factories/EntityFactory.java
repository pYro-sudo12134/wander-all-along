package by.losik.providers.factories;

import by.losik.components.core.ID;
import by.losik.components.core.Position;
import by.losik.components.core.State;
import com.artemis.World;

public abstract class EntityFactory {
    protected final World world;

    protected EntityFactory(World world) {
        this.world = world;
    }

    protected int createBaseEntity() {
        return world.create();
    }

    protected void addBasicComponents(int entity, ID id, Position pos) {
        world.edit(entity)
                .add(id)
                .add(pos)
                .add(new State());
    }
}