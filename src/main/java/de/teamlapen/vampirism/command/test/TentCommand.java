package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.tileentity.TentTileEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.TranslatableComponent;

public class TentCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("tent")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> tent(context.getSource(), context.getSource().getPlayerOrException(), false))
                .then(Commands.literal("advanced")
                        .executes(context -> tent(context.getSource(), context.getSource().getPlayerOrException(), true)));
    }

    private static int tent(CommandSourceStack commandSource, ServerPlayer asPlayer, boolean advanced) {
        HitResult result = UtilLib.getPlayerLookingSpot(asPlayer, 5);
        if (result.getType() == HitResult.Type.BLOCK) {

            BlockEntity tent = asPlayer.getCommandSenderWorld().getBlockEntity(((BlockHitResult) result).getBlockPos());
            if (tent instanceof TentTileEntity) {
                ((TentTileEntity) tent).setSpawn(true);
                if (advanced) ((TentTileEntity) tent).setAdvanced(true);
                commandSource.sendSuccess(new TranslatableComponent("command.vampirism.test.tent.success"), false);
            }

        }
        return 0;
    }
}
