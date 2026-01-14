package by.losik.components.survival;

import com.artemis.Component;

public class Rest extends Component {
    public float sleepiness = 0f;
    public boolean isSleeping = false;
    public float sleepQuality = 1.0f;
    public BedType bedType = BedType.GROUND;

    public Rest() {}

    public Rest(float sleepiness, boolean isSleeping,
                float sleepQuality, BedType bedType) {
        this.sleepiness = sleepiness;
        this.isSleeping = isSleeping;
        this.sleepQuality = sleepQuality;
        this.bedType = bedType;
    }
}