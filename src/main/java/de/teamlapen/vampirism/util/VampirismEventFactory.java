package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.api.world.ITotem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VampirismEventFactory {

    public static boolean fireVillagerCaptureEventPre(@Nonnull ITotem totem, @Nonnull List<Villager> villagerIn, boolean forced) {
        VampirismVillageEvent.VillagerCaptureFinish.Pre event = new VampirismVillageEvent.VillagerCaptureFinish.Pre(totem, villagerIn, forced);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult().equals(Event.Result.DENY);
    }

    public static void fireVillagerCaptureEventPost(@Nonnull ITotem totem, @Nonnull List<Villager> villagerIn, boolean forced) {
        MinecraftForge.EVENT_BUS.post(new VampirismVillageEvent.VillagerCaptureFinish.Post(totem, villagerIn, forced));
    }

    public static Villager fireSpawnNewVillagerEvent(@Nonnull ITotem totem, @Nullable Mob oldEntity, @Nonnull Villager newEntity, boolean replaceOld, boolean converted) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(totem, oldEntity, newEntity, replaceOld, converted);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewVillager();
    }

    public static void fireReplaceVillageBlockEvent(@Nonnull ITotem totem, @Nonnull BlockState b, @Nonnull BlockPos pos) {
        VampirismVillageEvent.ReplaceBlock event = new VampirismVillageEvent.ReplaceBlock(totem, b, pos);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static Event.Result fireCanJoinFactionEvent(@Nonnull IFactionPlayerHandler playerHandler, @Nullable IPlayableFaction<?> currentFaction, IPlayableFaction<?> newFaction) {
        PlayerFactionEvent.CanJoinFaction event = new PlayerFactionEvent.CanJoinFaction(playerHandler, currentFaction, newFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean fireChangeLevelOrFactionEvent(@Nonnull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> currentFaction, int currentLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChangePre event = new PlayerFactionEvent.FactionLevelChangePre(player, currentFaction, currentLevel, newFaction, newLevel);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void fireFactionLevelChangedEvent(@Nonnull IFactionPlayerHandler player, @Nullable IPlayableFaction<?> oldFaction, int oldLevel, @Nullable IPlayableFaction<?> newFaction, int newLevel) {
        PlayerFactionEvent.FactionLevelChanged event = new PlayerFactionEvent.FactionLevelChanged(player, oldFaction, oldLevel, newFaction, newLevel);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean fireMakeAggressive(@Nonnull ITotem totem, @Nonnull Villager entity) {
        VampirismVillageEvent.MakeAggressive event = new VampirismVillageEvent.MakeAggressive(totem, entity);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static Pair<Float, Float> fireDefineRaidStrengthEvent(@Nonnull ITotem totem, int badOmenLevel, float defendStrength, float attackStrength) {
        VampirismVillageEvent.DefineRaidStrength event = new VampirismVillageEvent.DefineRaidStrength(totem, badOmenLevel, defendStrength, attackStrength);
        MinecraftForge.EVENT_BUS.post(event);
        return Pair.of(event.getDefendStrength(), event.getAttackStrength());
    }
}