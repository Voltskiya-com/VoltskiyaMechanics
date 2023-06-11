package com.voltskiya.mechanics.physical.thirst.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.PhysicalModule;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Cleanup;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public class ThirstConfig {

    private static ThirstConfig instance;
    private final List<ThirstEffect> thirstyEffects = new ArrayList<>();
    private final Map<String, ThirstConfig.ConsumableItemConfig> consumables = new HashMap<>();
    private final Map<Material, Integer> defaultConsumables = new EnumMap<>(Material.class);
    private final List<DirtyWaterPotionEffect> dirtyWaterEffects = new ArrayList<>();
    private final int thirstRate = 1;

    public static ThirstConfig get() {
        return instance;
    }

    public static void load() {
        try {
            @Cleanup
            FileReader reader = new FileReader(getFile());

            instance = gson().fromJson(reader, ThirstConfig.class);
        } catch (IOException ignored) {
            instance = new ThirstConfig();
            instance.getDefaultConsumables().put(Material.POTION, 300);
            instance.getThirstyEffects().add(new ThirstEffect(PotionEffectType.HUNGER, 0, 0, 200));
            instance.getThirstyEffects().add(new ThirstEffect(PotionEffectType.WITHER, 0, 0, 0));
            instance.getDirtyWaterEffects().add(new DirtyWaterPotionEffect(PotionEffectType.POISON, 0, 100));
            instance.getDirtyWaterEffects().add(new DirtyWaterPotionEffect(PotionEffectType.CONFUSION, 0, 160));
        }
        save();
    }

    private static Gson gson() {
        return GsonComponentSerializer.gson().populator().apply((new GsonBuilder()).setPrettyPrinting()).create();
    }

    private static File getFile() {
        return PhysicalModule.get().getThirstFile("ThirstConfig.json");
    }

    private static void save() {
        File file = getFile();

        try {
            @Cleanup
            FileWriter writer = new FileWriter(file);
            gson().toJson(instance, ThirstConfig.class, writer);
        } catch (IOException var6) {
            VoltskiyaPlugin.get().getSLF4JLogger().error("Unable to write to {}", file.getPath(), var6);
        }

    }

    public static ConsumableItemConfig get(Item item) {
        return get().getConsumables().get(item.getId());
    }

    public static ThirstConfig.ConsumableItemConfig compute(String id, boolean isDirty, int uses) {
        ThirstConfig.ConsumableItemConfig config = get().getConsumables().get(id);
        if (null != config)
            return config;
        config = ThirstConfig.ConsumableItemConfig.createDefault(id, isDirty, uses);
        get().getConsumables().put(id, config);
        save();
        return config;
    }

    public static int getMaterialConsumeAmount(Material material) {
        return get().getDefaultConsumables().getOrDefault(material, 0);
    }

    public List<PotionEffect> getPotionEffects(int thirstLevel) {
        return getThirstyEffects()
            .stream()
            .filter(effect -> effect.shouldActivate(thirstLevel))
            .map(ThirstEffect::potion)
            .toList();
    }

    public int getThirstRate() {
        return 1;
    }

    public List<PotionEffect> getDirtyEffects() {
        return getDirtyWaterEffects().stream().map(DirtyWaterPotionEffect::potion).toList();
    }

    @Getter
    public static class ConsumableItemConfig extends Item.ItemConfig {

        private static final NamespacedKey USES_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.consumable.used_count");
        private int uses = 1;
        private int consumeAmount = 300;

        public ConsumableItemConfig(String id, int texture, Component name, List<Component> lore, int uses, int consumeAmount) {
            super(id, texture, name, lore);
            this.uses = uses;
            this.consumeAmount = consumeAmount;
        }

        public static ThirstConfig.ConsumableItemConfig createDefault(String id, boolean isDirty, int uses) {
            return new ThirstConfig.ConsumableItemConfig(id, 0, Component.text(id), Collections.emptyList(), uses,
                isDirty ? -100 : 300);
        }

        public static int getUses(VoltskiyaItemStack itemStack) {
            return itemStack.getKeyOrDefault(USES_KEY, PersistentDataType.INTEGER, 0);
        }

        public static void setUses(VoltskiyaItemStack itemStack, int uses) {
            itemStack.setKey(USES_KEY, PersistentDataType.INTEGER, uses);
        }

        public Item.Tag<?, ?>[] getTags() {
            return new Item.Tag[]{new Item.Tag<>(USES_KEY, PersistentDataType.INTEGER, uses)};
        }

        public int getConsumeAmount() {
            return consumeAmount;
        }
    }
}
