package by.losik.providers.builders.tool;

import java.util.Set;

import com.artemis.Component;

public interface ToolBuildable {
    ToolBuilder withToolStats(float efficiency, float durabilityLoss,
                              float harvestSpeed, Set<Class<? extends Component>> canHarvest);
}