package by.losik.systems.inventory;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class InventorySlotRenderer {
    private final ShapeRenderer shapeRenderer;

    public InventorySlotRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public void render(InventoryRenderContext context) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int row = 0; row < context.slotsY; row++) {
            for (int col = 0; col < context.slotsX; col++) {
                int slotIndex = row * context.slotsX + col;
                if (slotIndex >= context.maxSlots) continue;

                int slotX = context.getSlotX(col);
                int slotY = context.getSlotY(row);

                shapeRenderer.setColor(InventoryUIConfig.SLOT_NORMAL_COLOR);
                shapeRenderer.rect(slotX, slotY,
                        InventoryUIConfig.SLOT_SIZE, InventoryUIConfig.SLOT_SIZE);

                if (!context.state.inArmorSelection && slotIndex == context.state.selectedSlot) {
                    shapeRenderer.setColor(InventoryUIConfig.SLOT_SELECTED_COLOR);
                    shapeRenderer.rect(slotX - InventoryUIConfig.SELECTION_BORDER_THICKNESS,
                            slotY - InventoryUIConfig.SELECTION_BORDER_THICKNESS,
                            InventoryUIConfig.SLOT_SIZE + InventoryUIConfig.SELECTION_BORDER_THICKNESS * 2,
                            InventoryUIConfig.SLOT_SIZE + InventoryUIConfig.SELECTION_BORDER_THICKNESS * 2);
                }
            }
        }

        shapeRenderer.end();
    }
}