package com.onewhohears.onewholibs.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class CommandUtil {
    public static SuggestionProvider<CommandSourceStack> suggestStrings(String[] strings) {
        return (context, builder) ->
                SharedSuggestionProvider.suggest(strings, builder);
    }

    public static SuggestionProvider<CommandSourceStack> suggestStrings(Supplier<String[]> strings) {
        return (context, builder) ->
                SharedSuggestionProvider.suggest(strings.get(), builder);
    }

    public static void suggestStringToBuilder(SuggestionsBuilder builder, String[] strings) {
        for (String s : strings) builder.suggest(s);
    }

    public static void suggestStringToBuilder(SuggestionsBuilder builder, Set<String> strings) {
        for (String s : strings) builder.suggest(s);
    }

    public static void suggestStringToBuilder(SuggestionsBuilder builder, Collection<String> strings) {
        for (String s : strings) builder.suggest(s);
    }

    public static ArgumentBuilder<CommandSourceStack,?> presetIdArgument(String argName,
                                                                         JsonPresetReloader<?> reloader) {
        return Commands.argument(argName, StringArgumentType.word())
                .suggests(CommandUtil.suggestStrings(reloader::getAllIds));
    }

    public static ArgumentBuilder<CommandSourceStack,?> presetIdArgument(JsonPresetReloader<?> reloader) {
        return presetIdArgument("preset_id", reloader);
    }
}
