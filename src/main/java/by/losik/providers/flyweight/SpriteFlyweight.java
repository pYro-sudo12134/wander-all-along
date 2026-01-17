package by.losik.providers.flyweight;

import by.losik.components.ui.Sprite;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SpriteFlyweight {
    private static volatile SpriteFlyweight instance;
    private final Map<String, Sprite> spriteCache;

    SpriteFlyweight() {
        spriteCache = new ConcurrentHashMap<>();
    }

    public static SpriteFlyweight getInstance() {
        if (instance == null) {
            synchronized (SpriteFlyweight.class) {
                if (instance == null) {
                    instance = new SpriteFlyweight();
                }
            }
        }
        return instance;
    }

    public Sprite getBaseSprite(String texturePath, int layer) {
        String key = createKey(texturePath, layer, 0, 0, -1, -1);

        return spriteCache.computeIfAbsent(key, k -> {
            Sprite sprite = new Sprite();
            sprite.texturePath = texturePath;
            sprite.layer = layer;
            // -1 means full texture
            sprite.regionWidth = -1;
            sprite.regionHeight = -1;
            return sprite;
        });
    }

    public Sprite getBaseSprite(String texturePath, int layer,
                                int regionX, int regionY,
                                int regionWidth, int regionHeight) {
        String key = createKey(texturePath, layer, regionX, regionY, regionWidth, regionHeight);

        return spriteCache.computeIfAbsent(key, k -> {
            Sprite sprite = new Sprite();
            sprite.texturePath = texturePath;
            sprite.layer = layer;
            sprite.regionX = regionX;
            sprite.regionY = regionY;
            sprite.regionWidth = regionWidth;
            sprite.regionHeight = regionHeight;
            return sprite;
        });
    }

    public Sprite createSprite(Sprite baseSprite, float offsetX, float offsetY, float scale) {
        Sprite sprite = new Sprite();
        copyIntrinsicState(baseSprite, sprite);
        sprite.offsetX = offsetX;
        sprite.offsetY = offsetY;
        sprite.scale = scale;
        return sprite;
    }

    public Sprite createSprite(String texturePath, int layer) {
        Sprite base = getBaseSprite(texturePath, layer);
        return createSprite(base, 0, 0, 1.0f);
    }

    public Sprite createSprite(String texturePath, int layer,
                               int regionX, int regionY,
                               int regionWidth, int regionHeight) {
        Sprite base = getBaseSprite(texturePath, layer, regionX, regionY, regionWidth, regionHeight);
        return createSprite(base, 0, 0, 1.0f);
    }

    public Sprite createSpriteForEntity(String texturePath, int layer,
                                        float scale, float offsetX, float offsetY) {
        Sprite base = getBaseSprite(texturePath, layer);
        return createSprite(base, offsetX, offsetY, scale);
    }

    private void copyIntrinsicState(Sprite source, Sprite target) {
        target.texturePath = source.texturePath;
        target.layer = source.layer;
        target.regionX = source.regionX;
        target.regionY = source.regionY;
        target.regionWidth = source.regionWidth;
        target.regionHeight = source.regionHeight;
    }

    private String createKey(String texturePath, int layer,
                             int regionX, int regionY,
                             int regionWidth, int regionHeight) {
        return String.format("%s_L%d_X%d_Y%d_W%d_H%d",
                texturePath, layer, regionX, regionY, regionWidth, regionHeight);
    }

    public void clearCache() {
        spriteCache.clear();
    }

    public int getCacheSize() {
        return spriteCache.size();
    }

    public boolean containsSprite(String texturePath, int layer) {
        String key = createKey(texturePath, layer, 0, 0, -1, -1);
        return spriteCache.containsKey(key);
    }

    public void removeSprite(String texturePath, int layer) {
        String key = createKey(texturePath, layer, 0, 0, -1, -1);
        spriteCache.remove(key);
    }
}