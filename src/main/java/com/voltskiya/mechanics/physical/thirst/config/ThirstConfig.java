package com.voltskiya.mechanics.physical.thirst.config;

import com.voltskiya.lib.configs.data.config.AppleConfig;
import com.voltskiya.lib.configs.data.config.init.IAppleConfigInit;
import com.voltskiya.mechanics.physical.thirst.config.effect.DirtyWaterPotionEffect;
import com.voltskiya.mechanics.physical.thirst.config.effect.ThirstEffect;
import com.voltskiya.mechanics.physical.thirst.config.item.ConsumableThirstConfig;
import com.voltskiya.mechanics.physical.thirst.config.item.ExactThirstConfig;
import com.voltskiya.mechanics.physical.thirst.config.item.MaterialThirstConfig;
import com.voltskiya.mechanics.physical.thirst.item.ThirstItem;
import com.voltskiya.mechanics.physical.thirst.item.ThirstKeys;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class ThirstConfig implements IAppleConfigInit {


    private static ThirstConfig instance;
    protected List<ThirstEffect> thirstEffects = new ArrayList<>();
    protected Map<String, ExactThirstConfig> keyedConsumables = new HashMap<>();
    protected Map<Material, MaterialThirstConfig> defaultConsumables = new EnumMap<>(Material.class);
    protected List<DirtyWaterPotionEffect> dirtyWaterEffects = new ArrayList<>();
    protected int thirstRate = 5;

    public ThirstConfig() {
        instance = this;
        defaultConsumables.put(Material.POTION, new MaterialThirstConfig());
        defaultConsumables.put(Material.HONEY_BOTTLE, new MaterialThirstConfig());
        defaultConsumables.put(Material.GLASS_BOTTLE, new MaterialThirstConfig());
        thirstEffects.add(new ThirstEffect(PotionEffectType.HUNGER, 0, 0, 200));
        thirstEffects.add(new ThirstEffect(PotionEffectType.WITHER, 0, 0, 0));
        dirtyWaterEffects.add(new DirtyWaterPotionEffect(PotionEffectType.POISON, 0, 100));
        dirtyWaterEffects.add(new DirtyWaterPotionEffect(PotionEffectType.CONFUSION, 0, 160));
        for (ThirstItem item : ThirstItem.values()) {
            keyedConsumables.put(item.getId(), new ExactThirstConfig());
        }
    }

    public static ThirstConfig get() {
        return instance;
    }

    @Override
    public void setManager(AppleConfig<?> manager) {
        defaultConsumables.forEach((material, config) -> config.load(material));
        keyedConsumables.forEach((key, config) -> config.load(key));
    }

    @Nullable
    public ConsumableThirstConfig fromItem(@Nullable ItemStack item) {
        if (item == null) return null;
        String itemType = ThirstKeys.getItemType(item);
        if (itemType != null) {
            return keyedConsumables.get(itemType);
        }
        Material material = item.getType();
        return defaultConsumables.get(material);
    }

    public List<PotionEffect> getPotionEffects(Player player, int thirstLevel) {
        return thirstEffects
            .stream()
            .filter(effect -> effect.shouldActivate(player, thirstLevel))
            .map(ThirstEffect::potion)
            .toList();
    }

    public int getThirstRate() {
        return thirstRate;
    }

    public List<PotionEffect> getDirtyEffects() {
        return dirtyWaterEffects.stream().map(DirtyWaterPotionEffect::potion).toList();
    }

    public ExactThirstConfig getConfig(String id) {
        return this.keyedConsumables.get(id);
    }
}
