package by.losik.providers.builders.resources;

import by.losik.components.core.ItemType;
import by.losik.components.items.StackData;
import by.losik.providers.builders.base.BaseItemBuilder;
import com.artemis.World;

public class ResourceBuilder extends BaseItemBuilder {

    public ResourceBuilder(World world) {
        super(world);
        this.itemType = ItemType.RESOURCE;
    }

    public ResourceBuilder withResource(String id, String name, float weight, float value) {
        return (ResourceBuilder) withBaseItem(id, name, ItemType.RESOURCE, weight)
                .with(by.losik.components.core.Item.class, item -> {
                    item.baseValue = (int)value;
                })
                .withDescription("A resource material.");
    }

    public ResourceBuilder asStackable(int maxStack) {
        withComponent(new StackData(maxStack, 1));
        return this;
    }

    @Override
    protected int getDefaultMaxStackSize() {
        return 99;
    }

    @Override
    protected String getDefaultDescription() {
        return "A resource material";
    }
}