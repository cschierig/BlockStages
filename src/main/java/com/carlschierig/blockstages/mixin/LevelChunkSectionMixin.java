package com.carlschierig.blockstages.mixin;

import com.carlschierig.blockstages.BlockStages;
import com.carlschierig.blockstages.util.BlockStagesUtil;
import com.carlschierig.blockstages.util.IUncheckedBlockStateGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements IUncheckedBlockStateGetter {

    @Final
    @Shadow
    private PalettedContainer<BlockState> states;

    @Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
    private void getBlockState(int x, int y, int z, CallbackInfoReturnable<BlockState> ci) {
        var state = this.states.get(x, y, z);
        var stateIdf = BlockStagesUtil.getStateIdentifier(state);
        var map = BlockStages.REPLACEMENTS_MAP;
        var data = map.getData(stateIdf);

        if (data != null && !map.stageUnlockedAny(data.getStage())) {
            ci.setReturnValue(data.getReplacementBlock().defaultBlockState());
        }
    }

    public BlockState getBlockStateUnchecked(BlockPos pos) {
        return this.states.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }
}

