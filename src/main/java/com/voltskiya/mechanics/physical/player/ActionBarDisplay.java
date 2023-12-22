package com.voltskiya.mechanics.physical.player;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.temperature.Temperature;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
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
    private static final String BACKWARDS_SPACE = "\uf001";

    private Player player;
    private KeyedBossBar bossBarAir;
    private KeyedBossBar bossBarTemperature;

    @NotNull
    private static TextComponent temperatureComponent(double heatDirection, double temperaturePercentage) {
        int characterIndex = (int) Math.round(temperaturePercentage * 10); // between 0 and 10
        String temperatureChar = String.valueOf((char) ('\uF020' + characterIndex));

        double typical = TemperatureConsts.get().temperature.typicalHeatTranferRate();
        char directionSymbol = heatDirectionSymbol(heatDirection, typical);

        TextColor directionColor = temperatureColor(heatDirection / Math.abs(typical) / 2 / 2 + 0.5);
        return Component.text(temperatureChar, temperatureColor(temperaturePercentage))
            .append(Component.text(directionSymbol, directionColor));
    }

    private static char heatDirectionSymbol(double heatDirection, double typical) {
        boolean reverse = heatDirection < 0;
        double heatDirectionAbs = Math.abs(heatDirection);

        int direction;
        if (heatDirectionAbs < typical / 2)
            direction = -1; // 0
        else if (heatDirectionAbs < typical)
            direction = 0; // low
        else if (heatDirectionAbs < typical * 2)
            direction = 1; // medium
        else
            direction = 2; // high

        char directionSymbol = '\uF02B';
        if (direction == -1) return (char) (directionSymbol + 6);
        if (reverse) return (char) (directionSymbol + direction + 3);
        return (char) (directionSymbol + direction);
    }

    @NotNull
    private static TextColor temperatureColor(double percentage) {
        if (percentage < 0) percentage = 0;
        else if (percentage > 1) percentage = 1;

        Color a;
        Color b;
        if (percentage > 0.5) {
            a = Color.RED;
            b = Color.LIGHT_GRAY;
            percentage -= .5;
        } else {
            b = Color.BLUE;
            a = Color.LIGHT_GRAY;
        }
        percentage *= 2;
        double inverse = 1 - percentage;
        int red = (int) (a.getRed() * percentage + b.getRed() * inverse);
        int green = (int) (a.getGreen() * percentage + b.getGreen() * inverse);
        int blue = (int) (a.getBlue() * percentage + b.getBlue() * inverse);
        return TextColor.color(red, green, blue);
    }

    public void updateDisplay(double thirstPercentage, double staminaPercentage, Temperature temperature, double wetness) {
        actionBar(thirstPercentage, staminaPercentage);
        statusEffects(temperature);
        updateAir();
    }

    private void statusEffects(Temperature temperature) {
        double temperaturePercentage = Math.min(1, Math.max(0, temperature.getTemperature() / 200 + .5));
        Component temperatureSymbol = temperatureComponent(temperature.getLastHeatDirection(), temperaturePercentage);
        String serialized = LegacyComponentSerializer.legacySection().serialize(temperatureSymbol);

        this.bossBarTemperature.addPlayer(player);
        this.bossBarTemperature.setProgress(temperaturePercentage);
        this.bossBarTemperature.setTitle(serialized);
    }

    private void actionBar(double thirstPercentage, double staminaPercentage) {
        StringBuilder armorStr = calcArmorStr();

        Component thirstDisplay = thirstBar.display(thirstPercentage);
        Component staminaDisplay = staminaBar.display(staminaPercentage);
        TextComponent armor1 = Component.text(armorStr.substring(0, 1));
        TextComponent armorIcon = Component.text("\uf002");
        TextComponent armor2 = Component.text(armorStr.substring(1));
        TextComponent backwardsSpace = Component.text(BACKWARDS_SPACE.repeat(2));
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
        reorderBossBars(player);

        NamespacedKey temperatureKey = VoltskiyaPlugin.get().namespacedKey(TEMPERATURE_BOSS_BAR_KEY + player.getUniqueId());
        this.bossBarTemperature = Bukkit.createBossBar(temperatureKey, "Temperature", BarColor.YELLOW, BarStyle.SOLID);

        NamespacedKey airKey = VoltskiyaPlugin.get().namespacedKey(AIR_BOSS_BAR_KEY + player.getUniqueId());
        this.bossBarAir = Bukkit.createBossBar(airKey, "Air", BarColor.BLUE, BarStyle.SOLID);

    }

    private void reorderBossBars(Player player) {
        List<KeyedBossBar> bossBars = new ArrayList<>();
        Bukkit.getBossBars().forEachRemaining((bar) -> {
            if (bar.getPlayers().contains(player)) bossBars.add(bar);
        });

        // remove bars
        for (KeyedBossBar bar : bossBars) bar.removePlayer(player);

        // add them back later after the first tick
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> {
            for (KeyedBossBar bar : bossBars) bar.addPlayer(player);
        }, 1);
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
