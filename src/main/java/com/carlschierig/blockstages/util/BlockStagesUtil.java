package com.carlschierig.blockstages.util;

import com.carlschierig.blockstages.BlockStages;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public final class BlockStagesUtil {
    public static ResourceLocation getStateIdentifier(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock());
    }

    public static BlockState getBlockState(ServerLevel instance, Player player, BlockPos pos) {
        LevelChunk chunk = instance.getChunkAt(pos);
        int sectionIndex = chunk.getSectionIndex(pos.getY());
        LevelChunkSection section = chunk.getSection(sectionIndex);

        if (section instanceof IChunkSectionUtils getter) {
            var currentState = getter.getBlockStateUnchecked(pos);
            var currentIdf = BlockStagesUtil.getStateIdentifier(currentState);

            var data = BlockStages.REPLACEMENTS_MAP.getData(currentIdf);
            if (data != null && !GameStageHelper.hasStage(player, data.getStage())) {
                BlockStages.LOG.warn("returning {}->{}", data.getOriginalBlock(), data.getReplacementBlock());
                return data.getReplacementBlock().defaultBlockState();
            }
        }

        return instance.getBlockState(pos);
    }
}
