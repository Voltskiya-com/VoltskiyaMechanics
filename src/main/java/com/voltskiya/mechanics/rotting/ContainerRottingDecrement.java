package com.voltskiya.mechanics.rotting;

import com.voltskiya.mechanics.food.service.RottingDecrement;
import org.bukkit.inventory.Inventory;

public class ContainerRottingDecrement extends RottingDecrement {

    public ContainerRottingDecrement(Inventory inventory) {
        super(inventory, null);
    }

    @Override
    protected boolean shouldRemove() {
        return inventory.getViewers().isEmpty();
    }
}
