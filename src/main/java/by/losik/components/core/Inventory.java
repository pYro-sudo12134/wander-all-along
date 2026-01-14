package by.losik.components.core;

import com.artemis.Component;

import java.util.HashMap;
import java.util.Map;

public class Inventory extends Component {
    public Map<ID, Integer> items;
    public int maxSlots = 20;

    public boolean canAddItem() {
        return items.size() < maxSlots;
    }

    public Inventory() {}

    public Inventory(Map<ID, Integer> items, int maxSlots) {
        this.items = items != null ? items : new HashMap<>();
        this.maxSlots = maxSlots;
    }

    public Inventory(int maxSlots) {
        this.maxSlots = maxSlots;
        this.items = new HashMap<>();
    }
}