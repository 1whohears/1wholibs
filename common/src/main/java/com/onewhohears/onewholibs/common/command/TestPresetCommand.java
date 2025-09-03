package com.onewhohears.onewholibs.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.onewhohears.onewholibs.common.network.toclient.ToClientTestPreset;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresets;
import com.onewhohears.onewholibs.util.CommandUtil;
import com.onewhohears.onewholibs.util.UtilMCText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class TestPresetCommand {

    public TestPresetCommand(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("test_preset").requires((stack) -> stack.hasPermission(2))
                .then(CommandUtil.presetIdArgument(TestPresets.get()).executes(testPreset()))
        );
    }

    private static Command<CommandSourceStack> testPreset() {
        return context -> {
            String presetId = StringArgumentType.getString(context, "preset_id");
            if (!TestPresets.get().has(presetId)) {
                context.getSource().sendFailure(UtilMCText.literal("The Test Preset "+presetId+" does not exist!"));
                return 0;
            }
            TestPresetStats stats = TestPresets.get().get(presetId);
            if (stats == null) {
                context.getSource().sendFailure(UtilMCText.literal("The Test Preset "+presetId+" does not exist!"));
                return 0;
            }
            context.getSource().sendSuccess(() -> UtilMCText.literal(
                    "The Test Preset "+presetId+" has a value of "+stats.getValue()+"!"), false);
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) new ToClientTestPreset(presetId).sendTo(player);
            return 1;
        };
    }

}
