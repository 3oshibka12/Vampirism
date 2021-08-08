package de.teamlapen.vampirism.entity.villager;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * just {@link VillagerHostilesSensor} with a hostile map without zombies
 */
public class VampireVillagerHostilesSensor extends VillagerHostilesSensor {
    public static final Map<EntityType<?>, Float> hostiles;

    static {
        //Adding entries will probably not work outside Dev as hasPresence is not called for some reason
        hostiles = Maps.newHashMap(VillagerHostilesSensor.ACCEPTABLE_DISTANCE_FROM_HOSTILES);
        hostiles.remove(EntityType.ZOMBIE);
        hostiles.remove(EntityType.ZOMBIE_VILLAGER);
        hostiles.remove(EntityType.DROWNED);
        hostiles.remove(EntityType.HUSK);
    }

    @Override
    public boolean isClose(@Nonnull LivingEntity villager, LivingEntity hostile) {
        //hasPresence is not checked first, so entries may not be present
        @Nullable Float f = hostiles.get(hostile.getType()); //Careful about unboxing nullpointer
        if (f == null) return false;
        return hostile.distanceToSqr(villager) <= (double) (f * f);
    }

    @Override
    public boolean isHostile(LivingEntity hostile) { //For some reason this method is not called (as it does not properly override somehow maybe) outside dev
        return hostiles.containsKey(hostile.getType());
    }

}
