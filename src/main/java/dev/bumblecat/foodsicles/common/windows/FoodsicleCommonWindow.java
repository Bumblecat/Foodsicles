package dev.bumblecat.foodsicles.common.windows;

import dev.bumblecat.bumblecore.common.storage.IInventory;
import dev.bumblecat.bumblecore.common.windows.CommonWindow;
import dev.bumblecat.foodsicles.Foodsicles;
import dev.bumblecat.foodsicles.common.network.AutoFeedEnabledPacket;
import dev.bumblecat.foodsicles.common.objects.items.IFoodsicle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.SlotItemHandler;

import java.awt.*;

import org.jetbrains.annotations.NotNull;

public class FoodsicleCommonWindow extends CommonWindow {

    private final ItemStack itemStack;
    private final int activeSlot;

    private IInventory inventory;

    /**
     * @param windowId
     * @param inventory
     */
    public FoodsicleCommonWindow(int windowId, Inventory inventory) {
        super(Foodsicles.FS_WINDOW, windowId, inventory, 9);

        this.itemStack = inventory.player.getMainHandItem();
        this.activeSlot = inventory.selected;

        if (this.itemStack.getItem() instanceof IFoodsicle) {
            this.inventory = ((IFoodsicle) this.itemStack.getItem()).getInventory(this.itemStack);

            for (int i = 0; i < 9; ++i) {
                this.addSlot(new SlotItemHandler(this.inventory, i, 8 + (i % 9) * 18, 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.getItem().getFoodProperties() != null;
                    }
                });
            }
        }

        this.addPlayerInventory(new Point(0, 44));
    }

    @Override
    public void clicked(int slotId, int dragId, ClickType clickType, Player player) {
        if (clickType != ClickType.QUICK_CRAFT && slotId >= 0) {
            int clicked = slotId - this.inventory.getSlots() - 27;
            if (clicked == activeSlot || clickType == ClickType.SWAP) {
                return;
            }
        }
        super.clicked(slotId, dragId, clickType, player);
    }

    /**
     * @param player
     */
    @Override
    public void removed(Player player) {
        if (this.itemStack.getTag() == null)
            this.itemStack.setTag(new CompoundTag());
        this.itemStack.getTag().put("storage", this.inventory.serializeNBT());

        super.removed(player);
    }

    /**
     * @param player
     *
     * @return
     */
    @Override
    public boolean stillValid(Player player) {
        return true;//super.stillValid(player);
    }
}
