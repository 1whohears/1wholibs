package com.onewhohears.onewholibs.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.util.UtilMCText;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BackgroundScreen extends Screen {

    public final ResourceLocation background_texture;
    public final int image_width, image_height;
    public final int texture_width, texture_height;

    protected int guiX, guiY;
    protected int top_padding = 4, bottom_padding = 4;
    protected int left_padding = 4, right_padding = 4;
    protected int vertical_widget_shift = 0;

    protected BackgroundScreen(Component title, ResourceLocation backgroundTexture,
                               int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        super(title);
        background_texture = backgroundTexture;
        image_width = imageWidth;
        image_height = imageHeight;
        texture_width = textureWidth;
        texture_height = textureHeight;
    }

    protected BackgroundScreen(String translatableScreenName, ResourceLocation backgroundTexture,
                               int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        this(UtilMCText.translatable(translatableScreenName), backgroundTexture,
                imageWidth, imageHeight, textureWidth, textureHeight);
    }

    @Override
    protected void init() {
        super.init();
        guiX = width/2-image_width/2;
        guiY = height/2-image_height/2;
    }

    protected AbstractWidget positionWidgetGrid(AbstractWidget widget, int rows, int columns, int index,
                                                int padding, int widget_rows, int widget_columns) {
        if (rows <= 0) rows = 1;
        if (columns <= 0) columns = 1;
        int index_max = rows * columns - 1;
        if (index < 0) index = 0;
        else if (index > index_max) index = index_max;
        int widget_row = index / rows;
        int widget_column = index % columns;
        int column_width = (image_width - left_padding - right_padding) / columns;
        int row_height = (image_height - top_padding - bottom_padding) / rows;
        int widget_x = guiX + left_padding + widget_column * column_width + padding/2;
        int widget_y = guiY + vertical_widget_shift + top_padding + widget_row * row_height + padding/2;
        int widget_width = column_width * widget_columns - padding;
        int widget_height = row_height * widget_rows - padding;
        widget.x = widget_x;
        widget.y = widget_y;
        widget.setWidth(widget_width);
        widget.setHeight(widget_height);
        addRenderableWidget(widget);
        return widget;
    }

    protected AbstractWidget positionWidgetGrid(AbstractWidget widget, int rows, int columns, int index, int padding) {
        return positionWidgetGrid(widget, rows, columns, index, padding, 1, 1);
    }

    protected AbstractWidget positionWidgetGrid(AbstractWidget widget, int rows, int columns, int index) {
        return positionWidgetGrid(widget, rows, columns, index, 2);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(poseStack);
        super.render(poseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderBackground(@NotNull PoseStack poseStack) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, background_texture);
        blit(poseStack, guiX, guiY, 0, 0,
                image_width, image_height, texture_width, texture_height);
    }
}
