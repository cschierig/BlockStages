package com.carlschierig.blockstages;

import com.carlschierig.blockstages.data.ReplacementsMap;
import com.carlschierig.blockstages.resource.BlockStagesResourceReloader;
import com.carlschierig.blockstages.util.BlockStagesConstants;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.event.GameStagesEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockStages implements ModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger(BlockStagesConstants.NAME);
    public static final ReplacementsMap REPLACEMENTS_MAP = new ReplacementsMap();

    public static MinecraftServer server;

    @Override
    public void onInitialize(ModContainer mod) {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloader(new BlockStagesResourceReloader());

        ServerLifecycleEvents.READY.register(server -> BlockStages.server = server);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var data = GameStageHelper.getPlayerData(player);
            if (data != null) {
                REPLACEMENTS_MAP.addStagesForPlayer(player.getUUID(), data.getStages());
            }
            REPLACEMENTS_MAP.sync(player);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var player = handler.getPlayer();
            var data = GameStageHelper.getPlayerData(player);
            if (data != null) {
                REPLACEMENTS_MAP.removeStagesForPlayer(player.getUUID(), data.getStages());
            }
        });
        GameStagesEvents.ADDED_EVENT.register((player, stage) -> {
            REPLACEMENTS_MAP.addStageForPlayer(player.getUUID(), stage);
        });
        GameStagesEvents.REMOVED_EVENT.register((player, stage) -> {
            REPLACEMENTS_MAP.removeStageForPlayer(player.getUUID(), stage);
        });
        GameStagesEvents.CLEARED_EVENT.register(((player, stageData) -> {
            REPLACEMENTS_MAP.clearStagesForPlayer(player.getUUID());
        }));
    }
}
