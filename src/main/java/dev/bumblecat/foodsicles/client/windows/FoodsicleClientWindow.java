package dev.bumblecat.foodsicles.client.windows;

import dev.bumblecat.bumblecore.client.windows.ClientWindow;
import dev.bumblecat.bumblecore.client.windows.WindowType;
import dev.bumblecat.bumblecore.client.windows.widgets.Aperture;
import dev.bumblecat.foodsicles.Foodsicles;
import dev.bumblecat.foodsicles.client.windows.widgets.Lever;
import dev.bumblecat.foodsicles.common.network.AutoFeedEnabledPacket;
import dev.bumblecat.foodsicles.common.objects.items.IFoodsicle;
import dev.bumblecat.foodsicles.common.windows.FoodsicleCommonWindow;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

import org.jetbrains.annotations.NotNull;

public class FoodsicleClientWindow extends ClientWindow<FoodsicleCommonWindow> {

    private final ItemStack itemStack;

    /**
     * @param container
     * @param inventory
     * @param component
     */
    public FoodsicleClientWindow(FoodsicleCommonWindow container, Inventory inventory, Component component) {
        super(container, inventory, component);
        this.itemStack = inventory.player.getMainHandItem();
        this.construct();
    }

    /**
     *
     */
    private void construct() {
        if (this.itemStack != null && this.itemStack.getItem() instanceof IFoodsicle) {
            int slots = ((IFoodsicle) this.itemStack.getItem()).getUpgradeType().getValue();

            this.setSize(this.itemStack.isEnchanted() ? 208 : 176, 144 + (((slots / 9) * 18) - 18));
        }
    }

    /**
     *
     */
    @Override
    public void onWindowCreated() {
        super.onWindowCreated();
    }

    /**
     *
     */
    @Override
    public void onWindowLoading() {
        super.onWindowLoading();

        if (this.itemStack != null && this.itemStack.getItem() instanceof IFoodsicle) {
            int slots = ((IFoodsicle) this.itemStack.getItem()).getUpgradeType().getValue();

            for (int i = 0; i < (slots / 9); ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.attach(new Aperture(this, new Rectangle(7 + (j % 9) * 18, 17 + (i % 9) * 18, 18, 18))
                            .setTexture3x3().setVisible()
                    );
                }
            }

            if (this.itemStack.isEnchanted()) {
                Lever lever;
                this.attach((lever = new Lever(this, new Rectangle(184, 64 + (((slots / 9) * 18) - 18), 16, 48)))
                        .onMouseRelease(() -> onLeverClicked(lever, itemStack)).setVisible()
                );
                lever.setValue(((IFoodsicle) this.itemStack.getItem()).getIsAutofeeding(this.itemStack));
            }
        }


//        for (int i = 0; i < 9; ++i) {
//            this.attach(new Aperture(this, new Rectangle((i * 18) + 7, 17, 18, 18))
//                    .setTexture3x3()
//                    .setVisible()
//            );
//        }
//
//        if (this.itemStack != null && this.itemStack.isEnchanted()) {
//            Lever lever;
//            this.attach((lever = new Lever(this, new Rectangle(184, 64, 16, 48)))
//                    .onMouseRelease(() -> onLeverClicked(lever, itemStack)).setVisible());
//
//            lever.setValue(((IFoodsicle) this.itemStack.getItem()).getIsAutofeeding(this.itemStack));
//        }
    }

    /**
     * @param lever
     * @param stack
     */
    public void onLeverClicked(Lever lever, ItemStack stack) {
        Foodsicles.getNetwork().sendToServer(new AutoFeedEnabledPacket(lever.getValue()));
        ((IFoodsicle) this.itemStack.getItem()).setIsAutofeeding(this.itemStack, lever.getValue());
    }


    /**
     * @return
     */
    @Override
    public @NotNull
    WindowType getWindowType() {
        return WindowType.Generic;
    }
}
