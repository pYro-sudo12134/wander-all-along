package by.losik.systems.inventory;

import by.losik.components.combat.ArmorSlot;
import by.losik.components.core.Inventory;
import by.losik.components.core.InventoryState;

public class InventoryRenderContext {
    public final int entityId;
    public final Inventory inventory;
    public final InventoryState state;

    public final int screenWidth;
    public final int screenHeight;

    public final int inventoryX;
    public final int inventoryY;
    public final int inventoryWidth;
    public final int inventoryHeight;

    public final int infoPanelX;
    public final int armorPanelX;

    public final int slotsX;
    public final int slotsY;
    public final int maxSlots;

    public final int armorSlotsCount;
    public final int armorSlotSize;
    public final int armorSlotPadding;
    public final int armorSlotsHeight;
    public final int armorStartY;
    public InventoryRenderContext(int entityId, Inventory inventory, InventoryState state,
                                  int screenWidth, int screenHeight) {
        this.entityId = entityId;
        this.inventory = inventory;
        this.state = state;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.maxSlots = inventory.maxSlots;
        this.slotsX = InventoryUIConfig.SLOTS_PER_ROW;
        this.slotsY = Math.max(1, (int) Math.ceil(maxSlots / (float) slotsX));

        this.inventoryWidth = slotsX * InventoryUIConfig.SLOT_SIZE + (slotsX - 1) * InventoryUIConfig.PADDING;
        this.inventoryHeight = slotsY * InventoryUIConfig.SLOT_SIZE + (slotsY - 1) * InventoryUIConfig.PADDING;

        int totalWidth = inventoryWidth + InventoryUIConfig.INFO_PANEL_WIDTH
                + InventoryUIConfig.ARMOR_PANEL_WIDTH + InventoryUIConfig.PADDING * 3;

        this.inventoryX = (screenWidth - totalWidth) / 2;
        this.inventoryY = (screenHeight - inventoryHeight) / 2;

        this.infoPanelX = inventoryX + inventoryWidth + InventoryUIConfig.PADDING;
        this.armorPanelX = infoPanelX + InventoryUIConfig.INFO_PANEL_WIDTH + InventoryUIConfig.PADDING;

        this.armorSlotsCount = ArmorSlot.values().length;
        this.armorSlotSize = Math.min(
                InventoryUIConfig.ARMOR_SLOT_MAX_SIZE,
                (inventoryHeight - 40 - (armorSlotsCount - 1) * InventoryUIConfig.ARMOR_SLOT_PADDING) / armorSlotsCount
        );
        this.armorSlotPadding = InventoryUIConfig.ARMOR_SLOT_PADDING;
        this.armorSlotsHeight = armorSlotsCount * armorSlotSize + (armorSlotsCount - 1) * armorSlotPadding;
        this.armorStartY = inventoryY + (inventoryHeight - armorSlotsHeight) / 2;
    }

    public int getSlotX(int col) {
        return inventoryX + col * (InventoryUIConfig.SLOT_SIZE + InventoryUIConfig.PADDING);
    }

    public int getSlotY(int row) {
        return inventoryY + (slotsY - 1 - row) * (InventoryUIConfig.SLOT_SIZE + InventoryUIConfig.PADDING);
    }

    public int getArmorSlotY(int index) {
        return armorStartY + index * (armorSlotSize + armorSlotPadding);
    }

    public int getRenderIndexForArmor(int displayIndex) {
        return armorSlotsCount - 1 - displayIndex;
    }

    public ArmorSlot getArmorSlotForDisplayIndex(int displayIndex) {
        int renderIndex = getRenderIndexForArmor(displayIndex);
        return ArmorSlot.values()[renderIndex];
    }
}