package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.ProgressBar;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.tileentity.GarlicBeaconTileEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GarlicBeaconScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/garlic_beacon.png");
    protected final int xSize = 220;
    protected final int ySize = 114;
    private final GarlicBeaconTileEntity tile;
    protected int guiLeft;
    protected int guiTop;
    protected ProgressBar startupBar;
    protected ProgressBar fueledTimer;

    public GarlicBeaconScreen(GarlicBeaconTileEntity tile, Component title) {
        super(title);
        this.tile = tile;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);

        this.renderGuiBackground(mStack);

        this.drawTitle(mStack);

        super.render(mStack, mouseX, mouseY, partialTicks);

    }

    @Override
    public void tick() {
        startupBar.setProgress(tile.getBootProgress());
        float f = tile.getFueledState();
        if (f == 0) {
            fueledTimer.active = false;
        } else {
            fueledTimer.active = true;
            fueledTimer.setProgress(f);
        }

    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        startupBar = this.addButton(new ProgressBar(this, this.guiLeft + (xSize - 170) / 2, this.guiTop + 30, 170, new TranslatableComponent("gui.vampirism.garlic_beacon.startup")));
        startupBar.setColor(0xD0D0FF);
        startupBar.setFGColor(0xFFFFFF);

        fueledTimer = this.addButton(new ProgressBar(this, this.guiLeft + (xSize - 170) / 2, this.guiTop + 60, 170, new TranslatableComponent("gui.vampirism.garlic_beacon.fueled")));
        fueledTimer.setColor(0xD0FFD0);
        fueledTimer.setFGColor(0xFFFFFF);
    }

    protected void renderGuiBackground(PoseStack mStack) {
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(mStack, this.guiLeft, this.guiTop, this.getBlitOffset(), 0, 0, this.xSize, this.ySize, 256, 256);
    }

    private void drawTitle(PoseStack mStack) {
        this.font.drawShadow(mStack, title, this.guiLeft + 15, this.guiTop + 5, 0xFFFFFFFF);
    }
}
