package by.losik.components.core;

import com.artemis.Component;

public class BounceState extends Component {
    public float lastBounceTime = -1f;
    public float bounceCooldown = 0.1f; // 100ms

    public boolean canBounce(float currentTime) {
        return lastBounceTime < 0 || (currentTime - lastBounceTime) >= bounceCooldown;
    }

    public void setBounced(float currentTime) {
        lastBounceTime = currentTime;
    }
}