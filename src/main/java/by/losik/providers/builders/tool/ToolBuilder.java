package by.losik.providers.builders.tool;

import by.losik.components.core.ID;
import by.losik.components.core.ItemType;
import by.losik.components.core.Weight;
import by.losik.components.items.Durability;
import by.losik.components.items.ToolStats;
import by.losik.components.markers.items.Equippable;
import by.losik.providers.builders.base.BaseItemBuilder;
import com.artemis.World;
import com.artemis.Component;

import java.util.Set;

public abstract class ToolBuilder extends BaseItemBuilder {

    protected String toolName;
    protected float baseWeight;
    protected int baseValue;
    protected float baseDurability;

    public ToolBuilder(World world) {
        super(world);
        this.itemType = ItemType.TOOL;
        withComponent(new Equippable());
    }

    public ToolBuilder withMaterial(String materialName) {
        ID materialId = ID.of(materialName.toLowerCase(), materialName);
        float durabilityBonus = getDurabilityBonus(materialName);
        float weightMultiplier = getWeightMultiplier(materialName);
        float valueMultiplier = getValueMultiplier(materialName);

        updateDurability(durabilityBonus);
        updateWeight(weightMultiplier);
        updateValue(valueMultiplier);

        return (ToolBuilder) withMaterial(materialId,
                baseWeight * weightMultiplier,
                durabilityBonus,
                valueMultiplier);
    }

    public ToolBuilder withToolStats(float efficiency, float durabilityLoss,
                                     float harvestSpeed, Set<Class<? extends Component>> canHarvest) {
        withComponent(new ToolStats(efficiency, durabilityLoss, canHarvest, harvestSpeed));
        return this;
    }

    protected void updateDurability(float multiplier) {
        Durability durability = getComponent(Durability.class);
        if (durability != null) {
            durability.max = baseDurability * multiplier;
            durability.current = durability.max;
        }
    }

    protected void updateWeight(float multiplier) {
        Weight weight = getComponent(Weight.class);
        if (weight != null) {
            weight.value = baseWeight * multiplier;
        }
    }

    protected void updateValue(float multiplier) {
        by.losik.components.core.Item item = getComponent(by.losik.components.core.Item.class);
        if (item != null) {
            item.baseValue = (int)(baseValue * multiplier);
        }
    }

    protected float getDurabilityBonus(String material) {
        return switch (material.toLowerCase()) {
            case "wood" -> 0.8f;
            case "stone" -> 1.2f;
            case "iron" -> 2.0f;
            case "steel" -> 2.5f;
            case "diamond" -> 5.0f;
            default -> 1.0f;
        };
    }

    protected float getWeightMultiplier(String material) {
        return switch (material.toLowerCase()) {
            case "wood" -> 0.8f;
            case "stone" -> 1.2f;
            case "iron" -> 1.5f;
            case "steel" -> 1.7f;
            case "diamond" -> 1.3f;
            default -> 1.0f;
        };
    }

    protected float getValueMultiplier(String material) {
        return switch (material.toLowerCase()) {
            case "wood" -> 1.0f;
            case "stone" -> 2.0f;
            case "iron" -> 5.0f;
            case "steel" -> 8.0f;
            case "diamond" -> 20.0f;
            default -> 1.0f;
        };
    }

    @Override
    protected int getDefaultMaxStackSize() {
        return 1;
    }
}