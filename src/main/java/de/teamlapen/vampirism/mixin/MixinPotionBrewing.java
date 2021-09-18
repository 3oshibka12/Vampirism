package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.effects.VampirismPotion;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class MixinPotionBrewing {

    @Inject(method = "hasContainerMix", at = @At("HEAD"), cancellable = true)
    private static void handleItemConversionHunterPotion(ItemStack input, ItemStack reagent, CallbackInfoReturnable<Boolean> cir) {
        if (shouldBlockBrewing_vampirism(input, reagent)) {

            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private static void handleDoReactionHunterPotion(ItemStack reagent, ItemStack potionIn, CallbackInfoReturnable<ItemStack> cir) {
        if (shouldBlockBrewing_vampirism(potionIn, reagent)) {
            cir.setReturnValue(potionIn);
            cir.cancel();
        }
    }

    private static boolean shouldBlockBrewing_vampirism(ItemStack input, ItemStack reagent) {
        return VampirismPotion.isHunterPotion(input, true).map(Potion::getEffects).flatMap(effects -> effects.stream().map(MobEffectInstance::getEffect).filter(MobEffect::isBeneficial).findAny()).isPresent();

    }
}
