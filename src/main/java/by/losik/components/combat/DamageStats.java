package by.losik.components.combat;

import by.losik.components.core.ID;
import com.artemis.Component;

public class DamageStats extends Component {
    public ID damageTypeId;
    public float baseMultiplier;
    public float armorPenetration; // 0.0-1.0

    public DamageStats() {}

    public DamageStats(ID damageTypeId, float baseMultiplier, float armorPenetration) {
        this.damageTypeId = damageTypeId;
        this.baseMultiplier = baseMultiplier;
        this.armorPenetration = armorPenetration;
    }
}