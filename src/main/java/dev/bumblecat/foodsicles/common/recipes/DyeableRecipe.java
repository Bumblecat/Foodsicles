package dev.bumblecat.foodsicles.common.recipes;

import dev.bumblecat.foodsicles.common.ObjectHolders;
import dev.bumblecat.foodsicles.common.objects.items.IFoodsicle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DyeableRecipe extends CustomRecipe {

    public DyeableRecipe(ResourceLocation resource) {
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
            return isValidInSlots(container, 1, 7) || isValidInSlots(container, 3, 5);
        }
        return false;
    }

    /**
     * @param container
     *
     * @return
     */
    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack origin = null, result = null;

        if (container.getItem(4).getItem() instanceof IFoodsicle) {
            origin = container.getItem(4).copy();

            if (isValidInSlots(container, 1, 7) || isValidInSlots(container, 3, 5)) {
                result = origin.copy();
                result.getOrCreateTagElement("color").putInt("value",
                        ((DyeItem) container.getItem(isValidInSlots(container, 1, 7) ? 1 : 3).getItem()).getDyeColor().getTextColor()
                );
            }
        }
        return result == null || result.isEmpty() ? ItemStack.EMPTY : result;
    }

    /**
     * @param container
     * @param slot1
     * @param slot2
     *
     * @return
     */
    private boolean isValidInSlots(CraftingContainer container, int slot1, int slot2) {
        if (container.getItem(slot1).getItem() instanceof DyeItem && container.getItem(slot2).getItem() instanceof DyeItem) {
            return ItemStack.matches(container.getItem(slot1), container.getItem(slot2));
        }
        return false;
    }


    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return p_43999_ * p_44000_ >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ObjectHolders.FS_DYEABLE_RECIPE;
    }
}
