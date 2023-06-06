package com.voltskiya.mechanics.tribe.entity;

import com.voltskiya.mechanics.database.BaseEntity;
import com.voltskiya.mechanics.tribe.PlayerTeamJoinListener;
import com.voltskiya.mechanics.tribe.entity.claim.DTribeClaimManager;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.entity.member.TribeRole;
import com.voltskiya.mechanics.tribe.query.TribeStorage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "tribe")
public class DTribe extends BaseEntity {

    public static final Comparator<DTribeMember> MEMBER_COMPARATOR = Comparator.<DTribeMember>comparingInt(
            (member) -> member.getRole().getRank())
        .thenComparingLong((member) -> member.getJoined().toEpochMilli());
    private static final Pattern TRIBE_NAME_REGEX = Pattern.compile("^.{3,40}$");
    private static final Pattern TRIBE_TAG_REGEX = Pattern.compile("^[a-zA-Z]{3,5}$");
    @Id
    protected UUID id;
    @Column(unique = true)
    protected String name;
    @Column(unique = true)
    protected String tag;
    @OneToMany(cascade = CascadeType.ALL)
    protected List<DTribeMember> members = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    protected List<DTribeInvite> issuedInvites = new ArrayList<>();
    @OneToOne(optional = false, cascade = CascadeType.ALL, mappedBy = "tribe")
    protected DTribeClaimManager claims;

    private DTribe(String name, String tag) {
        this.name = name;
        this.tag = tag;
        this.claims = new DTribeClaimManager(this);
    }

    public static DTribe createAndRegister(String name, Player leaderPlayer, String tag) {
        if (TribeStorage.findTribe(name) != null) {
            throw new IllegalArgumentException("There is already a tribe named %s".formatted(name));
        }
        if (TribeStorage.findTribeByTag(tag) != null) {
            throw new IllegalArgumentException("There is already a tribe with tag %s".formatted(tag));
        }
        DTribe tribe = new DTribe(name, tag);
        if (!TRIBE_NAME_REGEX.asMatchPredicate().test(name))
            throw new IllegalArgumentException("%s does not match regex %s".formatted(name, TRIBE_NAME_REGEX.pattern()));
        if (!TRIBE_TAG_REGEX.asMatchPredicate().test(name))
            throw new IllegalArgumentException("%s does not match regex %s".formatted(tag, TRIBE_TAG_REGEX.pattern()));
        DTribeMember leader = tribe.join(leaderPlayer);
        tribe.save();
        leader.setRole(TribeRole.LEADER);
        leader.save();
        tribe.getTeam().addPlayer(leaderPlayer);
        return tribe;
    }

    @NotNull
    public Team getTeam() {
        String teamName = "tribe." + this.getName();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        @Nullable Team team = scoreboard.getTeam(teamName);
        if (team != null) return team;
        team = scoreboard.registerNewTeam(teamName);
        team.setAllowFriendlyFire(false);
        team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
        return team;
    }

    public DTribeInvite getInvite(UUID leaderUUID) {
        for (DTribeInvite invite : this.issuedInvites) {
            if (invite.toInvite.equals(leaderUUID)) return invite;
        }
        return null;
    }

    public DTribeMember join(OfflinePlayer player) {
        getClaims().addMember(player.getUniqueId());
        DTribeMember member = new DTribeMember(player.getUniqueId(), this);
        this.members.add(member);
        PlayerTeamJoinListener.leavePlayer(player);
        getTeam().addPlayer(player);
        return member;
    }

    public String getName() {
        return this.name;
    }

    public String getTag() {
        return tag;
    }

    public void showWelcome(Player player) {
        TextComponent header = Component.text("Welcome to %s".formatted(name));
        Times timings = Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1));
        player.showTitle(Title.title(header, Component.empty(), timings));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1, 1.5f);
        announce(Component.text("%s has joined %s".formatted(player.getName(), this.name)));
    }

    public void announce(Component announcement) {
        for (DTribeMember member : this.getMembers()) {
            if (member.getPlayer() instanceof Player player) {
                player.sendMessage(announcement);
            }
        }
    }

    public void leaderLeft() {
        Optional<DTribeMember> nextLeader = this.getMembers().stream().min(MEMBER_COMPARATOR);
        if (nextLeader.isEmpty()) {
            getTeam().unregister();
            DTribeClaimManager claims = this.getClaims();
            claims.getClaimsList().forEach(claims::unclaim);
            this.delete();
        } else {
            DTribeMember member = nextLeader.get();
            member.setRole(TribeRole.LEADER);
            member.save();
        }
    }


    public List<DTribeMember> getMembers() {
        return this.members.stream().sorted(MEMBER_COMPARATOR).toList();
    }

    public List<DTribeInvite> getInvites() {
        return List.copyOf(this.issuedInvites);
    }

    @Nullable
    public DTribeMember getMember(UUID uuid) {
        return this.members.stream()
            .filter(member -> member.getPlayerId().equals(uuid))
            .findAny()
            .orElse(null);
    }

    public DTribeClaimManager getClaims() {
        return this.claims;
    }

}
