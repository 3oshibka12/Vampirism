package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.TranslatableComponent;

public class CurrentDimensionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("currentDimension")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return currentDimension(context, context.getSource().getPlayerOrException());
                });
    }

    private static int currentDimension(CommandContext<CommandSourceStack> context, ServerPlayer asPlayer) {
        context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.base.currentdimension.dimension", asPlayer.getCommandSenderWorld().dimension().location() + " (" + asPlayer.getServer().registryAccess().dimensionTypes().getKey(asPlayer.getCommandSenderWorld().dimensionType())), false);
        return 0;
    }
}
