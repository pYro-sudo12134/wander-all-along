package by.losik.components.items;

import by.losik.components.core.ID;
import com.artemis.Component;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentEffects extends Component {
    public float defenseBonus;
    public float weightModifier;
    public float speedBonus;
    public Map<ID, Float> resistanceBonuses;

    public EnchantmentEffects() {}

    public EnchantmentEffects(float defenseBonus,
                              float weightModifier,
                              float speedBonus,
                              Map<ID, Float> resistanceBonuses) {
        this.defenseBonus = defenseBonus;
        this.weightModifier = weightModifier;
        this.speedBonus = speedBonus;
        this.resistanceBonuses = (resistanceBonuses != null ? resistanceBonuses : new HashMap<>());
    }
}