package by.losik.providers.builders.equipment;

import by.losik.components.combat.ArmorSlot;
import by.losik.components.combat.ArmorStats;
import by.losik.components.combat.DamageType;
import by.losik.components.combat.Resistances;
import by.losik.components.core.ID;
import by.losik.components.core.ItemType;
import by.losik.components.items.AppliedEnchantment;
import by.losik.components.items.EnchantmentEffects;
import by.losik.components.items.MaterialInfo;
import by.losik.components.markers.items.Equippable;
import by.losik.components.ui.Description;
import by.losik.providers.builders.base.BaseItemBuilder;
import com.artemis.World;

import java.util.HashMap;
import java.util.Map;

public class ArmorBuilder extends BaseItemBuilder {
    private final ArmorSlot slot;

    public ArmorBuilder(World world, ArmorSlot slot, String armorType) {
        super(world);
        this.slot = slot;
        withComponent(new Equippable());

        String armorTypeId = armorType.toLowerCase().replace(" ", "_");
        withComponent(new ArmorStats(
                ID.of(armorTypeId, armorType),
                getDefaultDefenseForSlot(slot),
                1.0f,
                slot
        ));
    }

    public ArmorBuilder withDefenseStats(int baseDefense, float speedModifier) {
        with(by.losik.components.combat.ArmorStats.class, stats -> {
            stats.baseDefense = baseDefense;
            stats.speedModifier = speedModifier;
        });
        return this;
    }

    public ArmorBuilder withDefaultDefenseStats() {
        int baseDefense = getDefaultDefenseForSlot(slot);
        with(by.losik.components.combat.ArmorStats.class, stats -> {
            stats.baseDefense = baseDefense;
            stats.speedModifier = 1.0f;
        });
        return this;
    }

    public ArmorBuilder withMaterial(String materialName, float baseWeight,
                                     float durability, float value) {
        ID materialId = ID.of(materialName.toLowerCase(), materialName);
        int baseDefense = getDefaultDefenseForSlot(slot);
        float materialMultiplier = getMaterialMultiplier(materialName);
        int finalDefense = (int)(baseDefense * materialMultiplier);

        with(by.losik.components.combat.ArmorStats.class, stats ->
                stats.baseDefense = finalDefense);

        return (ArmorBuilder) super.withMaterial(materialId, baseWeight, durability, value)
                .withComponent(new MaterialInfo(materialId, baseWeight, durability, value));
    }

    public ArmorBuilder addResistance(DamageType damageType, float resistance) {
        with(Resistances.class, res -> {
            res.set(
                    ID.of(damageType.getId(), damageType.getDisplayName()),
                    Math.max(0f, Math.min(1f, resistance))
            );
        });
        return this;
    }

    public ArmorBuilder withEnchantment(String enchantmentId, String enchantmentName,
                                        int level, float defenseBonus,
                                        float weightModifier, float speedBonus,
                                        Map<String, Float> resistanceBonuses) {
        ID enchantmentID = ID.of(enchantmentId, enchantmentName);

        withComponent(new AppliedEnchantment(enchantmentID, level));

        Map<ID, Float> resistanceBonusMap = new HashMap<>();
        if (resistanceBonuses != null) {
            for (Map.Entry<String, Float> entry : resistanceBonuses.entrySet()) {
                // Предполагаем, что ключ - это id типа урона
                ID damageTypeId = ID.of(entry.getKey(), entry.getKey());
                resistanceBonusMap.put(damageTypeId, entry.getValue());
            }
        }

        withComponent(new EnchantmentEffects(
                defenseBonus,
                weightModifier,
                speedBonus,
                resistanceBonusMap
        ));

        with(by.losik.components.combat.ArmorStats.class, stats -> {
            stats.baseDefense += defenseBonus;
            stats.speedModifier += speedBonus;
        });

        if (!resistanceBonusMap.isEmpty()) {
            with(Resistances.class, res -> {
                for (Map.Entry<ID, Float> entry : resistanceBonusMap.entrySet()) {
                    float current = res.get(entry.getKey());
                    res.set(entry.getKey(), Math.max(0f, Math.min(1f, current + entry.getValue())));
                }
            });
        }

        return this;
    }

    @Override
    public int build() {
        ID entityIdComponent = getEntityId();
        ArmorStats armorStats = getComponent(by.losik.components.combat.ArmorStats.class);
        MaterialInfo materialInfo = getMaterialInfo();

        if (entityIdComponent == null) {
            String armorId = generateArmorId();
            String armorName = generateArmorName();
            withId(armorId, armorName);
        }

        float weight = (materialInfo != null) ? materialInfo.baseWeight : getDefaultWeightForSlot(slot);
        this.itemType = ItemType.ARMOR;

        if (getComponent(by.losik.components.core.Item.class) == null) {
            ID entityId = getEntityId();
            withBaseItem(entityId.id, entityId.name, itemType, weight);
        }

        String description = generateDescription(armorStats, materialInfo);
        withComponent(new Description(description));

        return super.build();
    }

    private String generateArmorId() {
        ArmorStats stats = getComponent(by.losik.components.combat.ArmorStats.class);
        MaterialInfo material = getMaterialInfo();

        String id = slot.name().toLowerCase();
        if (stats != null && stats.armorTypeId != null) {
            id += "_" + stats.armorTypeId.id;
        }
        if (material != null) {
            id += "_" + material.materialId.id;
        }
        return id;
    }

    private String generateArmorName() {
        ArmorStats stats = getComponent(by.losik.components.combat.ArmorStats.class);
        MaterialInfo material = getMaterialInfo();

        StringBuilder name = new StringBuilder();
        if (material != null) {
            name.append(material.materialId.name).append(" ");
        }
        if (stats != null && stats.armorTypeId != null) {
            name.append(stats.armorTypeId.name).append(" ");
        }
        name.append(getSlotDisplayName(slot));
        return name.toString();
    }

    private String generateDescription(ArmorStats stats, MaterialInfo material) {
        StringBuilder description = new StringBuilder();

        if (stats != null && stats.armorTypeId != null) {
            description.append(stats.armorTypeId.name).append(" ");
        }

        description.append("armor for the ").append(slot.getBodyPart());

        if (stats != null) {
            description.append(". Provides ").append(stats.baseDefense).append(" defense");
            if (stats.speedModifier != 1.0f) {
                description.append(" and ");
                if (stats.speedModifier < 1.0f) {
                    description.append("slows movement by ");
                    int percent = (int)((1.0f - stats.speedModifier) * 100);
                    description.append(percent).append("%");
                } else {
                    description.append("increases movement speed by ");
                    int percent = (int)((stats.speedModifier - 1.0f) * 100);
                    description.append(percent).append("%");
                }
            }
        }

        if (material != null) {
            description.append(". Made from ").append(material.materialId.name.toLowerCase()).append(".");
        } else {
            description.append(".");
        }

        return description.toString();
    }

    private int getDefaultDefenseForSlot(ArmorSlot slot) {
        return switch (slot) {
            case HELMET -> 5;
            case TORSO -> 15;
            case LEGGINGS -> 10;
            case BOOTS -> 3;
            case NECK -> 2;
            case WAIST -> 4;
        };
    }

    private float getDefaultWeightForSlot(ArmorSlot slot) {
        return switch (slot) {
            case HELMET -> 1.5f;
            case TORSO -> 3.0f;
            case LEGGINGS -> 2.5f;
            case BOOTS -> 1.0f;
            case NECK -> 0.5f;
            case WAIST -> 1.0f;
        };
    }

    private float getMaterialMultiplier(String materialName) {
        return switch (materialName.toLowerCase()) {
            case "leather" -> 0.8f;
            case "iron" -> 1.2f;
            case "steel" -> 1.5f;
            case "mythril" -> 2.0f;
            default -> 1.0f;
        };
    }

    private String getSlotDisplayName(ArmorSlot slot) {
        return switch (slot) {
            case HELMET -> "Helmet";
            case TORSO -> "Chestplate";
            case LEGGINGS -> "Leggings";
            case BOOTS -> "Boots";
            case NECK -> "Necklace";
            case WAIST -> "Belt";
        };
    }

    @Override
    protected int getDefaultMaxStackSize() {
        return 1;
    }

    @Override
    protected String getDefaultDescription() {
        return "Protective armor";
    }
}