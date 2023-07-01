package com.voltskiya.mechanics.physical.thirst.config.item;

import com.voltskiya.mechanics.physical.thirst.item.ThirstItem;
import org.bukkit.Material;

public class MaterialThirstConfig implements ConsumableThirstConfig {

    protected int consumeAmount = 20000;
    protected transient Material material;

    @Override
    public int getConsumeAmount() {
        return consumeAmount;
    }

    @Override
    public ThirstItem getThirstItem() {
        return ThirstItem.NORMAL_BOTTLE;
    }

    @Override
    public int getUses() {
        return 1;
    }

    public void load(Material material) {
        this.material = material;
    }
}
