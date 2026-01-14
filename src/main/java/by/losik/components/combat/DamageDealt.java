package by.losik.components.combat;

import by.losik.components.core.ID;
import com.artemis.Component;

public class DamageDealt extends Component {
    public float amount;
    public ID damageTypeId;
    public int sourceEntityId;

    public DamageDealt(float amount, ID damageTypeId, int sourceEntityId) {
        this.amount = amount;
        this.damageTypeId = damageTypeId;
        this.sourceEntityId = sourceEntityId;
    }
}