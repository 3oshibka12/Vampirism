package de.teamlapen.vampirism.player.refinements;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public abstract class RefinementSet extends ForgeRegistryEntry<IRefinementSet> implements IRefinementSet {

    private final Set<IRefinement> refinements;
    private final Rarity rarity;
    private final int color;
    private final WeightedRandomItem<IRefinementSet> weightedRandom;
    private Component name;
    private Component desc;
    @Nullable
    private IRefinementItem.AccessorySlotType restrictedType;

    public RefinementSet(Rarity rarity, int color, Set<IRefinement> refinements) {
        this.refinements = refinements;
        this.rarity = rarity;
        this.weightedRandom = new WeightedRandomItem<>(this, this.rarity.weight);
        this.color = color;
    }

    public RefinementSet(Rarity rarity, int color, IRefinement... refinements) {
        this(rarity, color, UtilLib.newSortedSet(refinements));
    }

    @Override
    public int getColor() {
        return color;
    }

    @Nonnull
    @Override
    public Component getName() {
        return this.name != null ? this.name : (this.name = new TranslatableComponent("refinement_set." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()));
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return this.rarity;
    }

    @Nonnull
    @Override
    public Set<IRefinement> getRefinements() {
        return this.refinements;
    }

    @Override
    public Optional<IRefinementItem.AccessorySlotType> getSlotType() {
        return Optional.ofNullable(restrictedType);
    }

    public WeightedRandomItem<IRefinementSet> getWeightedRandom() {
        return weightedRandom;
    }

    /**
     * Specify the one and only accessory type this refinement can be put on
     */
    public RefinementSet onlyFor(IRefinementItem.AccessorySlotType restrictedType) {
        this.restrictedType = restrictedType;
        return this;
    }

    public static class VampireRefinementSet extends RefinementSet {
        public VampireRefinementSet(Rarity rarity, int color, Set<IRefinement> refinements) {
            super(rarity, color, refinements);
        }

        public VampireRefinementSet(Rarity rarity, int color, IRefinement... refinements) {
            super(rarity, color, refinements);
        }

        @Nonnull
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    }
}
