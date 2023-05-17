package com.carlschierig.blockstages.networking;

import com.carlschierig.blockstages.util.BlockStagesConstants;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class BlockStagesMessages {
    public static final ResourceLocation CHANNEL = new ResourceLocation(BlockStagesConstants.MOD_ID, "replacement_map");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL, BlockStagesPacketReceiver::receiveReplacements);
    }


}
