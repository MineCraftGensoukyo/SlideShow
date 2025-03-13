package org.teacon.slides.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector2i;
import org.teacon.slides.block.PictureBlockEntity;
import org.teacon.slides.block.ProjectorBlock;
import org.teacon.slides.slide.Slide;

import java.util.Optional;
import java.util.UUID;

public class PictureRender implements BlockEntityRenderer<PictureBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public PictureRender(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(PictureBlockEntity tileEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        BlockState state = tileEntity.getBlockState();
        UUID imageLocation = tileEntity.getImageLocation();
        Slide slide = SlideState.getSlide(imageLocation);
        if (slide != null) {
            poseStack.pushPose();
            PoseStack.Pose last = poseStack.last();
            tileEntity.transformToSlideSpaceMicros(last.pose(), last.normal());
            boolean flipped = state.getValue(ProjectorBlock.ROTATION).isFlipped();
            Optional<Vector2i> dimension = slide.getDimension();
            dimension.ifPresent(vector2i -> {
                int x = vector2i.x;
                int y = vector2i.y;
                byte sizeMode = tileEntity.getSizeMode();
                int widthMicros = tileEntity.getWidthMicros();
                int heightMicros = tileEntity.getHeightMicros();
                double scaleWidthMicros = tileEntity.getScaleWidthMicros() * 1e6;
                double scaleHeightMicros = tileEntity.getScaleHeightMicros() * 1e6;
                if (sizeMode == 1) {
                    var scale = Math.min(scaleWidthMicros / x, scaleHeightMicros / y);
                    scaleWidthMicros = scale * x;
                    scaleHeightMicros = scale * y;
                }
                slide.render(multiBufferSource, last, widthMicros, heightMicros,
                        scaleWidthMicros, scaleHeightMicros, tileEntity.getColor(),
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                        true, !flipped || tileEntity.getDoubleSided(),
                        SlideState.getAnimationTick(), partialTick);
            });

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
