package com.voltskiya.mechanics.chat;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;

public class PlayerChatEvent implements Listener {
    public static final Component MESSAGE_PREFIX = Component.text().append(Component.text("BetterChat ", NamedTextColor.AQUA), Component.text(">> ", NamedTextColor.DARK_GRAY)).build();
    public static final TextComponent CHAT_SEPARATOR = Component.text(" >> ", NamedTextColor.DARK_GRAY);

    public PlayerChatEvent() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncChatEvent event) {
        if(!event.isAsynchronous()) return;

        Player player = event.getPlayer();
        if(!player.isOnline()) return;

        event.setCancelled(true);

        sendChatMessageBecauseFUCKMICROSOFT(event.getPlayer(), event.getPlayer().displayName(), event.message(), Audience.audience(Bukkit.getOnlinePlayers()));
    }

    public Component constructPrefixComponent(Player player) {
        LuckPerms luckPerms = VoltskiyaPlugin.get().getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);


        ArrayList<PrefixNode> prefixNodes = new ArrayList<>(user.resolveInheritedNodes(NodeType.PREFIX, QueryOptions.nonContextual()));
        if(prefixNodes.size() < 1) return Component.empty();

        prefixNodes.sort(Comparator.comparingInt(PrefixNode::getPriority));

        String prefix = prefixNodes.get(prefixNodes.size() - 1).getKey().split("\\.")[2];

        return EnhancedLegacyText.get().parse(prefix);
    }

    private Component constructSuffixComponent(Player player) {
        LuckPerms luckPerms = VoltskiyaPlugin.get().getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);


        ArrayList<SuffixNode> suffixNodes = new ArrayList<>(user.resolveInheritedNodes(NodeType.SUFFIX, QueryOptions.nonContextual()));
        if(suffixNodes.size() < 1) return Component.empty();

        suffixNodes.sort(Comparator.comparingInt(SuffixNode::getPriority));

        String suffix = suffixNodes.get(suffixNodes.size() - 1).getKey().split("\\.")[2];

        return EnhancedLegacyText.get().parse(suffix);
    }

    private void sendChatMessageBecauseFUCKMICROSOFT(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        Component prefix = constructPrefixComponent(source);
        Component suffix = constructSuffixComponent(source);


        String displayNamePlain = PlainTextComponentSerializer.plainText().serialize(sourceDisplayName);

        String messagePlain = PlainTextComponentSerializer.plainText().serialize(message);
        if(!message.hasStyling()) {
            message = EnhancedLegacyText.get().parse(messagePlain);
        }

        //Component component = Component.join(JoinConfiguration.separator(CHAT_SEPARATOR), prefix.append(sourceDisplayName).append(suffix), message);
        Component component = Component.text().append(prefix, sourceDisplayName, suffix, CHAT_SEPARATOR, message).build();

        viewer.sendMessage(component);

        VoltskiyaPlugin.get().getLogger().log(Level.INFO, displayNamePlain + " >> " + messagePlain);
    }

}
