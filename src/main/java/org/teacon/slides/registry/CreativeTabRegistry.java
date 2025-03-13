package org.teacon.slides.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.teacon.slides.SlideShow;

import static org.teacon.slides.registry.ItemRegistry.*;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SlideShow.ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PROJECTOR_TABS = CREATIVE_MODE_TABS.register("projector", () -> CreativeModeTab.builder()
            .title(Component.translatable("item.slide_show.slide_item"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ItemRegistry.PROJECTOR_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(SLIDE_ITEM.get());
                output.accept(PROJECTOR_ITEM.get());
                output.accept(PICTURE_ITEM.get());
            })
            .build());
}
