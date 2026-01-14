package by.losik.components.world;

import com.artemis.Component;

public class Sensory extends Component {
    public float noiseLevel = 0f;
    public float scentLevel = 0f;
    public float visibility = 0f;

    public Sensory() {}
    public Sensory(float noiseLevel,
                   float scentLevel,
                   float visibility) {
        this.noiseLevel = noiseLevel;
        this.scentLevel = scentLevel;
        this.visibility = visibility;
    }
}