package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

import de.teamlapen.vampirism.api.entity.minion.IMinionTask.NoDesc;

public class SimpleMinionTask extends DefaultMinionTask<IMinionTask.NoDesc<MinionData>, MinionData> {


    @Nullable
    @Override
    public NoDesc<MinionData> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, MinionData data) {
        this.triggerAdvancements(lord);
        return new NoDesc<>(this);
    }

    @Override
    public void deactivateTask(NoDesc<MinionData> desc) {

    }

    @Override
    public NoDesc<MinionData> readFromNBT(CompoundTag nbt) {
        return new NoDesc<>(this);
    }
}
