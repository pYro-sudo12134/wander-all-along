package by.losik.components.survival;

import com.artemis.Component;

public class MentalState extends Component {
    public float mood = 50f;
    public float boredom = 0f;
    public float loneliness = 0f;
    public boolean isDepressed = false;

    public MentalState() {}
    public MentalState(float mood, float boredom,
                       float loneliness, boolean isDepressed) {
        this.mood = mood;
        this.boredom = boredom;
        this.loneliness = loneliness;
        this.isDepressed = isDepressed;
    }
}