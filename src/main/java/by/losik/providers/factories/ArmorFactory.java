package by.losik.providers.factories;

import by.losik.components.combat.ArmorSlot;
import by.losik.components.combat.ArmorStats;
import by.losik.components.core.ID;
import by.losik.components.core.ItemType;
import by.losik.components.items.MaterialInfo;
import by.losik.components.markers.items.Equippable;
import by.losik.components.ui.Description;
import by.losik.providers.config.ArmorConfig;
import com.artemis.World;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class ArmorFactory extends ItemFactory {

    private final ArmorConfig armorConfig;

    @Inject
    public ArmorFactory(World world, ArmorConfig armorConfig) {
        super(world);
        this.armorConfig = armorConfig;
    }

    public int createArmor(String armorId) {
        ArmorConfig.ArmorTemplate template = armorConfig.getTemplate(armorId);
        return createArmorFromTemplate(template);
    }

    public int createArmor(String armorId, Map<String, Object> overrides) {
        ArmorConfig.ArmorTemplate template = armorConfig.getTemplate(armorId);

        ArmorConfig.ArmorTemplate modifiedTemplate = new ArmorConfig.ArmorTemplate(
                template.id,
                template.name,
                template.slot.name(),
                template.material,
                overrides.containsKey("defense") ? (Integer) overrides.get("defense") : template.defense,
                overrides.containsKey("weight") ? (Float) overrides.get("weight") : template.weight,
                overrides.containsKey("durability") ? (Float) overrides.get("durability") : template.durability,
                overrides.containsKey("speed_modifier") ? (Float) overrides.get("speed_modifier") : template.speedModifier,
                overrides.containsKey("value") ? (Integer) overrides.get("value") : template.value,
                template.requiredSkills
        );

        return createArmorFromTemplate(modifiedTemplate);
    }

    public int createCustomArmor(String id, String name, ArmorSlot slot,
                                 String material, int defense, float weight,
                                 float durability, float speedModifier,
                                 int value) {

        int entity = createDurableItem(id, name, ItemType.ARMOR, weight, durability);

        world.edit(entity)
                .add(new ArmorStats(ID.of(id), defense, speedModifier, slot))
                .add(new MaterialInfo(ID.of(material), weight, durability, value))
                .add(new Equippable())
                .add(new Description(
                        name + " (" + slot.getBodyPart() + " armor, " + defense + " defense)"
                ));

        return entity;
    }

    private int createArmorFromTemplate(ArmorConfig.ArmorTemplate template) {
        int entity = createDurableItem(
                template.id,
                template.name,
                ItemType.ARMOR,
                template.weight,
                template.durability
        );

        world.edit(entity)
                .add(new ArmorStats(
                        new ID(template.id, template.name),
                        template.defense,
                        template.speedModifier,
                        template.slot
                ))
                .add(new MaterialInfo(
                        new ID(template.material, template.material),
                        template.weight,
                        template.durability,
                        template.value
                ))
                .add(new Equippable())
                .add(new Description(
                        template.name + " - " + template.material + " " +
                                template.slot.getBodyPart() + " armor"
                ));

        return entity;
    }

    public int createSet(String setName) {
        return createArmor(setName);
    }
}