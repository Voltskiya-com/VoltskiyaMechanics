package com.voltskiya.mechanics.physical.player;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBarDisplay {

    public static final String AIR_BOSS_BAR_TITLE = "Air";
    public static final String AIR_BOSS_BAR_KEY = "air.";
    public static final String TEMPERATURE_BOSS_BAR_KEY = "temperature.";
    public static final String WETNESS_BOSS_BAR_KEY = "wetness.";
    private static final Bar thirstBar = new Bar("\uf003", "\uf004", "\uf005", false);
    private static final Bar staminaBar = new Bar("\uf006", "\uf007", "\uf008", true);

    private Player player;
    private KeyedBossBar bossBarAir;
    private KeyedBossBar bossBarTemperature;

    public void updateDisplay(double thirstPercentage, double staminaPercentage, double temperature, double wetness) {
        actionBar(thirstPercentage, staminaPercentage);
        updateAir();
        statusEffects(temperature);
    }

    private void statusEffects(double temperature) {
        double temperaturePercentage = Math.min(1, Math.max(0, temperature / 200 + .5));

        int characterIndex = (int) Math.round(temperaturePercentage * 10); // between 0 and 10
        String temperatureChar = String.valueOf((char) ('\uF020' + characterIndex));

        TextColor color = temperatureColor(temperaturePercentage);
        TextComponent temperatureSymbol = Component.text(temperatureChar, color);
        String serialized = LegacyComponentSerializer.legacySection().serialize(temperatureSymbol);

        bossBarTemperature.setVisible(false);
        bossBarTemperature.setVisible(true);

        this.bossBarTemperature.addPlayer(player);
        this.bossBarTemperature.setProgress(temperaturePercentage);
        this.bossBarTemperature.setTitle(serialized);
    }

    @NotNull
    private TextColor temperatureColor(double percentage) {
        int red = (int) (0xff * percentage);
        int green = 0;
        int blue = (int) (0xff * percentage);
        return TextColor.color(red, green, blue);
    }

    private void actionBar(double thirstPercentage, double staminaPercentage) {
        StringBuilder armorStr = calcArmorStr();

        Component thirstDisplay = thirstBar.display(thirstPercentage);
        Component staminaDisplay = staminaBar.display(staminaPercentage);
        TextComponent armor1 = Component.text(armorStr.substring(0, 1));
        TextComponent armorIcon = Component.text("\uf002");
        TextComponent armor2 = Component.text(armorStr.substring(1));
        TextComponent backwardsSpace = Component.text("\uf001".repeat(2));
        Component armor = armor1.append(backwardsSpace).append(armorIcon).append(backwardsSpace).append(armor2);
        Component actionBar = thirstDisplay
            .append(Component.space())
            .append(armor)
            .append(Component.space())
            .append(staminaDisplay);
        player.sendActionBar(actionBar);
    }

    @NotNull
    private StringBuilder calcArmorStr() {
        AttributeInstance armorAttribute = player.getAttribute(Attribute.GENERIC_ARMOR);
        int armorValue = null == armorAttribute ? 0 : (int) armorAttribute.getValue();
        StringBuilder armorStr = new StringBuilder();
        for (char c : String.format("%02d", armorValue).toCharArray())
            armorStr.append((char) (c + '\uf010' - 48));
        return armorStr;
    }

    public void onLoad(Player player) {
        this.player = player;

        NamespacedKey temperatureKey = VoltskiyaPlugin.get().namespacedKey(TEMPERATURE_BOSS_BAR_KEY + player.getUniqueId());
        this.bossBarTemperature = Bukkit.createBossBar(temperatureKey, "Temperature", BarColor.YELLOW, BarStyle.SOLID);

        NamespacedKey airKey = VoltskiyaPlugin.get().namespacedKey(AIR_BOSS_BAR_KEY + player.getUniqueId());
        this.bossBarAir = Bukkit.createBossBar(airKey, "Air", BarColor.BLUE, BarStyle.SOLID);
    }

    private void updateAir() {
        double air = player.getRemainingAir() / (double) player.getMaximumAir();
        if (1 == air) bossBarAir.removePlayer(player);
        else {
            bossBarAir.addPlayer(player);
            bossBarAir.setProgress(Math.max(0, air));
        }
    }

    public void remove() {
        bossBarAir.removePlayer(this.player);
        bossBarTemperature.removePlayer(this.player);

        Bukkit.removeBossBar(bossBarAir.getKey());
        bossBarTemperature.removeAll();
        Bukkit.removeBossBar(this.bossBarTemperature.getKey());
    }

    public void onChangeGameMode() {
        bossBarAir.removePlayer(this.player);
        bossBarTemperature.removePlayer(this.player);
    }

    private record Bar(String emptyChar, String halfChar, String fullChar, boolean isReversed) {

        private Component display(double percentage) {
            percentage = Math.min(1, Math.max(0, percentage));
            int fullChars = (int) Math.round(percentage * 20.0D);
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
