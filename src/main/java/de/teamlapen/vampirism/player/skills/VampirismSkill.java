package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Extension of {@link DefaultSkill} with vampirism default unloc names/descriptions
 */
public abstract class VampirismSkill<T extends IFactionPlayer<T>> extends DefaultSkill<T> {
    private Supplier<Component> description = () -> null;
    private Consumer<T> activate = (T player) -> {
    };
    private Consumer<T> deactivate = (T player) -> {
    };

    @Override
    public Component getDescription() {
        return description.get();
    }

    public VampirismSkill<T> setDescription(Supplier<Component> descriptionIn) {
        this.description = descriptionIn;
        return this;
    }

    /**
     * Enable description using "text.vampirism.skill."+getID()+".desc" as unloc key
     */
    public VampirismSkill<T> setHasDefaultDescription() {
        description = () -> Component.translatable(getTranslationKey() + ".desc");
        return this;
    }

    public VampirismSkill<T> setToggleActions(Consumer<T> activateIn, Consumer<T> deactivateIn) {
        this.activate = activateIn;
        this.deactivate = deactivateIn;
        return this;
    }

    @Override
    protected void onDisabled(T player) {
        deactivate.accept(player);
        super.onDisabled(player);
    }

    @Override
    protected void onEnabled(T player) {
        activate.accept(player);
        super.onEnabled(player);
    }

    /**
     * Simple hunter skill implementation. Does nothing by itself
     */
    public static class SimpleHunterSkill extends VampirismSkill<IHunterPlayer> {

        /**
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleHunterSkill(boolean desc) {
            if (desc) this.setHasDefaultDescription();
        }

        @Nonnull
        @Override
        public Optional<IPlayableFaction<?>> getFaction() {
            return Optional.of(VReference.HUNTER_FACTION);
        }
    }


    /**
     * Simple vampire skill implementation. Does nothing by itself
     */
    public static class SimpleVampireSkill extends VampirismSkill<IVampirePlayer> {
        /**
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleVampireSkill(boolean desc) {
            if (desc) setHasDefaultDescription();
        }

        @Nonnull
        @Override
        public Optional<IPlayableFaction<?>> getFaction() {
            return Optional.of(VReference.VAMPIRE_FACTION);
        }
    }
}
