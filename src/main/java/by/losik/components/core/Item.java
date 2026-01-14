package by.losik.components.core;

import com.artemis.Component;

public class Item extends Component {
    public ID itemId;
    public String displayName;
    public ItemType type;
    public int baseValue = 1;

    public Item() {}
    public Item(ID itemId, String displayName,
                ItemType itemType, int baseValue) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.type = itemType;
        this.baseValue = baseValue;
    }
}