package by.losik.systems;

import by.losik.components.core.Creature;
import by.losik.components.core.CreatureType;
import by.losik.components.core.Velocity;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@All({Creature.class, Velocity.class})
public class PlayerInputSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(PlayerInputSystem.class);
    protected ComponentMapper<Creature> mCreature;
    protected ComponentMapper<Velocity> mVelocity;

    private float movementSpeed = 5.0f;

    @Override
    protected void process(int entityId) {
        Creature creature = mCreature.get(entityId);

        if (creature.type == CreatureType.PLAYER) {
            Velocity velocity = mVelocity.get(entityId);

            velocity.value.set(0, 0, 0);
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                velocity.value.y += movementSpeed;
                logger.info("W");
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                velocity.value.y -= movementSpeed;
                logger.info("S");
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocity.value.x -= movementSpeed;
                logger.info("A");
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocity.value.x += movementSpeed;
                logger.info("D");
            }

            if (velocity.value.x != 0 && velocity.value.y != 0) {
                velocity.value.normalize().mul(movementSpeed);
            }
        }
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }
}