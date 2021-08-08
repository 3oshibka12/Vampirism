package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class CuredVampireVillagerTrigger extends SimpleCriterionTrigger<CuredVampireVillagerTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "cured_vampire_villager");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, ConvertedVillagerEntity vampire, Villager villager) {
        LootContext lootcontext = EntityPredicate.createContext(player, vampire);
        LootContext lootcontext1 = EntityPredicate.createContext(player, villager);
        this.trigger(player, (instance) -> instance.test(lootcontext, lootcontext1));
    }

    @Nonnull
    @Override
    protected Instance createInstance(@Nonnull JsonObject json, @Nonnull EntityPredicate.Composite entityPredicate, @Nonnull DeserializationContext conditionsParser) {
        EntityPredicate.Composite vampire = EntityPredicate.Composite.fromJson(json, "vampire", conditionsParser);
        EntityPredicate.Composite villager = EntityPredicate.Composite.fromJson(json, "villager", conditionsParser);
        return new Instance(entityPredicate, vampire, villager);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public static Instance any() {
            return new Instance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
        }
        private final EntityPredicate.Composite vampire;
        private final EntityPredicate.Composite villager;

        public Instance(EntityPredicate.Composite player, EntityPredicate.Composite vampire, EntityPredicate.Composite villager) {
            super(ID, player);
            this.vampire = vampire;
            this.villager = villager;
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(@Nonnull SerializationContext conditions) {
            JsonObject json = super.serializeToJson(conditions);
            json.add("vampire", this.vampire.toJson(conditions));
            json.add("villager", this.villager.toJson(conditions));
            return json;
        }

        public boolean test(LootContext vampire, LootContext villager) {
            if (!this.vampire.matches(vampire)) {
                return false;
            } else {
                return this.villager.matches(villager);
            }
        }
    }
}
