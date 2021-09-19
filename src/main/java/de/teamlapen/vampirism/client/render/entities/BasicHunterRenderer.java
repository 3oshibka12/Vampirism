package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.render.layers.CloakLayer;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterRenderer extends DualBipedRenderer<BasicHunterEntity, BasicHunterModel<BasicHunterEntity>> {

    private static final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_cloak.png");

    private final Pair<ResourceLocation, Boolean> textureDefault = Pair.of(new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png"), false);
    private final Pair<ResourceLocation, Boolean>[] textures;

    public BasicHunterRenderer(EntityRendererProvider.Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER_SLIM), true), 0.5F);
        this.addLayer(new HunterEquipmentLayer<>(this, context.getModelSet(), entity -> (entity.getLevel() < 2 || entity.isCrossbowInMainhand()) ? HunterEquipmentModel.StakeType.ONLY : HunterEquipmentModel.StakeType.FULL, entity -> entity.getLevel() == 0 ? HunterEquipmentModel.HatType.from(entity.getEntityTextureType() % 3) : HunterEquipmentModel.HatType.HAT1));
        this.addLayer(new CloakLayer<>(this, textureCloak, entity -> entity.getLevel() > 0));
        textures = gatherTextures("textures/entity/hunter", true);
    }


    @Override
    protected Pair<ResourceLocation, Boolean> determineTextureAndModel(BasicHunterEntity entity) {
        int level = entity.getLevel();
        if (level > 0) return textureDefault;
        return textures[entity.getEntityTextureType() % textures.length];
    }
}
