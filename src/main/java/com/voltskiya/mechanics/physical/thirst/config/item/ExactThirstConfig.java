package com.voltskiya.mechanics.physical.thirst.config.item;

import com.voltskiya.mechanics.physical.thirst.item.ThirstItem;

public class ExactThirstConfig implements ConsumableThirstConfig {

    protected int uses = 1;
    protected int consumeAmount = 20000;
    protected transient ThirstItem thirstItem;

    @Override
    public int getConsumeAmount() {
        return this.consumeAmount;
    }

    @Override
    public ThirstItem getThirstItem() {
        return thirstItem;
    }

    @Override
    public int getUses() {
        return this.uses;
    }


    public void load(String key) {
        this.thirstItem = ThirstItem.fromId(key);
    }
}
