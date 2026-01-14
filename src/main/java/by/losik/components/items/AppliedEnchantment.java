package by.losik.components.items;

import by.losik.components.core.ID;
import com.artemis.Component;

public class AppliedEnchantment extends Component {
    public ID enchantmentId;
    public int level;

    public AppliedEnchantment() {}

    public AppliedEnchantment(ID enchantmentId, int level) {
        this.enchantmentId = enchantmentId;
        this.level = level;
    }
}