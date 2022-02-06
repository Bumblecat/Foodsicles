package dev.bumblecat.foodsicles;

import dev.bumblecat.bumblecore.common.Functions;
import dev.bumblecat.bumblecore.common.modular.Module;
import dev.bumblecat.bumblecore.common.objects.CreativeTab;
import dev.bumblecat.foodsicles.client.windows.FoodsicleClientWindow;
import dev.bumblecat.foodsicles.common.ObjectHolders;
import dev.bumblecat.foodsicles.common.enchant.AutoFeedEnchantment;
import dev.bumblecat.foodsicles.common.network.AutoFeedEnabledPacket;
import dev.bumblecat.foodsicles.common.trading.VillagerTrading;
import dev.bumblecat.foodsicles.config.Configuration;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Foodsicles.MOD)
public class Foodsicles extends Module {
    public static final String MOD = "foodsicles";

    /**
     *
     */
    public static CreativeTab creativeTab;

    /**
     *
     */
    public Foodsicles() {
        Configuration.initialize();

        creativeTab = new CreativeTab("foodsicles");
        ObjectHolders.register();

        Functions.Shortcuts.getForgeEventBus().register(new VillagerTrading());
    }

    /**
     * @param event
     */
    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        super.onCommonSetupEvent(event);

        creativeTab.setIconItem(new ItemStack(ObjectHolders.FS_DEFAULT));
        creativeTab.setEnchantmentCategories(AutoFeedEnchantment.Category);

        getNetwork().registerMessage(AutoFeedEnabledPacket.class, AutoFeedEnabledPacket::encode, AutoFeedEnabledPacket::decode, AutoFeedEnabledPacket::handle);
    }

    /**
     * @param event
     */
    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        super.onClientSetupEvent(event);

        MenuScreens.register(ObjectHolders.FS_WINDOW, FoodsicleClientWindow::new);
    }
}
