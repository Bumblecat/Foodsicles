package dev.bumblecat.foodsicles.common.enchant;

import dev.bumblecat.foodsicles.common.objects.items.IFoodsicle;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class AutoFeedEnchantment extends Enchantment {

    /**
     * Category
     */
    public static EnchantmentCategory Category =
            EnchantmentCategory.create("fs_enchantment_category", item -> item instanceof IFoodsicle);

    /**
     * @param rarity
     */
    public AutoFeedEnchantment(Rarity rarity) {
        super(rarity, Category, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * @return
     */
    @Override
    public boolean isTradeable() {
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isDiscoverable() {
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }

    /**
     * @param itemStack
     *
     * @return
     */
    @Override
    public boolean canEnchant(ItemStack itemStack) {
        return itemStack.getItem() instanceof IFoodsicle;
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof IFoodsicle || isAllowedOnBooks();
    }
}
