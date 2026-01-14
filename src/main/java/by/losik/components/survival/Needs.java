package by.losik.components.survival;

import com.artemis.Component;

public class Needs extends Component {
    public float hunger = 0f;      // 0-100
    public float thirst = 0f;      // 0-100
    public float stamina = 100f;   // 0-100
    public float hygiene = 100f;   // 0-100

    public boolean isStarving() { return hunger > 80f; }
    public boolean isDehydrated() { return thirst > 80f; }
    public boolean isExhausted() { return stamina < 20f; }

    public Needs() {}

    public void reset() {
        hunger = thirst = 0;
        stamina = hygiene = 100;
    }
}