package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.command.arguments.ActionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class BindActionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("bind-action")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .then(Commands.argument("shortcutnumber", IntegerArgumentType.integer(1, 3))
                        .then(Commands.argument("action", ActionArgument.actions())
                                .executes(context -> bindAction(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "shortcutnumber"), ActionArgument.getAction(context, "action")))))
                .then(Commands.literal("help")
                        .executes(BindActionCommand::help));
    }

    private static int bindAction(CommandContext<CommandSourceStack> context, ServerPlayer asPlayer, int number, IAction<?> action) {
        FactionPlayerHandler.getOpt(asPlayer).ifPresent(fp-> {
            fp.setBoundAction(number, action, true, true);
            context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.base.bind_action.success", action.getName(), number), false);
        });
        return 0;
    }

    private static int help(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.base.bind_action.help"), false);
        return 0;
    }

}
