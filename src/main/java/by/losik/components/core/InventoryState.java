package by.losik.components.core;

import com.artemis.Component;

public class InventoryState extends Component {
    public boolean isOpen = false;
    public int selectedSlot = 0;
    public boolean inArmorSelection = false;
    public int selectedArmorSlot = 0;

    public void toggle() {
        isOpen = !isOpen;
        if (isOpen) {
            selectedSlot = 0;
        }
    }
}