package org.odinallfather.odinsworld.inventory;

public class ListInventoryData extends InventoryData{

    private final ItemArea area;

    public ListInventoryData(Class<? extends OdinsInventoryController> controllerClass, int size, String title, InventorySettings settings, ItemArea area) {
        super(controllerClass, size, title, settings);
        this.area = area;
    }

    public ItemArea getArea() {
        return area;
    }
}
