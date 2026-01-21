package by.losik.systems.music;

import by.losik.components.audio.AudioPlayer;
import by.losik.systems.time.TimeSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Singleton
@All(AudioPlayer.class)
public class MusicSystem extends IteratingSystem {
    private static final Logger logger = LoggerFactory.getLogger(MusicSystem.class);

    protected ComponentMapper<AudioPlayer> mMusic;
    private final ObjectMap<String, Music> loadedMusic = new ObjectMap<>();
    private Music currentMusic;
    private float masterVolume = 0.5f;
    private boolean musicEnabled = true;
    private boolean isPlaying = false;

    private TimeSystem timeSystem;
    private float nextMusicTime = 0f;
    private float musicCooldown = 30f;
    private float minCooldown = 80f;
    private float maxCooldown = 240f;
    private float lastMusicStartTime = 0f;
    private final Map<String, Float> trackDurations = new HashMap<>();

    @Override
    protected void initialize() {
        timeSystem = world.getSystem(TimeSystem.class);
        nextMusicTime = (float) (Math.random() * 10f) + 5f;
        logger.info("MusicSystem initialized. First music in {} seconds", nextMusicTime);
    }

    @Override
    protected void process(int entityId) {
        AudioPlayer audioPlayer = mMusic.get(entityId);

        if (!musicEnabled || !audioPlayer.isActive ||
                audioPlayer.playlist == null || audioPlayer.playlist.length == 0) {
            return;
        }

        float currentTime = getCurrentGameTime();

        if (!isPlaying && currentMusic == null) {
            if (currentTime >= nextMusicTime) {
                playRandomTrack(audioPlayer);
                lastMusicStartTime = currentTime;
                musicCooldown = minCooldown + (float) Math.random() * (maxCooldown - minCooldown);
                nextMusicTime = currentTime + musicCooldown;
                logger.debug("Next music scheduled in {} seconds at time {}",
                        musicCooldown, nextMusicTime);
            }
        }

        if (currentMusic != null && isPlaying) {
            currentMusic.setVolume(masterVolume * audioPlayer.volume);

            String currentTrackPath = getCurrentTrackPath();
            if (currentTrackPath != null && trackDurations.containsKey(currentTrackPath)) {
                float trackDuration = trackDurations.get(currentTrackPath);
                if (currentTime - lastMusicStartTime > trackDuration + 5f) {
                    logger.debug("Track should have ended, stopping");
                    safeStopCurrentMusic();
                }
            }
        }
    }

    private float getCurrentGameTime() {
        return timeSystem != null ? timeSystem.getGameTime() : 0f;
    }

    private void playRandomTrack(AudioPlayer audioPlayer) {
        if (audioPlayer.playlist.length == 0) return;

        int randomIndex = (int)(Math.random() * audioPlayer.playlist.length);
        playTrack(audioPlayer, randomIndex);
    }

    private void playTrack(AudioPlayer audioPlayer, int index) {
        safeStopCurrentMusic();

        String trackPath = audioPlayer.playlist[index];
        Music music = loadMusic(trackPath);

        if (music == null) {
            logger.error("Failed to load music: {}", trackPath);
            return;
        }

        currentMusic = music;

        if (audioPlayer.loop) {
            music.setLooping(true);
        } else {
            music.setLooping(false);
            music.setOnCompletionListener(music1 -> {
                logger.debug("Track completed via callback");
                isPlaying = false;
                if (currentMusic == music1) {
                    safeDisposeCurrentMusic();
                }
            });
        }

        music.setVolume(masterVolume * audioPlayer.volume);
        music.play();
        isPlaying = true;
        lastMusicStartTime = getCurrentGameTime();

        logger.info("Now playing: {} at time {} (volume: {})",
                trackPath, lastMusicStartTime, masterVolume * audioPlayer.volume);
    }

    private String getCurrentTrackPath() {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);
            AudioPlayer audioPlayer = mMusic.get(entityId);
            if (audioPlayer.isActive && audioPlayer.playlist != null) {
                for (String track : audioPlayer.playlist) {
                    if (loadedMusic.get(track) == currentMusic) {
                        return track;
                    }
                }
            }
        }
        return null;
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

    private void safeStopCurrentMusic() {
        if (currentMusic != null) {
            try {
                currentMusic.stop();
                isPlaying = false;
            } catch (Exception e) {
                logger.error("Error stopping music: {}", e.getMessage());
            }
        }
    }

    private void safeDisposeCurrentMusic() {
        if (currentMusic != null) {
            try {
                currentMusic.dispose();
                currentMusic = null;
                isPlaying = false;
            } catch (Exception e) {
                logger.error("Error disposing music: {}", e.getMessage());
                currentMusic = null;
                isPlaying = false;
            }
        }
    }

    public void playRandom() {
        for (int i = 0; i < getEntityIds().size(); i++) {
            int entityId = getEntityIds().get(i);
            AudioPlayer audioPlayer = mMusic.get(entityId);
            if (audioPlayer.isActive) {
                playRandomTrack(audioPlayer);
                float currentTime = getCurrentGameTime();
                nextMusicTime = currentTime + musicCooldown;
                break;
            }
        }
    }

    public void stop() {
        safeStopCurrentMusic();
        float currentTime = getCurrentGameTime();
        nextMusicTime = currentTime + musicCooldown;
    }

    public void pause() {
        if (currentMusic != null && isPlaying) {
            try {
                currentMusic.pause();
                isPlaying = false;
                logger.debug("Music paused");
            } catch (Exception e) {
                logger.error("Error pausing music: {}", e.getMessage());
            }
        }
    }

    public void resume() {
        if (currentMusic != null && !isPlaying) {
            try {
                currentMusic.play();
                isPlaying = true;
                logger.debug("Music resumed");
            } catch (Exception e) {
                logger.error("Error resuming music: {}", e.getMessage());
            }
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        if (currentMusic != null) {
            for (int i = 0; i < getEntityIds().size(); i++) {
                int entityId = getEntityIds().get(i);
                AudioPlayer audioPlayer = mMusic.get(entityId);
                if (audioPlayer.isActive) {
                    currentMusic.setVolume(masterVolume * audioPlayer.volume);
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
                float currentTime = getCurrentGameTime();
                nextMusicTime = currentTime + (float) (Math.random() * 5f) + 2f;
            }
        }
        logger.info("Music {}abled", enabled ? "en" : "dis");
    }

    public boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public float getTimeUntilNextMusic() {
        float currentTime = getCurrentGameTime();
        return Math.max(0, nextMusicTime - currentTime);
    }

    public void setMusicCooldown(float minSeconds, float maxSeconds) {
        minCooldown = Math.max(1f, minSeconds);
        maxCooldown = Math.max(minCooldown + 1f, maxSeconds);
        logger.info("Music cooldown would be set to: {} - {} seconds (remove final modifier)",
                minSeconds, maxSeconds);
    }

    @Override
    protected void dispose() {
        logger.info("Disposing MusicSystem");

        safeStopCurrentMusic();
        safeDisposeCurrentMusic();

        for (Music music : loadedMusic.values()) {
            try {
                if (music != null) {
                    music.dispose();
                }
            } catch (Exception e) {
                logger.error("Error disposing music in cache: {}", e.getMessage());
            }
        }
        loadedMusic.clear();
        trackDurations.clear();
    }

    public float getMasterVolume() {
        return masterVolume;
    }
}