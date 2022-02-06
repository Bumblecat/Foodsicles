package dev.bumblecat.foodsicles.common.recipes;

import dev.bumblecat.foodsicles.common.ObjectHolders;
import dev.bumblecat.foodsicles.common.objects.items.Foodsicle;
import dev.bumblecat.foodsicles.common.objects.items.IFoodsicle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

public class UpgradeRecipe extends CustomRecipe {

    public UpgradeRecipe(ResourceLocation resource) {
        super(resource);
    }

    /**
     * @param container
     * @param level
     *
     * @return
     */
    @Override
    public boolean matches(CraftingContainer container, Level level) {
        if (container.getItem(4).getItem() instanceof IFoodsicle) {
            IFoodsicle foodsicle = (IFoodsicle) container.getItem(4).getItem();
            if (!foodsicle.getIsUpgradable())
                return false;

            int occupiedSlots = 0;
            for (int i = 0; i < container.getContainerSize(); ++i) {
                if (container.getItem(i).is(foodsicle.getUpgradeType().getUpgradeIngredient().getItem()))
                    occupiedSlots++;
            }
            return occupiedSlots >= 8;


        }
        return false;
    }

    /**
     * @param container
     *
     * @return
     */
    @Override
    public @NotNull ItemStack assemble(CraftingContainer container) {
        ItemStack origin = null, result = null;

        if (container.getItem(4).getItem() instanceof IFoodsicle) {
            origin = container.getItem(4);

            if (container.countItem(((IFoodsicle) origin.getItem()).getUpgradeType().getUpgradeIngredient().getItem()) > 0) {
                ItemStack material = ((IFoodsicle) origin.getItem()).getUpgradeType().getUpgradeIngredient();

                if (material != ItemStack.EMPTY) {

                    result = new ItemStack((material.getItem().equals(Items.EMERALD) ? ObjectHolders.FS_EMERALD : ObjectHolders.FS_DIAMOND));

                    ItemStack copy = origin.copy();
                    copy.removeTagKey("storage");

                    result.setTag(copy.getTag());

                    for (int i = 0; i < ((IFoodsicle) origin.getItem()).getInventory(origin).getSlots(); ++i) {
                        ItemStack stack = ((IFoodsicle) origin.getItem()).getInventory(origin).getStackInSlot(i);
                        if (stack != ItemStack.EMPTY)
                            ((IFoodsicle) result.getItem()).getInventory(result).insertItem(i, stack, false);
                    }
                }
            }
        }

        return result == null || result.isEmpty() ? ItemStack.EMPTY : result;
    }

    /**
     * @param p_43999_
     * @param p_44000_
     *
     * @return
     */
    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return p_43999_ * p_44000_ >= 2;
    }

    /**
     * @return
     */
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ObjectHolders.FS_UPGRADE_RECIPE;
    }
}
