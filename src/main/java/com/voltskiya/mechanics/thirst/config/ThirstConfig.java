package com.voltskiya.mechanics.thirst.config;

import com.google.gson.Gson;
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
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ThirstConfig {

    private final List<ThirstEffect> effects = new ArrayList<>();
    private final Map<Material, Integer> consumables = new HashMap<>();

    private int thirstRate = 1;
    private static ThirstConfig instance;

    public ThirstConfig() {
        instance = this;
    }

    public static ThirstConfig get() {
        return instance;
    }

    @SneakyThrows
    public static void load() {
        try (FileReader reader = new FileReader(getFile())) {
            instance = new Gson().fromJson(reader, ThirstConfig.class);
        } catch (IOException e) {
            instance = new ThirstConfig();
            instance.consumables.put(Material.POTION, 300);
            instance.effects.add(new ThirstEffect(PotionEffectType.HUNGER, 0, 0, 200));
            instance.effects.add(new ThirstEffect(PotionEffectType.WITHER, 0, 0, 100));
        }
        save(); // save no matter what in case we make changes
    }

    private static File getFile() {
        return ThirstModule.get().getFile("ThirstConfig.json");
    }

    private static void save() {
        File file = getFile();
        try (FileWriter writer = new FileWriter(file)) {
            new Gson().toJson(instance, writer);
        } catch (IOException e) {
            VoltskiyaPlugin.get().getSLF4JLogger().error("Unable to write to " + file.getPath(), e);
        }
    }


    public int getConsumeAmount(Material material) {
        return consumables.getOrDefault(material, 0);
    }

    public List<PotionEffect> getPotionEffects(int thirstLevel) {
        System.out.println(thirstLevel);
        return this.effects.stream().filter(effect -> effect.shouldActivate(thirstLevel))
            .map(ThirstEffect::potion).toList();
    }

    public int getThirstRate() {
        return thirstRate;
    }
}
