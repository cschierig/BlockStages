package com.carlschierig.blockstages.mixin;

import com.carlschierig.blockstages.BlockStages;
import com.carlschierig.blockstages.util.BlockStagesUtil;
import com.carlschierig.blockstages.util.IChunkSectionUtils;
import com.carlschierig.blockstages.util.IScheduleChunkRebuild;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements IScheduleChunkRebuild {

    @Shadow
    @Final
    private LevelRenderer levelRenderer;

    @Shadow
    @Final
    private Minecraft minecraft;

    protected ClientLevelMixin(WritableLevelData worldProperties, ResourceKey<Level> registryKey, RegistryAccess registryManager, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean client, boolean debug, long seed, int maxChainedNeighborUpdates) {
        super(worldProperties, registryKey, registryManager, dimension, profiler, client, debug, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "setServerVerifiedBlockState", at = @At("HEAD"), cancellable = true)
    public void setServerVerifiedBlockState(BlockPos pos, BlockState state, int i, CallbackInfo ci) {
        if (BlockStages.REPLACEMENTS_MAP.containsReplacement(BlockStagesUtil.getStateIdentifier(state))) {
            LevelChunk chunk = super.getChunkAt(pos);
            int sectionIndex = chunk.getSectionIndex(pos.getY());
            LevelChunkSection section = chunk.getSection(sectionIndex);

            if (section instanceof IChunkSectionUtils getter) {
                var currentState = getter.getBlockStateUnchecked(pos);
                var currentIdf = BlockStagesUtil.getStateIdentifier(currentState);

                var data = BlockStages.REPLACEMENTS_MAP.getData(currentIdf);
                if (data != null
                    && state.is(data.getReplacementBlock())
                    && !GameStageHelper.hasStage(minecraft.player, data.getStage())) {
                    ci.cancel();
                }
            }
        }
    }


    @Override
    public void rebuild() {
        if (levelRenderer instanceof IScheduleChunkRebuild scheduleChunkRebuild) {
            scheduleChunkRebuild.rebuild();
        }
    }
}
