package org.odinallfather.odinsworld.inventory;

import org.odinallfather.odinsworld.util.Pair;

class InventorySettings {

    final boolean canMoveItems;
    final boolean onOpenEvent;
    final boolean onCloseEvent;

    public InventorySettings(boolean canMoveItems, boolean onOpenEvent, boolean onCloseEvent) {
        this.canMoveItems = canMoveItems;
        this.onOpenEvent = onOpenEvent;
        this.onCloseEvent = onCloseEvent;
    }

    static InventorySettings empty() {
        return new InventorySettings(true, false, false);
    }
}
