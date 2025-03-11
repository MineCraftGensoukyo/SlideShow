package org.teacon.slides.utils;

import net.minecraft.client.Minecraft;
import org.teacon.slides.block.PictureBlockEntity;
import org.teacon.slides.screen.PictureScreen;

public class ClientMethod {
    public static void openPictureScreen(PictureBlockEntity pictureBlockEntity) {
        Minecraft.getInstance().setScreen(new PictureScreen(pictureBlockEntity));
    }
}
