package org.teacon.slides.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.teacon.slides.SlideShow;
import org.teacon.slides.block.PictureBlockEntity;
import org.teacon.slides.url.ProjectorURL;
import org.teacon.slides.url.ProjectorURLSavedData;
import org.teacon.slides.utils.StreamCodecUtil;

import java.util.UUID;

public record PictureUpdatePacket(String url, int color, float width, float height, Vec3f vec3, BlockPos blockPos,
                                  PictureBlockEntity.SizeMode sizeMode) implements CustomPacketPayload {
    public static final Type<PictureUpdatePacket> TYPE = new Type<>(SlideShow.id("picture_update"));

    public static final StreamCodec<ByteBuf, Vec3f> VEC3_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            Vec3f::x,
            ByteBufCodecs.FLOAT,
            Vec3f::y,
            ByteBufCodecs.FLOAT,
            Vec3f::z,
            Vec3f::new
    );
    public static final StreamCodec<FriendlyByteBuf, PictureUpdatePacket> STREAM_CODEC = StreamCodecUtil.composite(
            ByteBufCodecs.STRING_UTF8,
            PictureUpdatePacket::url,
            ByteBufCodecs.VAR_INT,
            PictureUpdatePacket::color,
            ByteBufCodecs.FLOAT,
            PictureUpdatePacket::width,
            ByteBufCodecs.FLOAT,
            PictureUpdatePacket::height,
            VEC3_CODEC,
            PictureUpdatePacket::vec3,
            BlockPos.STREAM_CODEC,
            PictureUpdatePacket::blockPos,
            NeoForgeStreamCodecs.enumCodec(PictureBlockEntity.SizeMode.class),
            PictureUpdatePacket::sizeMode,
            PictureUpdatePacket::new
    );


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                Level level = serverPlayer.level();
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof PictureBlockEntity pictureBlock) {
                    ProjectorURLSavedData savedData = ProjectorURLSavedData.get(serverPlayer.server);
                    ProjectorURL projectorURL = new ProjectorURL(this.url);
                    UUID idByUrl = savedData.getOrCreateIdByItem(projectorURL, player);
                    pictureBlock.setImageLocation(idByUrl);
                    pictureBlock.setColor(color);
                    pictureBlock.setSizeX(width);
                    pictureBlock.setSizeY(height);
                    pictureBlock.setDeltaX(vec3.x);
                    pictureBlock.setDeltaY(vec3.y);
                    pictureBlock.setDeltaZ(vec3.z);
                    pictureBlock.setUrl(url);
                    pictureBlock.setSizeMode((byte) sizeMode.ordinal());
                    pictureBlock.setChanged();
                    level.sendBlockUpdated(blockPos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                }

            }
        });
    }

    public record Vec3f(float x, float y, float z) {
    }
}
