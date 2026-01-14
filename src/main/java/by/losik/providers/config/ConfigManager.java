package by.losik.providers.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Object> configCache = new HashMap<>();

    @Inject
    public ConfigManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        setupObjectMapper();
    }

    private void setupObjectMapper() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T loadConfig(String configPath, Class<T> configType) {
        return loadConfig(configPath, configType, true);
    }

    public <T> T loadConfig(String configPath, Class<T> configType, boolean cache) {
        if (cache && configCache.containsKey(configPath)) {
            return configType.cast(configCache.get(configPath));
        }

        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(configPath);

            if (inputStream == null) {
                Path filePath = Paths.get("src/main/resources", configPath);
                if (Files.exists(filePath)) {
                    inputStream = Files.newInputStream(filePath);
                } else {
                    throw new RuntimeException("Config file not found: " + configPath);
                }
            }

            T config = objectMapper.readValue(inputStream, configType);

            if (cache) {
                configCache.put(configPath, config);
            }

            logger.info("Loaded config: {}", configPath);
            return config;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + configPath, e);
        }
    }

    public <T> List<T> loadConfigList(String configPath, Class<T> elementType) {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(configPath);

            if (inputStream == null) {
                Path filePath = Paths.get("src/main/resources", configPath);
                if (Files.exists(filePath)) {
                    inputStream = Files.newInputStream(filePath);
                } else {
                    throw new RuntimeException("Config file not found: " + configPath);
                }
            }

            List<T> configList = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
            );

            logger.info("Loaded config list: {} ({} items)", configPath, configList.size());
            return configList;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config list: " + configPath, e);
        }
    }

    public void reloadConfig(String configPath) {
        configCache.remove(configPath);
        logger.info("Reloaded config: {}", configPath);
    }

    public void reloadAll() {
        configCache.clear();
        logger.info("Reloaded all configs");
    }

    public <K, V> Map<K, V> loadConfigMap(String configPath, Class<K> keyType, Class<V> valueType) {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(configPath);

            if (inputStream == null) {
                Path filePath = Paths.get("src/main/resources", configPath);
                if (Files.exists(filePath)) {
                    inputStream = Files.newInputStream(filePath);
                } else {
                    throw new RuntimeException("Config file not found: " + configPath);
                }
            }

            Map<K, V> configMap = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructMapType(Map.class, keyType, valueType)
            );

            logger.info("Loaded config map: {} ({} entries)", configPath, configMap.size());
            return configMap;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config map: " + configPath, e);
        }
    }
}