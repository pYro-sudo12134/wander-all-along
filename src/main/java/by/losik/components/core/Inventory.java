package by.losik.components.core;

import com.artemis.Component;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;

import java.util.HashMap;
import java.util.Map;

public class Inventory extends Component {
    public Object2IntArrayMap<ID> items;
    public int maxSlots = 20;

    public boolean canAddItem() {
        return items.size() < maxSlots;
    }

    public Inventory() {}

    public Inventory(Object2IntArrayMap<ID> items, int maxSlots) {
        this.items = items != null ? items : new Object2IntArrayMap<>();
        this.maxSlots = maxSlots;
    }

    public Inventory(int maxSlots) {
        this.maxSlots = maxSlots;
        this.items = new Object2IntArrayMap<>();
    }
}