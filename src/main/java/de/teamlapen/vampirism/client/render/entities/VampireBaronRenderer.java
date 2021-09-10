package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.BaronModel;
import de.teamlapen.vampirism.client.model.BaronWrapperModel;
import de.teamlapen.vampirism.client.model.BaronessModel;
import de.teamlapen.vampirism.client.render.layers.BaronAttireLayer;
import de.teamlapen.vampirism.client.render.layers.WingsLayer;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class VampireBaronRenderer extends MobRenderer<VampireBaronEntity, BaronWrapperModel> {

    private static final ResourceLocation textureLord = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baron.png");
    private static final ResourceLocation textureLady = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baroness.png");
    private static final ResourceLocation textureLordEnraged = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baron_enraged.png");
    private static final ResourceLocation textureLadyEnraged = new ResourceLocation(REFERENCE.MODID + ":textures/entity/baroness_enraged.png");


    public VampireBaronRenderer(EntityRendererProvider.Context context) {
        super(context, new BaronWrapperModel(new BaronModel(context.bakeLayer(ModEntitiesRender.BARON)), new BaronessModel(context.bakeLayer(ModEntitiesRender.BARONESS))), 0.5F);
        this.addLayer(new WingsLayer<>(this, context.getModelSet(), vampireBaronEntity -> true, (entity, model) -> model.getBodyPart(entity)));
        this.addLayer(new BaronAttireLayer(this, context, VampireBaronEntity::isLady));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(VampireBaronEntity entity) {
        return entity.isEnraged() ? (entity.isLady() ? textureLadyEnraged : textureLordEnraged) : (entity.isLady() ? textureLady : textureLord);
    }

}