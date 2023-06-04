package com.voltskiya.mechanics.tribe.command;

import com.voltskiya.mechanics.tribe.entity.DTribe;
import com.voltskiya.mechanics.tribe.entity.claim.ClaimStorage;
import com.voltskiya.mechanics.tribe.entity.claim.DTribeClaim;
import com.voltskiya.mechanics.tribe.entity.claim.DTribeClaimManager;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface TribeCommandClaim extends TribeCommandUtil {

    default void claim(Player player, DTribeMember member) {
        if (checkClaimPerms(player, member)) return;
        DTribe tribe = member.getTribe();
        DTribeClaimManager claims = tribe.getClaims();
        if (!claims.canClaimChunk()) {
            red(player, "Claim limit reached!");
            return;
        }
        DTribeClaim existingClaim = ClaimStorage.findClaim(player.getChunk().getChunkKey());
        if (existingClaim != null) {
            red(player, "This chunk has already been claimed by %s".formatted(existingClaim.getTribe().getName()));
            return;
        }
        try {
            claims.claim(player.getChunk());
        } catch (IllegalArgumentException e) {
            red(player, e.getMessage());
            return;
        }
        aqua(player, "Successfully claimed chunk for %s".formatted(tribe.getName()));
    }

    private boolean checkClaimPerms(Player player, DTribeMember member) {
        if (member == null) {
            noTribeMessage(player);
            return true;
        }
        if (!member.getRole().canClaim()) {
            red(player, "You do not have permission to claim chunks");
            return true;
        }
        return false;
    }

    default void unclaim(Player player, DTribeMember member, Long chunkId) {
        if (checkClaimPerms(player, member)) return;
        if (chunkId == null) chunkId = player.getChunk().getChunkKey();

        DTribeClaim claim = ClaimStorage.findClaim(chunkId);
        if (claim == null) {
            red(player, "Your tribe has not claimed this area");
            return;
        }
        if (!claim.getTribe().equals(member.getTribe())) {
            red(player, "This is claimed by " + claim.getTribe().getName());
            return;
        }
        claim.getClaims().unclaim(claim);
        aqua(player, "Successfully unclaimed chunk for %s".formatted(member.getTribe().getName()));
    }

    default void claimList(Player player, DTribeMember member) {
        if (member == null) {
            noTribeMessage(player);
            return;
        }
        List<DTribeClaim> claimsList = member.getTribe().getClaims().getClaimsList();
        if (claimsList.isEmpty()) {
            red(player, "Your tribe has not claimed anything!");
            return;
        }
        List<Component> message = new ArrayList<>(claimsList.size());
        for (DTribeClaim claim : claimsList) {
            Chunk chunk = claim.getChunk();
            if (chunk == null) continue;
            Block block = chunk.getBlock(7, 0, 7);
            String coordsString = "[%d %d]".formatted(block.getX(), block.getZ());
            Component prefix = Component.text("Claimed chunk at ", TextColor.color(0x828282));
            Component coords = Component.text(coordsString, TextColor.color(0xc2e3f2));
            ClickEvent clickEvent = ClickEvent.clickEvent(Action.SUGGEST_COMMAND, "/tribe claims unclaim " + chunk.getChunkKey());
            Component delete = Component.text(" [X]", TextColor.color(Color.RED.getRGB())).clickEvent(clickEvent);
            message.add(Component.join(JoinConfiguration.noSeparators(), prefix, coords, delete));
        }
        player.sendMessage(Component.join(JoinConfiguration.newlines(), message));
    }
}
