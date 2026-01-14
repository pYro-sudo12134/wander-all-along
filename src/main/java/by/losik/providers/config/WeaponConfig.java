package by.losik.providers.config;

import by.losik.components.combat.DamageType;
import by.losik.components.combat.WeaponType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class WeaponConfig extends BaseConfig<WeaponConfig.WeaponTemplate, String> {

    public static class WeaponTemplate {
        public final String id;
        public final String name;
        public final WeaponType weaponType;
        public final DamageType damageType;
        public final String material;
        public final float baseDamage;
        public final float attackSpeed;
        public final float range;
        public final float weight;
        public final float durability;
        public final int value;

        @JsonCreator
        public WeaponTemplate(
                @JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("weapon_type") String weaponTypeStr,
                @JsonProperty("damage_type") String damageTypeStr, // Изменено на String
                @JsonProperty("material") String material,
                @JsonProperty("base_damage") float baseDamage,
                @JsonProperty("attack_speed") float attackSpeed,
                @JsonProperty("range") float range,
                @JsonProperty("weight") float weight,
                @JsonProperty("durability") float durability,
                @JsonProperty("value") int value
        ) {
            this.id = id;
            this.name = name;
            this.weaponType = WeaponType.valueOf(weaponTypeStr.toUpperCase());
            this.damageType = DamageType.valueOf(damageTypeStr.toUpperCase()); // Конвертируем здесь
            this.material = material;
            this.baseDamage = baseDamage;
            this.attackSpeed = attackSpeed;
            this.range = range;
            this.weight = weight;
            this.durability = durability;
            this.value = value;
        }
    }

    @Inject
    public WeaponConfig(ConfigManager configManager) {
        super(configManager, WeaponTemplate.class);
    }

    @Override
    protected String getConfigPath() {
        return "configs/weapons/templates.json";
    }

    @Override
    protected String getTemplateId(WeaponTemplate template) {
        return template.id;
    }

    @Override
    protected String getConfigName() {
        return "weapon";
    }

    @Override
    protected void initializeDefaultTemplates() {
        templates.put("wooden_sword", new WeaponTemplate(
                "wooden_sword", "Wooden Sword", "SWORD", "SLASHING", "wood",
                5.0f, 1.2f, 1.5f, 2.0f, 50f, 20
        ));

        templates.put("iron_sword", new WeaponTemplate(
                "iron_sword", "Iron Sword", "SWORD", "SLASHING", "iron",
                15.0f, 1.0f, 1.5f, 3.0f, 200f, 100
        ));

        logger.info("Initialized {} default weapon templates", templates.size());
    }

    public List<WeaponTemplate> getTemplatesByType(WeaponType weaponType) {
        return templates.values().stream()
                .filter(t -> t.weaponType == weaponType)
                .collect(Collectors.toList());
    }
}