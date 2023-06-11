package com.voltskiya.mechanics.chat;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.query.TribeStorage;
import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerChatEvent implements Listener {

    public static final TextComponent CHAT_SEPARATOR = Component.text(" >> ", NamedTextColor.DARK_GRAY);

    public PlayerChatEvent() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncChatEvent event) {
        if (!event.isAsynchronous()) return;

        Player player = event.getPlayer();
        if (!player.isOnline()) return;

        //String tribeTag = TribeStorage.findPlayer(player.getUniqueId()).getTribe().getTag();
        //Component tribeTagComponent = Component.text().append(Component.text(" [", NamedTextColor.DARK_GRAY), Component.text
        // (tribeTag, NamedTextColor.AQUA), Component.text("]", NamedTextColor.DARK_GRAY)).build();

        Component tribeTagComponent = null;

        DTribeMember tribeMember = TribeStorage.findPlayer(player.getUniqueId());
        if (tribeMember != null) {
            tribeTagComponent = Component.text().append(Component.text(" [", NamedTextColor.DARK_GRAY),
                    Component.text(tribeMember.getTribe().getTag(), NamedTextColor.AQUA), Component.text("]",
                        NamedTextColor.DARK_GRAY))
                .build();
        }

        event.setCancelled(true);

        sendChatMessageBecauseFUCKMICROSOFT(event.getPlayer(), event.getPlayer().displayName(), event.message(),
            Audience.audience(Bukkit.getOnlinePlayers()), tribeTagComponent);
    }

    public Component constructPrefixComponent(Player player) {
        LuckPerms luckPerms = VoltskiyaPlugin.get().getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        ArrayList<PrefixNode> prefixNodes = new ArrayList<>(user.resolveInheritedNodes(NodeType.PREFIX, QueryOptions.nonContextual()));
        if (prefixNodes.size() < 1) return Component.empty();

        prefixNodes.sort(Comparator.comparingInt(PrefixNode::getPriority));

        String prefix = prefixNodes.get(prefixNodes.size() - 1).getKey().split("\\.")[2];

        return EnhancedLegacyText.get().parse(prefix);
    }

    private Component constructSuffixComponent(Player player) {
        LuckPerms luckPerms = VoltskiyaPlugin.get().getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        ArrayList<SuffixNode> suffixNodes = new ArrayList<>(user.resolveInheritedNodes(NodeType.SUFFIX, QueryOptions.nonContextual()));
        if (suffixNodes.size() < 1) return Component.empty();

        suffixNodes.sort(Comparator.comparingInt(SuffixNode::getPriority));

        String suffix = suffixNodes.get(suffixNodes.size() - 1).getKey().split("\\.")[2];

        return EnhancedLegacyText.get().parse(suffix);
    }

    private Component constructNameComponent(Player player) {
        LuckPerms luckPerms = VoltskiyaPlugin.get().getLuckPerms();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        Optional<MetaNode> optional = user.getNodes(NodeType.INHERITANCE).stream()
            .map(inheritanceNode -> luckPerms.getGroupManager().getGroup(inheritanceNode.getGroupName()))
            .flatMap((Function<Group, Stream<MetaNode>>) group -> {
                if (group == null) return Stream.empty();
                return group.getNodes(NodeType.META).stream();
            })
            .filter(node -> node.getMetaKey().equals("namecolor"))
            .findAny();

        if (optional.isEmpty()) return Component.text(player.getName());

        String nameColorNode = optional.get().getMetaValue();

        return EnhancedLegacyText.get().buildComponent(nameColorNode + player.getName()).build();
    }

    private void sendChatMessageBecauseFUCKMICROSOFT(@NotNull Player source, @NotNull Component sourceDisplayName,
        @NotNull Component message, @NotNull Audience viewer, Component tribeTag) {
        Component prefix = constructPrefixComponent(source);
        Component suffix = constructSuffixComponent(source);
        Component displayName = constructNameComponent(source);

        String displayNamePlain = PlainTextComponentSerializer.plainText().serialize(sourceDisplayName);

        String messagePlain = PlainTextComponentSerializer.plainText().serialize(message);
        if (!message.hasStyling()) {
            message = EnhancedLegacyText.get().parse(messagePlain);
        }

        Component component = Component.text().append(prefix, displayName, suffix, CHAT_SEPARATOR, message).build();
        //Component component = Component.join(JoinConfiguration.separator(CHAT_SEPARATOR), prefix.append(sourceDisplayName).append
        // (suffix), message);
        if (tribeTag != null) {
            component = Component.text().append(prefix, displayName, tribeTag, suffix, CHAT_SEPARATOR, message).build();
        }

        viewer.sendMessage(component);

        VoltskiyaPlugin.get().getLogger().log(Level.INFO, displayNamePlain + " >> " + messagePlain);
    }

}
