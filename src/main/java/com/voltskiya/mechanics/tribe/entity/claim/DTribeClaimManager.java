package com.voltskiya.mechanics.tribe.entity.claim;

import com.voltskiya.mechanics.database.BaseEntity;
import com.voltskiya.mechanics.tribe.entity.DTribe;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.Chunk;

@Entity
@Table(name = "tribe_claim_manager")
public class DTribeClaimManager extends BaseEntity {

    private static final int MAX_CLAIM_CHUNKS = 10;
    @Id
    protected UUID id;
    @OneToOne(optional = false)
    protected DTribe tribe;
    @OneToMany(cascade = CascadeType.ALL)
    protected List<DTribeClaim> chunks = new ArrayList<>();
    private transient TribeClaimsWorldGuard worldGuard;

    public DTribeClaimManager(DTribe tribe) {
        this.tribe = tribe;
    }


    public boolean canClaimChunk() {
        return this.chunks.size() < MAX_CLAIM_CHUNKS;
    }

    public DTribe getTribe() {
        return tribe;
    }

    public void claim(Chunk chunk) {
        this.worldGuard().claim(chunk);
        new DTribeClaim(chunk, this).save();
    }


    public List<DTribeClaim> getClaimsList() {
        return List.copyOf(this.chunks);
    }

    public void unclaim(DTribeClaim claim) {
        this.worldGuard().unclaim(claim);
        claim.delete();
    }

    public void removeMember(UUID player) {
        this.worldGuard().removeMember(player);
    }

    public void addMember(UUID player) {
        this.worldGuard().addMember(player);
    }

    private TribeClaimsWorldGuard worldGuard() {
        if (this.worldGuard != null) return this.worldGuard;
        return this.worldGuard = new TribeClaimsWorldGuard(this);
    }
}
