package by.losik.components.world;

import com.artemis.Component;

public class TimeOfDay extends Component {
    public float timeOfDay = 12.0f;
    public float dayLength = 24.0f;
    public int dayNumber = 1;

    public boolean isNight() {
        return timeOfDay < 6.0f || timeOfDay > 20.0f;
    }

    public boolean isDawn() {
        return timeOfDay >= 5.0f && timeOfDay < 7.0f;
    }

    public boolean isDusk() {
        return timeOfDay >= 18.0f && timeOfDay < 20.0f;
    }
}