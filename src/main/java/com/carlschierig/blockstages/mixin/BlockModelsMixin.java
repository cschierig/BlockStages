package com.carlschierig.blockstages.mixin;


import com.carlschierig.blockstages.BlockStages;
import com.carlschierig.blockstages.util.BlockStagesUtil;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public class BlockModelsMixin {
    @Shadow
    private Map<BlockState, BakedModel> modelByStateCache;

    @Shadow
    @Final
    private ModelManager modelManager;

    /**
     * @author CozyPenguin
     * @reason Block stages replaces block models at runtime
     */
    @Overwrite
    public BakedModel getBlockModel(BlockState state) {
        var stateIdf = BlockStagesUtil.getStateIdentifier(state);
        var data = BlockStages.REPLACEMENTS_MAP.getData(stateIdf);
        var player = Minecraft.getInstance().player;

        if (data != null && !GameStageHelper.hasStage(player, data.getStage())) {
            state = data.getReplacementBlock().defaultBlockState();
        }

        BakedModel model = modelByStateCache.get(state);
        if (model == null) {
            model = this.modelManager.getMissingModel();
        }
        return model;
    }
}
