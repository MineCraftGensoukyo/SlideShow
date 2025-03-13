package org.teacon.slides.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.teacon.slides.block.PictureBlockEntity;
import org.teacon.slides.network.PictureUpdatePacket;

import java.awt.*;

import static java.lang.Float.parseFloat;
import static org.teacon.slides.screen.ProjectorScreen.*;
import static org.teacon.slides.screen.SlideItemScreen.URL_MAX_LENGTH;
import static org.teacon.slides.screen.SlideItemScreen.URL_TEXT;

public class PictureScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger(PictureScreen.class);
    private final int BACKGROUND_COLOR = FastColor.ARGB32.color(128, Color.BLACK.getRGB());
    private final EditBox urlInput;

    private final EditBox ColorInput;
    private final EditBox WidthInput;
    private final EditBox HeightInput;
    private final EditBox OffsetXInput;
    private final EditBox OffsetYInput;
    private final EditBox OffsetZInput;

    private final CycleButton<PictureBlockEntity.SizeMode> SizeMode;

    private final BlockPos blockPos;

    public PictureScreen(PictureBlockEntity pictureBlockEntity) {
        super(Component.translatable("gui.slide_show.picture"));
        this.blockPos = pictureBlockEntity.getBlockPos();
        this.font = Minecraft.getInstance().font;
        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
        int j = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;
        this.urlInput = new EditBox(font, i - 75, j - 55, 175, 15, URL_TEXT);
        this.urlInput.setMaxLength(URL_MAX_LENGTH);
        this.urlInput.setValue(pictureBlockEntity.getUrl());

        this.ColorInput = new EditBox(font, i - 75, j - 25, 70, 15, COLOR_TEXT);
        this.ColorInput.setValue(String.format("%08X", pictureBlockEntity.getColor()));
        this.ColorInput.setMaxLength(COLOR_MAX_LENGTH);

        this.WidthInput = new EditBox(font, i - 75, j + 5, 65, 15, WIDTH_TEXT);
        this.WidthInput.setValue(String.valueOf(pictureBlockEntity.getScaleWidthMicros()));
        this.HeightInput = new EditBox(font, i, j + 5, 65, 15, HEIGHT_TEXT);
        this.HeightInput.setValue(String.valueOf(pictureBlockEntity.getScaleHeightMicros()));

        this.OffsetXInput = new EditBox(font, i - 75, j + 35, 40, 15, OFFSET_X_TEXT);
        this.OffsetXInput.setValue(String.valueOf(pictureBlockEntity.getDeltaX()));
        this.OffsetYInput = new EditBox(font, i - 25, j + 35, 40, 15, OFFSET_Y_TEXT);
        this.OffsetYInput.setValue(String.valueOf(pictureBlockEntity.getDeltaY()));
        this.OffsetZInput = new EditBox(font, i + 25, j + 35, 40, 15, OFFSET_Z_TEXT);
        this.OffsetZInput.setValue(String.valueOf(pictureBlockEntity.getDeltaZ()));

        this.SizeMode = CycleButton.builder(PictureBlockEntity.SizeMode::getSymbol)
                .displayOnlyValue()
                .withValues(PictureBlockEntity.SizeMode.values())
                .withInitialValue(PictureBlockEntity.SizeMode.values()[pictureBlockEntity.getSizeMode()])
                .create(i + 80, j + 5, 40,20, Component.literal("size"));
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(this.urlInput);
        this.addRenderableWidget(this.ColorInput);
        this.addRenderableWidget(this.WidthInput);
        this.addRenderableWidget(this.HeightInput);
        this.addRenderableWidget(this.OffsetXInput);
        this.addRenderableWidget(this.OffsetYInput);
        this.addRenderableWidget(this.OffsetZInput);
        this.addRenderableWidget(this.SizeMode);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
        int j = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;
        guiGraphics.fill(i - 100, j - 75, i + 125, j + 75, -10, BACKGROUND_COLOR);
        try {
            int color = Integer.parseUnsignedInt(this.ColorInput.getValue(), 16);
            guiGraphics.fill(i + 10, j - 25, i + 35, j - 10, color);
        } catch (NumberFormatException ignored) {

        }
        guiGraphics.drawString(this.font, Component.translatable("gui.slide_show.url"), i - 75, j - 65, Color.WHITE.getRGB());
        guiGraphics.drawString(this.font, Component.translatable("gui.slide_show.color"), i - 75, j - 35, Color.WHITE.getRGB());
        guiGraphics.drawString(this.font, Component.translatable("gui.slide_show.section.size"), i - 75, j - 5, Color.WHITE.getRGB());
        guiGraphics.drawString(this.font, Component.translatable("gui.slide_show.section.offset"), i - 75, j + 25, Color.WHITE.getRGB());
    }

    @Override
    public void removed() {
        try {
            String urlInput = this.urlInput.getValue();
            int color = Integer.parseUnsignedInt(this.ColorInput.getValue(), 16);
            float width = parseFloat(this.WidthInput.getValue());
            float height = parseFloat(this.HeightInput.getValue());
            float offsetX = parseFloat(this.OffsetXInput.getValue());
            float offsetY = parseFloat(this.OffsetYInput.getValue());
            float offsetZ = parseFloat(this.OffsetZInput.getValue());
            PictureBlockEntity.SizeMode sizeModeValue = this.SizeMode.getValue();
            PictureUpdatePacket packet = new PictureUpdatePacket(urlInput, color, width, height, new PictureUpdatePacket.Vec3f(offsetX, offsetY, offsetZ), blockPos, sizeModeValue);
            PacketDistributor.sendToServer(packet);
        } catch (IllegalArgumentException argumentException) {
            LOGGER.error(argumentException);
        }
    }
}
