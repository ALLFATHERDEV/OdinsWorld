package org.odinallfather.odinsworld;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.odinallfather.odinsworld.inventory.InventoryManager;
import org.odinallfather.odinsworld.inventory.OdinsInventoryController;
import org.odinallfather.odinsworld.util.OWLogger;
import org.odinallfather.test.TestInventoryController;

public class OdinsWorld extends JavaPlugin {

    public static final OWLogger LOGGER = new OWLogger("OdinsWorld");
    private static OdinsWorld instance;
    private InventoryManager inventoryManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.addInventoryFile("test");
        this.inventoryManager.loadInventories();
        getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        InventoryManager.openInventory(TestInventoryController.class, player);
        return true;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public void onDisable() {
        LOGGER.closeLogger();
    }

    public static OdinsWorld getInstance() {
        return instance;
    }
}
