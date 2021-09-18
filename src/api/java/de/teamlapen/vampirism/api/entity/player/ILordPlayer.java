package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for the player lord related data.
 */
public interface ILordPlayer {

    /**
     * @return The faction of this lord player or null if not currently a lord
     */
    @Nullable
    IPlayableFaction<?> getLordFaction();

    int getLordLevel();

    /**
     * @return Null, if level ==0
     */
    @Nullable
    Component getLordTitle();

    @Nonnull
    Player getPlayer();
}
