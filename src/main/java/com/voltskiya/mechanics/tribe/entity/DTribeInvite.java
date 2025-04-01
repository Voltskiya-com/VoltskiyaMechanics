package com.voltskiya.mechanics.tribe.entity;

import com.voltskiya.mechanics.database.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@Entity
@Table(name = "tribe_invite", uniqueConstraints = {@UniqueConstraint(columnNames = {"to_invite", "tribe_id"})})
public class DTribeInvite extends BaseEntity {

    @Id
    protected UUID id;
    @Column(nullable = false)
    protected UUID fromInvite;
    @Column(nullable = false)
    protected UUID toInvite;
    @ManyToOne(optional = false)
    protected DTribe tribe;

    public DTribeInvite(UUID fromInvite, UUID toInvite, DTribe tribe) {
        this.fromInvite = fromInvite;
        this.toInvite = toInvite;
        this.tribe = tribe;
    }

    public OfflinePlayer getTargetPlayer() {
        return Bukkit.getOfflinePlayer(this.toInvite);
    }
}
