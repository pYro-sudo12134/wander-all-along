package by.losik.providers.config;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseConfig<T, ID> {
    protected static final Logger logger = LoggerFactory.getLogger(BaseConfig.class);

    protected final Map<ID, T> templates = new HashMap<>();
    protected final ConfigManager configManager;
    protected final Class<T> templateClass;

    @Inject
    public BaseConfig(ConfigManager configManager, Class<T> templateClass) {
        this.configManager = configManager;
        this.templateClass = templateClass;
        loadTemplates();
    }

    protected abstract String getConfigPath();
    protected abstract ID getTemplateId(T template);

    protected void loadTemplates() {
        try {
            List<T> loadedTemplates = configManager.loadConfigList(
                    getConfigPath(),
                    templateClass
            );

            for (T template : loadedTemplates) {
                templates.put(getTemplateId(template), template);
            }

            logger.info("Loaded {} {} templates from {}",
                    templates.size(), getConfigName(), getConfigPath());

        } catch (Exception e) {
            logger.error("Failed to load {} config from {}: {}",
                    getConfigName(), getConfigPath(), e.getMessage());
            initializeDefaultTemplates();
        }
    }

    protected abstract void initializeDefaultTemplates();
    protected abstract String getConfigName();

    public T getTemplate(ID templateId) {
        T template = templates.get(templateId);
        if (template == null) {
            throw new IllegalArgumentException(
                    "Unknown " + getConfigName() + " id: " + templateId
            );
        }
        return template;
    }

    public Map<ID, T> getAllTemplates() {
        return new HashMap<>(templates);
    }

    public boolean hasTemplate(ID templateId) {
        return templates.containsKey(templateId);
    }
}