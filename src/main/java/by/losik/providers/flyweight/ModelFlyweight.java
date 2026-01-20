package by.losik.providers.flyweight;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ModelFlyweight {
    private static final Logger logger = LoggerFactory.getLogger(ModelFlyweight.class);
    private static ModelFlyweight instance;

    private final ObjectMap<String, Model> modelCache = new ObjectMap<>();
    private final ObjectMap<Integer, ModelInstance> instanceCache = new ObjectMap<>();

    public static ModelFlyweight getInstance() {
        if (instance == null) {
            synchronized (ModelFlyweight.class) {
                if (instance == null) {
                    instance = new ModelFlyweight();
                }
            }
        }
        return instance;
    }

    public Model getModel(String modelPath) {
        if (modelPath == null || modelPath.trim().isEmpty()) {
            logger.error("Model path is null or empty");
            return null;
        }

        if (modelCache.containsKey(modelPath)) {
            logger.debug("Returning cached model: {}", modelPath);
            return modelCache.get(modelPath);
        }

        logger.info("Model not in cache: {}", modelPath);
        return null;
    }

    public void cacheModel(String modelPath, Model model) {
        if (modelPath != null && model != null) {
            modelCache.put(modelPath, model);
            logger.info("Cached model: {}", modelPath);
        }
    }

    public ModelInstance getInstance(String modelPath) {
        Model model = getModel(modelPath);
        if (model != null) {
            return new ModelInstance(model);
        }
        return null;
    }

    public ModelInstance getOrCreateInstance(int entityId, String modelPath) {
        if (instanceCache.containsKey(entityId)) {
            return instanceCache.get(entityId);
        }

        ModelInstance instance = getInstance(modelPath);
        if (instance != null) {
            instanceCache.put(entityId, instance);
        }
        return instance;
    }

    public void cacheInstance(int entityId, ModelInstance instance) {
        if (instance != null) {
            instanceCache.put(entityId, instance);
        }
    }

    public void removeInstance(int entityId) {
        instanceCache.remove(entityId);
    }

    public void clearCache() {
        for (Model model : modelCache.values()) {
            model.dispose();
        }
        modelCache.clear();
        instanceCache.clear();
        logger.info("Model cache cleared");
    }

    public void dispose() {
        clearCache();
    }

    public int getModelCacheSize() {
        return modelCache.size;
    }

    public int getInstanceCacheSize() {
        return instanceCache.size;
    }
}