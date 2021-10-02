package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class SleepInMultiplayerModScreen extends InBedChatScreen {

    private final String leaveText;

    public SleepInMultiplayerModScreen(String text) {
        this.leaveText = text;
    }

    @Override
    protected void init() {
        super.init();
        GuiEventListener l = this.children().get(1);
        if (l instanceof AbstractWidget) {
            ((AbstractWidget) l).setMessage(new TranslatableComponent(leaveText));
        }
    }
}
