package se.inera.statistics.service.warehouse;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class WarehouseManager {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseManager.class);

    @Autowired
    private WidelineLoader loader;

    @Autowired
    private Warehouse warehouse;

    @PostConstruct
    public int loadWideLines() {
        LOG.info("Reloading warehouse");
        int lines = loader.populateWarehouse();
        LOG.info("Reloaded warehouse {} lines", lines);
        warehouse.complete(LocalDateTime.now());
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
}
