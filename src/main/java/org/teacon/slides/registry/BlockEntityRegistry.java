package org.teacon.slides.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.teacon.slides.SlideShow;
import org.teacon.slides.block.PictureBlockEntity;
import org.teacon.slides.block.ProjectorBlockEntity;

import java.util.function.Supplier;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SlideShow.ID);
    public static final Supplier<BlockEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("projector", () -> BlockEntityType.Builder.of(ProjectorBlockEntity::new, BlockRegistry.PROJECTOR_BLOCK.value()).build(null));
    public static final Supplier<BlockEntityType<PictureBlockEntity>> PICTURE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("picture", () -> BlockEntityType.Builder.of(PictureBlockEntity::new, BlockRegistry.PICTURE_BLOCK.value()).build(null));
}
