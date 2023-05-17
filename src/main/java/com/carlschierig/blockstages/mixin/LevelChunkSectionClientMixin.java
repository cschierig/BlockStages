package com.carlschierig.blockstages.mixin;

import com.carlschierig.blockstages.BlockStages;
import com.carlschierig.blockstages.util.BlockStagesUtil;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.Minecraft;
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
public abstract class LevelChunkSectionClientMixin {

    @Final
    @Shadow
    private PalettedContainer<BlockState> states;

    @Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
    private void getBlockState(int x, int y, int z, CallbackInfoReturnable<BlockState> ci) {
        var state = this.states.get(x, y, z);
        var stateIdf = BlockStagesUtil.getStateIdentifier(state);
        var data = BlockStages.REPLACEMENTS_MAP.getData(stateIdf);
        var player = Minecraft.getInstance().player;

        if (data != null && !GameStageHelper.hasStage(player, data.getStage())) {
            ci.setReturnValue(data.getReplacementBlock().defaultBlockState());
        }
    }
}

