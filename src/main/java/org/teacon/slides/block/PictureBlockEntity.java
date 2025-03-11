package org.teacon.slides.block;

import com.mojang.datafixers.util.Either;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.teacon.slides.SlideShow;
import org.teacon.slides.registry.BlockEntityRegistry;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
@Setter
public class PictureBlockEntity extends BlockEntity {

    float deltaX;
    float deltaY;
    float deltaZ;

    float sizeX = 1;
    float sizeY = 1;

    boolean doubleSided;
    int color = 0xFFFFFFFF;

    String url = Strings.EMPTY;
    Either<UUID, String> mImageLocation = Either.left(UUID.randomUUID());

    public PictureBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.PICTURE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public UUID getImageLocation() {
        return mImageLocation.left().orElseGet(UUID::randomUUID);
    }

    public void setImageLocation(UUID imageLocation) {
        mImageLocation = Either.left(imageLocation);
        SlideShow.requestUrlPrefetch(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.deltaX = tag.getFloat("deltaX");
        this.deltaY = tag.getFloat("deltaY");
        this.deltaZ = tag.getFloat("deltaZ");
        this.sizeX = tag.getFloat("sizeX");
        this.sizeY = tag.getFloat("sizeY");
        this.doubleSided = tag.getBoolean("doubleSided");
        this.color = tag.getInt("color");
        this.url = tag.getString("url");


        if (tag.hasUUID("ImageLocation")) {
            mImageLocation = Either.left(tag.getUUID("ImageLocation"));
            SlideShow.requestUrlPrefetch(this);
        } else {
            mImageLocation = Either.right(tag.getString("ImageLocation"));
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("deltaX", deltaX);
        tag.putFloat("deltaY", deltaY);
        tag.putFloat("deltaZ", deltaZ);
        tag.putFloat("sizeX", sizeX);
        tag.putFloat("sizeY", sizeY);
        tag.putBoolean("doubleSided", doubleSided);
        tag.putInt("color", color);
        tag.putString("url", url);

        tag.put("ImageLocation", mImageLocation.map(NbtUtils::createUUID, StringTag::valueOf));
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public int getWidthMicros() {
        return (int) (this.sizeX * 1e6);
    }

    public int getHeightMicros() {
        return (int) (this.sizeY * 1e6);
    }

    public double getScaleWidthMicros() {
        return this.sizeX;
    }

    public double getScaleHeightMicros() {
        return this.sizeY;
    }

    public boolean getDoubleSided() {
        return this.doubleSided;
    }

    public void transformToSlideSpaceMicros(Matrix4f pose, Matrix3f normal) {
        var state = getBlockState();
        // get direction
        var direction = state.getValue(BlockStateProperties.FACING);
        // get internal rotation
        var rotation = state.getValue(ProjectorBlock.ROTATION);
        // matrix 1: translation to block center
        pose.translate(1F / 2F, 1F / 2F, 1F / 2F);
        // matrix 2: rotation
        pose.rotate(direction.getRotation());
        normal.rotate(direction.getRotation());
        // matrix 3: translation to block surface
        pose.translate(0F, 1F / 2F, 0F);
        // matrix 4: float to micros
        pose.scale(1E-6F, 1E-6F, 1E-6F);
        // matrix 5: internal rotation
        rotation.transform(pose);
        rotation.transform(normal);
        // matrix 6: translation for slide
        pose.translate(-5E5F, 0F, 5E5F - 1E6F);
        // matrix 7: offset for slide
        pose.translate(this.deltaX * 1e6F,this.deltaY * 1e6F, this.deltaZ * 1e6F);
    }
}
