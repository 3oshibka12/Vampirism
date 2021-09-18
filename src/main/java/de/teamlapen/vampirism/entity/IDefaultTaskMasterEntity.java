package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.OptionalInt;

public interface IDefaultTaskMasterEntity extends ForceLookEntityGoal.TaskOwner, ITaskMasterEntity {

    Component CONTAINER_NAME = new TranslatableComponent("container.vampirism.taskmaster");
    Component NO_TASK = new TranslatableComponent("text.vampirism.taskmaster.no_tasks");

    /**
     * @return The biome type based on where this entity was spawned
     */
    VillagerType getBiomeType();

    default boolean processInteraction(Player playerEntity, Entity entity) {
        if (FactionPlayerHandler.getOpt(playerEntity).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(IFactionPlayer::getTaskManager).map(taskManager -> taskManager.hasAvailableTasks(entity.getUUID())).orElse(false)) {
            OptionalInt containerIdOpt = playerEntity.openMenu(new SimpleMenuProvider((containerId, playerInventory, player) -> new TaskBoardContainer(containerId, playerInventory), entity.getDisplayName().plainCopy()));
            if (containerIdOpt.isPresent()) {
                FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(iFactionPlayer -> {
                    iFactionPlayer.getTaskManager().openTaskMasterScreen(entity.getUUID());
                }));
                return true;
            }
        } else {
            playerEntity.displayClientMessage(NO_TASK, true);
        }
        return false;
    }

}
