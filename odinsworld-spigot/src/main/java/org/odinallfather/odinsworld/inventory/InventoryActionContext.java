package org.odinallfather.odinsworld.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryActionContext {

    private final ItemStack clickedItem;
    private final int clickedSlot;
    private final Player clickedPlayer;

    InventoryActionContext(ItemStack clickedItem, int clickedSlot, Player clickedPlayer) {
        this.clickedItem = clickedItem;
        this.clickedSlot = clickedSlot;
        this.clickedPlayer = clickedPlayer;
    }

    public int getClickedSlot() {
        return clickedSlot;
    }

    public ItemStack getClickedItem() {
        return clickedItem;
    }

    public Player getClickedPlayer() {
        return clickedPlayer;
    }
}
