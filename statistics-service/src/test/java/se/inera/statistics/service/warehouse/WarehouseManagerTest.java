package se.inera.statistics.service.warehouse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class WarehouseManagerTest {

    @Mock
    private Warehouse warehouse;

    @Mock
    private Aisle aisle;

    @Mock
    private WidelineLoader loader;

    @InjectMocks
    private WarehouseManager manager = new WarehouseManager();

    @Test
    public void verifyOrderOfActionWhenLoadingData() {
        Mockito.when(warehouse.iterator()).thenReturn(Collections.singletonList(aisle).iterator());

        manager.load();

        InOrder inOrder = Mockito.inOrder(loader, warehouse, aisle);
        inOrder.verify(warehouse).clear();
        inOrder.verify(loader).populateWarehouse();
        inOrder.verify(aisle).sort();


    }
}
