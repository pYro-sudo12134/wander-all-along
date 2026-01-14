package by.losik.components.combat;

import by.losik.components.core.ID;
import com.artemis.Component;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

public class Resistances extends Component {
    public final Object2FloatOpenHashMap<ID> values;

    public Resistances() {
        this.values = new Object2FloatOpenHashMap<>();
        values.defaultReturnValue(0f);
    }

    public Resistances(Object2FloatOpenHashMap<ID> values) {
        this.values = values;
    }

    public float get(ID damageTypeId) {
        return values.getFloat(damageTypeId);
    }

    public Resistances set(ID damageTypeId, float resistance) {
        values.put(damageTypeId, Math.max(0f, Math.min(1f, resistance)));
        return this;
    }
}