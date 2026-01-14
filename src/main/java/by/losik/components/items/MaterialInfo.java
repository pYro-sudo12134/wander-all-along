package by.losik.components.items;

import by.losik.components.core.ID;
import com.artemis.Component;

public class MaterialInfo extends Component {
    public ID materialId;
    public float baseWeight;
    public float durability;
    public float value;

    public MaterialInfo() {}

    public MaterialInfo(ID materialId, float baseWeight, float durability, float value) {
        this.materialId = materialId;
        this.baseWeight = baseWeight;
        this.durability = durability;
        this.value = value;
    }
}