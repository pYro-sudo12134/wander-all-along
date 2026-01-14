package by.losik.components.survival;

import com.artemis.Component;

public class BodyStatus extends Component {
    public float bodyTemperature = 36.6f;    // °C
    public float wetness = 0f;            // 0-1
    public float dirtiness = 0f;          // 0-1
    public float exhaustion = 0f;         // 0-1
    public float stress = 0f;             // 0-1

    public BodyStatus() {}

    public boolean isHypothermic() {
        return bodyTemperature < 35f;
    }

    public boolean isHyperthermic() {
        return bodyTemperature > 39f;
    }
}