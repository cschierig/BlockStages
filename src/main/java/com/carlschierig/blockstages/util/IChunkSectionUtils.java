package com.carlschierig.blockstages.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IUncheckedBlockStateGetter {
    BlockState getBlockStateUnchecked(BlockPos pos);
}
