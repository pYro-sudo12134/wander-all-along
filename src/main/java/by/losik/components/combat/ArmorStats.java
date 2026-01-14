package by.losik.components.combat;

import by.losik.components.core.ID;
import com.artemis.Component;

public class ArmorStats extends Component {
    public ID armorTypeId;
    public int baseDefense;
    public float speedModifier;
    public ArmorSlot slot;

    public ArmorStats() {}

    public ArmorStats(ID armorTypeId, int baseDefense, float speedModifier, ArmorSlot slot) {
        this.armorTypeId = armorTypeId;
        this.baseDefense = baseDefense;
        this.speedModifier = speedModifier;
        this.slot = slot;
    }
}