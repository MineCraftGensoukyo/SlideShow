package org.teacon.slides.registry;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.teacon.slides.SlideShow;
import org.teacon.slides.block.ProjectorBlock;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SlideShow.ID);
    public static final DeferredBlock<Block> PROJECTOR_BLOCK = BLOCKS.register("projector", ProjectorBlock::new);
}
