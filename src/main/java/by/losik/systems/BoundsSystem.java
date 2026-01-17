package by.losik.systems;

import by.losik.components.core.Bounds;
import by.losik.components.core.Position;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All({Position.class, Bounds.class})
public class BoundsSystem extends IteratingSystem {
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Bounds> mBounds;
    private float worldMinX = -100f;
    private float worldMaxX = 100f;
    private float worldMinY = -100f;
    private float worldMaxY = 100f;
    private boolean enforceWorldBounds = true;

    @Override
    protected void process(int entityId) {
        Position position = mPosition.get(entityId);
        Bounds bounds = mBounds.get(entityId);

        if (enforceWorldBounds) {
            float halfWidth = bounds.getHalfWidth();
            if (position.value.x - halfWidth < worldMinX) {
                position.value.x = worldMinX + halfWidth;
            } else if (position.value.x + halfWidth > worldMaxX) {
                position.value.x = worldMaxX - halfWidth;
            }

            float halfHeight = bounds.getHalfHeight();
            if (position.value.y - halfHeight < worldMinY) {
                position.value.y = worldMinY + halfHeight;
            } else if (position.value.y + halfHeight > worldMaxY) {
                position.value.y = worldMaxY - halfHeight;
            }
        }
    }

    public void setWorldBounds(float minX, float maxX, float minY, float maxY) {
        this.worldMinX = minX;
        this.worldMaxX = maxX;
        this.worldMinY = minY;
        this.worldMaxY = maxY;
    }

    public void setEnforceWorldBounds(boolean enforce) {
        this.enforceWorldBounds = enforce;
    }
}