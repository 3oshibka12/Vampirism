package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.util.EnumSet;

public class DefendLeaderGoal extends TargetGoal {
    private final BasicVampireEntity entity;
    private LivingEntity attacker;
    private int timestamp;

    public DefendLeaderGoal(BasicVampireEntity basicVampire) {
        super(basicVampire, false);
        this.entity = basicVampire;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        IEntityLeader leader = this.entity.getAdvancedLeader();
        if (leader == null) {
            return false;
        } else {
            this.attacker = leader.getRepresentingEntity().getLastHurtByMob();
            int i = leader.getRepresentingEntity().getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
        }

    }

    public void start() {
        this.mob.setTarget(this.attacker);
        IEntityLeader leader = this.entity.getAdvancedLeader();
        if (leader != null) {
            this.timestamp = leader.getRepresentingEntity().getLastHurtByMobTimestamp();
        }

        super.start();
    }
}