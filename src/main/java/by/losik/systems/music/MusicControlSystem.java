package by.losik.systems.music;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MusicControlSystem extends BaseSystem {
    private static final Logger logger = LoggerFactory.getLogger(MusicControlSystem.class);
    private MusicSystem musicSystem;
    private boolean mKeyWasPressed = false;
    private boolean nKeyWasPressed = false;
    private boolean commaKeyWasPressed = false;
    private boolean periodKeyWasPressed = false;

    @Override
    protected void initialize() {
        musicSystem = world.getSystem(MusicSystem.class);
    }

    @Override
    protected void processSystem() {
        boolean mPressed = Gdx.input.isKeyPressed(Input.Keys.M);
        if (mPressed && !mKeyWasPressed) {
            boolean isPlaying = musicSystem.isPlaying();
            if (isPlaying) {
                musicSystem.pause();
                logger.info("Music paused");
            } else {
                musicSystem.resume();
                logger.info("Music resumed");
            }
        }
        mKeyWasPressed = mPressed;

        boolean nPressed = Gdx.input.isKeyPressed(Input.Keys.N);
        if (nPressed && !nKeyWasPressed) {
            musicSystem.playRandom();
            logger.info("Random track");
        }
        nKeyWasPressed = nPressed;

        boolean commaPressed = Gdx.input.isKeyPressed(Input.Keys.COMMA);
        if (commaPressed && !commaKeyWasPressed) {
            adjustVolume(-0.1f);
            logger.info("Volume down: {}", musicSystem.getMasterVolume());
        }
        commaKeyWasPressed = commaPressed;

        boolean periodPressed = Gdx.input.isKeyPressed(Input.Keys.PERIOD);
        if (periodPressed && !periodKeyWasPressed) {
            adjustVolume(0.1f);
            logger.info("Volume up: {}", musicSystem.getMasterVolume());
        }
        periodKeyWasPressed = periodPressed;

        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            adjustVolume(0.1f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            adjustVolume(-0.1f);
        }
    }

    private void adjustVolume(float delta) {
        float currentVolume = musicSystem.getMasterVolume();
        float newVolume = Math.max(0, Math.min(1, currentVolume + delta));
        musicSystem.setMasterVolume(newVolume);
    }
}