package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.factions.*;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;


public class FactionRegistry implements IFactionRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Integer, Predicate<LivingEntity>> predicateMap = new HashMap<>();
    private List<Faction<?>> temp = new CopyOnWriteArrayList<>(); //Copy on write is costly, but we only expect very few elements anyway
    private Faction<?>[] allFactions;
    private PlayableFaction<?>[] playableFactions;

    /**
     * Finishes registrations during InterModProcessEvent
     */
    public void finish() {
        allFactions = temp.toArray(new Faction[0]);
        temp = null;
        List<PlayableFaction<?>> temp2 = new ArrayList<>();
        for (Faction<?> allFaction : allFactions) {
            if (allFaction instanceof PlayableFaction) {
                temp2.add((PlayableFaction<?>) allFaction);
            }
        }
        playableFactions = temp2.toArray(new PlayableFaction[0]);
    }

    @Override
    @Nullable
    public IFaction<?> getFaction(Entity entity) {
        if (entity instanceof IFactionEntity) {
            return ((IFactionEntity) entity).getFaction();
        } else if (entity instanceof Player) {
            return VampirismPlayerAttributes.get((Player) entity).faction;
        }
        return null;
    }

    @Nullable
    @Override
    public IFaction<?> getFactionByID(ResourceLocation id) {
        if (allFactions == null) {
            return null;
        }
        for (IFaction<?> f : allFactions) {
            if (f.getID().equals(id)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public Faction<?>[] getFactions() {
        return allFactions;
    }

    @Override
    public PlayableFaction<?>[] getPlayableFactions() {
        return playableFactions;
    }

    @Override
    public Predicate<LivingEntity> getPredicate(IFaction<?> thisFaction, boolean ignoreDisguise) {

        return getPredicate(thisFaction, true, true, true, ignoreDisguise, null);
    }

    @Override
    public Predicate<LivingEntity> getPredicate(IFaction<?> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable IFaction<?> otherFaction) {
        int key = 0;
        if (otherFaction != null) {
            int id = otherFaction.hashCode();
            if (id > 63) {
                LOGGER.warn("Faction id over 64, predicates won't work");
            }
            key |= ((id & 63) << 10);
        }
        if (player) {
            key |= (1 << 9);
        }
        if (mob) {
            key |= (1 << 8);
        }
        if (neutralPlayer) {
            key |= (1 << 7);
        }
        if (ignoreDisguise) {
            key |= (1 << 6);
        }
        int id = thisFaction.hashCode();
        if (id > 64) {
            LOGGER.warn("Faction id over 64, predicates won't work");
        }
        key |= id & 63;
        Predicate<LivingEntity> predicate;
        if (predicateMap.containsKey(key)) {
            predicate = predicateMap.get(key);
        } else {
            predicate = new PredicateFaction(thisFaction, player, mob, neutralPlayer, ignoreDisguise, otherFaction);
            predicateMap.put(key, predicate);
        }
        return predicate;
    }

    @ThreadSafeAPI
    private void addFaction(Faction<?> faction) {
        if (temp == null) {
            throw new IllegalStateException(String.format("[Vampirism]You have to register factions during InterModEnqueueEvent. (%s)", faction.getID()));
        } else {
            temp.add(faction);
        }
    }

    @Override
    public <T extends IFactionEntity> IFactionBuilder<T> createFaction(ResourceLocation id, Class<T> entityInterface) {
        if (!UtilLib.isNonNull(id, entityInterface)) {
            throw new IllegalArgumentException("[Vampirism] Parameter for faction cannot be null");
        }
        return new FactionBuilder<>(id, entityInterface);
    }

    @Override
    public <T extends IFactionPlayer<T>> IPlayableFactionBuilder<T> createPlayableFaction(ResourceLocation id, Class<T> entityInterface, NonNullSupplier<Capability<T>> playerCapabilitySupplier) {
        if (!UtilLib.isNonNull(id, entityInterface, playerCapabilitySupplier)) {
            throw new IllegalArgumentException("[Vampirism] Parameters for faction cannot be null");
        }
        return new PlayableFactionBuilder<>(id, entityInterface, playerCapabilitySupplier);
    }

    private class FactionBuilder<T extends IFactionEntity> implements IFactionBuilder<T> {

        protected final ResourceLocation id;
        protected final Class<T> entityInterface;
        protected int color= Color.WHITE.getRGB();
        protected boolean hostileTowardsNeutral;
        protected IVillageFactionData villageFactionData;
        protected ChatFormatting chatColor;
        protected String name;
        protected String namePlural;

        public FactionBuilder(ResourceLocation id, Class<T> entityInterface) {
            this.id = id;
            this.entityInterface = entityInterface;
        }

        @Override
        public IFactionBuilder<T> color(int color) {
            this.color = color;
            return this;
        }

        @Override
        public IFactionBuilder<T> chatColor(ChatFormatting color) {
            this.chatColor = color;
            return this;
        }

        @Override
        public IFactionBuilder<T> hostileTowardsNeutral() {
            this.hostileTowardsNeutral = true;
            return this;
        }

        @Override
        public IFactionBuilder<T> village(@Nullable IVillageFactionData villageFactionData) {
            this.villageFactionData = villageFactionData;
            return this;
        }

        @Override
        public IFactionBuilder<T> name(String nameKey) {
            this.name = nameKey;
            return this;
        }

        @Override
        public IFactionBuilder<T> namePlural(String namePluralKey) {
            this.namePlural = namePluralKey;
            return this;
        }

        @Override
        public IFaction<T> register() {
            Faction<T> faction = new Faction<>(
                    this.id,
                    this.entityInterface,
                    this.color,
                    this.hostileTowardsNeutral,
                    this.villageFactionData,
                    this.chatColor != null ? this.chatColor : ChatFormatting.WHITE,
                    this.name == null?new TextComponent(id.toString()):new TranslatableComponent(this.name),
                    this.namePlural == null ? this.name == null?new TextComponent(id.toString()):new TranslatableComponent(this.name):new TranslatableComponent(this.namePlural));
            addFaction(faction);
            return faction;
        }
    }

    private class PlayableFactionBuilder<T extends IFactionPlayer<T>> extends FactionBuilder<T> implements IPlayableFactionBuilder<T> {

        protected final NonNullSupplier<Capability<T>> playerCapabilitySupplier;
        protected int highestLevel = 1;
        protected int highestLordLevel = 0;
        protected BiFunction<Integer, Boolean, Component> lordTitleFunction;
        protected Function<IRefinementItem.AccessorySlotType, IRefinementItem> refinementItemBySlot;

        public PlayableFactionBuilder(ResourceLocation id, Class<T> entityInterface, NonNullSupplier<Capability<T>> playerCapabilitySupplier) {
            super(id, entityInterface);
            this.playerCapabilitySupplier = playerCapabilitySupplier;
        }

        @Override
        public IPlayableFactionBuilder<T> color(int color) {
            this.color = color;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> hostileTowardsNeutral() {
            this.hostileTowardsNeutral = true;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> highestLevel(int highestLevel) {
            this.highestLevel = highestLevel;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> lordLevel(int highestLordLevel) {
            this.highestLordLevel = highestLordLevel;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> lordTitle(@Nonnull BiFunction<Integer, Boolean, Component> lordTitleFunction) {
            this.lordTitleFunction = lordTitleFunction;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> village(@Nullable IVillageFactionData villageFactionData) {
            this.villageFactionData = villageFactionData;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> refinementItems(Function<IRefinementItem.AccessorySlotType, IRefinementItem> refinementItemBySlot) {
            this.refinementItemBySlot = refinementItemBySlot;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> chatColor(ChatFormatting color) {
            this.chatColor = color;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> name(String nameKey) {
            this.name = nameKey;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> namePlural(String namePluralKey) {
            this.namePlural = namePluralKey;
            return this;
        }

        @Override
        public IPlayableFaction<T> register() {
            if (this.lordTitleFunction == null) {
                this.lordTitleFunction = (a, b) -> new TextComponent("Lord " + a);
            }
            PlayableFaction<T> faction = new PlayableFaction<>(
                    this.id,
                    this.entityInterface,
                    this.color,
                    this.hostileTowardsNeutral,
                    this.playerCapabilitySupplier,
                    this.highestLevel,
                    this.highestLordLevel,
                    this.lordTitleFunction,
                    this.villageFactionData,
                    this.refinementItemBySlot,
                    this.chatColor != null ? this.chatColor : ChatFormatting.WHITE,
                    this.name == null?new TextComponent(id.toString()):new TranslatableComponent(this.name),
                    this.namePlural == null ? this.name == null?new TextComponent(id.toString()):new TranslatableComponent(this.name):new TranslatableComponent(this.namePlural));
            addFaction(faction);
            return faction;
        }
    }

}
