package com.carlschierig.blockstages.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class ReplacementData {
    private final ResourceLocation original;
    private final ResourceLocation replacement;
    private final String stage;

    private Block originalBlock;
    private Block replacementBlock;

    public ReplacementData(ResourceLocation original, ResourceLocation replacement, String stage) {
        this.original = original;
        this.replacement = replacement;
        this.stage = stage;
    }

    public ReplacementData(FriendlyByteBuf buffer) {
        this.original = buffer.readResourceLocation();
        this.replacement = buffer.readResourceLocation();
        this.stage = buffer.readUtf();
    }

    public ResourceLocation getOriginalId() {
        return original;
    }

    public Block getOriginalBlock() {
        if (originalBlock == null) {
            originalBlock = BuiltInRegistries.BLOCK.get(original);
        }
        return originalBlock;
    }

    public ResourceLocation getReplacementId() {
        return replacement;
    }

    public Block getReplacementBlock() {
        if (replacementBlock == null) {
            replacementBlock = BuiltInRegistries.BLOCK.get(replacement);
        }
        return replacementBlock;
    }

    public String getStage() {
        return stage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ReplacementData) obj;
        return Objects.equals(this.original, that.original) &&
            Objects.equals(this.replacement, that.replacement) &&
            Objects.equals(this.stage, that.stage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original, replacement, stage);
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeResourceLocation(original);
        buf.writeResourceLocation(replacement);
        buf.writeUtf(stage);
    }
}
