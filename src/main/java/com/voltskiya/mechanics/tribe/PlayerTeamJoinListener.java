package com.voltskiya.mechanics.tribe;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.tribe.query.TribeStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class PlayerTeamJoinListener implements Listener {

    private static Team team;

    public PlayerTeamJoinListener() {
        String teamName = "player";
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        team.setAllowFriendlyFire(true);
        team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);

        VoltskiyaPlugin.get().registerEvents(this);
    }

    public static void joinPlayer(OfflinePlayer player) {
        team.addPlayer(player);
    }

    public static void leavePlayer(OfflinePlayer player) {
        team.removePlayer(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (TribeStorage.findPlayer(player.getUniqueId()) == null)
            joinPlayer(player);
    }
}
