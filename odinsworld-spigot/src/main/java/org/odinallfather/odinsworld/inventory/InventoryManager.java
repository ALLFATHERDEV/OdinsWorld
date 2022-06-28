package org.odinallfather.odinsworld.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.odinallfather.odinsworld.OdinsWorld;
import org.odinallfather.odinsworld.util.BooleanCallback;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryManager {

    private final List<String> inventoryFiles = Lists.newArrayList();
    private final Plugin plugin;
    private final Map<String, InventoryData> data = Maps.newHashMap();
    private static final Map<Class<? extends OdinsInventoryController>, InventoryData> controllerData = Maps.newHashMap();
    private final Pattern listInvPattern = Pattern.compile("(\\d):(\\d)-(\\d):(\\d)");

    public InventoryManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void addInventoryFile(String inventoryFile) {
        if(!inventoryFiles.contains(inventoryFile))
            inventoryFiles.add(inventoryFile);
        else
            OdinsWorld.LOGGER.warning("Tried to add duplicated inventory file: %s", inventoryFile);
    }

    public void loadInventories() {
        if(!inventoryFiles.isEmpty()) {
            OdinsWorld.LOGGER.info("Loading %s inventories", inventoryFiles.size());
            AtomicInteger successCounter = new AtomicInteger();
            inventoryFiles.forEach(fileName -> {
                InputStream is = plugin.getResource("inventories/" + fileName + ".inv.json");
                if(is == null) {
                    OdinsWorld.LOGGER.error("Could not read %s inventory file", fileName);
                    throw new RuntimeException();
                }
                Gson gson = new Gson();
                JsonObject obj = gson.fromJson(new JsonReader(new InputStreamReader(is)), JsonObject.class);
                BooleanCallback<InventoryData> inventoryDataCallback;
                if(obj.has("type")) {
                    String type = obj.get("type").getAsString();
                    if(type.equals("list")) {
                        inventoryDataCallback = this.readInventoryListFile(obj, fileName);
                    } else {
                        throw new IllegalArgumentException("Unknown list type: " + type);
                    }
                } else {
                    inventoryDataCallback = this.readInventoryFile(obj, fileName);
                }
                if(inventoryDataCallback.getResult()) {
                    successCounter.getAndIncrement();
                    InventoryData inventoryData = inventoryDataCallback.getType();
                    this.data.put(fileName, inventoryData);
                }
            });
            OdinsWorld.LOGGER.info("Loaded %s/%s inventories", successCounter.get(), inventoryFiles.size());
            this.buildingInventories();
        }
    }

    public void buildingInventories() {
        OdinsWorld.LOGGER.info("Building %s inventories", data.size());
        AtomicInteger counter = new AtomicInteger();
        this.data.forEach((fileName, inventoryData) -> {
            if(inventoryData.build(fileName))
                counter.getAndIncrement();
        });
        OdinsWorld.LOGGER.info("Builded %s/%s inventories", counter.get(), data.size());
    }

    private BooleanCallback<InventoryData> readInventoryListFile(JsonObject object, String fileName /*Only for logging*/) {
        if(!this.checkHead(object, fileName))
            return BooleanCallback.fail();
        Class<?> controllerClass;
        try {
            controllerClass = Class.forName(object.get("controller").getAsString());
            if(!controllerClass.getSuperclass().equals(OdinsInventoryController.class)) {
                OdinsWorld.LOGGER.error("Controller class is not a child from OdinsInventoryController. Skipping inventory file: %s", fileName);
                return BooleanCallback.fail();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int size = object.get("size").getAsInt();
        if(!object.has("list-range")) {
            OdinsWorld.LOGGER.error("Could not find list-range in file: %s", fileName);
            return BooleanCallback.fail();
        }
        String listRange = object.get("list-range").getAsString();
        Matcher matcher = listInvPattern.matcher(listRange);
        if(!matcher.matches()) {
            OdinsWorld.LOGGER.error("Wrong list-range syntax. Syntax should be: X:X-X:X where X is a number. Skipping inventory file: %s", fileName);
            return BooleanCallback.fail();
        }
        String title = "";
        if(object.has("title"))
            title = object.get("title").getAsString();
        //Normalize the coordinates
        int startX = Integer.parseInt(matcher.group()) - 1;
        int startY = Integer.parseInt(matcher.group(2)) - 1;
        int endX = Integer.parseInt(matcher.group(3)) - 1;
        int endY = Integer.parseInt(matcher.group(4)) - 1;
        ItemArea area = new ItemArea(startX, startY, endX, endY);
        ListInventoryData listInventoryData = new ListInventoryData((Class<? extends OdinsInventoryController>) controllerClass, size, title, this.readSettings(object), area);
        controllerData.put((Class<? extends OdinsInventoryController>) controllerClass, listInventoryData);
        return BooleanCallback.success(listInventoryData);
    }

    private BooleanCallback<InventoryData> readInventoryFile(JsonObject object, String fileName /*Only for logging*/) {
        if(!this.checkHead(object, fileName))
            return BooleanCallback.fail();
        Class<?> controllerClass;
        try {
            controllerClass = Class.forName(object.get("controller").getAsString());
            if(!controllerClass.getSuperclass().equals(OdinsInventoryController.class)) {
                OdinsWorld.LOGGER.error("Controller class is not a child from OdinsInventoryController. Skipping inventory file: %s", fileName);
                return BooleanCallback.fail();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int size = object.get("size").getAsInt();
        if(!object.has("layout")) {
            OdinsWorld.LOGGER.warning("Missing layout for inventory file: %s", fileName);
            return BooleanCallback.fail();
        }
        String title = "";
        if(object.has("title"))
            title = object.get("title").getAsString();
        InventoryData data = new InventoryData((Class<? extends OdinsInventoryController>) controllerClass, size, title, this.readSettings(object));
        JsonObject layout = object.get("layout").getAsJsonObject();
        for(String key : layout.keySet()) {
            JsonObject keyObject = layout.get(key).getAsJsonObject();
            if(key.contains(":")) {
                int startSlot;
                int endSlot;
                String[] res = key.split(":");
                try {
                    startSlot = Integer.parseInt(res[0]);
                    endSlot = Integer.parseInt(res[1]);
                } catch (NumberFormatException e) {
                    OdinsWorld.LOGGER.warning("Wrong pattern character: %s:%s skipping inventory file: %s",res[0], res[1], fileName);
                    return BooleanCallback.fail();
                }
                if(!keyObject.has("item")) {
                    OdinsWorld.LOGGER.warning("Could not find item name for slot: %s:%s in inventory file: %s", startSlot, endSlot, fileName);
                    return BooleanCallback.fail();
                }
                String itemName = layout.get(key).getAsJsonObject().get("item").getAsString();
                String methodName = "<SKIP>";
                if(keyObject.has("action")) {
                    methodName = keyObject.get("action").getAsString();
                }
                data.addData(new ItemRange(startSlot, endSlot), itemName, methodName);
            } else {
                int slot;
                try {
                    slot = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    OdinsWorld.LOGGER.warning("Wrong pattern character: %s skipping inventory file: %s", key, fileName);
                    return BooleanCallback.fail();
                }
                if(!keyObject.has("item")) {
                    OdinsWorld.LOGGER.warning("Could not find item name for slot: %s in inventory file: %s", slot, fileName);
                    return BooleanCallback.fail();
                }
                String itemName = layout.get(key).getAsJsonObject().get("item").getAsString();
                String methodName = "<SKIP>";
                if(keyObject.has("action")) {
                    methodName = keyObject.get("action").getAsString();
                }
                data.addData(new ItemRange(slot, -1), itemName, methodName);
            }
        }
        controllerData.put((Class<? extends OdinsInventoryController>) controllerClass, data);
        return BooleanCallback.success(data);
    }

    private boolean checkHead(JsonObject object, String fileName /*Only for logging*/) {
        if(!object.has("controller")) {
            OdinsWorld.LOGGER.warning("Could not find controller class for file %s. Skipping inventory", fileName);
            return false;
        }
        Class<?> controllerClass;
        try {
            controllerClass = Class.forName(object.get("controller").getAsString());
            if(!controllerClass.getSuperclass().equals(OdinsInventoryController.class)) {
                OdinsWorld.LOGGER.error("Controller class is not a child from OdinsInventoryController. Skipping inventory file: %s", fileName);
                return false;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(!object.has("size")) {
            OdinsWorld.LOGGER.warning("Could not find inventory size for file %s. Skipping inventory", fileName);
            return false;
        }
        int size = object.get("size").getAsInt();
        if(size % 9 != 0) {
            OdinsWorld.LOGGER.warning("Size is not a factor of 9. Skipping inventory file: %s", fileName);
            return false;
        }
        return true;
    }

    private InventorySettings readSettings(JsonObject mainObject) {
        if(mainObject.has("settings")) {
            JsonObject settingsObj = mainObject.getAsJsonObject("settings");
            Optional<JsonElement> canMoveItems = this.get("can-move-items", settingsObj);
            boolean cmi = false;
            if(canMoveItems.isPresent())
                cmi = canMoveItems.get().getAsBoolean();
            Optional<JsonElement> onOpenInventory = this.get("onOpenInventory", settingsObj);
            boolean ooi = false;
            if(onOpenInventory.isPresent())
                ooi = onOpenInventory.get().getAsBoolean();
            Optional<JsonElement> onCloseInventory = this.get("onCloseInventory", settingsObj);
            boolean oci = false;
            if(onCloseInventory.isPresent())
                oci = onCloseInventory.get().getAsBoolean();
            return new InventorySettings(cmi, ooi, oci);
        } else {
            return InventorySettings.empty();
        }
    }

    private Optional<JsonElement> get(String key, JsonObject object) {
        if(object.has(key))
            return Optional.of(object.get(key));
        return Optional.empty();
    }

    public InventoryData getInventoryData(String fileName) {
        return this.data.get(fileName);
    }

    public static void openInventory(Class<? extends OdinsInventoryController> controller, Player player) {
        InventoryData data = controllerData.get(controller);
        if(data != null)
            player.openInventory(data.getInventory());
    }


}
