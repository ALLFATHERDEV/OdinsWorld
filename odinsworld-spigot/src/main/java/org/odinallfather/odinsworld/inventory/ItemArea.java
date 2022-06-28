package org.odinallfather.odinsworld.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemArea {

    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    public ItemArea(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void place(Inventory inventory, ItemStack stack) {
        for(int y = startY; y < endY; y++) {
            for(int x = startX; x < endX; x++)
                inventory.setItem((y * 9) + x, stack);
        }
    }
}
