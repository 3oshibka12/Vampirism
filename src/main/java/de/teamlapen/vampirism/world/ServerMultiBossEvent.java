package de.teamlapen.vampirism.world;

import com.google.common.collect.Sets;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.MultiBossEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import java.util.Set;

public class ServerMultiBossEvent extends MultiBossEvent {

    private final Set<ServerPlayer> players = Sets.newHashSet();
    private boolean visible = true;


    public ServerMultiBossEvent(Component nameIn, BossEvent.BossBarOverlay overlayIn, Color... entries) {
        super(Mth.createInsecureUUID(), nameIn, overlayIn, entries);
    }

    public void addPlayer(ServerPlayer player) {
        if (this.players.add(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new MultiBossEventPacket(MultiBossEventPacket.OperationType.ADD, this), player);
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.sendUpdate(MultiBossEventPacket.OperationType.UPDATE_PROGRESS);
    }

    public Set<ServerPlayer> getPlayers() {
        return players;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;

            for (ServerPlayer player : this.players) {
                VampirismMod.dispatcher.sendTo(new MultiBossEventPacket(visible ? MultiBossEventPacket.OperationType.ADD : MultiBossEventPacket.OperationType.REMOVE, this), player);
            }
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new MultiBossEventPacket(MultiBossEventPacket.OperationType.REMOVE, this), player);
        }
    }

    @Override
    public void setColors(Color... entries) {
        super.setColors(entries);
        this.sendUpdate(MultiBossEventPacket.OperationType.ADD);
    }

    @Override
    public void setName(Component name) {
        super.setName(name);
        this.sendUpdate(MultiBossEventPacket.OperationType.UPDATE_NAME);
    }

    @Override
    public void setOverlay(BossEvent.BossBarOverlay overlay) {
        super.setOverlay(overlay);
        this.sendUpdate(MultiBossEventPacket.OperationType.UPDATE_STYLE);
    }

    @Override
    public void setPercentage(Color color, float perc) {
        super.setPercentage(color, perc);
        this.sendUpdate(MultiBossEventPacket.OperationType.UPDATE_PROGRESS);
    }

    @Override
    public void setPercentage(float... perc) {
        super.setPercentage(perc);
        this.sendUpdate(MultiBossEventPacket.OperationType.UPDATE_PROGRESS);
    }

    private void sendUpdate(MultiBossEventPacket.OperationType operation) {
        if (this.visible) {
            MultiBossEventPacket packet = new MultiBossEventPacket(operation, this);

            for (ServerPlayer player : this.players) {
                VampirismMod.dispatcher.sendTo(packet, player);
            }
        }
    }
}
