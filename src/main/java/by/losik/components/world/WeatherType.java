package by.losik.components.world;

public enum WeatherType {
    CLEAR("clear"),
    CLOUDY("cloudy"),
    RAIN("rain"),
    STORM("storm"),
    SNOW("snow"),
    FOG("fog");

    private final String weatherType;
    WeatherType(String weatherType) {
        this.weatherType = weatherType;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
