package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Item's implementing this can only be used by players that match the requirements.
 * Currently, only affects {@link Player#attack(Entity)} and {@link Player#startUsingItem(InteractionHand)}
 */
public interface IFactionLevelItem<T extends IFactionPlayer> { //TODO 1.17 extend IFactionExclusiveItem and modify

    @SuppressWarnings("RedundantCast")
    @OnlyIn(Dist.CLIENT)
    default void addFactionLevelToolTip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, @Nullable Player player) {
        ChatFormatting factionC = ChatFormatting.DARK_RED;
        ChatFormatting levelC = ChatFormatting.DARK_RED;
        ChatFormatting skillC = ChatFormatting.DARK_RED;

        LazyOptional<IFactionPlayerHandler> playerHandler = player != null && player.isAlive() ? VampirismAPI.getFactionPlayerHandler(player) : LazyOptional.empty();

        IPlayableFaction usingFaction = getUsingFaction(stack);
        ISkill requiredSkill = getRequiredSkill(stack);
        int reqLevel = getMinLevel(stack);
        if ((Boolean) playerHandler.map(p -> p.isInFaction(usingFaction)).orElse(false)) {
            factionC = ChatFormatting.GREEN;
            if (playerHandler.map(IFactionPlayerHandler::getCurrentLevel).orElse(0) >= reqLevel) {
                levelC = ChatFormatting.GREEN;
            }
            if ((Boolean) playerHandler.map(IFactionPlayerHandler::getCurrentFactionPlayer).flatMap(a -> a.map(b -> b.getSkillHandler().isSkillEnabled(requiredSkill))).orElse(false)) {
                skillC = ChatFormatting.GREEN;
            }
        }


        if (usingFaction == null && getMinLevel(stack) == 0) return;
        MutableComponent string = new TextComponent("").append(usingFaction == null ? new TranslatableComponent("text.vampirism.all") : usingFaction.getNamePlural()).withStyle(factionC);
        if (getMinLevel(stack) > 0) {
            string.append(new TextComponent("@" + getMinLevel(stack)).withStyle(levelC));
        }
        tooltip.add(string);
        ISkill reqSkill = this.getRequiredSkill(stack);
        if (reqSkill != null) {
            tooltip.add(new TranslatableComponent("text.vampirism.required_skill", reqSkill.getName()).withStyle(skillC));
        }
    }

    /**
     * @return The level the player has to be to use this item
     */
    int getMinLevel(@Nonnull ItemStack stack);

    /**
     * @return The skill required to use this or null if none
     */
    @Nullable
    ISkill getRequiredSkill(@Nonnull ItemStack stack);

    /**
     * @return The faction that can use this item or null if any
     */
    @Nullable
    IPlayableFaction<T> getUsingFaction(@Nonnull ItemStack stack);
}
