package by.losik.systems.inventory;

import com.badlogic.gdx.graphics.Color;

public final class InventoryUIConfig {
    public static final int SLOT_SIZE = 64;
    public static final int SLOTS_PER_ROW = 10;
    public static final int PADDING = 10;
    public static final int INFO_PANEL_WIDTH = 300;
    public static final int ARMOR_PANEL_WIDTH = 200;

    public static final Color INVENTORY_BG_COLOR = new Color(0.1f, 0.1f, 0.15f, 0.95f);
    public static final Color PANEL_BG_COLOR = new Color(0.15f, 0.15f, 0.2f, 0.95f);

    public static final Color SLOT_NORMAL_COLOR = new Color(0.3f, 0.3f, 0.4f, 1f);
    public static final Color SLOT_SELECTED_COLOR = new Color(1f, 0.8f, 0f, 1f);

    public static final Color ARMOR_SLOT_NORMAL_COLOR = new Color(0.4f, 0.4f, 0.6f, 1f);
    public static final Color ARMOR_SLOT_BORDER_COLOR = new Color(0.6f, 0.6f, 0.8f, 1f);
    public static final Color ARMOR_SLOT_SELECTED_COLOR = new Color(1f, 1f, 0f, 1f);

    public static final Color TEXT_TITLE_COLOR = Color.WHITE;
    public static final Color TEXT_NORMAL_COLOR = Color.LIGHT_GRAY;
    public static final Color TEXT_SLOT_NUMBER_COLOR = Color.LIGHT_GRAY;
    public static final Color TEXT_DESCRIPTION_COLOR = Color.GRAY;
    public static final Color TEXT_ARMOR_SELECTED_COLOR = Color.YELLOW;
    public static final Color TEXT_INVENTORY_SELECTED_COLOR = Color.CYAN;
    public static final Color TEXT_CONTROLS_COLOR = Color.LIGHT_GRAY;
    public static final Color TEXT_CONTROLS_HIGHLIGHT_COLOR = Color.YELLOW;

    public static final float FONT_TITLE_SCALE = 1.1f;
    public static final float FONT_HEADER_SCALE = 1.1f;
    public static final float FONT_NORMAL_SCALE = 1.0f;
    public static final float FONT_SMALL_SCALE = 1.0f;
    public static final float FONT_CONTROLS_SCALE = 1.0f;

    public static final int TITLE_OFFSET_Y = 20;
    public static final int HEADER_OFFSET_Y = 50;
    public static final int CONTROL_START_OFFSET_Y = 70;
    public static final int CONTROL_LINE_SPACING = 15;
    public static final int CONTROL_INDENT = 20;

    public static final int ARMOR_SLOT_MAX_SIZE = 64;
    public static final int ARMOR_SLOT_PADDING = 10;
    public static final int ARMOR_SLOT_OFFSET_X = 20;
    public static final int ARMOR_TEXT_OFFSET_X = 40;
    public static final int ARMOR_TEXT_VERTICAL_SPACING = 15;

    public static final int SELECTION_BORDER_THICKNESS = 4;
    public static final int ARMOR_SELECTION_BORDER_THICKNESS = 2;

    public static final int ARMOR_NAME_MAX_LENGTH = 12;

    private InventoryUIConfig() {

    }

    public static Color blendColor(Color base, Color overlay, float alpha) {
        return new Color(
                base.r * (1 - alpha) + overlay.r * alpha,
                base.g * (1 - alpha) + overlay.g * alpha,
                base.b * (1 - alpha) + overlay.b * alpha,
                base.a * (1 - alpha) + overlay.a * alpha
        );
    }
}