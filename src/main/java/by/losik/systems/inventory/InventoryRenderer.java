package by.losik.systems.inventory;

import by.losik.components.core.ID;
import by.losik.components.core.Inventory;
import by.losik.components.core.InventoryState;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class InventoryRenderer {
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    private final BackgroundRenderer backgroundRenderer;
    private final InventorySlotRenderer slotRenderer;
    private final ArmorSlotRenderer armorRenderer;
    private final TextRenderer textRenderer;

    public InventoryRenderer(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, BitmapFont font) {
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
        this.font = font;

        this.backgroundRenderer = new BackgroundRenderer(shapeRenderer);
        this.slotRenderer = new InventorySlotRenderer(shapeRenderer);
        this.armorRenderer = new ArmorSlotRenderer(shapeRenderer);
        this.textRenderer = new TextRenderer(spriteBatch, font);
    }

    public void render(int entityId, Inventory inventory, InventoryState state,
                       int screenWidth, int screenHeight) {
        InventoryRenderContext context = new InventoryRenderContext(
                entityId, inventory, state, screenWidth, screenHeight
        );

        backgroundRenderer.render(context);
        slotRenderer.render(context);
        armorRenderer.render(context);
        textRenderer.render(context);
    }

    public void setIDMapper(ComponentMapper<ID> mID) {
        this.textRenderer.setIDMapper(mID);
    }
}