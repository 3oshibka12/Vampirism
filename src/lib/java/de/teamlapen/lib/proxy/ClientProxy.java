package de.teamlapen.lib.proxy;


import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.UpdateEntityPacket;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.SoundReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    private static final Logger LOGGER = LogManager.getLogger();

    private static void handleCapability(Entity e, ResourceLocation key, CompoundTag data) {
        Capability cap = HelperRegistry.getSyncableEntityCaps().get(key);
        if (cap == null && e instanceof Player) {
            cap = HelperRegistry.getSyncablePlayerCaps().get(key);
        }
        if (cap == null) {
            LOGGER.warn("Capability with key {} is not registered in the HelperRegistry", key);
        } else {
            LazyOptional opt = e.getCapability(cap, null); //Lazy Optional is kinda strange
            opt.ifPresent(inst -> {
                if (inst instanceof ISyncable) {
                    ((ISyncable) inst).loadUpdateFromNBT(data);
                } else {
                    LOGGER.warn("Target entity's capability {} ({})does not implement ISyncable", inst, key);
                }
            });
            if (!opt.isPresent()) {
                LOGGER.warn("Target entity {} does not have capability {}", e, cap);

            }
        }
    }

    @Nonnull
    @Override
    public ISoundReference createMasterSoundReference(SoundEvent event, float volume, float pinch) {
        return new SoundReference(SimpleSoundInstance.forUI(event, volume, pinch));
    }

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundSource category, BlockPos pos, float volume, float pinch) {
        return new SoundReference(new SimpleSoundInstance(event, category, volume, pinch, pos));
    }

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundSource category, double x, double y, double z, float volume, float pinch) {
        return new SoundReference(new SimpleSoundInstance(event, category, volume, pinch, (float) x, (float) y, (float) z));
    }

    @Override
    public String getActiveLanguage() {
        return Minecraft.getInstance().getLanguageManager().getSelected().toString();
    }

    @Override
    public Player getPlayerEntity(NetworkEvent.Context ctx) {
        //Need to double check the side for some reason
        return (EffectiveSide.get() == LogicalSide.CLIENT ? Minecraft.getInstance().player : super.getPlayerEntity(ctx));
    }

    @Override
    public Level getWorldFromKey(ResourceKey<Level> world) {
        Level serverWorld = super.getWorldFromKey(world);
        if (serverWorld != null) return serverWorld;
        Level clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            if (clientWorld.dimension().equals(world)) {
                return clientWorld;
            }
        }
        return null;
    }

    @Override
    public void handleUpdateEntityPacket(UpdateEntityPacket msg) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            LOGGER.error("Cannot handle update package because sending player entity is null. Message: {}", msg);
        } else {
            Entity e = player.getCommandSenderWorld().getEntity(msg.getId());
            if (e == null) {
                LOGGER.error("Did not find entity {}", msg.getId());
                if (msg.isPlayerItself()) {
                    LOGGER.error("Message is meant for player itself, but id mismatch {} {}. Loading anyway.", player.getId(), msg.getId());
                    e = player;
                }
            }
            if (e != null) {
                if (msg.getData() != null) {
                    ISyncable syncable;
                    try {
                        syncable = (ISyncable) e;
                        syncable.loadUpdateFromNBT(msg.getData());

                    } catch (ClassCastException ex) {
                        LOGGER.warn("Target entity {} does not implement ISyncable ({})", e, ex);
                    }
                }
                if (msg.getCaps() != null) {

                    for (String key : msg.getCaps().getAllKeys()) {
                        handleCapability(e, new ResourceLocation(key), msg.getCaps().getCompound(key));
                    }


                }
            }
        }
    }
}
