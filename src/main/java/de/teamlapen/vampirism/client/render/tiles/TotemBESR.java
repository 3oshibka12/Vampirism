package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TotemBESR extends VampirismBESR<TotemBlockEntity> {


    private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation(REFERENCE.MODID, "textures/entity/totem_beam.png");
    private final static int HEIGHT = 100;

    public TotemBESR(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(TotemBlockEntity te, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        float textureScale = te.shouldRenderBeam();
        if (textureScale > 0.0f) {
            long totalWorldTime = te.getLevel().getGameTime();
            int captureProgress = te.getCaptureProgress();
            float[] baseColors = te.getBaseColors();
            int offset = 0;
            if (captureProgress > 0) {
                float[] overtakeColors = te.getCapturingColors();
                offset = (captureProgress * HEIGHT) / 100;
                BeaconRenderer.renderBeaconBeam(matrixStack, iRenderTypeBuffer, TEXTURE_BEACON_BEAM, partialTicks, textureScale, totalWorldTime, 0, offset, overtakeColors, 0.2f, 0.25f);
            }
            BeaconRenderer.renderBeaconBeam(matrixStack, iRenderTypeBuffer, TEXTURE_BEACON_BEAM, partialTicks, textureScale, totalWorldTime, offset, HEIGHT - offset, baseColors, 0.2f, 0.25f);
        } else {
            IFaction<?> faction = te.getControllingFaction();
            if (faction != null) {
                renderFactionName(faction, matrixStack, iRenderTypeBuffer, i);
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@Nonnull TotemBlockEntity te) {
        return true;
    }

    private void renderFactionName(IFaction<?> faction, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int packedLight) {
        Component displayNameIn = faction.getNamePlural().plainCopy().withStyle(style -> style.withColor((faction.getChatColor())));
        matrixStack.pushPose();
        matrixStack.translate(0.5, 1, 0.5);
        matrixStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        matrixStack.scale(-0.025f, -0.025f, -0.025f);
        Matrix4f matrix4f = matrixStack.last().pose();
        float f1 = 0; //Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25f);
        int j = (int) (f1 * 255f) << 24;
        Font font = Minecraft.getInstance().font;
        float nameOffset = (float) (-font.width(displayNameIn) / 2);
        font.drawInBatch(displayNameIn, nameOffset, 0, 553648127, false, matrix4f, iRenderTypeBuffer, true, j, packedLight);
        font.drawInBatch(displayNameIn, nameOffset, 0, -1, false, matrix4f, iRenderTypeBuffer, true, 0, packedLight);
        matrixStack.popPose();
    }


}
