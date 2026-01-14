package by.losik.components.core;

import com.artemis.Component;

public class Creature extends Component {
    public ID creatureId;
    public CreatureType type;

    public Creature() {}
    public Creature(ID creatureId, CreatureType creatureType) {
        this.creatureId = creatureId;
        this.type = creatureType;
    }
}
