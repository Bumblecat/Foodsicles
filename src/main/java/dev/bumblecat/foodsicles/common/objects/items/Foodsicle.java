package dev.bumblecat.foodsicles.common.objects.items;

import dev.bumblecat.bumblecore.client.objects.items.IDyeableItem;
import dev.bumblecat.bumblecore.common.objects.InteractionResult;
import dev.bumblecat.bumblecore.common.objects.ObjectEventItemArgs;
import dev.bumblecat.bumblecore.common.objects.items.CustomItem;
import dev.bumblecat.bumblecore.common.objects.items.Variables;
import dev.bumblecat.bumblecore.common.storage.IInventory;
import dev.bumblecat.bumblecore.common.storage.InventoryHandler;
import dev.bumblecat.bumblecore.common.windows.ICommonWindow;
import dev.bumblecat.foodsicles.common.windows.FoodsicleCommonWindow;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class Foodsicle extends CustomItem implements IFoodsicle, IDyeableItem {

    private final FoodsicleType foodsicleType;

    /**
     * @param variables
     */
    public Foodsicle(Variables variables) {
        this(variables, null);
    }

    /**
     * @param variables
     * @param foodsicleType
     */
    public Foodsicle(Variables variables, FoodsicleType foodsicleType) {
        super(variables);
        this.foodsicleType = foodsicleType;
    }

    /**
     * @param stack
     * @param arguments
     *
     * @return
     */
    @Override
    public InteractionResult<ItemStack> onInteraction(ItemStack stack, ObjectEventItemArgs arguments) {
        if (arguments.getEventLevel().isClientSide())
            return InteractionResult.proceed(stack);

        if (arguments.getEventHand() == InteractionHand.MAIN_HAND) {
            if (!arguments.getEventPlayer().isSecondaryUseActive()) {
                if (arguments.getEventPlayer().getFoodData().needsFood()) {

                    /**
                     * Check if the inventory is not empty.
                     * If the inventory is empty, play a soundeffect. If not, start consuming!
                     *
                     * @todo work on soundevents. find a sound suitable for something that is empty.
                     */
                    if (!getInventory(arguments.getEventPlayer().getItemInHand(arguments.getEventHand())).isEmpty()) {
                        arguments.getEventPlayer().startUsingItem(arguments.getEventHand());
                    } else {
                        arguments.getEventLevel().playSound(null,
                                arguments.getEventPlayer().getX(), arguments.getEventPlayer().getY(), arguments.getEventPlayer().getZ(),
                                SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 0.1F, .03F);
                    }
                    return InteractionResult.consume(stack);
                }
            } else {

                /**
                 * Open the inventory window.
                 */
                if (arguments.getEventPlayer() instanceof ServerPlayer) {
                    NetworkHooks.openGui((ServerPlayer) arguments.getEventPlayer(), this);
                }
            }
        }
        return InteractionResult.proceed(stack);
    }

    /**
     * @param stack
     * @param arguments
     *
     * @return
     */
    @Override
    public InteractionResult<ItemStack> onInteractionFinished(ItemStack stack, ObjectEventItemArgs arguments) {
        if (arguments.getEventLevel().isClientSide())
            return InteractionResult.proceed(stack);

        /**
         * If the player still requires food, consume!
         */
        if (arguments.getEventPlayer().getFoodData().needsFood()) {
            if (!getInventory(stack).isEmpty()) {

                ItemStack returned = getEdibleBestInSlot(stack, arguments.getEventPlayer(), true)
                        .finishUsingItem(arguments.getEventLevel(), arguments.getEventPlayer());

                /**
                 * If the Item returns another item, like .. an empty bowl?
                 */
                if (!(returned == ItemStack.EMPTY)) {
                    ItemHandlerHelper.giveItemToPlayer(arguments.getEventPlayer(), returned);
                }
            }
        }

        return InteractionResult.proceed(stack);
    }


    /**
     * @param stack
     * @param level
     * @param entity
     * @param p_41407_
     * @param p_41408_
     */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {

        /**
         * If we're not inside the foodsicle inventory window.
         */
        Player player = (Player) entity;
        if (!(player.containerMenu instanceof FoodsicleCommonWindow)) {

            /**
             * If the foodsicle is enchanted and autofeeding is enabled
             */
            if (stack.isEnchanted() && getIsAutofeeding(stack)) {
                if (player.getFoodData().needsFood()) {

                    /**
                     * select best in slot and consume it.
                     */
                    ItemStack edible = getEdibleBestInSlot(stack, player, false);

                    if (edible != ItemStack.EMPTY) {
                        int required = 20 - player.getFoodData().getFoodLevel();
                        int selected = edible.getItem().getFoodProperties().getNutrition();
                        if (required >= selected) {
                            finishUsingItem(stack, level, player);
                        }
                    }

                }
            }
        }

        super.inventoryTick(stack, level, entity, p_41407_, p_41408_);
    }

    /**
     * @param stack
     * @param level
     * @param player
     */
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.getOrCreateTagElement("storage");
        super.onCraftedBy(stack, level, player);
    }

    /**
     * @return
     */
    public FoodsicleType getUpgradeType() {
        return this.foodsicleType == null ? FoodsicleType.Default : this.foodsicleType;
    }

    /**
     * @return
     */
    public boolean getIsUpgradable() {
        return this.foodsicleType != null;
    }

    /**
     * @return
     */
    @Override
    public int getIndexBestInSlot(ItemStack stack, Player player) {
        IInventory inventory = getInventory(stack);
        int selected = 0;

        if (!inventory.isEmpty()) {
            Map<Integer, Integer> objects = new HashMap<Integer, Integer>();

            /**
             * check all slots inside the inventory.
             * if the item has food properties, store the index and the nutrition value in a Map.
             */
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                    FoodProperties properties = inventory.getStackInSlot(i).getItem().getFoodProperties();
                    if (properties != null) {
                        objects.put(i, properties.getNutrition());
                    }
                }
            }

            /**
             * Check how much the player requires to fulfill their hunger level.
             */
            int requires = (20 - player.getFoodData().getFoodLevel());


            /**
             * check each entry in the earlier created Map and compare their values.
             * select the best entry based on how much hunger we need to fulfill.
             *
             * i.e. if we need to fulfill 8, select the item with a value closest to 8.
             */
            int compared = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Integer> entry : objects.entrySet()) {
                if (Math.abs(requires - entry.getValue()) < compared) {
                    compared = entry.getValue();
                    selected = entry.getKey();
                }
            }
        } else {
            selected = -1;
        }

        /**
         * Return the selected food item.
         */
        return selected;
    }

    /**
     * @param stack
     * @param player
     * @param extract
     *
     * @return
     */
    @Override
    public ItemStack getEdibleBestInSlot(ItemStack stack, Player player, boolean extract) {
        IInventory inventory = getInventory(stack);

        /**
         * Check if inventory is not empty and if there is a 'best in slot' edible.
         * Return the edible and extract it from the inventory if 'extract == true'
         */
        if (!inventory.isEmpty() && getIndexBestInSlot(stack, player) >= 0) {
            if (extract) {
                return inventory.extractItem(getIndexBestInSlot(stack, player), 1, false);
            } else {
                return inventory.getStackInSlot(getIndexBestInSlot(stack, player));
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        return 24;
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    /**
     * @return
     */
    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.getUpgradeType().getValue() * 64;
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (stack.getTagElement("storage") == null)
            return false;

        return getInventory(stack).getObjectCountByStackSize() != getMaxDamage(stack);
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13F * ((float) getInventory(stack).getObjectCountByStackSize() / getMaxDamage(stack)));
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(Math.max(0F, (float) (getInventory(stack).getObjectCountByStackSize() - stack.getDamageValue()) / getMaxDamage(stack)) / 3F, 1F, 1F);
    }


    /**
     * @param stack
     *
     * @return
     */
    public boolean getIsAutofeeding(ItemStack stack) {
        if (!stack.isEnchanted())
            return false;
        return stack.getOrCreateTagElement("enabled").getBoolean("value");
    }

    /**
     * @param stack
     * @param value
     */
    public void setIsAutofeeding(ItemStack stack, boolean value) {
        stack.getOrCreateTagElement("enabled").putBoolean("value", value);
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public IInventory getInventory(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        int size = stack.getItem() instanceof Foodsicle ?
                ((Foodsicle) stack.getItem()).getUpgradeType().getValue() : 9;

        IInventory inventory = new InventoryHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                stack.getOrCreateTag().put("storage", serializeNBT());
            }
        };

        if (stack.hasTag())
            inventory.deserializeNBT(stack.getTag().getCompound("storage"));

        return inventory;
    }

    /**
     * @param windowId
     * @param inventory
     * @param player
     *
     * @return
     */
    @Override
    public ICommonWindow getWindow(int windowId, Inventory inventory, Player player) {
        return new FoodsicleCommonWindow(windowId, inventory);
    }

    /**
     * @return
     */
    @Override
    public Component getDisplayName() {
        return getDescription();
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public ItemColor getColor(@Nullable ItemStack stack) {

        return new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int index) {
                return index > 0 ? index > 1 ? getDyedColor(stack) : getTypeColor(stack) : DyeColor.BROWN.getTextColor();
            }

            private int getTypeColor(ItemStack stack) {
                return ((IFoodsicle) stack.getItem()).getUpgradeType().getColor();
            }

            private int getDyedColor(ItemStack stack) {
                return ((IFoodsicle) stack.getItem()).getColorValue(stack);
            }
        };

    }

    /**
     * @param stack
     *
     * @return
     */
    public int getColorValue(ItemStack stack) {
        return stack.getTagElement("color") != null ? stack.getTagElement("color").getInt("value") : 10511680;
    }

    /**
     * @param stack
     * @param color
     */
    public void setColorValue(ItemStack stack, int color) {
        stack.getOrCreateTagElement("color").putInt("value", color);
    }

    /**
     * @return
     */
    @Override
    public boolean isImpractical() {
        return true;
    }
}
