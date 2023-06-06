package com.voltskiya.mechanics.tribe.entity.member;

import com.voltskiya.mechanics.database.BaseEntity;
import com.voltskiya.mechanics.tribe.PlayerTeamJoinListener;
import com.voltskiya.mechanics.tribe.entity.DTribe;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@Entity
@Table(name = "tribe_member")
public class DTribeMember extends BaseEntity {

    @Id
    public UUID player;
    @Column
    @Enumerated
    public TribeRole role;
    @Column
    private Timestamp joined;
    @ManyToOne
    private DTribe tribe;

    public DTribeMember(UUID player, DTribe tribe) {
        this.tribe = tribe;
        this.role = TribeRole.MEMBER;
        this.player = player;
        this.joined = Timestamp.from(Instant.now());
    }

    public DTribeMember() {
    }

    public DTribe getTribe() {
        return this.tribe;
    }

    public void leave() {
        OfflinePlayer player = this.getPlayer();
        getTribe().getClaims().removeMember(this.getPlayerId());
        this.delete();
        this.tribe.getTeam().removePlayer(player);
        PlayerTeamJoinListener.joinPlayer(player);
        if (this.role.isLeader()) {
            this.tribe.leaderLeft();
        }
    }

    public UUID getPlayerId() {
        return this.player;
    }

    public TribeRole getRole() {
        return this.role;
    }

    public void setRole(TribeRole role) {
        this.role = role;
    }

    public Instant getJoined() {
        return this.joined.toInstant();
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(this.player);
    }
}
