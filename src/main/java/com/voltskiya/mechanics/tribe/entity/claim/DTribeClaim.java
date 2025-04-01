package com.voltskiya.mechanics.tribe.entity.claim;


import com.voltskiya.mechanics.database.BaseEntity;
import com.voltskiya.mechanics.tribe.entity.DTribe;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "tribe_claim")
public class DTribeClaim extends BaseEntity {

    @Id
    protected UUID id;
    @Column(nullable = false)
    protected long chunkKey;
    @Column(nullable = false)
    protected UUID world;

    @ManyToOne(optional = false)
    private DTribeClaimManager claims;

    public DTribeClaim(Chunk chunk, DTribeClaimManager claims) {
        this.chunkKey = chunk.getChunkKey();
        this.world = chunk.getWorld().getUID();
        this.claims = claims;
    }

    public DTribe getTribe() {
        return getClaims().getTribe();
    }

    public DTribeClaimManager getClaims() {
        return claims;
    }

    public long getChunkKey() {
        return this.chunkKey;
    }

    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    @Nullable
    public Chunk getChunk() {
        @Nullable World world = this.getWorld();
        if (world == null) return null;
        long chunkId = this.getChunkKey();
        return world.getChunkAt(chunkId);
    }
}
