package org.teacon.slides.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.teacon.slides.SlideShow;
import org.teacon.slides.item.PictureItem;
import org.teacon.slides.item.ProjectorItem;
import org.teacon.slides.item.SlideItem;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SlideShow.ID);
    public static final DeferredItem<Item> SLIDE_ITEM = ITEMS.register("slide_item", SlideItem::new);
    public static final DeferredItem<Item> PROJECTOR_ITEM = ITEMS.register("projector", ProjectorItem::new);
    public static final DeferredItem<Item> PICTURE_ITEM = ITEMS.register("picture", PictureItem::new);
}
