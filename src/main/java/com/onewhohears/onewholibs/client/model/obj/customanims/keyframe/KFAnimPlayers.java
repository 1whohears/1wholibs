package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetAssetReader;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KFAnimPlayers extends JsonPresetAssetReader<KFAnimData> {

    private static KFAnimPlayers instance;

    public static KFAnimPlayers get() {
        if (instance == null) instance = new KFAnimPlayers();
        return instance;
    }

    public static void close() {
        instance = null;
    }

    private static Map<String, KeyframeAnimationPlayerFactory> playerFactoryMap = new HashMap<>();

    public static void addAnimationPlayerFactory(String id, KeyframeAnimationPlayerFactory player) {
        playerFactoryMap.put(id, player);
    }

    @Nullable
    public static <T extends Entity> KeyframeAnimationPlayer<T> createAnimationPlayer(String id, KFAnimData stats) {
        if (!playerFactoryMap.containsKey(id)) return null;
        return playerFactoryMap.get(id).create(stats);
    }

    public static <T extends Entity> List<KeyframeAnimationPlayer<T>> getAnimPlayersFromDataIds(String[] anim_data_ids) {
        List<KeyframeAnimationPlayer<T>> animationPlayers = new ArrayList<>();
        for (String id : anim_data_ids) {
            KFAnimData data = KFAnimPlayers.get().get(id);
            if (data != null) animationPlayers.add(data.getAnimationPlayer());
        }
        return animationPlayers;
    }


    private KFAnimPlayers() {
        super("animation_data");
    }

    @Override
    protected void registerPresetTypes() {
        addPresetType(KFAnimType.BLOCK_BENCH);
    }

    @Override
    public KFAnimData[] getNewArray(int size) {
        return new KFAnimData[size];
    }

    @Override
    protected void resetCache() {

    }

    public interface KeyframeAnimationPlayerFactory<T extends Entity> {
        KeyframeAnimationPlayer<T> create(KFAnimData stats);
    }
}
