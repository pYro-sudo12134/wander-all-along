package by.losik.components.ui;

import com.artemis.Component;

public class Sprite extends Component {
    public String texturePath;
    public int regionX = 0;
    public int regionY = 0;
    public int regionWidth = 32;
    public int regionHeight = 32;
    public float offsetX = 0;
    public float offsetY = 0;
    public float scale = 1.0f;
    public int layer = 0; // 0: background, 1: objects, 2: creatures, 3: effects, 4: UI

    public Sprite() {}

    public Sprite(String texturePath) {
        this.texturePath = texturePath;
    }

    public Sprite(String texturePath, int layer) {
        this.texturePath = texturePath;
        this.layer = layer;
    }
}