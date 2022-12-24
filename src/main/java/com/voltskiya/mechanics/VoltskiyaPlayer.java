package com.voltskiya.mechanics;

import com.voltskiya.mechanics.stamina.StaminaConfig;
import com.voltskiya.mechanics.thirst.ThirstModule;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class VoltskiyaPlayer {
    private static final NamespacedKey DRINK_AMOUNT_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.drink_amount");
    private static final Map<Player, VoltskiyaPlayer> players = new HashMap<>();
    public static final int MAX_THIRST = 1000;
    public static final int MIN_THIRST = 0;
    public static final int MAX_STAMINA = 10_000;
    public static final int MIN_STAMINA = 0;

    private static final VoltskiyaPlayer.Bar thirstBar = new VoltskiyaPlayer.Bar("\uf003", "\uf004", "\uf005", false);
    private static final VoltskiyaPlayer.Bar staminaBar = new VoltskiyaPlayer.Bar("\uf006", "\uf007", "\uf008", true);

    private final Player player;
    private int thirst;
    private boolean isThirsty;
    private int stamina;
    private boolean outOfStamina;

    public VoltskiyaPlayer(Player player) {
        this(player, 1000, true, 1000, false);
    }

    private void update() {
        if (GameMode.SURVIVAL != player.getGameMode())
            return;

        updateStamina();
        updateThirst();
        updateDisplay();
    }

    private void updateStamina() {
        if (0 == getNMS().walkDistO)
            increaseStamina(StaminaConfig.get().getStandingStillIncrement());
    }

    private void updateThirst() {
        if (!isThirsty)
            return;
        if (MIN_THIRST < thirst)
            thirst = Math.max(MIN_THIRST, thirst - ThirstConfig.get().getThirstRate());
        List<PotionEffect> effectsToAdd = ThirstConfig.get().getPotionEffects(thirst);
        if (!effectsToAdd.isEmpty())
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> player.addPotionEffects(effectsToAdd));
    }

    public void increaseStamina(int amount) {
        stamina = Math.max(MIN_STAMINA, Math.min(MAX_STAMINA, stamina + amount));
        if (MIN_STAMINA == stamina)
            outOfStamina = true;
        else if (outOfStamina && StaminaConfig.get().getRunAgainThreshold() < stamina)
            outOfStamina = false;
    }

    private void updateDisplay() {
        updateDisplay(thirst / (double) MAX_THIRST, stamina / (double) MAX_STAMINA);
    }

    private boolean shouldDisableSprint() {
        return 100 > thirst || outOfStamina;
    }

    public void onSprint() {
        if (!shouldDisableSprint())
            return;

        var connection = ((CraftPlayer) player).getHandle().connection;
        float saturation = player.getSaturation();
        double health = player.getHealth();
        connection.send(new ClientboundSetHealthPacket((float)health, 6, saturation));
        connection.send(new ClientboundSetHealthPacket((float)health, player.getFoodLevel(), saturation));
    }

    public void resetThirst() {
        thirst = MAX_THIRST;
    }

    public boolean toggleIsThirsty() {
        return isThirsty = !isThirsty;
    }

    private void reset() {
        thirst = MAX_THIRST;
        stamina = MAX_STAMINA;
    }

    public void watchAir() {
        BossBar bossBar = Bukkit.createBossBar("Air", BarColor.BLUE, BarStyle.SOLID);
        bossBar.addPlayer(player);
        Bukkit.getScheduler().runTaskTimer(VoltskiyaPlugin.get(), () -> {
            double air = player.getRemainingAir() / (double) player.getMaximumAir();
            if (1 == air) bossBar.removePlayer(player);
            else bossBar.setProgress(Math.max(0, air));
        }, 0L, 1L);
    }

    private void updateDisplay(double thirstPercentage, double staminaPercentage) {
        AttributeInstance armorAttribute = player.getAttribute(Attribute.GENERIC_ARMOR);
        int armorValue = null == armorAttribute ? 0 : (int) armorAttribute.getValue();
        StringBuilder armorStr = new StringBuilder();
        for (char c : String.format("%02d", armorValue).toCharArray())
            armorStr.append((char) (c + '\uf010' - 48));

        Component thirstDisplay = thirstBar.display(thirstPercentage);
        Component staminaDisplay = staminaBar.display(staminaPercentage);
        TextComponent armor1 = Component.text(armorStr.substring(0, 1));
        TextComponent armorIcon = Component.text("\uf002");
        TextComponent armor2 = Component.text(armorStr.substring(1));
        TextComponent backwardsSpace = Component.text("\uf001".repeat(2));
        Component armor = armor1.append(backwardsSpace).append(armorIcon).append(backwardsSpace).append(armor2);
        player.sendActionBar(thirstDisplay.append(Component.space()).append(armor).append(Component.space()).append(staminaDisplay));
    }

    private ServerPlayer getNMS() {
        return ((CraftPlayer) player).getHandle();
    }

    @SneakyThrows
    private void savePlayer() {
        File saveFile = ThirstModule.get().getFile("players", player.getUniqueId() + ".player");
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
            if (!saveFile.createNewFile())
                log.error("Unable to create save file for {} ({})", player.getName(), player.getUniqueId());
        }


        @Cleanup
        FileWriter fileWriter = new FileWriter(saveFile, false);
        fileWriter.write(thirst);
        fileWriter.write(isThirsty ? 1 : 0);
        fileWriter.write(stamina);
        fileWriter.write(outOfStamina ? 1 : 0);
    }

    @SneakyThrows
    private static VoltskiyaPlayer loadPlayer(Player player) {
        File saveFile = ThirstModule.get().getFile("players", player.getUniqueId() + ".player");
        if (!saveFile.exists()) {
            VoltskiyaPlayer voltskiyaPlayer = new VoltskiyaPlayer(player);
            players.put(player, voltskiyaPlayer);
            return voltskiyaPlayer;
        }
        @Cleanup
        FileReader fileReader = new FileReader(saveFile);
        VoltskiyaPlayer var4;
        VoltskiyaPlayer voltskiyaPlayer = new VoltskiyaPlayer(player, fileReader.read(), 1 == fileReader.read(), fileReader.read(), 1 == fileReader.read());
        players.put(player, voltskiyaPlayer);
        fileReader.close();
        return voltskiyaPlayer;
    }

    public static synchronized void load() {
        Bukkit.getOnlinePlayers().forEach(VoltskiyaPlayer::loadPlayer);
    }

    public static synchronized void updatePlayers() {
        players.values().forEach(VoltskiyaPlayer::update);
    }

    public static synchronized void save() {
        players.values().forEach(VoltskiyaPlayer::savePlayer);
    }

    @NotNull
    public static synchronized VoltskiyaPlayer getPlayer(Player player) {
        return players.computeIfAbsent(player, VoltskiyaPlayer::new);
    }

    public static synchronized VoltskiyaPlayer join(Player player) {
        return loadPlayer(player);
    }

    public void drink(int consumeAmount, boolean isDirty) {
        if (isDirty)
            player.addPotionEffects(ThirstConfig.get().getDirtyEffects());
        thirst = Math.max(MIN_THIRST, Math.min(MAX_THIRST, thirst + consumeAmount));
    }

    public synchronized void leave() {
        Optional.ofNullable(players.remove(player)).ifPresentOrElse(VoltskiyaPlayer::savePlayer, () ->
                log.error("The unregistered player {} left the server!", player.getName()));
    }

    public static synchronized void reset(Player player) {
        Optional.ofNullable(players.get(player)).ifPresentOrElse(VoltskiyaPlayer::reset, () ->
                log.error("The unregistered player {} died!", player.getName()));
    }

    private static record Bar(String emptyChar, String halfChar, String fullChar, boolean isReversed) {
        private Component display(double percentage) {
            int fullChars = (int)Math.round(percentage * 20.0D);
            boolean isOdd = 1 == fullChars % 2;
            int emptyChars = (20 - fullChars) / 2;
            fullChars /= 2;
            String empty = emptyChar.repeat(fullChars);
            String half = isOdd ? halfChar : "";
            String full = fullChar.repeat(emptyChars);
            return Component.text(isReversed ? full + half + empty : empty + half + full);
        }
    }
}
