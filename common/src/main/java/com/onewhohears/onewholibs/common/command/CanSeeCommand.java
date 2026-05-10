package com.onewhohears.onewholibs.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.onewhohears.onewholibs.common.core.DistantVisibleManager;
import com.onewhohears.onewholibs.util.UtilMCText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

import static com.onewhohears.onewholibs.common.core.DistantVisibleManager.CAN_SEE_TEST_DATA;
import static com.onewhohears.onewholibs.common.core.DistantVisibleManager.RED;

public class CanSeeCommand {

    public static final Style YELLOW = Style.EMPTY.withColor(ChatFormatting.YELLOW);

    public CanSeeCommand(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("can_see").requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("target_entities", EntityArgument.entities())
                        .executes(ctx -> {
                            Entity eye = ctx.getSource().getEntity();
                            if (eye == null) {
                                ctx.getSource().sendFailure(UtilMCText.literal("Command must be ran as an entity.")
                                        .setStyle(RED));
                                return 0;
                            }
                            Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "target_entities");
                            for (Entity target : targets) {
                                try {
                                    DistantVisibleManager.queryVisible(ctx.getSource().getServer(), eye, target, CAN_SEE_TEST_DATA);
                                } catch (Exception e) {
                                    ctx.getSource().sendFailure(UtilMCText.literal("Failed: "+e.getMessage())
                                            .setStyle(RED));
                                    e.printStackTrace();
                                    return 0;
                                }
                                ctx.getSource().sendSuccess(() -> UtilMCText.literal(
                                                "Querying if "+eye.getScoreboardName()+" can see "
                                                        +target.getScoreboardName()+". Check Console for Result.")
                                                .setStyle(YELLOW), true);
                            }
                            return targets.size();
                        })
                )
        );
    }

}
