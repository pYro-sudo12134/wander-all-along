package by.losik.systems.music;

import by.losik.components.audio.MusicPlayer;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@All(MusicPlayer.class)
public class MusicSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(MusicSystem.class);

    protected ComponentMapper<MusicPlayer> mMusic;
    private final ObjectMap<String, Music> loadedMusic = new ObjectMap<>();
    private Music currentMusic;
    private float masterVolume = 0.5f;
    private boolean musicEnabled = true;
    private boolean isPlaying = false;

    @Override
    protected void initialize() {
        logger.info("MusicSystem initialized");
    }

    @Override
    protected void process(int entityId) {
        MusicPlayer musicPlayer = mMusic.get(entityId);

        if (!musicEnabled || !musicPlayer.isActive ||
                musicPlayer.playlist == null || musicPlayer.playlist.length == 0) {
            return;
        }

        if (!isPlaying && currentMusic == null) {
            playRandomTrack(musicPlayer);
        }

        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.setVolume(masterVolume * musicPlayer.volume);
        }
    }

    private void playRandomTrack(MusicPlayer musicPlayer) {
        if (musicPlayer.playlist.length == 0) return;

        int randomIndex = (int)(Math.random() * musicPlayer.playlist.length);
        playTrack(musicPlayer, randomIndex);
    }

    private void playTrack(MusicPlayer musicPlayer, int index) {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }

        String trackPath = musicPlayer.playlist[index];
        Music music = loadMusic(trackPath);

        if (music == null) {
            logger.error("Failed to load music: {}", trackPath);
            return;
        }

        currentMusic = music;

        music.setVolume(masterVolume * musicPlayer.volume);
        music.setLooping(true);

        music.play();
        isPlaying = true;

        logger.info("Now playing: {} (volume: {})",
                trackPath, masterVolume * musicPlayer.volume);
    }

    private Music loadMusic(String path) {
        if (loadedMusic.containsKey(path)) {
            return loadedMusic.get(path);
        }

        try {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
            loadedMusic.put(path, music);
            logger.debug("Loaded music: {}", path);
            return music;
        } catch (Exception e) {
            logger.error("Error loading music from {}: {}", path, e.getMessage());
            return null;
        }
    }


    public void playRandom() {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);
            MusicPlayer musicPlayer = mMusic.get(entityId);
            if (musicPlayer.isActive) {
                playRandomTrack(musicPlayer);
                break;
            }
        }
    }

    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            isPlaying = false;
        }
    }

    public void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
            isPlaying = false;
        }
    }

    public void resume() {
        if (currentMusic != null) {
            currentMusic.play();
            isPlaying = true;
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        if (currentMusic != null) {
            for (int i = 0; i < getEntityIds().size(); i++) {
                int entityId = getEntityIds().get(i);
                MusicPlayer musicPlayer = mMusic.get(entityId);
                if (musicPlayer.isActive) {
                    currentMusic.setVolume(masterVolume * musicPlayer.volume);
                    break;
                }
            }
        }
        logger.info("Music volume set to: {}", masterVolume);
    }

    public void setEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            pause();
        } else {
            if (!isPlaying()) {
                playRandom();
            }
        }
        logger.info("Music {}abled", enabled ? "en" : "dis");
    }

    public boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    @Override
    protected void dispose() {
        logger.info("Disposing MusicSystem");

        stop();

        for (Music music : loadedMusic.values()) {
            music.dispose();
        }
        loadedMusic.clear();
    }

    public float getMasterVolume() {
        return masterVolume;
    }
}