package com.voltskiya.mechanics.tribe.entity.claim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class TribeClaimsWorldGuard {

    private final DTribeClaimManager claims;
    private final Set<ProtectedRegion> tribeParents = new HashSet<>();

    public TribeClaimsWorldGuard(DTribeClaimManager claims) {
        this.claims = claims;
        for (DTribeClaim claim : claims.getClaimsList()) {
            org.bukkit.World world = claim.getWorld();
            if (world != null) tribeParentRegion(getRegionManger(world));
        }
    }

    private static ProtectedRegion allTribeParentRegion(RegionManager regionManager) {
        ProtectedRegion region = regionManager.getRegion("_tribe");
        if (region != null) return region;
        region = new GlobalProtectedRegion("_tribe");
        region.setFlag(Flags.PVP, State.ALLOW);
        regionManager.addRegion(region);
        return region;
    }

    private ProtectedRegion tribeParentRegion(RegionManager regionManager) {
        ProtectedRegion parentRegion = regionManager.getRegion(regionName());
        if (parentRegion != null) {
            addParentRegion(parentRegion);
            return parentRegion;
        }

        parentRegion = new GlobalProtectedRegion(regionName());
        try {
            parentRegion.setParent(allTribeParentRegion(regionManager));
        } catch (CircularInheritanceException e) {
            throw new RuntimeException(e);
        }
        addParentRegion(parentRegion);
        return parentRegion;
    }

    private void addParentRegion(ProtectedRegion parentRegion) {
        if (tribeParents.add(parentRegion)) {
            for (DTribeMember member : claims.getTribe().getMembers()) {
                parentRegion.getMembers().addPlayer(member.getPlayerId());
            }
        }
    }

    @NotNull
    private RegionManager getRegionManger(org.bukkit.World bukkitWorld) {
        World world = BukkitAdapter.adapt(bukkitWorld);
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);
        if (regionManager == null) throw new IllegalStateException("World does not exist!");
        return regionManager;
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

    @NotNull
    private String regionName(long extra) {
        return regionName() + "-" + extra;
    }

    private String regionName() {
        return "tribe-" + claims.getTribe().getTag();
    }


    public void unclaim(DTribeClaim claim) {
        Chunk chunk = claim.getChunk();
        if (chunk == null) {
            throw new IllegalStateException("Chunk " + claim.getChunkKey() + " does not exist!");
        }
        String regionName = regionName(claim.getChunkKey());
        getRegionManger(chunk.getWorld()).removeRegion(regionName);
    }


    public void claim(Chunk chunk) {
        RegionManager regionManager = getRegionManger(chunk.getWorld());
        ProtectedCuboidRegion region = tribeRegion(chunk, regionManager);
        ApplicableRegionSet preexisting = regionManager.getApplicableRegions(region);
        if (preexisting.size() != 0) {
            throw new IllegalArgumentException("All or part of this chunk have been claimed!");
        }
        regionManager.addRegion(region);
    }

    public void removeMember(UUID player) {
        for (ProtectedRegion region : tribeParents) {
            region.getMembers().removePlayer(player);
        }
    }

    public void addMember(UUID player) {
        for (ProtectedRegion region : tribeParents) {
            region.getMembers().addPlayer(player);
        }
    }
}
