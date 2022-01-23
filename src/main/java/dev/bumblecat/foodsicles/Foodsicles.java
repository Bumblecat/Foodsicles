package dev.bumblecat.foodsicles;

import dev.bumblecat.bumblecore.common.Functions;
import dev.bumblecat.bumblecore.common.modular.Module;
import dev.bumblecat.bumblecore.common.objects.CreativeTab;
import dev.bumblecat.bumblecore.common.objects.items.CustomItem;
import dev.bumblecat.bumblecore.common.objects.items.Variables;
import dev.bumblecat.bumblecore.common.register.Registry;
import dev.bumblecat.foodsicles.client.windows.FoodsicleClientWindow;
import dev.bumblecat.foodsicles.common.enchant.AutoFeedEnchantment;
import dev.bumblecat.foodsicles.common.network.AutoFeedEnabledPacket;
import dev.bumblecat.foodsicles.common.objects.items.Foodsicle;
import dev.bumblecat.foodsicles.common.trading.VillagerTrading;
import dev.bumblecat.foodsicles.common.windows.FoodsicleCommonWindow;
import dev.bumblecat.foodsicles.config.Configuration;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod(Foodsicles.MOD)
public class Foodsicles extends Module {
    public static final String MOD = "foodsicles";


    @ObjectHolder("foodsicle")
    public static final Foodsicle
            FS_OBJECT = null;

    @ObjectHolder("foodsicle")
    public static final MenuType<FoodsicleCommonWindow>
            FS_WINDOW = null;


    @ObjectHolder("foodsicle_grip")
    public static final CustomItem FS_SICLEGRIP = null;

    @ObjectHolder("foodsicle_face")
    public static final CustomItem FS_SICLEFACE = null;


    public static CreativeTab creativeTab;

    public Foodsicles() {
        Configuration.initialize();

        creativeTab = new CreativeTab("foodsicles");

        Registry.register("foodsicle", () -> new Foodsicle(new Variables().setStackSize(1).setCreativeTab(creativeTab)));
        Registry.register("foodsicle_grip", () -> new CustomItem(new Variables().setStackSize(16).setCreativeTab(creativeTab)));
        Registry.register("foodsicle_face", () -> new CustomItem(new Variables().setStackSize(16).setCreativeTab(creativeTab)));

        Registry.register("foodsicle", ()
                -> IForgeMenuType.create((windowId, inventory, data)
                -> new FoodsicleCommonWindow(windowId, inventory))
        );

        Registry.register("foodsicle", ()
                -> new AutoFeedEnchantment(Enchantment.Rarity.UNCOMMON));

        Functions.Shortcuts.getForgeEventBus().register(new VillagerTrading());
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        super.onCommonSetupEvent(event);

        creativeTab.setIconItem(new ItemStack(FS_OBJECT));
        creativeTab.setEnchantmentCategories(AutoFeedEnchantment.Category);

        getNetwork().registerMessage(AutoFeedEnabledPacket.class, AutoFeedEnabledPacket::encode, AutoFeedEnabledPacket::decode, AutoFeedEnabledPacket::handle);
    }

    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        super.onClientSetupEvent(event);

        MenuScreens.register(FS_WINDOW, FoodsicleClientWindow::new);
    }
}
