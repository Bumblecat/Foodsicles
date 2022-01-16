package dev.bumblecat.foodsicles.common.objects.items;

import dev.bumblecat.bumblecore.common.objects.items.IEnchantable;
import dev.bumblecat.bumblecore.common.objects.items.IImpractical;
import dev.bumblecat.bumblecore.common.storage.IInventoryProvider;
import dev.bumblecat.bumblecore.common.windows.ICommonWindowProvider;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IFoodsicle extends ICommonWindowProvider, IInventoryProvider,
        IEnchantable, IImpractical {

    /**
     * @param itemStack
     * @param player
     *
     * @return
     */
    int getIndexBestInSlot(ItemStack itemStack, Player player);

    /**
     * @param itemStack
     *
     * @return
     */
    default ItemStack getEdibleBestInSlot(ItemStack itemStack, Player player) {
        return getEdibleBestInSlot(itemStack, player, true);
    }

    /**
     * @param itemStack
     * @param player
     * @param extract
     *
     * @return
     */
    ItemStack getEdibleBestInSlot(ItemStack itemStack, Player player, boolean extract);


    /**
     * @param stack
     * @param value
     */
    void setIsAutofeeding(ItemStack stack, boolean value);

    /**
     * @param stack
     *
     * @return
     */
    boolean getIsAutofeeding(ItemStack stack);
}
