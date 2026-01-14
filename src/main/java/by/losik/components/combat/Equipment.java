package by.losik.components.combat;

import by.losik.components.core.ID;
import com.artemis.Component;

import java.util.EnumMap;
import java.util.Map;

public class Equipment extends Component {
    private final Map<ArmorSlot, ID> equippedArmor = new EnumMap<>(ArmorSlot.class);
    private ID equippedWeapon;

    public void equip(ArmorSlot slot, ID itemId) {
        equippedArmor.put(slot, itemId);
    }

    public ID getEquipped(ArmorSlot slot) {
        return equippedArmor.get(slot);
    }
}