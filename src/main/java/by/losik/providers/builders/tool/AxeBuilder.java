package by.losik.providers.builders.tool;

import by.losik.components.core.Weight;
import by.losik.components.items.Durability;
import by.losik.components.items.ToolStats;
import by.losik.components.world.Crop;
import com.artemis.Component;
import com.artemis.World;

import java.util.HashSet;
import java.util.Set;

public class AxeBuilder extends ToolBuilder {

    public AxeBuilder(World world) {
        super(world);
        this.toolName = "Axe";
        this.baseWeight = 3.0f;
        this.baseValue = 12;
        this.baseDurability = 120.0f;

        initializeDefaultAxe();
    }

    private void initializeDefaultAxe() {
        withId("axe", "Axe");
        withComponent(new Weight(baseWeight));
        withComponent(new Durability(baseDurability));

        Set<Class<? extends Component>> canHarvest = new HashSet<>();
        canHarvest.add(Crop.class);

        withComponent(new ToolStats(1.8f, 0.015f, canHarvest, 1.2f));

        with(by.losik.components.core.Item.class, item ->
                item.baseValue = baseValue);
    }

    public AxeBuilder forChoppingWood() {
        ToolStats stats = getComponent(ToolStats.class);
        if (stats != null) {
            stats.efficiency = 2.2f;
            stats.harvestSpeed = 1.5f;
        }
        return this;
    }

    public AxeBuilder asBattleAxe() {
        with(by.losik.components.combat.WeaponStats.class, weapon -> {
            weapon.baseDamage = 18f;
            weapon.attackSpeed = 0.8f;
            weapon.range = 1.3f;
        });
        return this;
    }

    @Override
    protected String getDefaultDescription() {
        return "A tool for chopping wood";
    }
}