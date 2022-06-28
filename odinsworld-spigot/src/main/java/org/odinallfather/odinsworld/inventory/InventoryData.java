package org.odinallfather.odinsworld.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.odinallfather.odinsworld.OdinsWorld;
import org.odinallfather.odinsworld.reflection.MethodAccessor;
import org.odinallfather.odinsworld.reflection.ReflectionHelper;
import org.odinallfather.odinsworld.util.Tripple;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class InventoryData implements Listener {

    private final Class<? extends OdinsInventoryController> controllerClass;
    private OdinsInventoryController controller;
    private final List<Tripple<ItemRange, String, String>> data = Lists.newArrayList();
    private final int size;
    private final String title;
    private Inventory inventory;
    private final Map<String, MethodAccessor<OdinsInventoryController.ActionResult>> clickMethods = Maps.newHashMap();
    private final InventorySettings settings;

    public InventoryData(Class<? extends OdinsInventoryController> controllerClass, int size, String title, InventorySettings settings) {
        this.controllerClass = controllerClass;
        this.size = size;
        this.title = title;
        this.settings = settings;
    }

    public void addData(ItemRange range, String stackName, String methodName) {
        this.data.add(new Tripple<>(range, stackName, methodName));
    }

    public boolean build(String fileName) {
        try {
            controller = controllerClass.getConstructor(String.class).newInstance(fileName);
            for (Tripple<ItemRange, String, String> t : this.data) {
                String methodName = t.getC();
                MethodAccessor<OdinsInventoryController.ActionResult> method = ReflectionHelper.getMethod(controllerClass, methodName, InventoryActionContext.class);
                if (method == null) {
                    OdinsWorld.LOGGER.error("Could not find method with name %s", methodName);
                    return false;
                }
                this.clickMethods.put(t.getB(), method);
            }
            controller.setup();
            if (this instanceof ListInventoryData)
                this.buildListInventoryAndSetItems(controller);
            else
                this.buildInventoryAndSetItems(controller);
            Bukkit.getPluginManager().registerEvents(this, OdinsWorld.getInstance());
            return true;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildListInventoryAndSetItems(OdinsInventoryController controller) {
        this.inventory = Bukkit.createInventory(null, size, title);
        ListInventoryData listInventoryData = (ListInventoryData) this;
    }

    private void buildInventoryAndSetItems(OdinsInventoryController controller) {
        this.inventory = Bukkit.createInventory(null, size, title);
        Map<String, ItemStack> items = controller.getItems();
        for (Tripple<ItemRange, String, String> t : this.data) {
            ItemRange range = t.getA();
            String itemName = t.getB();
            ItemStack item = items.get(itemName);
            if (item == null) {
                OdinsWorld.LOGGER.error("Could not find item with name %s", itemName);
                throw new NullPointerException();
            }
            range.place(inventory, item);
        }

    }

    public List<Tripple<ItemRange, String, String>> getData() {
        return data;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(inventory)) {
            if (!this.settings.canMoveItems)
                event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            String name = existItem(clicked);
            if (name != null) {
                MethodAccessor<OdinsInventoryController.ActionResult> accessor = this.clickMethods.get(name);
                if (accessor != null) {
                    InventoryActionContext ctx = new InventoryActionContext(clicked, event.getSlot(), (Player) event.getWhoClicked());
                    accessor.invoke(controller, ctx);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().equals(inventory)) {
            if (this.settings.onOpenEvent)
                this.controller.onInventoryOpen((Player) event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            if (this.settings.onCloseEvent)
                this.controller.onInventoryClose((Player) event.getPlayer());
        }
    }

    private String existItem(ItemStack stack) {
        for (String s : this.controller.getItems().keySet()) {
            if (this.controller.getItems().get(s).isSimilar(stack))
                return s;
        }
        return null;
    }

}
