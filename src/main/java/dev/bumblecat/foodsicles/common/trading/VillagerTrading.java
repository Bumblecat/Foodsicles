package dev.bumblecat.foodsicles.common.trading;

import dev.bumblecat.foodsicles.Foodsicles;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VillagerTrading {
    /**
     * @param event
     */
    @SubscribeEvent
    public void onVillagerTradesEvent(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FARMER) {
            event.getTrades().get(2).add(new BasicItemListing(6,
                    new ItemStack(Foodsicles.FS_OBJECT, 1), 1, 2));
            event.getTrades().get(4).add(new BasicItemListing(3,
                    new ItemStack(Foodsicles.FS_OBJECT, 1), 1, 2));
        }
    }

    /**
     * @param event
     */
    @SubscribeEvent
    public void onWandererTradesEvent(WandererTradesEvent event) {
        event.getRareTrades().add(new BasicItemListing(3,
                new ItemStack(Foodsicles.FS_OBJECT, 1), 1, 2));
    }
}
