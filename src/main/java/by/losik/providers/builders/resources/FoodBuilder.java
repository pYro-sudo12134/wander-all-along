package by.losik.providers.builders.resources;

import by.losik.components.core.ItemType;
import by.losik.components.items.FoodStats;
import by.losik.components.markers.items.Consumable;
import by.losik.components.markers.items.Perishable;
import by.losik.providers.builders.base.BaseItemBuilder;
import com.artemis.World;

public class FoodBuilder extends BaseItemBuilder {

    public FoodBuilder(World world) {
        super(world);
        this.itemType = ItemType.FOOD;
        withComponent(new Consumable());
        withComponent(new Perishable());
    }

    public FoodBuilder withNutrition(float nutrition, float hydration) {
        withComponent(new FoodStats(nutrition, hydration, 72.0f));
        return this;
    }

    public FoodBuilder asCooked() {
        FoodStats stats = getComponent(FoodStats.class);
        if (stats != null) {
            stats.isCooked = true;
            stats.nutrition *= 1.5f;
        }
        return this;
    }

    @Override
    protected int getDefaultMaxStackSize() {
        return 16;
    }

    @Override
    protected String getDefaultDescription() {
        FoodStats stats = getComponent(FoodStats.class);
        if (stats != null) {
            return String.format("Food that restores %.0f nutrition", stats.nutrition);
        }
        return "Food";
    }
}