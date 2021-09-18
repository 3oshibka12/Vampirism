package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.container.HunterTrainerContainer;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Gui for the Hunter Trainer interaction
 */
@OnlyIn(Dist.CLIENT)
public class HunterTrainerScreen extends AbstractContainerScreen<HunterTrainerContainer> {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_trainer.png");
    private Button buttonLevelup;

    public HunterTrainerScreen(HunterTrainerContainer inventorySlotsIn, Inventory playerInventory, Component name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void init() {
        super.init();
        Component name = new TranslatableComponent("text.vampirism.level_up");
        this.addRenderableWidget(this.buttonLevelup = new Button(this.leftPos + 120, this.topPos + 24, this.font.width(name) + 5, 20, name, (context) -> {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TRAINERLEVELUP, ""));
            Player player = Minecraft.getInstance().player;
            UtilLib.spawnParticles(player.getCommandSenderWorld(), ParticleTypes.ENCHANT, player.getX(), player.getY(), player.getZ(), 1, 1, 1, 100, 1);
            player.playSound(SoundEvents.NOTE_BLOCK_HARP, 4.0F, (1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F) * 0.7F);
            this.onClose();
        }));
        this.buttonLevelup.active = false;
    }

    @Override
    public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);

    }

    @Override
    protected void containerTick() {
        if (menu.hasChanged() || this.minecraft.player.getRandom().nextInt(40) == 6) {
            buttonLevelup.active = menu.canLevelup();
        }
    }

    @Override
    protected void renderBg(@Nonnull PoseStack stack, float var1, int var2, int var3) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, altarGuiTextures);
        this.blit(stack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack stack, int par1, int par2) {
        super.renderLabels(stack, par1, par2);

        Component text = null;
        if (!menu.getMissingItems().isEmpty()) {
            ItemStack missing = menu.getMissingItems();
            Component item = missing.getItem() instanceof HunterIntelItem ? ((HunterIntelItem) missing.getItem()).getCustomName() : new TranslatableComponent(missing.getDescriptionId());
            text = new TranslatableComponent("text.vampirism.hunter_trainer.ritual_missing_items", missing.getCount(), item);
        }
        if (text != null) this.font.drawWordWrap(text, 8, 50, this.imageWidth - 10, 0x000000);
    }
}
