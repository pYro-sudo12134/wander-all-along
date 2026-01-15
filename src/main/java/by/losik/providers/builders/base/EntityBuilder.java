package by.losik.providers.builders.base;

import by.losik.components.core.Bounds;
import by.losik.components.core.ID;
import by.losik.components.core.Inventory;
import by.losik.components.core.Position;
import by.losik.components.items.MaterialInfo;
import by.losik.components.survival.Health;
import by.losik.components.ui.Description;
import by.losik.components.ui.Texture;
import by.losik.providers.builders.flyweight.TextureFlyweight;
import com.artemis.World;
import com.artemis.Component;

import java.util.function.Consumer;

public abstract class EntityBuilder {
    private final World world;
    private final int entityId;

    public EntityBuilder(World world) {
        this.world = world;
        this.entityId = world.create();
    }

    public EntityBuilder withId(String id, String name) {
        world.edit(entityId).add(new ID(id, name));
        return this;
    }

    public EntityBuilder withPosition(float x, float y) {
        world.edit(entityId).add(new Position(x, y));
        return this;
    }

    public EntityBuilder withPosition(float x, float y, float z) {
        world.edit(entityId).add(new Position(x, y, z));
        return this;
    }

    public EntityBuilder withBounds(float width, float height) {
        world.edit(entityId).add(new Bounds(width, height));
        return this;
    }

    public EntityBuilder withComponent(Component component) {
        world.edit(entityId).add(component);
        return this;
    }

    public EntityBuilder withDescription(String description) {
        world.edit(entityId).add(new Description(description));
        return this;
    }

    public EntityBuilder withHealth(float current, float max, float regen) {
        world.edit(entityId).add(new Health(current, max, regen));
        return this;
    }

    public EntityBuilder withInventory(int maxSlots) {
        world.edit(entityId).add(new Inventory(maxSlots));
        return this;
    }

    public EntityBuilder withTexture(String texturePath) {
        Texture texture = TextureFlyweight.getInstance().getTexture(texturePath);
        world.edit(entityId).add(texture);
        return this;
    }

    public EntityBuilder asCollectable() {
        world.edit(entityId).add(new by.losik.components.markers.items.Collectable());
        return this;
    }

    public EntityBuilder asInteractable() {
        world.edit(entityId).add(new by.losik.components.markers.items.Interactable());
        return this;
    }

    public <T extends Component> EntityBuilder with(Class<T> componentClass,
                                                    Consumer<T> configurator) {
        T component = world.getMapper(componentClass).create(entityId);
        configurator.accept(component);
        world.edit(entityId).add(component);
        return this;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return world.getMapper(componentClass).get(entityId);
    }

    public <T extends Component> T getOrCreateComponent(Class<T> componentClass,
                                                        Consumer<T> initializer) {
        T component = getComponent(componentClass);
        if (component == null) {
            component = world.getMapper(componentClass).create(entityId);
            if (initializer != null) {
                initializer.accept(component);
            }
            withComponent(component);
        }
        return component;
    }

    public ID getEntityId() {
        return getComponent(ID.class);
    }

    public MaterialInfo getMaterialInfo() {
        return getComponent(MaterialInfo.class);
    }

    public int build() {
        return entityId;
    }

    public World and() {
        return world;
    }
}