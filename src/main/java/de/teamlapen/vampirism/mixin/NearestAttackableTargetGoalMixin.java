package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.entity.ai.goals.NearestTargetGoalModifier;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin implements NearestTargetGoalModifier {

    @Shadow protected TargetingConditions targetConditions;
    private static final Predicate<LivingEntity> nonVampireCheck = entity -> !Helper.isVampire(entity);

    @Override
    public void ignoreVampires() {
        Predicate<LivingEntity> predicate = nonVampireCheck;
        if (((TargetConditionAccessor) this.targetConditions).getSelector() != null) {
            predicate = predicate.and(((TargetConditionAccessor) this.targetConditions).getSelector());
        }
        this.targetConditions.selector(predicate);
    }
}
