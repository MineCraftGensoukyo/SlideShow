package org.teacon.slides.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.teacon.slides.block.PictureBlockEntity;
import org.teacon.slides.block.ProjectorBlock;
import org.teacon.slides.slide.Slide;

import java.util.UUID;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class PictureRender implements BlockEntityRenderer<PictureBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public PictureRender(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(PictureBlockEntity tileEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        BlockState state = tileEntity.getBlockState();
        Direction value = state.getValue(FACING);
        UUID imageLocation = tileEntity.getImageLocation();
        Slide slide = SlideState.getSlide(imageLocation);
        if (slide != null) {
            poseStack.pushPose();
            PoseStack.Pose last = poseStack.last();
            tileEntity.transformToSlideSpaceMicros(last.pose(), last.normal());
            boolean flipped = state.getValue(ProjectorBlock.ROTATION).isFlipped();

            slide.render(multiBufferSource, last, tileEntity.getWidthMicros(), tileEntity.getHeightMicros(),
                    tileEntity.getScaleWidthMicros() * 1e6, tileEntity.getScaleHeightMicros() * 1e6, tileEntity.getColor(),
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                    true, !flipped || tileEntity.getDoubleSided(),
                    SlideState.getAnimationTick(), partialTick);
//            slide.render(multiBufferSource, last, tileEntity.getWidthMicros(), tileEntity.getHeightMicros(),
//                    tileEntity.getScaleWidthMicros(),tileEntity.getScaleHeightMicros(), tileEntity.getColor(),
//                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
//                    flipped || tileEntity.getDoubleSided(), !flipped || tileEntity.getDoubleSided(),
//                    SlideState.getAnimationTick(), partialTick);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(PictureBlockEntity tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
