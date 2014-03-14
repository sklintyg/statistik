package se.inera.statistics.service.warehouse;

import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseManager {

    @Autowired
    private WidelineLoader loader;

    @Autowired
    private Warehouse warehouse;

    public int loadWideLines() {
        warehouse.clear();
        int lines = loader.populateWarehouse();
        sortAisles();
        return lines;
    }

    public int countAisles() {
        return warehouse.getAllVardgivare().size();
    }

    public int getAisleSize(String vardgivareId) {
        Aisle aisle = warehouse.get(vardgivareId);
        if (aisle == null) {
            return 0;
        }
        return aisle.getSize();
    }

    private void sortAisles() {
        for (Aisle aisle: warehouse) {
            aisle.sort();
        }
    }
}
