package org.odinallfather.test;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.odinallfather.odinsworld.inventory.InventoryActionContext;
import org.odinallfather.odinsworld.inventory.OdinsInventoryController;

public class TestInventoryController extends OdinsInventoryController {

    public TestInventoryController(String fileName) {
        super(fileName);
    }

    @Override
    protected void setup() {
        this.addItem("filler", new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        this.addItem("diamond", new ItemStack(Material.DIAMOND));
        this.addItem("abc", new ItemStack(Material.DIAMOND_SWORD));
    }

    public ActionResult onClickFiller(InventoryActionContext ctx) {
        System.out.println("onClickFiller");
        return ActionResult.PASS;
    }

    public ActionResult onClickDiamond(InventoryActionContext ctx) {
        System.out.println("onClickDiamond");
        return ActionResult.PASS;
    }

    public ActionResult onClickAbc(InventoryActionContext ctx) {
        System.out.println("onClickAbc");
        return ActionResult.PASS;
    }

    public ActionResult onClickFiller2(InventoryActionContext ctx) {
        System.out.println("onClickFiller2");
        return ActionResult.PASS;
    }

}

