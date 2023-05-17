package com.carlschierig.blockstages;

import com.carlschierig.blockstages.networking.BlockStagesMessages;
import com.carlschierig.blockstages.util.IScheduleChunkRebuild;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientWorldTickEvents;

import java.util.HashSet;
import java.util.Set;

public class BlockStagesClient implements ClientModInitializer {
    public static boolean needRebuild;

    public final Set<ResourceLocation> hiddenBlocks = new HashSet<>();

    @Override
    public void onInitializeClient(ModContainer mod) {
        BlockStagesMessages.registerS2CPackets();
        StagesSyncedEvent.STAGES_SYNCED_EVENT.register((data) -> {
            needRebuild = true;
        });
        ClientWorldTickEvents.END.register((client, world) -> {
            if (needRebuild && world instanceof IScheduleChunkRebuild scheduleChunkRebuild) {
                BlockStages.LOG.warn("REBUILDING");
                scheduleChunkRebuild.rebuild();
                needRebuild = false;
            }
        });
    }
}
