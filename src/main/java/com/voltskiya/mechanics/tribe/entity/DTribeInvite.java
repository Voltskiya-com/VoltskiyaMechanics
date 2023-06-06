package com.voltskiya.mechanics.tribe.entity;

import com.voltskiya.mechanics.database.BaseEntity;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
