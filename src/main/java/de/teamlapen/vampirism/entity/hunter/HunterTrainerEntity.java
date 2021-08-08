package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Optional;

import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class HunterTrainerEntity extends HunterBaseEntity implements ForceLookEntityGoal.TaskOwner, ICaptureIgnore {
    private static final Component name = new TranslatableComponent("container.huntertrainer");

    public static AttributeSupplier.Builder getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.ATTACK_DAMAGE, 19)
                .add(Attributes.MOVEMENT_SPEED, 0.17)
                .add(Attributes.FOLLOW_RANGE, 5);
    }
    private final int MOVE_TO_RESTRICT_PRIO = 3;
    private Player trainee;
    private boolean shouldCreateHome;

    public HunterTrainerEntity(EntityType<? extends HunterTrainerEntity> type, Level world) {
        super(type, world, false);
        saveHome = true;
        hasArms = true;
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);

        this.setDontDropEquipment();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("createHome", this.shouldCreateHome);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (trainee != null && !(trainee.containerMenu instanceof HunterTrainerContainer)) {
            this.trainee = null;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    /**
     * @return The player which has the trainings gui open.
     */
    @Nonnull
    @Override
    public Optional<Player> getForceLookTarget() {
        return Optional.ofNullable(trainee);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("createHome") && (this.shouldCreateHome = nbt.getBoolean("createHome"))) {
            if (this.getRestrictCenter().equals(BlockPos.ZERO)) {
                restrictTo(this.blockPosition(), 5);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && getHome() == null;
    }

    @Override
    public void setHome(AABB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (tryCureSanguinare(player)) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            int lvl = VampirismPlayerAttributes.get(player).hunterLevel;
            if (!this.level.isClientSide && lvl > 0) {
                int levelCorrect = HunterLevelingConf.instance().isLevelValidForTrainer(lvl + 1);
                if (levelCorrect == 0) {
                    if (trainee == null) {
                        player.openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) -> new HunterTrainerContainer(id, playerInventory, this), name));
                        this.trainee = player;
                        this.getNavigation().stop();
                    } else {
                        player.sendMessage(new TranslatableComponent("text.vampirism.i_am_busy_right_now"), Util.NIL_UUID);
                    }

                } else if (levelCorrect == -1) {
                    player.sendMessage(new TranslatableComponent("text.vampirism.hunter_trainer.trainer_level_wrong"), Util.NIL_UUID);
                } else {
                    player.sendMessage(new TranslatableComponent("text.vampirism.hunter_trainer.trainer_level_to_high"), Util.NIL_UUID);
                }

            }

            return InteractionResult.SUCCESS;
        }


        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 13F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<PathfinderMob>(this, PathfinderMob.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }


}
