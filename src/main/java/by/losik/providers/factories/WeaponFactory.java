package by.losik.providers.factories;

import by.losik.components.combat.DamageType;
import by.losik.components.combat.WeaponStats;
import by.losik.components.combat.WeaponType;
import by.losik.components.core.ID;
import by.losik.components.core.ItemType;
import by.losik.components.items.Durability;
import by.losik.components.items.MaterialInfo;
import by.losik.components.markers.items.Equippable;
import by.losik.components.ui.Description;
import by.losik.providers.config.WeaponConfig;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class WeaponFactory extends ItemFactory {

    private final WeaponConfig weaponConfig;

    @Inject
    public WeaponFactory(World world, WeaponConfig weaponConfig) {
        super(world);
        this.weaponConfig = weaponConfig;
    }

    public int createWeapon(String weaponId) {
        WeaponConfig.WeaponTemplate template = weaponConfig.getTemplate(weaponId);
        return createWeaponFromTemplate(template);
    }

    private int createWeaponFromTemplate(WeaponConfig.WeaponTemplate template) {
        int entity = createBaseEntity();
        ID weaponId = new ID(template.id, template.name);

        world.edit(entity)
                .add(weaponId)
                .add(new by.losik.components.core.Item(weaponId, template.name, ItemType.WEAPON, 1))
                .add(new by.losik.components.core.Weight(template.weight))
                .add(new Durability(template.durability))
                .add(new WeaponStats(
                        template.weaponType,
                        template.damageType,
                        template.baseDamage,
                        template.attackSpeed,
                        template.range
                ))
                .add(new MaterialInfo(
                        new ID(template.material, template.material),
                        template.weight,
                        template.durability,
                        template.value
                ))
                .add(new Equippable())
                .add(new Description(
                        template.name + " (" + template.damageType.getDisplayName() + " damage)"
                ));

        return entity;
    }

    public int createWeapon(String weaponId, Map<String, Object> overrides) {
        WeaponConfig.WeaponTemplate template = weaponConfig.getTemplate(weaponId);

        String damageTypeStr = overrides.containsKey("damage_type") ?
                (String) overrides.get("damage_type") : template.damageType.name();

        WeaponConfig.WeaponTemplate modifiedTemplate = new WeaponConfig.WeaponTemplate(
                template.id,
                template.name,
                template.weaponType.name(),
                damageTypeStr,
                template.material,
                overrides.containsKey("base_damage") ? (Float) overrides.get("base_damage") : template.baseDamage,
                overrides.containsKey("attack_speed") ? (Float) overrides.get("attack_speed") : template.attackSpeed,
                overrides.containsKey("range") ? (Float) overrides.get("range") : template.range,
                overrides.containsKey("weight") ? (Float) overrides.get("weight") : template.weight,
                overrides.containsKey("durability") ? (Float) overrides.get("durability") : template.durability,
                overrides.containsKey("value") ? (Integer) overrides.get("value") : template.value
        );

        return createWeaponFromTemplate(modifiedTemplate);
    }

    public int createCustomWeapon(String id, String name, String weaponTypeStr,
                                  String damageTypeStr, String material,
                                  float baseDamage, float attackSpeed, float range,
                                  float weight, float durability, int value) {
        int entity = createBaseEntity();
        ID weaponId = new ID(id, name);

        DamageType damageType = DamageType.valueOf(damageTypeStr.toUpperCase());

        world.edit(entity)
                .add(weaponId)
                .add(new by.losik.components.core.Item(weaponId, name, ItemType.WEAPON, 1))
                .add(new by.losik.components.core.Weight(weight))
                .add(new Durability(durability))
                .add(new WeaponStats(
                        WeaponType.valueOf(weaponTypeStr.toUpperCase()),
                        damageType,
                        baseDamage,
                        attackSpeed,
                        range
                ))
                .add(new MaterialInfo(
                        new ID(material, material),
                        weight,
                        durability,
                        value
                ))
                .add(new Equippable())
                .add(new Description(
                        name + " (" + damageType.getDisplayName() + " damage)"
                ));

        return entity;
    }
}