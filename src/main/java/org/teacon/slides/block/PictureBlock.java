package org.teacon.slides.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.teacon.slides.utils.ClientMethod;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static org.teacon.slides.block.ProjectorBlock.ROTATION;

public final class PictureBlock extends BaseEntityBlock {
    public static final BooleanProperty SHAPE = BooleanProperty.create("shape");
    public static final MapCodec<PictureBlock> CODEC = simpleCodec((properties) -> new PictureBlock());

    public PictureBlock() {
        super(Block.Properties.of() // TODO 1.20 material
                .strength(20F)
                .lightLevel(state -> 15) // TODO Configurable
                .noCollission());
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState pState, Level level, BlockPos blockPos, Player player, BlockHitResult pHitResult) {
        if (level.isClientSide() && player.isCreative()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof PictureBlockEntity pictureBlockEntity) {
                ClientMethod.openPictureScreen(pictureBlockEntity);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection())
                .setValue(ROTATION, ProjectorBlock.InternalRotation.HORIZONTAL_FLIPPED)
                .setValue(SHAPE, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, SHAPE, ROTATION);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(SHAPE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PictureBlockEntity(blockPos, blockState);
    }
}
