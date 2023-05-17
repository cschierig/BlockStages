package com.carlschierig.blockstages.networking;

import com.carlschierig.blockstages.data.ReplacementData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.Collection;

public class BlockStagesS2CPackets {
    public static void sendReplacements(ServerPlayer player, Collection<ReplacementData> data) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(data.size());
        for (var replacementData : data) {
            replacementData.writeToBuf(buf);
        }
        ServerPlayNetworking.send(player, BlockStagesMessages.CHANNEL, buf);
    }
}
