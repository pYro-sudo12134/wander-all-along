package by.losik.systems.collisions;

import by.losik.components.core.Bounds;
import by.losik.components.core.Interpolation;
import by.losik.components.core.Position;
import by.losik.components.core.SmoothPhysics;
import by.losik.components.core.Velocity;
import by.losik.systems.time.TimeSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.google.inject.Singleton;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
@All({Position.class, Bounds.class, SmoothPhysics.class})
public class SmoothPhysicsSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(SmoothPhysicsSystem.class);

    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<SmoothPhysics> mSmoothPhysics;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Interpolation> mInterpolation;

    private CollisionSystem collisionSystem;
    private TimeSystem timeSystem;
    private float smoothnessFactor = 0.2f;
    private float springStiffness = 50.0f;
    private float springDamping = 10.0f;
    private float maxCorrectionSpeed = 6.0f;
    private float predictionFactor = 0.0001f;
    private float collisionCorrectionFactor = 0.05f;
    private float minPenetrationForCorrection = 0.02f;
    private float velocityDampingOnCollision = 1f;

    private final Map<Integer, List<CollisionCorrection>> frameCorrections = new HashMap<>();
    private int processedEntities = 0;
    private int activeSmoothEntities = 0;

    @Override
    protected void initialize() {
        collisionSystem = world.getSystem(CollisionSystem.class);
        timeSystem = world.getSystem(TimeSystem.class);

        if (collisionSystem == null) {
            logger.warn("CollisionSystem not found! Smooth physics will not work properly.");
        }

        logger.info("SmoothPhysicsSystem initialized (optimized for minimal bouncing)");
        logger.info("Parameters: smoothness={}, spring=[{}, {}], maxCorrection={}, collisionCorrection={}",
                smoothnessFactor, springStiffness, springDamping, maxCorrectionSpeed, collisionCorrectionFactor);
    }

    @Override
    protected void begin() {
        frameCorrections.clear();
        processedEntities = 0;
        activeSmoothEntities = 0;
    }

    @Override
    protected void process(int entityId) {
        processedEntities++;

        Position position = mPosition.get(entityId);
        SmoothPhysics smooth = mSmoothPhysics.get(entityId);

        if (smooth.renderPosition.lengthSquared() == 0) {
            smooth.initialize(position.value);
            activeSmoothEntities++;
        }

        smooth.savePreviousState(position.value);
        boolean isColliding = collisionSystem != null && collisionSystem.isColliding(entityId);

        if (isColliding) {
            applySmoothCollisionCorrections(entityId, position, smooth);
        }

        applySpringPhysics(entityId, smooth, position, isColliding);
        updateInterpolationComponent(entityId, smooth);
    }

    @Override
    protected void end() {
        float currentTime = timeSystem != null ? timeSystem.getGameTime() : 0f;
        if (currentTime % 10.0f < world.getDelta()) {
            logger.debug("SmoothPhysics stats: processed={}, active={}, corrections={}",
                    processedEntities, activeSmoothEntities, frameCorrections.size());
        }
    }

    private void applySmoothCollisionCorrections(int entityId, Position position, SmoothPhysics smooth) {
        Set<Integer> collidingWith = collisionSystem.getCollidingEntities(entityId);
        List<CollisionCorrection> corrections = new ArrayList<>();

        Vector3f totalCorrection = new Vector3f();

        for (int otherId : collidingWith) {
            if (otherId <= entityId) continue;

            Vector3f normal = collisionSystem.getCollisionNormal(entityId, otherId);
            float penetration = collisionSystem.getPenetrationDepth(entityId, otherId);

            if (penetration < minPenetrationForCorrection) {
                continue;
            }

            float correctionAmount = penetration * collisionCorrectionFactor;
            Vector3f correction = new Vector3f(normal).mul(correctionAmount);

            totalCorrection.add(correction);
            corrections.add(new CollisionCorrection(correction, otherId, penetration));

            float physicalCorrectionFactor = 0.03f;
            position.value.x -= correction.x * physicalCorrectionFactor;
            position.value.y -= correction.y * physicalCorrectionFactor;
            position.value.z -= correction.z * physicalCorrectionFactor;

            smooth.renderVelocity.mul(velocityDampingOnCollision);
        }

        if (!corrections.isEmpty()) {
            float maxCorrectionThisFrame = maxCorrectionSpeed * world.getDelta();
            if (totalCorrection.length() > maxCorrectionThisFrame) {
                totalCorrection.normalize().mul(maxCorrectionThisFrame);
            }

            List<CollisionCorrection> finalList = new ArrayList<>();
            finalList.add(new CollisionCorrection(totalCorrection, -1, corrections.get(0).penetration));
            frameCorrections.put(entityId, finalList);
        }
    }

    private void applySpringPhysics(int entityId, SmoothPhysics smooth, Position position, boolean isColliding) {
        float deltaTime = world.getDelta();
        if (deltaTime <= 0) return;

        Vector3f targetPosition = new Vector3f(position.value);

        if (mVelocity.has(entityId) && !isColliding) {
            Velocity velocity = mVelocity.get(entityId);
            targetPosition.add(new Vector3f(velocity.value).mul(deltaTime * predictionFactor));
        }

        Vector3f displacement = new Vector3f(targetPosition).sub(smooth.renderPosition);

        float currentStiffness = isColliding ? springStiffness * 0.7f : springStiffness;
        float currentDamping = isColliding ? springDamping * 1.5f : springDamping;

        Vector3f springForce = new Vector3f(displacement).mul(currentStiffness);
        Vector3f dampingForce = new Vector3f(smooth.renderVelocity).mul(-currentDamping);
        Vector3f totalForce = new Vector3f(springForce).add(dampingForce);
        smooth.renderVelocity.add(new Vector3f(totalForce).mul(deltaTime));
        List<CollisionCorrection> corrections = frameCorrections.get(entityId);
        if (corrections != null && !corrections.isEmpty()) {
            CollisionCorrection correction = corrections.get(0);
            Vector3f smoothedCorrection = new Vector3f(correction.correction).mul(0.8f);
            smooth.renderPosition.sub(smoothedCorrection);
        }

        smooth.renderPosition.add(new Vector3f(smooth.renderVelocity).mul(deltaTime));
        float dampingFactor = isColliding ? 0.7f : 0.95f;
        smooth.renderVelocity.mul(dampingFactor);
        float maxRenderSpeed = isColliding ? 4.0f : 10.0f;
        if (smooth.renderVelocity.length() > maxRenderSpeed) {
            smooth.renderVelocity.normalize().mul(maxRenderSpeed);
        }

        if (smooth.renderVelocity.length() < 0.05f) {
            smooth.renderVelocity.set(0, 0, 0);
        }
    }

    private void updateInterpolationComponent(int entityId, SmoothPhysics smooth) {
        if (mInterpolation.has(entityId)) {
            Interpolation interp = mInterpolation.get(entityId);

            interp.previousPosition.set(smooth.previousPosition);
            float delta = world.getDelta();
            if (delta > 0) {
                interp.velocity.set(
                        (smooth.renderPosition.x - smooth.previousPosition.x) / delta,
                        (smooth.renderPosition.y - smooth.previousPosition.y) / delta,
                        (smooth.renderPosition.z - smooth.previousPosition.z) / delta
                );
            }

            interp.interpolationAlpha = 0.5f;

            if (delta > 0 && interp.velocity.lengthSquared() > 0.01f) {
                smooth.predictedPosition.set(smooth.renderPosition)
                        .add(new Vector3f(interp.velocity).mul(delta * 0.5f));
            }
        }
    }

    public Vector3f getRenderPosition(int entityId) {
        if (!mSmoothPhysics.has(entityId)) {
            return mPosition.has(entityId) ?
                    new Vector3f(mPosition.get(entityId).value) :
                    new Vector3f();
        }
        return new Vector3f(mSmoothPhysics.get(entityId).renderPosition);
    }

    public Vector3f getRenderVelocity(int entityId) {
        if (!mSmoothPhysics.has(entityId)) {
            return new Vector3f();
        }
        return new Vector3f(mSmoothPhysics.get(entityId).renderVelocity);
    }

    public Vector3f getPredictedPosition(int entityId) {
        if (!mSmoothPhysics.has(entityId)) {
            return getRenderPosition(entityId);
        }
        return new Vector3f(mSmoothPhysics.get(entityId).predictedPosition);
    }

    public boolean hasSmoothPhysics(int entityId) {
        return mSmoothPhysics.has(entityId);
    }

    public float getSmoothnessFactor() {
        return smoothnessFactor;
    }

    public float[] getSpringParameters() {
        return new float[]{springStiffness, springDamping};
    }

    public float getMaxCorrectionSpeed() {
        return maxCorrectionSpeed;
    }

    public int getActiveSmoothEntities() {
        return activeSmoothEntities;
    }

    public void setSmoothnessFactor(float factor) {
        this.smoothnessFactor = Math.max(0, Math.min(1, factor));
        logger.info("Smoothness factor set to: {}", smoothnessFactor);
    }

    public void setSpringParameters(float stiffness, float damping) {
        this.springStiffness = Math.max(0.1f, stiffness);
        this.springDamping = Math.max(0.1f, damping);
        logger.info("Spring parameters updated: stiffness={}, damping={}", stiffness, damping);
    }

    public void setMaxCorrectionSpeed(float speed) {
        this.maxCorrectionSpeed = Math.max(0.1f, speed);
        logger.info("Max correction speed set to: {}", maxCorrectionSpeed);
    }

    public void setPredictionFactor(float factor) {
        this.predictionFactor = Math.max(0, Math.min(0.5f, factor));
        logger.info("Prediction factor set to: {}", predictionFactor);
    }
    public void setCollisionCorrectionFactor(float factor) {
        this.collisionCorrectionFactor = Math.max(0.01f, Math.min(0.5f, factor));
        logger.info("Collision correction factor set to: {}", collisionCorrectionFactor);
    }

    public void setMinPenetrationForCorrection(float minPenetration) {
        this.minPenetrationForCorrection = Math.max(0.001f, minPenetration);
        logger.info("Min penetration for correction set to: {}", minPenetrationForCorrection);
    }

    public void setVelocityDampingOnCollision(float damping) {
        this.velocityDampingOnCollision = Math.max(0.1f, Math.min(1.0f, damping));
        logger.info("Velocity damping on collision set to: {}", velocityDampingOnCollision);
    }
    public void setCollisionDampingMultiplier(float multiplier) {
        setVelocityDampingOnCollision(multiplier > 1 ? 0.8f : 0.95f);
    }

    public void resetRenderPosition(int entityId) {
        if (mSmoothPhysics.has(entityId) && mPosition.has(entityId)) {
            SmoothPhysics smooth = mSmoothPhysics.get(entityId);
            Position position = mPosition.get(entityId);
            smooth.initialize(position.value);
            smooth.renderVelocity.set(0, 0, 0);
            logger.debug("Reset smooth physics for entity {}", entityId);
        }
    }
}