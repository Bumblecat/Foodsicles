package dev.bumblecat.foodsicles.common.objects.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.MaterialColor;

public enum FoodsicleType {
    Default(9, MaterialColor.WOOD.col, new ItemStack(Items.DIAMOND)),
    Diamond(18, MaterialColor.DIAMOND.col, new ItemStack(Items.EMERALD)),
    Emerald(27, MaterialColor.EMERALD.col, ItemStack.EMPTY);

    private final int value;
    private final int color;

    private final ItemStack ingredient;

    /**
     * @param slots
     * @param color
     */
    FoodsicleType(int slots, int color, ItemStack ingredient) {
        this.value = slots;
        this.color = color;
        this.ingredient = ingredient;
    }

    /**
     * @return
     */
    public int getValue() {
        return this.value;
    }

    /**
     * @return
     */
    public int getColor() {
        return this.color;
    }

    /**
     * @return
     */
    public ItemStack getUpgradeIngredient() {
        return this.ingredient;
    }
}
