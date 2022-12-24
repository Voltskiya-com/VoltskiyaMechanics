package com.voltskiya.mechanics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class Display {
    private static final Bar thirstBar = new Bar("\uf003", "\uf004", "\uf005", false);
    private static final Bar staminaBar = new Bar("\uf006", "\uf007", "\uf008", true);

    private final Player player;

    public void updateDisplay(double thirstPercentage, double staminaPercentage) {
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

    public void watchAir() {
        BossBar bossBar = Bukkit.createBossBar("Air", BarColor.BLUE, BarStyle.SOLID);
        bossBar.addPlayer(player);
        Bukkit.getScheduler().runTaskTimer(VoltskiyaPlugin.get(), () -> {
            double air = player.getRemainingAir() / (double) player.getMaximumAir();
            if (1 == air) bossBar.removePlayer(player);
            else bossBar.setProgress(Math.max(0, air));
        }, 0L, 1L);
    }

}
