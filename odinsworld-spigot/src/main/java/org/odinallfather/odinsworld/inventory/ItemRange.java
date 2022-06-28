package org.odinallfather.odinsworld.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemRange {

    private final int start;
    private int end;

    public ItemRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public void place(Inventory inventory, ItemStack stack) {
        if(end == -1)
            inventory.setItem(start, stack);
        else {
            if(end == inventory.getSize())
                end -= 1;
            for(int i = start; i <= end; i++) {
                inventory.setItem(i, stack);
            }
        }
    }

}
