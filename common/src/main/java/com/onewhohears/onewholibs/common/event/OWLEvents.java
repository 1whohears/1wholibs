package com.onewhohears.onewholibs.common.event;

import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OWLEvents {

    public static @NotNull List<JsonPresetReloadListener<?>> getJsonPresetReloadListeners() {
        List<JsonPresetReloadListener<?>> gathered = new ArrayList<>();
        GET_JSON_PRESET_LISTENERS.invoker().gather(gathered);
        return gathered;
    }

    public static void registerPresetTypesEvent() {
        REGISTER_PRESET_TYPES.invoker().register();
    }

    public static final Event<GetJsonPresetListeners> GET_JSON_PRESET_LISTENERS =
            EventFactory.createLoop(GetJsonPresetListeners.class);

    public static final Event<RegisterPresetTypesEvent> REGISTER_PRESET_TYPES =
            EventFactory.createEventResult(RegisterPresetTypesEvent.class);

    public static final Event<OnSyncBoolGameRuleEvent> SYNC_BOOL_GAME_RULE =
            EventFactory.createEventResult(OnSyncBoolGameRuleEvent.class);

    public static final Event<OnSyncIntGameRuleEvent> SYNC_INT_GAME_RULE =
            EventFactory.createEventResult(OnSyncIntGameRuleEvent.class);

    @FunctionalInterface
    public interface GetJsonPresetListeners {
        void gather(List<JsonPresetReloadListener<?>> listeners);
    }

    @FunctionalInterface
    public interface RegisterPresetTypesEvent {
        void register();
    }

    @FunctionalInterface
    public interface OnSyncBoolGameRuleEvent {
        void sync(String id, boolean bool);
    }

    @FunctionalInterface
    public interface OnSyncIntGameRuleEvent {
        void sync(String id, int num);
    }
}
