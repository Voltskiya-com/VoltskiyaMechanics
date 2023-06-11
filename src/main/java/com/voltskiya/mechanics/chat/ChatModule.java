package com.voltskiya.mechanics.chat;

import com.voltskiya.lib.AbstractModule;

public class ChatModule extends AbstractModule {

    private static ChatModule instance;

    public ChatModule() {
        instance = this;
    }

    public static ChatModule get() {
        return instance;
    }

    @Override
    public void enable() {
        new PlayerChatEvent();
    }

    @Override
    public String getName() {
        return "Chat";
    }
}

