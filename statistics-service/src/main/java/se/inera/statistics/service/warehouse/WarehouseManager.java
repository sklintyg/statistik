package se.inera.statistics.service.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseManager {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseManager.class);

    @Autowired
    private WidelineLoader loader;

    @Autowired
    private Warehouse warehouse;

    public int loadWideLines() {
        LOG.info("Reloading warehouse");
        warehouse.clear();
        int lines = loader.populateWarehouse();
        LOG.info("Reloaded warehouse {} lines", lines);
        sortAisles();
        LOG.info("Prepared warehouse with ailes {}", warehouse.getAllVardgivare().keySet());
        return lines;
    }

    public int countAisles() {
        return warehouse.getAllVardgivare().size();
    }

    public int getAisleSize(String vardgivareId) {
        Aisle aisle = warehouse.get(vardgivareId);
        return aisle.getSize();
    }

    private void sortAisles() {
        for (Aisle aisle: warehouse) {
            aisle.sort();
        }
    }
}
