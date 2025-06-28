package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims.BBAnimData;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetType;

public class KFAnimType extends JsonPresetType {
    public static final KFAnimType BLOCK_BENCH = new KFAnimType("block_bench", BBAnimData::new);
    public KFAnimType(String id, JsonPresetStatsFactory<? extends JsonPresetStats> statsFactory) {
        super(id, statsFactory);
    }
}
