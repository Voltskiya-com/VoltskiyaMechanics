package com.voltskiya.mechanics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerHud {

    public static @NotNull BossBar bossBar = Bukkit.createBossBar("Air", BarColor.BLUE, BarStyle.SOLID);

    public static void watchAir(Player player) {
        bossBar.addPlayer(player);
        Bukkit.getScheduler().runTaskTimer(VoltskiyaPlugin.get(), () -> {
            double air = player.getRemainingAir() / (double) player.getMaximumAir();
            if (air != 1) {
                bossBar.addPlayer(player);
                bossBar.setProgress(Math.max(0, air));
            } else
                bossBar.removePlayer(player);
        }, 0, 1);
    }

    public static void updateDisplay(Player player, double thirstPercentage, double staminaPercentage) {
        AttributeInstance armorAttribute = player.getAttribute(Attribute.GENERIC_ARMOR);
        int armorValue = (int) (armorAttribute == null ? 0 : armorAttribute.getValue());

        StringBuilder armorStr = new StringBuilder();
        for (char c : String.format("%02d", armorValue).toCharArray())
            armorStr.append((char) (c + '\uF010' - '0'));//shifted numbers

        Component thirst = new Bar("\uF003", "\uF004", "\uF005", thirstPercentage).display(false);
        Component stamina = new Bar("\uF006", "\uF007", "\uF008", staminaPercentage).display(true);
        TextComponent armor1 = Component.text(armorStr.substring(0, 1));
        TextComponent armorIcon = Component.text("\uF002");
        TextComponent armor2 = Component.text(armorStr.substring(1));
        TextComponent backwardsSpace = Component.text("\uF001".repeat(2));
        Component armor = (armor1).append(backwardsSpace).append(armorIcon).append(backwardsSpace)
            .append(armor2);
        player.sendActionBar(
            thirst.append(Component.space())
                    .append(armor)
                    .append(Component.space())
                    .append(stamina));

    }

    private record Bar(String emptyChar, String halfChar, String fullChar, double percentage) {

        private Component display(boolean isReversed) {
            int fullChars = (int) Math.round(percentage * 20);
            boolean isOdd = fullChars % 2 == 1;
            int emptyChars = (20 - fullChars) / 2;
            fullChars /= 2;
            String empty = emptyChar.repeat(fullChars);
            String half = isOdd ? halfChar : "";
            String full = fullChar.repeat(emptyChars);
            return Component.text(isReversed ? full + half + empty : empty + half + full);
        }
    }
}
