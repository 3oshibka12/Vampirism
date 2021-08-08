package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RefinementSetArgument implements ArgumentType<IRefinementSet> {
    public static final DynamicCommandExceptionType REFINEMENT_NOT_FOUND = new DynamicCommandExceptionType((particle) -> {
        return new TranslatableComponent("command.vampirism.argument.refinement_set.notfound", particle);
    });
    private static final Collection<String> EXAMPLES = Arrays.asList("refinement_set", "modid:refinement_set");

    public static RefinementSetArgument actions() {
        return new RefinementSetArgument();
    }

    public static IRefinementSet getSet(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, IRefinementSet.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(ModRegistries.REFINEMENT_SETS.getKeys(), builder);
    }

    @Override
    public IRefinementSet parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        IRefinementSet set = ModRegistries.REFINEMENT_SETS.getValue(id);
        if (set == null)
            throw REFINEMENT_NOT_FOUND.create(id);
        return set;
    }
}
