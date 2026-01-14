package by.losik.providers.builders.equipment;

import by.losik.components.combat.DamageStats;
import by.losik.components.combat.DamageType;
import by.losik.components.combat.WeaponStats;
import by.losik.components.combat.WeaponType;
import by.losik.components.core.ID;
import by.losik.components.items.AppliedEnchantment;
import by.losik.components.items.MaterialInfo;
import by.losik.components.markers.items.Equippable;
import by.losik.components.ui.Description;
import by.losik.providers.builders.base.BaseItemBuilder;
import com.artemis.World;

public class WeaponBuilder extends BaseItemBuilder {
    private final WeaponType weaponType;
    private DamageType damageType;

    public WeaponBuilder(World world, WeaponType weaponType) {
        super(world);
        this.weaponType = weaponType;
        this.itemType = weaponType.getItemType();
        this.damageType = getDefaultDamageTypeForWeapon(weaponType);

        withComponent(new Equippable());

        float[] defaultStats = getDefaultStatsForWeaponType(weaponType);
        withComponent(new WeaponStats(
                weaponType,
                damageType,
                defaultStats[0],
                defaultStats[1],
                defaultStats[2]
        ));

        float armorPenetration = getArmorPenetrationForDamageType(damageType);
        withComponent(new DamageStats(
                ID.of(damageType.getId(), damageType.getDisplayName()),
                1.0f,
                armorPenetration
        ));
    }

    public WeaponBuilder withCombatStats(float baseDamage, float attackSpeed, float range) {
        with(WeaponStats.class, stats -> {
            stats.baseDamage = baseDamage;
            stats.attackSpeed = attackSpeed;
            stats.range = range;
        });
        return this;
    }

    public WeaponBuilder withDamageModifier(DamageType damageType, float multiplier) {
        ID damageTypeID = ID.of(damageType.getId(), damageType.getDisplayName());
        withComponent(new DamageStats(damageTypeID, multiplier, 0.0f));
        return this;
    }

    public WeaponBuilder withMaterial(String materialName, float baseWeight,
                                      float durability, float value) {
        ID materialId = ID.of(materialName.toLowerCase(), materialName);

        super.withMaterial(materialId, baseWeight, durability, value);

        float materialMultiplier = getMaterialMultiplier(materialName);
        with(WeaponStats.class, stats ->
                stats.baseDamage *= materialMultiplier);

        return this;
    }

    public WeaponBuilder withEnchantment(String enchantmentId, String enchantmentName, int level) {
        ID enchantmentID = ID.of(enchantmentId, enchantmentName);
        withComponent(new AppliedEnchantment(enchantmentID, level));
        return this;
    }

    public WeaponBuilder withCustomDamageType(DamageType damageType) {
        this.damageType = damageType;

        ID damageTypeId = ID.of(damageType.getId(), damageType.getDisplayName());

        with(WeaponStats.class, stats -> {
            stats.damageType = damageType;
        });

        with(DamageStats.class, damage -> {
            damage.damageTypeId = damageTypeId;
            damage.armorPenetration = getArmorPenetrationForDamageType(damageType);
        });

        return this;
    }

    public WeaponBuilder withCustomDamageType(String damageTypeStr) {
        DamageType damageType = DamageType.valueOf(damageTypeStr.toUpperCase());
        return withCustomDamageType(damageType);
    }

    @Override
    public int build() {
        ID entityIdComponent = getEntityId();
        WeaponStats weaponStats = getComponent(WeaponStats.class);
        DamageStats damageStats = getComponent(DamageStats.class);
        MaterialInfo materialInfo = getMaterialInfo();

        if (entityIdComponent == null) {
            String weaponId = generateWeaponId();
            String weaponName = generateWeaponName();
            withId(weaponId, weaponName);
        }

        if (this.itemType == null) {
            this.itemType = weaponType.getItemType();
        }

        float weight = (materialInfo != null) ? materialInfo.baseWeight : getDefaultWeightForWeaponType(weaponType);

        if (getComponent(by.losik.components.core.Item.class) == null) {
            ID entityId = getEntityId();
            withBaseItem(entityId.id, entityId.name, itemType, weight);
        }

        String description = generateDescription(weaponStats, damageStats, materialInfo);
        withComponent(new Description(description));

        return super.build();
    }

    private String generateWeaponId() {
        WeaponStats stats = getComponent(WeaponStats.class);
        MaterialInfo material = getMaterialInfo();

        StringBuilder id = new StringBuilder(weaponType.getId());

        if (stats != null && stats.damageType != null) {
            id.append("_").append(stats.damageType.getId());
        }
        if (material != null) {
            id.append("_").append(material.materialId.id);
        }
        return id.toString();
    }

    private String generateWeaponName() {
        WeaponStats stats = getComponent(WeaponStats.class);
        MaterialInfo material = getMaterialInfo();

        StringBuilder name = new StringBuilder();
        if (material != null) {
            name.append(material.materialId.name).append(" ");
        }
        name.append(weaponType.getDisplayName());
        if (stats != null && stats.damageType != null) {
            name.append(" of ").append(stats.damageType.getDisplayName());
        }
        return name.toString();
    }

    private String generateDescription(WeaponStats stats, DamageStats damageStats, MaterialInfo material) {
        StringBuilder description = new StringBuilder();

        if (material != null) {
            description.append(material.materialId.name).append(" ");
        }

        description.append(weaponType.getDisplayName().toLowerCase());

        if (stats != null) {
            description.append(" that deals ");
            if (stats.baseDamage >= 15) {
                description.append("heavy");
            } else if (stats.baseDamage >= 8) {
                description.append("moderate");
            } else {
                description.append("light");
            }
            description.append(" damage");

            if (stats.damageType != null) {
                description.append(" (").append(stats.damageType.getDisplayName()).append(")");
            }

            description.append(" with ");
            if (stats.attackSpeed >= 1.5f) {
                description.append("fast");
            } else if (stats.attackSpeed >= 1.0f) {
                description.append("moderate");
            } else {
                description.append("slow");
            }
            description.append(" attacks");

            if (stats.range > 2.0f) {
                description.append(" at range");
            }
        }

        if (damageStats != null && damageStats.armorPenetration > 0.2f) {
            description.append(". Has armor-piercing capability.");
        } else {
            description.append(".");
        }

        return description.toString();
    }

    private float[] getDefaultStatsForWeaponType(WeaponType type) {
        return switch (type) {
            case SWORD -> new float[]{12f, 1.2f, 1.5f};
            case AXE -> new float[]{15f, 0.9f, 1.3f};
            case MACE -> new float[]{18f, 0.7f, 1.2f};
            case DAGGER -> new float[]{8f, 1.8f, 1.0f};
            case SPEAR -> new float[]{14f, 1.0f, 2.5f};
            case BOW -> new float[]{10f, 1.5f, 15.0f};
            case CROSSBOW -> new float[]{25f, 0.5f, 12.0f};
            case STAFF -> new float[]{8f, 1.0f, 2.0f};
            case WAND -> new float[]{6f, 1.5f, 10.0f};
            case SHIELD -> new float[]{5f, 0.5f, 1.0f};
        };
    }

    private float getDefaultWeightForWeaponType(WeaponType type) {
        return switch (type) {
            case SWORD -> 2.5f;
            case AXE -> 3.5f;
            case MACE -> 4.0f;
            case DAGGER -> 1.0f;
            case SPEAR -> 3.0f;
            case BOW -> 2.0f;
            case CROSSBOW -> 4.5f;
            case STAFF -> 3f;
            case WAND -> 0.5f;
            case SHIELD -> 5.0f;
        };
    }

    private float getMaterialMultiplier(String materialName) {
        return switch (materialName.toLowerCase()) {
            case "wood" -> 0.7f;
            case "stone" -> 0.9f;
            case "bronze" -> 1.1f;
            case "iron" -> 1.3f;
            case "steel" -> 1.5f;
            case "mythril" -> 2.0f;
            default -> 1.0f;
        };
    }

    private float getArmorPenetrationForDamageType(DamageType damageType) {
        return switch (damageType) {
            case PIERCING -> 0.3f;
            case BLUDGEONING -> 0.1f;
            case ARCANE -> 0.5f;
            default -> 0.0f;
        };
    }

    private DamageType getDefaultDamageTypeForWeapon(WeaponType weaponType) {
        return switch (weaponType) {
            case SWORD, DAGGER -> DamageType.SLASHING;
            case AXE, MACE, SHIELD -> DamageType.BLUDGEONING;
            case SPEAR, BOW, CROSSBOW -> DamageType.PIERCING;
            case STAFF, WAND -> DamageType.ARCANE;
        };
    }

    @Override
    protected int getDefaultMaxStackSize() {
        return 1;
    }

    @Override
    protected String getDefaultDescription() {
        return "A weapon for combat";
    }
}