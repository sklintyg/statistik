package se.inera.statistics.service.warehouse;

import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseManager {

    @Autowired
    private WidelineLoader loader;

    @Autowired
    private Warehouse warehouse;

    public void load() {
        System.err.println("Call load");
        warehouse.clear();
        loader.populateWarehouse();
        sortAisles();
    }

    public int countAisles() {
        return warehouse.getAllVardgivare().size();
    }

    private void sortAisles() {
        for (Aisle aisle: warehouse) {
            aisle.sort();
        }
    }
}
