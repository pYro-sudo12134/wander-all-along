package by.losik.components.survival;

import com.artemis.Component;

public class BodyParts extends Component {

    public BodyPart bodyPart;
    public float health = 100f;

    public BodyParts() {}

    public BodyParts(BodyPart bodyPart,
                     float health) {
        this.bodyPart = bodyPart;
        this.health = health;
    }

    public boolean isPartFunctional() {
        return health > 30f;
    }
}