package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemRewardInstance implements ITaskRewardInstance {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "item");

    public static ItemRewardInstance decode(FriendlyByteBuf buffer) {
        return new ItemRewardInstance(buffer.readItem());
    }

    public static ItemRewardInstance readNbt(CompoundTag nbt) {
        return new ItemRewardInstance(ItemStack.of(nbt.getCompound("reward")));
    }
    @Nonnull
    protected final ItemStack reward;

    public ItemRewardInstance(@Nonnull ItemStack reward) {
        this.reward = reward;
    }

    @Override
    public void applyReward(IFactionPlayer<?> player) {
        if (!player.getRepresentingPlayer().addItem(this.reward.copy())) {
            player.getRepresentingPlayer().drop(this.reward.copy(), true);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeItem(this.reward);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    public ItemStack getReward() {
        return reward.copy();
    }

    @Override
    public CompoundTag writeNBT(@Nonnull CompoundTag nbt) {
        nbt.put("reward", this.reward.save(new CompoundTag()));
        return nbt;
    }
}
