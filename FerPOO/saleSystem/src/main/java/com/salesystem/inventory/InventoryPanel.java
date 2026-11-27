package com.salesystem.inventory;

import com.salesystem.db.Database;
import com.salesystem.ui.RecordsPanel;

public final class InventoryPanel extends RecordsPanel<InventoryRepository.StockItem> {
    public InventoryPanel(Database database) {
        super(database, new String[]{"ID", "Product", "Category", "Stock"},
                new InventoryRepository()::findAll,
                item -> new Object[]{item.id(), item.name(), item.category(), item.available()});
    }
}
