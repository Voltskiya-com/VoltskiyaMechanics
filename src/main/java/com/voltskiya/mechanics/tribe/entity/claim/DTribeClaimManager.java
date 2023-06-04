package com.voltskiya.mechanics.tribe.entity.claim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

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

    public DTribeClaimManager(DTribe tribe) {
        this.tribe = tribe;
    }

    private static ProtectedRegion allTribeParentRegion(RegionManager regionManager) {
        ProtectedRegion region = regionManager.getRegion("-tribe");
        if (region != null) return region;
        region = new GlobalProtectedRegion("-tribe");
        regionManager.addRegion(region);
        return region;
    }

    @NotNull
    private static RegionManager getRegionManger(Chunk chunk) {
        World world = BukkitAdapter.adapt(chunk.getWorld());
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);
        if (regionManager == null) throw new IllegalStateException("World does not exist!");
        return regionManager;
    }

    public boolean canClaimChunk() {
        return getSize() < MAX_CLAIM_CHUNKS;
    }

    private int getSize() {
        return this.chunks.size();
    }

    public DTribe getTribe() {
        return tribe;
    }

    public void claim(Chunk chunk) {
        RegionManager regionManager = getRegionManger(chunk);
        ProtectedCuboidRegion region = tribeRegion(chunk, regionManager);
        ApplicableRegionSet preexisting = regionManager.getApplicableRegions(region);
        if (preexisting.size() != 0) {
            throw new IllegalArgumentException("All or part of this chunk have been claimed!");
        }
        regionManager.addRegion(region);
        new DTribeClaim(chunk, this).save();
    }

    @NotNull
    private ProtectedCuboidRegion tribeRegion(Chunk chunk, RegionManager regionManager) {
        ProtectedRegion parent = tribeParentRegion(regionManager);
        Location minLoc = chunk.getBlock(0, chunk.getWorld().getMinHeight(), 0).getLocation();
        Location maxLoc = chunk.getBlock(15, chunk.getWorld().getMaxHeight(), 15).getLocation();
        String regionName = regionName(chunk.getChunkKey());
        BlockVector3 min = BukkitAdapter.asBlockVector(minLoc);
        BlockVector3 max = BukkitAdapter.asBlockVector(maxLoc);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, min, max);
        try {
            region.setParent(parent);
        } catch (CircularInheritanceException e) {
            throw new RuntimeException(e);
        }
        return region;
    }

    private ProtectedRegion tribeParentRegion(RegionManager regionManager) {
        ProtectedRegion parentRegion = regionManager.getRegion(regionName());
        if (parentRegion != null) return parentRegion;

        parentRegion = new GlobalProtectedRegion(regionName());
        parentRegion.getMembers().addGroup(regionName());
        try {
            parentRegion.setParent(allTribeParentRegion(regionManager));
        } catch (CircularInheritanceException e) {
            throw new RuntimeException(e);
        }
        return parentRegion;
    }

    @NotNull
    private String regionName(long extra) {
        return regionName() + "-" + extra;
    }

    private String regionName() {
        return "tribe-" + getTribe().getTag();
    }

    public List<DTribeClaim> getClaimsList() {
        return List.copyOf(this.chunks);
    }

    public void unclaim(DTribeClaim claim) {
        Chunk chunk = claim.getChunk();
        if (chunk == null) {
            throw new IllegalStateException("Chunk " + claim.getChunkKey() + " does not exist!");
        }
        String regionName = regionName(claim.getChunkKey());
        getRegionManger(chunk).removeRegion(regionName);
        claim.delete();
    }
}
