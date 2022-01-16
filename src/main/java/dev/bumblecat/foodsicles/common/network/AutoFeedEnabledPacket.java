package dev.bumblecat.foodsicles.common.network;

import dev.bumblecat.foodsicles.common.objects.items.IFoodsicle;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AutoFeedEnabledPacket {

    private final boolean value;


    public AutoFeedEnabledPacket(boolean value) {
        this.value = value;
    }

    public static void encode(AutoFeedEnabledPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.value);
    }

    public static AutoFeedEnabledPacket decode(FriendlyByteBuf buffer) {
        return new AutoFeedEnabledPacket(buffer.readBoolean());
    }

    public static void handle(AutoFeedEnabledPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {

            ServerPlayer player = context.get().getSender();
            if (player == null)
                return;

            ((IFoodsicle) player.getMainHandItem().getItem()).setIsAutofeeding(player.getMainHandItem(), packet.value);
        });
        context.get().setPacketHandled(true);
    }

    /**
     * @return
     */
    public boolean getValue() {
        return this.value;
    }
}
