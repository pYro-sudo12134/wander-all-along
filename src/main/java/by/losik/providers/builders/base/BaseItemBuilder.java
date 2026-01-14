package by.losik.providers.builders.base;

import by.losik.components.core.ID;
import by.losik.components.core.Item;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.Durability;
import by.losik.components.items.MaterialInfo;
import by.losik.components.items.StackData;
import by.losik.components.ui.Description;
import com.artemis.World;

public abstract class BaseItemBuilder extends EntityBuilder {

    protected ItemType itemType;
    protected String itemId;
    protected String itemName;

    public BaseItemBuilder(World world) {
        super(world);
    }

    public BaseItemBuilder withBaseItem(String id, String name, ItemType type, float weight) {
        this.itemId = id;
        this.itemName = name;
        this.itemType = type;

        return (BaseItemBuilder) withId(id, name)
                .withComponent(new Item(ID.of(id), name, type, 1))
                .withComponent(new Weight(weight))
                .asCollectable()
                .withComponent(new StackData(getDefaultMaxStackSize(), 1))
                .withComponent(new Description(getDefaultDescription()));
    }

    public BaseItemBuilder withMaterial(ID materialId, float baseWeight,
                                        float durability, float value) {
        withComponent(new MaterialInfo(materialId, baseWeight, durability, value));
        return this;
    }

    public BaseItemBuilder withDurability(float maxDurability) {
        withComponent(new Durability(maxDurability));
        return this;
    }

    public BaseItemBuilder withDescription(String text) {
        Description existing = getComponent(Description.class);
        if (existing != null) {
            existing.text = text;
        } else {
            withComponent(new Description(text));
        }
        return this;
    }

    protected float getWeightFromComponents() {
        Weight weight = getComponent(Weight.class);
        if (weight != null) {
            return weight.value;
        }

        MaterialInfo material = getComponent(MaterialInfo.class);
        if (material != null) {
            return material.baseWeight;
        }

        return 1.0f;
    }

    protected abstract int getDefaultMaxStackSize();
    protected abstract String getDefaultDescription();
}