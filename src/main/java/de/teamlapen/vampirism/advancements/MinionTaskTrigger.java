package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;

public class MinionTaskTrigger extends SimpleCriterionTrigger<MinionTaskTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "minion_tasks");

    public static Instance tasks(IMinionTask<?, ?> task) {
        return new Instance(task);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, IMinionTask<?, ?> task) {
        this.trigger(player, instance -> instance.test(task));
    }

    @Nonnull
    @Override
    protected Instance createInstance(@Nonnull JsonObject json, @Nonnull EntityPredicate.Composite entityPredicate, @Nonnull DeserializationContext conditionsParser) {
        IMinionTask<?, ?> task = ModRegistries.MINION_TASKS.getValue(new ResourceLocation(json.get("action").getAsString()));
        if (task != null) {
            return new Instance(task);
        } else {
            throw new IllegalArgumentException("Could not deserialize minion trigger");
        }
    }

    static class Instance extends AbstractCriterionTriggerInstance {
        @Nonnull
        private final IMinionTask<?, ?> task;

        Instance(@Nonnull IMinionTask<?, ?> task) {
            super(ID, EntityPredicate.Composite.ANY);
            this.task = task;
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(@Nonnull SerializationContext serializer) {
            JsonObject json = super.serializeToJson(serializer);
            json.addProperty("action", task.getRegistryName().toString());
            return json;
        }

        boolean test(IMinionTask<?, ?> action) {
            return this.task == action;
        }
    }
}
