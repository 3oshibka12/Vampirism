package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class NameSwordScreen extends Screen {
    /**
     * Suggested sword names.
     * Credits to http://www.fantasynamegenerators.com
     */
    private final static String[] sword_names = new String[]{"Felthorn", "Aetherius", "Agatha", "Alpha", "Amnesia", "Anduril", "Apocalypse", "Armageddon", "Arondite", "Ashrune", "Betrayal", "Betrayer", "Blackout", "Blazefury", "Blazeguard", "Blinkstrike", "Bloodquench", "Bloodweep", "Brutality", "Celeste", "Chaos", "Cometfell", "Convergence", "Darkheart", "Dawn", "Dawnbreaker", "Deathbringer", "Deathraze", "Decimation", "Desolation", "Destiny's Song", "Dirge", "Doomblade", "Doombringer", "Draughtbane", "Due Diligence", "Echo", "Eclipse", "Endbringer", "Epilogue", "Espada", "Extinction", "Faithkeeper", "Fate", "Fleshrender", "Florance", "Frenzy", "Fury", "Ghost Reaver", "Ghostwalker", "Gladius", "Glimmer", "Godslayer", "Grasscutter", "Gutrender", "Hatred's Bite", "Heartseeker", "Heartstriker", "Hell's Scream", "Hellfire", "Piece Maker", "Hellreaver", "Honor's Call", "Hope's End", "Infamy", "Interrogator", "Justifier", "Kinslayer", "Klinge", "Knightfall", "Lament", "Lazarus", "Lifedrinker", "Light's Bane", "Lightbane", "Lightbringer", "Lightning", "Limbo", "Loyalty", "Malice", "Mangler", "Massacre", "Mercy", "Misery", "Mournblade", "Narcoleptic", "Needle", "Nethersbane", "Night's Edge", "Night's Fall", "Nightbane", "Nightcrackle", "Nightfall", "Nirvana", "Oathbreaker", "Oathkeeper", "Oblivion", "Omega", "Orenmir", "Peacekeeper", "Persuasion", "Prick", "Purifier", "Rage", "Ragespike", "Ragnarok", "Reckoning", "Reign", "Remorse", "Requiem", "Retirement", "Rigormortis", "Savagery", "Scalpel", "Scar", "Seethe", "Severance", "Shadow Strike", "Shadowsteel", "Silence", "Silencer", "Silver Saber", "Silverlight", "Skullcrusher", "Slice of Life", "Soul Reaper", "Soulblade", "Soulrapier", "Spada", "Spike", "Spineripper", "Spiteblade", "Stalker", "Starshatterer", "Sting", "Stinger", "Storm", "Storm Breaker", "Stormbringer", "Stormcaller", "Story-Weaver", "Striker", "Sun Strike", "Suspension", "Swan Song", "The Ambassador", "The Black Blade", "The End", "The Facelifter", "The Light", "The Oculus", "The Stake", "The Untamed", "The Unyielding", "The Void", "Thorn", "Thunder", "Toothpick", "Tranquility", "Treachery", "Trinity", "Tyrhung", "Unending Tyranny", "Unholy Might", "Valkyrie", "Vanquisher", "Vengeance", "Venom", "Venomshank", "Warmonger", "Widow Maker", "Willbreaker", "Winterthorn", "Wit's End", "Witherbrand", "Wolf", "Worldbreaker", "Worldslayer"};
    private final Component yes;
    private final Component no;
    private final List<FormattedCharSequence> listLines = new ArrayList<>();
    private final Component text1;
    private final Component text2;
    private final ItemStack sword;
    private EditBox nameField;

    public NameSwordScreen(ItemStack sword) {
        super(new TranslatableComponent("gui.vampirism.name_sword.title"));
        this.yes = new TranslatableComponent("gui.yes");
        this.no = new TranslatableComponent("gui.no");
        this.text1 = new TranslatableComponent("gui.vampirism.name_sword.title");
        this.text2 = new TranslatableComponent("gui.vampirism.name_sword.text");
        this.sword = sword;
    }

    @Override
    public void init() {
        super.init();
        this.addButton(new OptionButton(this.width / 2 - 155, this.height / 6 + 96, 150, 20, Option.AMBIENT_OCCLUSION, this.yes, (context) -> {
            if (!StringUtils.isBlank(nameField.getValue())) {
                NameSwordScreen.this.sword.setHoverName(new TextComponent(nameField.getValue()));
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, nameField.getValue()));
            }
            this.minecraft.setScreen(null);
            this.minecraft.setWindowActive(true);
        }));
        this.addButton(new OptionButton(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, Option.AMBIENT_OCCLUSION, this.no, (context) -> {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, VampirismVampireSword.DO_NOT_NAME_STRING));
            this.minecraft.setScreen(null);
            this.minecraft.setWindowActive(true);
        }));

        this.listLines.clear();
        this.listLines.addAll(this.font.split(this.text2, this.width - 50));

        this.nameField = new EditBox(this.font, this.width / 2 - 155 + 77, this.height / 6 + 70, 155, 20, new TextComponent("text_sword"));
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(true);
        this.nameField.setMaxLength(35);
        this.nameField.setValue(sword_names[new Random().nextInt(sword_names.length)]);
        this.children.add(nameField);
        this.setInitialFocus(nameField);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.text1, this.width / 2, 70, 16777215);
        int i = 90;
        for (FormattedCharSequence s : this.listLines) {
            font.draw(stack, s, (float) this.width / 2 - (float) font.width(s) / 2.0F, (float) i, 16777215);
            i += this.font.lineHeight;
        }
        this.nameField.render(stack, mouseX, mouseY, partialTicks);


        super.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableLighting();
        RenderSystem.disableBlend();
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String text = nameField.getValue();
        super.resize(p_resize_1_, p_resize_2_, p_resize_3_); //Text gets deleted as this calls init again
        nameField.setValue(text);
    }

    @Override
    public void tick() {
        nameField.tick();
    }
}
