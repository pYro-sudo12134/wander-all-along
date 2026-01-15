package by.losik.providers.builders.tool;

import by.losik.components.core.Weight;
import by.losik.components.items.Durability;
import by.losik.components.items.ToolStats;
import by.losik.components.world.Building;
import com.artemis.Component;
import com.artemis.World;

import java.util.HashSet;
import java.util.Set;

public class PickaxeBuilder extends ToolBuilder {

    public PickaxeBuilder(World world) {
        super(world);
        this.toolName = "Pickaxe";
        this.baseWeight = 2.5f;
        this.baseValue = 15;
        this.baseDurability = 100.0f;

        initializeDefaultPickaxe();
    }

    private void initializeDefaultPickaxe() {
        withId("pickaxe", "Pickaxe");
        withComponent(new Weight(baseWeight));
        withComponent(new Durability(baseDurability));

        Set<Class<? extends Component>> canHarvest = new HashSet<>();
        canHarvest.add(Building.class);

        withComponent(new ToolStats(1.5f, 0.02f, canHarvest, 1.0f));

        with(by.losik.components.core.Item.class, item -> {
            item.baseValue = baseValue;
        });
    }

    public PickaxeBuilder forMining(Set<Class<? extends Component>> canHarvest, float efficiency, float durabilityLossPerUse) {
        ToolStats stats = getComponent(ToolStats.class);
        if (stats != null) {
            stats.durabilityLossPerUse = durabilityLossPerUse;
            stats.efficiency = efficiency;
            stats.canHarvest = canHarvest;
        }
        return this;
    }

    @Override
    protected String getDefaultDescription() {
        return "A tool for mining ores and stone";
    }
}