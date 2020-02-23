package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBasicVampire extends RenderBiped<EntityBasicVampire> {

    private static final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire1.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire3.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire4.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire5.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire6.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire7.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire8.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire9.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire10.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire11.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire12.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire13.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire14.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire15.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire16.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire17.png"),

    };

    public static ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    public RenderBasicVampire(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPlayer(0, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBasicVampire entity) {
        return getVampireTexture(entity.getEntityId());
    }
}
