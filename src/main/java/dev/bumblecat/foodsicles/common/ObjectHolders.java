package dev.bumblecat.foodsicles.common;

import dev.bumblecat.bumblecore.common.objects.items.CustomItem;
import dev.bumblecat.bumblecore.common.objects.items.Variables;
import dev.bumblecat.bumblecore.common.register.Registry;
import dev.bumblecat.foodsicles.Foodsicles;
import dev.bumblecat.foodsicles.common.enchant.AutoFeedEnchantment;
import dev.bumblecat.foodsicles.common.objects.items.Foodsicle;
import dev.bumblecat.foodsicles.common.objects.items.FoodsicleType;
import dev.bumblecat.foodsicles.common.recipes.DyeableRecipe;
import dev.bumblecat.foodsicles.common.recipes.UpgradeRecipe;
import dev.bumblecat.foodsicles.common.windows.FoodsicleCommonWindow;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Foodsicles.MOD)
public class ObjectHolders {

    /**
     * Default Foodsicle. (1x9 slots)
     */
    @ObjectHolder("foodsicle")
    public static final Foodsicle FS_DEFAULT = null;

    /**
     * Diamond Foodsicle. (2x9 slots)
     */
    @ObjectHolder("diamond_foodsicle")
    public static final Foodsicle FS_DIAMOND = null;

    /**
     * Emerald Foodsicle. (3x9 slots)
     */
    @ObjectHolder("emerald_foodsicle")
    public static final Foodsicle FS_EMERALD = null;


    @ObjectHolder("foodsicle_grip")
    public static final CustomItem FS_SICLEGRIP = null;

    @ObjectHolder("foodsicle_face")
    public static final CustomItem FS_SICLEFACE = null;


    @ObjectHolder("foodsicle_window")
    public static final MenuType<FoodsicleCommonWindow> FS_WINDOW = null;


    @ObjectHolder("upgrade_recipe")
    public static final SimpleRecipeSerializer<UpgradeRecipe> FS_UPGRADE_RECIPE = null;

    @ObjectHolder("dyeable_recipe")
    public static final SimpleRecipeSerializer<UpgradeRecipe> FS_DYEABLE_RECIPE = null;

    public static void register() {

        /**
         * Foodsicles
         */
        Registry.register("foodsicle", () -> new Foodsicle(new Variables()
                .setStackSize(1).setCreativeTab(Foodsicles.creativeTab),
                FoodsicleType.Default
        ));
        Registry.register("diamond_foodsicle", () -> new Foodsicle(new Variables()
                .setStackSize(1).setCreativeTab(Foodsicles.creativeTab),
                FoodsicleType.Diamond
        ));
        Registry.register("emerald_foodsicle", () -> new Foodsicle(new Variables()
                .setStackSize(1).setCreativeTab(Foodsicles.creativeTab),
                FoodsicleType.Emerald
        ));


        /**
         * Components
         */
        Registry.register("foodsicle_grip", () -> new CustomItem(new Variables()
                .setStackSize(16).setCreativeTab(Foodsicles.creativeTab)
        ));
        Registry.register("foodsicle_face", () -> new CustomItem(new Variables()
                .setStackSize(16).setCreativeTab(Foodsicles.creativeTab)
        ));

        /**
         * Window
         */
        Registry.register("foodsicle_window", ()
                -> IForgeMenuType.create((windowId, inventory, data)
                -> new FoodsicleCommonWindow(windowId, inventory))
        );

        /**
         * Enchantment
         */
        Registry.register("foodsicle_autofeed", ()
                -> new AutoFeedEnchantment(Enchantment.Rarity.UNCOMMON));

        /**
         * Recipes
         */
        Registry.register("upgrade_recipe", () -> new SimpleRecipeSerializer<>(UpgradeRecipe::new));
        Registry.register("dyeable_recipe", () -> new SimpleRecipeSerializer<>(DyeableRecipe::new));
    }
}
