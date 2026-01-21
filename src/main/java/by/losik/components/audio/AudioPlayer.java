package by.losik.components.audio;

import com.artemis.Component;

public class AudioPlayer extends Component {
    public String[] playlist;
    public float volume = 1.0f;
    public boolean isActive = true;

    public boolean loop = false;

    public AudioPlayer() {}
    public AudioPlayer(String... playlist) {
        this.playlist = playlist;
    }
}