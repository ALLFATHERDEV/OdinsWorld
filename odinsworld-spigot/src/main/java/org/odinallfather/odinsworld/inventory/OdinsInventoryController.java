package org.odinallfather.odinsworld.inventory;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.odinallfather.odinsworld.OdinsWorld;
import org.odinallfather.odinsworld.util.Tripple;

import java.util.List;
import java.util.Map;

public abstract class OdinsInventoryController {

    private final Map<String, ItemStack> items = Maps.newHashMap();
    private final String fileName;

    public OdinsInventoryController(String fileName) {
        this.fileName = fileName;
    }

    protected abstract void setup();

    protected void addItem(String itemName, ItemStack stack) {
        InventoryData data = this.getData();
        List<Tripple<ItemRange, String, String>> dataList = data.getData();
        String found = Tripple.findB(dataList, itemName);
        if (found == null) {
            OdinsWorld.LOGGER.error("Could not find item name %s in json file", itemName);
            throw new RuntimeException();
        }
        if (!this.items.containsKey(itemName))
            this.items.put(itemName, stack);
    }

    public void onInventoryOpen(Player player) {

    }

    public void onInventoryClose(Player player) {

    }

    public InventoryData getData() {
        return OdinsWorld.getInstance().getInventoryManager().getInventoryData(this.fileName);
    }

    public Map<String, ItemStack> getItems() {
        return items;
    }

    public enum ActionResult {

        CLICK,
        PASS

    }

}
