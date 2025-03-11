package org.teacon.slides.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import org.teacon.slides.registry.BlockRegistry;

public final class PictureItem extends BlockItem {
    public PictureItem() {
        super(BlockRegistry.PICTURE_BLOCK.get(), new Properties().rarity(Rarity.RARE));
    }
}
