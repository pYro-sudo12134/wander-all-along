package by.losik.components.items;

import com.artemis.Component;

import java.util.HashSet;
import java.util.Set;

public class ToolStats extends Component {
    public float efficiency = 1.0f;
    public float durabilityLossPerUse = 0.01f;
    public Set<Class<? extends Component>> canHarvest;
    public float harvestSpeed = 1.0f;

    public ToolStats() {}
    public ToolStats(float efficiency, float durabilityLoss, Set<Class<? extends Component>> canHarvest, float harvestSpeed) {
        this.efficiency = efficiency;
        this.durabilityLossPerUse = durabilityLoss;
        this.canHarvest = canHarvest != null ? canHarvest : new HashSet<>();
        this.harvestSpeed = harvestSpeed;
    }
}