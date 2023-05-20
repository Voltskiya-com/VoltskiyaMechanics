package com.voltskiya.mechanics.food.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Ticks;

public class FoodItemLore {

    private static final List<FoodItemLore> UNITS = new ArrayList<>();
    private static final TextColor GRAY_COLOR = TextColor.color(0x595959);

    static {
//        UNITS.add(new FoodItemLore(ChronoUnit.DAYS, TextColor.color(0x6196cf)));
        UNITS.add(new FoodItemLore(ChronoUnit.HOURS, TextColor.color(0x61aacf)));
        UNITS.add(new FoodItemLore(ChronoUnit.MINUTES, TextColor.color(0x61b2cf)));
        UNITS.add(new FoodItemLore(ChronoUnit.SECONDS, TextColor.color(0x61c2cf)));
    }

    private final ChronoUnit unit;
    private final TextColor color;
    private final String single;
    private final String plural;

    public FoodItemLore(ChronoUnit unit, TextColor color) {
        this.unit = unit;
        this.plural = unit.toString();
        this.single = plural.substring(0, plural.length() - 1);
        this.color = color;
    }

    public static synchronized List<Component> makeLore(long ticks) {
        long ticksSecondRemainder = ticks % Ticks.TICKS_PER_SECOND;
        if (ticksSecondRemainder != 0) ticks = ticks - ticksSecondRemainder + Ticks.TICKS_PER_SECOND;
        Duration duration = Ticks.duration(ticks);
        int truncate = 3;
        List<Component> loreParts = new ArrayList<>(truncate);
        for (FoodItemLore loreUnit : UNITS) {
            long time = duration.dividedBy(loreUnit.unit.getDuration());
            if (time == 0) continue;
            duration = duration.minus(time, loreUnit.unit);
            loreParts.add(loreUnit.component(time));
        }
        JoinConfiguration separator = JoinConfiguration.separator(Component.text(", ", GRAY_COLOR));
        Component lorePartsJoined = Component.join(separator, loreParts);
        Component lore = Component.text("Spoils in ", GRAY_COLOR).append(lorePartsJoined);
        return List.of(lore);
    }

    private Component component(long time) {
        TextComponent timeText = Component.text(time, Style.style(this.color, TextDecoration.BOLD));
        TextComponent nameText = Component.text(time == 1 ? this.single : this.plural);
        return Component.join(JoinConfiguration.noSeparators(), timeText, nameText);
    }

}
