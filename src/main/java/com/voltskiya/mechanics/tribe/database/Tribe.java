package com.voltskiya.mechanics.tribe.database;

import apple.utilities.database.HasFilename;
import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDTyped;
import com.voltskiya.mechanics.tribe.TribeIOService;
import com.voltskiya.mechanics.tribe.TribeModule;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Tribe implements HasFilename {

    private static AppleAJDTyped<Tribe> manager;
    private static Map<String, Tribe> tribesByName = new HashMap<>();
    private static Map<UUID, Tribe> tribesByUUID = new HashMap<>();
    private static Map<UUID, Tribe> tribesByMember = new HashMap<>();
    private UUID uuid;
    private TribeMeta meta;
    private Map<UUID, TribeMember> members;

    private Tribe(TribeMeta meta) {
        this.uuid = UUID.randomUUID();
        this.meta = meta;
        this.members = new HashMap<>();
    }

    public Tribe() {
    }

    public static void createAndRegister(String name, Player leaderPlayer) {
        TribeMeta meta = TribeMeta.createAndValidate(name);
        Tribe tribe = new Tribe(meta);
        UUID leaderUUID = leaderPlayer.getUniqueId();
        TribeMember leader = new TribeMember(leaderUUID);
        leader.role = TribeRole.LEADER;
        tribe.members.put(leaderUUID, leader);
    }

    public static void loadAll() {
        manager = AppleAJD.createTyped(Tribe.class, TribeModule.get().getFile("data"), TribeIOService.taskCreator());
        for (Tribe tribe : manager.loadFolderNow()) {
            tribesByName.put(tribe.meta.name, tribe);
            tribesByUUID.put(tribe.uuid, tribe);
            tribesByMember.putAll(tribe.members.keySet().stream().collect(Collectors.toMap(m -> m, m -> tribe)));
        }
    }

    @Nullable
    public static Tribe findPlayer(UUID player) {
        return tribesByMember.get(player);
    }

    @Override
    public String getSaveFileName() {
        return uuid + ".json";
    }
}
