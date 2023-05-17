package com.carlschierig.blockstages.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockStagesUtil {
    public static ResourceLocation getStateIdentifier(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock());
    }
}
