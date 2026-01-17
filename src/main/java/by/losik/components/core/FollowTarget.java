package by.losik.components.core;

import com.artemis.Component;

public class FollowTarget extends Component {
    public float targetX;
    public float targetY;
    public float targetZ;

    public FollowTarget() {}

    public FollowTarget(float x, float y, float z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }
}