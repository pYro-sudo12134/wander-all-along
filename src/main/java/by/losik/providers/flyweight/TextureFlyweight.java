package by.losik.providers.flyweight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TextureFlyweight {
    private static volatile TextureFlyweight instance;

    private final Map<String, Texture> gdxTextureCache;
    private final Map<String, TextureRegion> regionCache;
    private final Map<String, by.losik.components.ui.Texture> componentCache;

    private Texture defaultTexture;

    TextureFlyweight() {
        gdxTextureCache = new ConcurrentHashMap<>();
        regionCache = new ConcurrentHashMap<>();
        componentCache = new ConcurrentHashMap<>();
        loadDefaultTexture();
    }

    public static TextureFlyweight getInstance() {
        if (instance == null) {
            synchronized (TextureFlyweight.class) {
                if (instance == null) {
                    instance = new TextureFlyweight();
                }
            }
        }
        return instance;
    }

    public Texture getGdxTexture(String path) {
        if (path == null || path.trim().isEmpty()) {
            return getDefaultGdxTexture();
        }

        return gdxTextureCache.computeIfAbsent(path, p -> {
            try {
                if (Gdx.files == null) {
                    return getDefaultGdxTexture();
                }
                return new Texture(Gdx.files.internal(p));
            } catch (Exception e) {
                return getDefaultGdxTexture();
            }
        });
    }

    public TextureRegion getTextureRegion(String path, int x, int y, int width, int height) {
        String key = createRegionKey(path, x, y, width, height);

        return regionCache.computeIfAbsent(key, k -> {
            Texture texture = getGdxTexture(path);
            if (texture == null) {
                return new TextureRegion(getDefaultGdxTexture());
            }

            int actualWidth = width > 0 ? width : texture.getWidth();
            int actualHeight = height > 0 ? height : texture.getHeight();

            actualWidth = Math.min(actualWidth, texture.getWidth() - x);
            actualHeight = Math.min(actualHeight, texture.getHeight() - y);

            return new TextureRegion(texture, x, y, actualWidth, actualHeight);
        });
    }

    public by.losik.components.ui.Texture getTextureComponent(String path) {
        if (path == null || path.trim().isEmpty()) {
            return getDefaultTextureComponent();
        }

        return componentCache.computeIfAbsent(path, by.losik.components.ui.Texture::new);
    }

    public by.losik.components.ui.Texture getTextureComponent(String path, boolean isShared) {
        if (!isShared) {
            return new by.losik.components.ui.Texture(path);
        }
        return getTextureComponent(path);
    }

    public by.losik.components.ui.Texture getTexture(String path) {
        return getTextureComponent(path, true);
    }

    private Texture getDefaultGdxTexture() {
        if (defaultTexture == null) {
            loadDefaultTexture();
        }
        return defaultTexture;
    }

    private by.losik.components.ui.Texture getDefaultTextureComponent() {
        return getTextureComponent("textures/default.png");
    }

    private void loadDefaultTexture() {
        try {
            if (Gdx.files != null) {
                defaultTexture = new Texture(Gdx.files.internal("textures/default.png"));
                defaultTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            } else {
                defaultTexture = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGB888);
            }
        } catch (Exception e) {
            System.err.println("Failed to load default texture");
            defaultTexture = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGB888);
        }
    }

    private String createRegionKey(String path, int x, int y, int width, int height) {
        return String.format("%s_%d_%d_%d_%d", path, x, y, width, height);
    }

    public void clearCache() {
        for (Texture texture : gdxTextureCache.values()) {
            if (texture != defaultTexture) {
                texture.dispose();
            }
        }
        gdxTextureCache.clear();
        regionCache.clear();
        componentCache.clear();

        if (defaultTexture != null) {
            gdxTextureCache.put("textures/default.png", defaultTexture);
        }
    }

    public int getCacheSize() {
        return gdxTextureCache.size() + regionCache.size() + componentCache.size();
    }

    public boolean containsTexture(String path) {
        return gdxTextureCache.containsKey(path) || componentCache.containsKey(path);
    }

    public void removeTexture(String path) {
        Texture texture = gdxTextureCache.remove(path);
        if (texture != null && texture != defaultTexture) {
            texture.dispose();
        }
        componentCache.remove(path);

        regionCache.keySet().removeIf(key -> key.startsWith(path + "_"));
    }

    public void dispose() {
        clearCache();
        if (defaultTexture != null) {
            defaultTexture.dispose();
            defaultTexture = null;
        }
    }
}