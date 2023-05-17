package com.carlschierig.blockstages.mixin;

import com.carlschierig.blockstages.util.IScheduleChunkRebuild;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements IScheduleChunkRebuild {
    @Shadow
    private ViewArea viewArea;

    @Override
    public void rebuild() {
        for (var chunk : viewArea.chunks) {
            chunk.setDirty(false);
        }
    }
}
