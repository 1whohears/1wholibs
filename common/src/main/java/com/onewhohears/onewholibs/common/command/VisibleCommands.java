package com.onewhohears.onewholibs.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.onewhohears.onewholibs.common.core.DistantVisibleManager;
import com.onewhohears.onewholibs.common.core.HeightMapManager;
import com.onewhohears.onewholibs.util.UtilMCText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

import static com.onewhohears.onewholibs.common.core.DistantVisibleManager.CAN_SEE_TEST_DATA;
import static com.onewhohears.onewholibs.common.core.DistantVisibleManager.RED;

public class VisibleCommands {

    public static final Style YELLOW = Style.EMPTY.withColor(ChatFormatting.YELLOW);

    public VisibleCommands(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("gen_lod_height_map").requires((stack) -> stack.hasPermission(2))
                .executes(ctx -> {
                    int k = HeightMapManager.massHeightMapLoadWB(ctx.getSource().getLevel());
                    if (k != -1) {
                        ctx.getSource().sendSuccess(() -> UtilMCText.literal("Started generating a height map " +
                                        "in the world border! Reading "+k+" chunks! ETA: "+k/20/60+" minutes")
                                .setStyle(YELLOW), true);
                        return 1;
                    }
                    ctx.getSource().sendFailure(UtilMCText.literal("Can't generate a height map " +
                                    "in the default world border.").setStyle(RED));
                    return 0;
                })
                .then(Commands.argument("diameter", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            Vec3 pos = ctx.getSource().getPosition();
                            int diameter = IntegerArgumentType.getInteger(ctx, "diameter");
                            int k = HeightMapManager.massHeightMapLoad(ctx.getSource().getLevel(), pos.x, pos.z, diameter);
                            if (k != -1) {
                                ctx.getSource().sendSuccess(() -> UtilMCText.literal("Started generating a height map " +
                                                "at "+pos+" within radius "+(diameter/2)+"! Reading "+k+" chunks! " +
                                                "ETA: "+k/20/60+" minutes")
                                        .setStyle(YELLOW), true);
                                return 1;
                            }
                            ctx.getSource().sendFailure(UtilMCText.literal("Can't generate a height map " +
                                    "in the default world border.").setStyle(RED));
                            return 0;
                        })
                        .then(Commands.argument("center", Vec2Argument.vec2())
                                .executes(ctx -> {
                                    Vec2 pos = Vec2Argument.getVec2(ctx, "center");
                                    int diameter = IntegerArgumentType.getInteger(ctx, "diameter");
                                    int k = HeightMapManager.massHeightMapLoad(ctx.getSource().getLevel(), pos.x, pos.y, diameter);
                                    if (k != -1) {
                                        ctx.getSource().sendSuccess(() -> UtilMCText.literal("Started generating a height map " +
                                                        "at "+pos+" within radius "+(diameter/2)+"! Reading "+k+" chunks! " +
                                                        "ETA: "+k/20/60+" minutes")
                                                .setStyle(YELLOW), true);
                                        return 1;
                                    }
                                    ctx.getSource().sendFailure(UtilMCText.literal("Can't generate a height map " +
                                            "in the default world border.").setStyle(RED));
                                    return 0;
                                })
                        )
                )
        );
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
        d.register(Commands.literal("get_lod_map_height").requires((stack) -> stack.hasPermission(2))
                .executes(ctx -> {
                    Vec3 pos = ctx.getSource().getPosition();
                    short height = HeightMapManager.getHeight(ctx.getSource().getLevel().dimension(), pos);
                    ctx.getSource().sendSuccess(() -> UtilMCText.literal(height+"").setStyle(YELLOW), false);
                    return height;
                })
        );
    }

}
