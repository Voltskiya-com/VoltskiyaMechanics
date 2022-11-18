package com.voltskiya.mechanics.thirst.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.thirst.ThirstModule;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirstConfig {

    private final List<ThirstEffect> effects = new ArrayList<>();
    private final Map<String, ConsumableItemConfig> consumables = new HashMap<>();
    private final Map<Material, ConsumableItemConfig> defaultConsumables = new HashMap<>();

    private final int thirstRate = 1;
    private static ThirstConfig instance;

    public static ThirstConfig get() {
        return instance;
    }

    @SneakyThrows
    public static void load() {
        try (FileReader reader = new FileReader(getFile())) {
            instance = gson().fromJson(reader, ThirstConfig.class);
        } catch (IOException e) {
            instance = new ThirstConfig();
            instance.effects.add(new ThirstEffect(PotionEffectType.HUNGER, 0, 0, 200));
            instance.effects.add(new ThirstEffect(PotionEffectType.WITHER, 0, 0, 0));
        }
        save(); // save no matter what in case we make changes
    }

    private static Gson gson() {
        return GsonComponentSerializer.gson().populator().apply(new GsonBuilder()).create();
    }

    private static File getFile() {
        return ThirstModule.get().getFile("ThirstConfig.json");
    }

    private static void save() {
        File file = getFile();
        try (FileWriter writer = new FileWriter(file)) {
            gson().toJson(instance, ThirstConfig.class, writer);
        } catch (IOException e) {
            VoltskiyaPlugin.get().getSLF4JLogger().error("Unable to write to " + file.getPath(), e);
        }
    }

    public List<PotionEffect> getPotionEffects(int thirstLevel) {
        return this.effects.stream().filter(effect -> effect.shouldActivate(thirstLevel))
            .map(ThirstEffect::potion).toList();
    }

    public int getThirstRate() {
        return thirstRate;
    }

    public static ConsumableItemConfig get(String id) {
        return get().consumables.get(id);
    }

    public static class ConsumableItemConfig extends Item.ItemConfig {
        public static final NamespacedKey USES_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.consumable.used_count");
        public static final NamespacedKey CONSUME_AMOUNT_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.consumable.consume_amount");
        private final int uses;
        private final int consumeAmount;

        public ConsumableItemConfig(int texture, Component name, List<Component> lore, int uses, int consumeAmount) {
            super(texture, name, lore);
            this.uses = uses;
            this.consumeAmount = consumeAmount;
        }

        @Override
        public Item.Tag[] getTags() {
            return new Item.Tag[] {new Item.Tag(USES_KEY, uses), new Item.Tag(CONSUME_AMOUNT_KEY, consumeAmount)};
        }
    }
}
