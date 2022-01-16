package dev.bumblecat.foodsicles.common.objects.items;

import dev.bumblecat.bumblecore.client.objects.items.IDyeableItem;
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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Foodsicle extends CustomItem implements IFoodsicle, IDyeableItem {

    /**
     * @param variables
     */
    public Foodsicle(Variables variables) {
        super(variables);
    }


    /**
     * @param level
     * @param player
     * @param hand
     *
     * @return
     */
    @Override
    public @NotNull
    InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide())
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        if (hand == InteractionHand.MAIN_HAND) {
            if (!player.isSecondaryUseActive()) {
                if (player.getFoodData().needsFood()) {

                    /**
                     * Check if the inventory is not empty.
                     * If the inventory is empty, play a soundeffect. If not, start consuming!
                     */
                    if (!getInventory(player.getItemInHand(hand)).isEmpty()) {
                        player.startUsingItem(hand);
                    } else {
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                //SoundEvents.MAGMA_CUBE_HURT_SMALL,
                                SoundEvents.SHOVEL_FLATTEN,
                                SoundSource.PLAYERS, 0.1F, .01F);
                    }

                    return InteractionResultHolder.consume(player.getItemInHand(hand));
                }
            } else {
                /**
                 * Open the inventory window.
                 */
                if (player instanceof ServerPlayer) {
                    NetworkHooks.openGui((ServerPlayer) player, this);
                }
            }
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    /**
     * @param stack
     * @param level
     * @param entity
     *
     * @return
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {

        if (entity instanceof Player) {
            Player player = (Player) entity;

            /**
             * If the player still requires food, consume!
             */
            if (player.getFoodData().needsFood()) {
                if (!getInventory(stack).isEmpty()) {

                    ItemStack returned = getEdibleBestInSlot(stack, player, true)
                            .finishUsingItem(level, player);

                    /**
                     * If the Item returned another item, like .. an empty bowl?
                     */
                    if (!(returned == ItemStack.EMPTY)) {
                        ItemHandlerHelper.giveItemToPlayer(player, returned);
                    }
                }
            }
        }

        return super.finishUsingItem(stack, level, entity);
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
        // show the durability bar upon creation.
        stack.getOrCreateTagElement("storage");
        super.onCraftedBy(stack, level, player);
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
        return 9 * 64;
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
        return Math.round(13F * ((float) getInventory(stack).getObjectCount() / getMaxDamage(stack)));
    }

    /**
     * @param stack
     *
     * @return
     */
    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(Math.max(0F, (float) (getInventory(stack).getObjectCount() - stack.getDamageValue()) / getMaxDamage(stack)) / 3F, 1F, 1F);
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

        IInventory inventory = new InventoryHandler(9) {
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
     *
     * @todo create the dyeable recipes.
     */
    @Override
    public ItemColor getColor(@Nullable ItemStack stack) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public boolean isImpractical() {
        return true;
    }
}
