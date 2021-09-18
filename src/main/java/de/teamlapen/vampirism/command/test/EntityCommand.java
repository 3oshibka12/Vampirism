package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;

import java.util.List;

public class EntityCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("entity")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return entity(context.getSource(), context.getSource().getPlayerOrException());
                });
    }

    private static int entity(CommandSourceStack commandSource, ServerPlayer asPlayer) {
        List<Entity> l = asPlayer.getCommandSenderWorld().getEntities(asPlayer, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Entity entity : l) {
            if (entity instanceof PathfinderMob) {
                ResourceLocation id = Helper.getIDSafe(entity.getType());
                commandSource.sendSuccess(new TextComponent(id.toString()), true);
            } else {
                commandSource.sendSuccess(new TranslatableComponent("Not biteable %s", entity.getClass().getName()), true);
            }
        }
        return 0;
    }
}
