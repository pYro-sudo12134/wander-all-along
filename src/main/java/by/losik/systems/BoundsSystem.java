package by.losik.systems;

import by.losik.components.core.Bounds;
import by.losik.components.core.Position;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.google.inject.Singleton;

@Singleton
@All({Position.class, Bounds.class})
public class BoundsSystem extends IteratingSystem {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BoundsSystem.class);
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Bounds> mBounds;
    private float worldMinX = -1f;
    private float worldMaxX = 1f;
    private float worldMinZ = -1f;
    private float worldMaxZ = 1f;
    private float worldMinY = -100f;
    private float worldMaxY = 100f;

    private boolean enforceWorldBounds = true;
    private boolean enforceVerticalBounds = true;

    @Override
    protected void process(int entityId) {
        Position position = mPosition.get(entityId);
        Bounds bounds = mBounds.get(entityId);

        if (!enforceWorldBounds) {
            return;
        }

        float halfWidth = bounds.getHalfWidth();

        if (position.value.x - halfWidth < worldMinX) {
            position.value.x = worldMinX + halfWidth;
        } else if (position.value.x + halfWidth > worldMaxX) {
            position.value.x = worldMaxX - halfWidth;
        }

        float halfDepth = bounds.getHalfDepth() > 0 ? bounds.getHalfDepth() : halfWidth;
        if (position.value.z - halfDepth < worldMinZ) {
            position.value.z = worldMinZ + halfDepth;
        } else if (position.value.z + halfDepth > worldMaxZ) {
            position.value.z = worldMaxZ - halfDepth;
        }

        if (enforceVerticalBounds) {
            float objectHeight = bounds.getHeight() > 0 ? bounds.getHeight() : 1.0f;

            if (position.value.y < worldMinY) {
                position.value.y = worldMinY;
                logger.info("Entity {} hit bottom boundary at y={}", entityId, worldMinY);
            }

            if (position.value.y + objectHeight > worldMaxY) {
                position.value.y = worldMaxY - objectHeight;
                logger.info("Entity {} hit top boundary at y={}", entityId, worldMaxY);
            }
        }
    }

    public void setWorldBounds(float minX, float maxX, float minZ, float maxZ) {
        this.worldMinX = minX;
        this.worldMaxX = maxX;
        this.worldMinZ = minZ;
        this.worldMaxZ = maxZ;
    }

    public void setVerticalBounds(float minY, float maxY) {
        this.worldMinY = minY;
        this.worldMaxY = maxY;
    }

    public void setAllBounds(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.worldMinX = minX;
        this.worldMaxX = maxX;
        this.worldMinY = minY;
        this.worldMaxY = maxY;
        this.worldMinZ = minZ;
        this.worldMaxZ = maxZ;
    }

    public void setEnforceWorldBounds(boolean enforce) {
        this.enforceWorldBounds = enforce;
    }

    public void setEnforceVerticalBounds(boolean enforce) {
        this.enforceVerticalBounds = enforce;
    }
}