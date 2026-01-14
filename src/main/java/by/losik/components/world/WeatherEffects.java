package by.losik.components.world;

import com.artemis.Component;

public class WeatherEffects extends Component {

    public WeatherType current = WeatherType.CLEAR;
    public float temperature = 20f;
    public float humidity = 50f;
    public float windSpeed = 0f;
    public float precipitation = 0f;

    public boolean isFreezing() { return temperature < 0f; }
    public boolean isHot() { return temperature > 30f; }
}