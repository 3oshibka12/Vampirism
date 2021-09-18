package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class FangCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("fang")
                .then(Commands.argument("type", IntegerArgumentType.integer(0, REFERENCE.FANG_TYPE_COUNT - 1))
                        .executes(context -> setFang(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "type"))));
    }

    private static int setFang(CommandContext<CommandSourceStack> context, Player player, int type) {
        if (VampirePlayer.getOpt(player).map(vampire -> vampire.setFangType(type)).orElse(false)) {
            context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.base.fang.success", type), false);
        }
        return type;
    }

}
