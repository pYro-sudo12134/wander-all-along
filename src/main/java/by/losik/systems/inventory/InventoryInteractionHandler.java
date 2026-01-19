package by.losik.systems.inventory;

import by.losik.components.combat.ArmorSlot;
import by.losik.components.core.Inventory;
import by.losik.components.core.InventoryState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: add interaction with items
public class InventoryInteractionHandler {
    private static final Logger logger = LoggerFactory.getLogger(InventoryInteractionHandler.class);
    public InventoryInteractionHandler() {
    }

    public void handleInteraction(int entityId, InventoryState inventoryState, Inventory inventory) {
        if (inventoryState.inArmorSelection) {
            handleArmorInteraction(entityId, inventoryState);
        } else {
            handleStandardInteraction(entityId, inventoryState, inventory);
        }
    }

    private void handleStandardInteraction(int entityId, InventoryState inventoryState, Inventory inventory) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            useSelectedItem(entityId, inventoryState.selectedSlot, inventory);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            dropSelectedItem(entityId, inventoryState.selectedSlot, inventory);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            examineSelectedItem(entityId, inventoryState.selectedSlot, inventory);
        }

        for (int i = Input.Keys.NUM_1; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                int slot = i - Input.Keys.NUM_1;
                if (slot < inventory.maxSlots) {
                    inventoryState.selectedSlot = slot;
                    logger.debug("Quick select slot: {}", slot + 1);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            int slot = 9;
            if (slot < inventory.maxSlots) {
                inventoryState.selectedSlot = slot;
                logger.debug("Quick select slot: 10");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            equipItemToArmor(entityId, inventoryState.selectedSlot, inventory);
        }
    }

    private void handleArmorInteraction(int entityId, InventoryState inventoryState) {
        ArmorSlot selectedArmorSlot = ArmorSlot.values()[inventoryState.selectedArmorSlot];

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            equipArmorFromInventory(entityId, selectedArmorSlot);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            unequipArmor(entityId, selectedArmorSlot);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            examineEquippedArmor(entityId, selectedArmorSlot);
        }
    }

    private void useSelectedItem(int entityId, int slot, Inventory inventory) {
        logger.info("Using item in slot {}", slot + 1);
    }

    private void dropSelectedItem(int entityId, int slot, Inventory inventory) {
        logger.info("Dropping item in slot {}", slot + 1);
    }

    private void examineSelectedItem(int entityId, int slot, Inventory inventory) {
        logger.info("Examining item in slot {}", slot + 1);
    }

    private void equipItemToArmor(int entityId, int slotIndex, Inventory inventory) {
        logger.info("Trying to equip item from slot {} to armor for entity {}",
                slotIndex + 1, entityId);
    }

    private void equipArmorFromInventory(int entityId, ArmorSlot slot) {
        logger.info("Trying to equip armor to {} slot for entity {}", slot, entityId);
    }

    private void unequipArmor(int entityId, ArmorSlot slot) {
        logger.info("Trying to unequip armor from {} slot for entity {}", slot, entityId);
    }

    private void examineEquippedArmor(int entityId, ArmorSlot slot) {
        logger.info("Examining armor in {} slot for entity {}", slot, entityId);
    }
}