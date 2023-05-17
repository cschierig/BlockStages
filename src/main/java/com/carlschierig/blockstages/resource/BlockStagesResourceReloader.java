package com.carlschierig.blockstages.resource;

import com.carlschierig.blockstages.BlockStages;
import com.carlschierig.blockstages.util.BlockStagesConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

public class BlockStagesResourceReloader implements SimpleSynchronousResourceReloader {

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        BlockStages.REPLACEMENTS_MAP.clearReplacements();
        var resources = manager.listResources("gamestages/blockstages", path -> path.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {

            var path = entry.getKey();
            var resource = entry.getValue();
            BlockStages.REPLACEMENTS_MAP.addFromResource(path, resource);
        }

        if (BlockStages.server != null) {
            for (var player : PlayerLookup.all(BlockStages.server)) {
                BlockStages.REPLACEMENTS_MAP.sync(player);
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getQuiltId() {
        return new ResourceLocation(BlockStagesConstants.MOD_ID, "reloader");
    }
}
