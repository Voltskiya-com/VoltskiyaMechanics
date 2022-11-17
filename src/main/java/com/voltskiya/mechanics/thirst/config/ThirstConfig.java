package com.voltskiya.mechanics.thirst.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.thirst.ThirstModule;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class ThirstConfig {

    private final List<ThirstEffect> effects = new ArrayList<>();
    private final Map<String, ThirstConsumableConfig> consumables = new HashMap<>();
    private final Map<Material, ThirstConsumableConfig> defaultConsumables = new HashMap<>();

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

    @Nullable
    public ThirstConsumableConfig getConsumable(String id) {
        return this.consumables.get(id);
    }

    @Nullable
    public ThirstConsumableConfig getConsumable(ItemStack item) {
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        @Nullable String id = dataContainer.get(ThirstConsumableConfig.THIRST_CONSUMALBE_KEY,
            PersistentDataType.STRING);
        ThirstConsumableConfig consumable = this.consumables.get(id);
        if (consumable != null)
            return consumable;
        return this.defaultConsumables.get(item.getType());
    }

    public ThirstConsumableConfig createConsumable(String id) {
        ThirstConsumableConfig created = new ThirstConsumableConfig();
        this.consumables.put(id, created);
        save();
        return created;
    }
}
