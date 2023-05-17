package com.carlschierig.blockstages.networking;

import com.carlschierig.blockstages.BlockStages;
import com.carlschierig.blockstages.BlockStagesClient;
import com.carlschierig.blockstages.data.ReplacementData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import org.quiltmc.qsl.networking.api.PacketSender;

public class BlockStagesPacketReceiver {

    public static void receiveReplacements(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int count = buf.readInt();
        BlockStages.REPLACEMENTS_MAP.clearReplacements();
        for (int i = 0; i < count; i++) {
            BlockStages.REPLACEMENTS_MAP.addReplacement(new ReplacementData(buf));
        }
        BlockStagesClient.needRebuild = true;
    }
}
