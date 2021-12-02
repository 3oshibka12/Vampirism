package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;

import javax.annotation.Nonnull;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public interface IFaction<T extends IFactionEntity> {

    /**
     * If not set returns white
     */
    ChatFormatting getChatColor();

    /**
     * Used for some rendering, e.g. for displaying the level
     */
    int getColor();

    /**
     * @return The interface all entities of this faction implement (or for players the IExtendedEntityProperty) implements
     */
    Class<T> getFactionEntityInterface();


    /**
     * @return Unique key of this faction
     */
    ResourceLocation getID();

    Component getName();

    /**
     * Preferably a TextComponentTranslation
     */
    Component getNamePlural();

    /**
     * Gets Village Totem related utility class
     *
     * @return the village data class
     */
    @Nonnull
    IFactionVillage getVillageData();

    boolean isEntityOfFaction(PathfinderMob creature);

    /**
     * @return Whether entities of this faction are hostile towards neutral entities
     */
    boolean isHostileTowardsNeutral();

}
