package de.teamlapen.lib.network;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import de.teamlapen.lib.lib.storage.ISyncable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ClientPayloadHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleUpdateEntityPacket(ClientboundUpdateEntityPacket pkt, IPayloadContext context) {
        context.workHandler().execute(() -> {
            context.player().ifPresent(player -> {
                context.level().ifPresent(level -> {
                    Entity e = level.getEntity(pkt.getId());
                    if (e == null) {
                        LOGGER.error("Did not find entity {}", pkt.getId());
                        if (pkt.isPlayerItself()) {
                            LOGGER.error("Message is meant for player itself, but id mismatch {} {}. Loading anyway.", player.getId(), pkt.getId());
                            e = player;
                        }
                    }
                    if (e != null) {
                        if (pkt.getData() != null) {
                            if (e instanceof ISyncable syncable) {
                                syncable.deserializeUpdateNBT(pkt.getData());
                            } else {
                                LOGGER.warn("Target entity {} does not implement ISyncable", e);
                            }
                        }
                        if (pkt.getAttachments() != null) {
                            for (String key : pkt.getAttachments().getAllKeys()) {
                                handleCapability(e, new ResourceLocation(key), pkt.getAttachments().getCompound(key));
                            }
                        }
                    }
                });
            });
        });
    }

    private static void handleCapability(Entity e, ResourceLocation key, CompoundTag data) {
        AttachmentType<IAttachedSyncable> cap = HelperRegistry.getSyncableEntityCaps().get(key);
        if (cap == null && e instanceof Player) {
            cap = HelperRegistry.getSyncablePlayerCaps().get(key);
        }
        if (cap == null) {
            LOGGER.warn("Capability with key {} is not registered in the HelperRegistry", key);
        } else {
            e.getData(cap).deserializeUpdateNBT(data);
        }
    }
}
