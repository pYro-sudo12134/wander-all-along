package by.losik.providers.flyweight;

import by.losik.components.ui.Texture;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TextureFlyweight {
    private static TextureFlyweight instance;

    private final Map<String, Texture> textureCache;

    private TextureFlyweight() {
        textureCache = new ConcurrentHashMap<>();
    }

    public static TextureFlyweight getInstance() {
        if (instance == null) {
            instance = new TextureFlyweight();
        }
        return instance;
    }

    public Texture getTexture(String path) {
        if (path == null || path.trim().isEmpty()) {
            return getDefaultTexture();
        }

        return textureCache.computeIfAbsent(path, Texture::new);
    }

    public Texture getTexture(String path, boolean isShared) {
        if (!isShared) {
            return new Texture(path);
        }
        return getTexture(path);
    }

    public Texture getDefaultTexture() {
        return getTexture("textures/default.png");
    }

    public void clearCache() {
        textureCache.clear();
    }

    public int getCacheSize() {
        return textureCache.size();
    }

    public boolean containsTexture(String path) {
        return textureCache.containsKey(path);
    }

    public void removeTexture(String path) {
        textureCache.remove(path);
    }
}