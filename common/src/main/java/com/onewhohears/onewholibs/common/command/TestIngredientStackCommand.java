package com.onewhohears.onewholibs.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.onewhohears.onewholibs.data.crafting.IngredientStack;
import com.onewhohears.onewholibs.util.UtilItem;
import com.onewhohears.onewholibs.util.UtilMCText;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class TestIngredientStackCommand {

    public TestIngredientStackCommand(CommandDispatcher<CommandSourceStack> d, CommandBuildContext context) {
        d.register(Commands.literal("test_ingredient_stack").requires((stack) -> stack.hasPermission(2))
                .then(Commands.argument("ingredient", ItemArgument.item(context))
                        .then(Commands.argument("cost", IntegerArgumentType.integer(1, 64))
                                .executes((ctx) -> {
                                    ServerPlayer player = ctx.getSource().getPlayer();
                                    if (player == null) {
                                        ctx.getSource().sendFailure(UtilMCText.literal("This command requires a player"));
                                        return 0;
                                    }
                                    ItemStack selected = player.getInventory().getSelected();
                                    ItemInput itemInput = ItemArgument.getItem(ctx, "ingredient");
                                    int cost = IntegerArgumentType.getInteger(ctx, "cost");
                                    String itemId = UtilItem.getItemKeyString(itemInput.getItem());
                                    IngredientStack ingredientStack = IngredientStack.fromItem(itemId, cost);
                                    if (ingredientStack.test(selected)) {
                                        ctx.getSource().sendSuccess(()->UtilMCText.literal("Selected Item Passed Ingredient Test!"), false);
                                        return 1;
                                    } else {
                                        ctx.getSource().sendFailure(UtilMCText.literal("Selected Item Failed Ingredient Test"));
                                        return 0;
                                    }
                                })
                        )
                )
        );
    }

}
