package com.carlschierig.blockstages.data;

import com.carlschierig.blockstages.BlockStages;
import com.carlschierig.blockstages.networking.BlockStagesS2CPackets;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ReplacementsMap {
    private final Map<ResourceLocation, ReplacementData> replacements = new HashMap<>();

    private final Set<ResourceLocation> replacingBlocks = new HashSet<>();

    private final Map<String, Set<UUID>> stagesByPlayer = new HashMap<>();

    public void addStagesForPlayer(UUID player, Collection<String> stages) {
        for (var stage : stages) {
            addStageForPlayer(player, stage);
        }
    }

    public void addStageForPlayer(UUID player, String stage) {
        if (!stagesByPlayer.containsKey(stage)) {
            stagesByPlayer.put(stage, new HashSet<>());
        }
        stagesByPlayer.get(stage).add(player);
    }

    public void removeStagesForPlayer(UUID player, Collection<String> stages) {
        for (var stage : stages) {
            removeStageForPlayer(player, stage);
        }
    }

    public void removeStageForPlayer(UUID player, String stage) {
        if (stagesByPlayer.containsKey(stage)) {
            stagesByPlayer.get(stage).remove(player);
        }
    }

    public void clearStagesForPlayer(UUID player) {
        for (var set : stagesByPlayer.values()) {
            set.remove(player);
        }
    }

    /**
     * Checks if any of the logged in players has the given stage.
     *
     * @param stage The stage which should be checked.
     * @return {@code true} if a logged in player has the stage, {@code false} otherwise.
     */
    public boolean stageUnlockedAny(String stage) {
        return stagesByPlayer.containsKey(stage) && stagesByPlayer.get(stage).size() > 0;
    }

    public void addReplacement(ReplacementData data) {
        if (replacements.containsKey(data.getOriginalId())) {
            BlockStages.LOG.warn("Overriding replacement {} for {} with {}",
                replacements.get(data.getOriginalId()).getReplacementId(),
                data.getOriginalId(),
                data.getReplacementId()
            );
        }
        replacements.put(data.getOriginalId(), data);
        replacingBlocks.add(data.getReplacementId());
        BlockStages.LOG.warn("adding {}->{} if '{}'", data.getOriginalId(), data.getReplacementId(), data.getStage());
    }

    public void sync(ServerPlayer player) {
        BlockStagesS2CPackets.sendReplacements(player, replacements.values());
    }

    public void addFromResource(ResourceLocation id, Resource resource) {
        // based on LambdasBetterGrass LBGLayerType#load()
        // TODO: error handling
        try (var reader = new InputStreamReader(resource.open())) {
            var json = JsonParser.parseReader(reader).getAsJsonObject();

            var stage = json.get("stage").getAsString();
            var replacementsArray = json.get("replacements").getAsJsonArray();

            for (var entry : replacementsArray) {
                var replacementObject = entry.getAsJsonObject();
                var original = new ResourceLocation(replacementObject.get("original").getAsString());
                var replacement = new ResourceLocation(replacementObject.get("replacement").getAsString());
                addReplacement(new ReplacementData(original, replacement, stage));
            }
        } catch (IOException exception) {
            BlockStages.LOG.error("Could not load replacement data from '{}'", id);
        } catch (JsonSyntaxException exception) {
            BlockStages.LOG.error("Could not parse '{}' replacement data syntax: {}", id, exception.getMessage());
            throw exception;
        }
    }

    public boolean containsReplacement(ResourceLocation replacement) {
        return replacingBlocks.contains(replacement);
    }

    public ReplacementData getData(ResourceLocation key) {
        return replacements.get(key);
    }

    public void clearReplacements() {
        replacements.clear();
        replacingBlocks.clear();
    }
}
