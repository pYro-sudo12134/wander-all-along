package by.losik.components.core;

import com.artemis.Component;

public class FollowTarget extends Component {
    public int targetEntityId = -1;
    public float targetX;
    public float targetY;
    public float targetZ;
    public float followSpeed = 0.1f;
    public float offsetX = 0;
    public float offsetY = 15f;
    public float offsetZ = 15f;

    public FollowTarget() {}

    public FollowTarget(int targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public FollowTarget(float x, float y, float z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }
}