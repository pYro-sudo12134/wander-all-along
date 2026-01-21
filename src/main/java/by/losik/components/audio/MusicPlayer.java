package by.losik.components.audio;

import com.artemis.Component;

public class MusicPlayer extends Component {
    public String[] playlist;
    public float volume = 1.0f;
    public boolean isActive = true;

    public MusicPlayer() {}
    public MusicPlayer(String... playlist) {
        this.playlist = playlist;
    }
}