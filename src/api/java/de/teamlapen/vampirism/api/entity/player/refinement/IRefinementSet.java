package de.teamlapen.vampirism.api.entity.player.refinement;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public interface IRefinementSet extends IForgeRegistryEntry<IRefinementSet> {

    int getColor();

    @Nonnull
    IFaction<?> getFaction();

    @Nonnull
    Component getName();

    @Nonnull
    Rarity getRarity();

    @Nonnull
    Set<IRefinement> getRefinements();

    /**
     * @return The accessory type this can be on, or empty if all
     */
    Optional<IRefinementItem.AccessorySlotType> getSlotType();

    enum Rarity {
        COMMON(4, ChatFormatting.WHITE),
        UNCOMMON(3, ChatFormatting.GREEN),
        RARE(3, ChatFormatting.BLUE),
        EPIC(2, ChatFormatting.DARK_PURPLE),
        LEGENDARY(1, ChatFormatting.GOLD);

        public final int weight;
        public final ChatFormatting color;

        Rarity(int weight, ChatFormatting color) {
            this.weight = weight;
            this.color = color;
        }
    }
}
