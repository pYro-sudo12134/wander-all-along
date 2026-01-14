package by.losik.components.items;

import by.losik.components.core.ID;
import com.artemis.Component;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class CraftingRequirements extends Component {
    public final Object2IntOpenHashMap<ID> materials;

    public CraftingRequirements() {
        this.materials = new Object2IntOpenHashMap<>();
    }

    public CraftingRequirements(Object2IntOpenHashMap<ID> materials) {
        this.materials = materials;
    }

    public int getRequired(ID materialId) {
        return materials.getInt(materialId);
    }

    public CraftingRequirements add(ID materialId, int count) {
        materials.put(materialId, count);
        return this;
    }
}