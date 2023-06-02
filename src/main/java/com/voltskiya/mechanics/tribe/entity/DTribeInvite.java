package com.voltskiya.mechanics.tribe.entity;

import com.voltskiya.mechanics.database.BaseEntity;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tribe_invite")
public class DTribeInvite extends BaseEntity {

    @Id
    protected UUID id;
    @Column(nullable = false)
    protected UUID fromInvite;
    @Column(nullable = false, unique = true)
    protected UUID toInvite;
    @ManyToOne(optional = false)
    protected DTribe tribe;

    public DTribeInvite(UUID fromInvite, UUID toInvite, DTribe tribe) {
        this.fromInvite = fromInvite;
        this.toInvite = toInvite;
        this.tribe = tribe;
    }
}
