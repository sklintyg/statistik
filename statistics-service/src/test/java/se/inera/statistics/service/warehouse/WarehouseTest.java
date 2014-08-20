package se.inera.statistics.service.warehouse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:warehouse-test.xml", "classpath:icd10.xml"  })
public class WarehouseTest {

    private HSAService hsaService = new HSAServiceMock();

    private JsonNode rawDocument = JSONParser.parse(JSONSource.readTemplateAsString());
    private JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vardgivarid", "enhetid", "lakareid"));
    
    @Autowired
    private Warehouse warehouse;

    @Autowired
    private WidelineConverter widelineConverter;

    @Autowired
    private FactPopulator factPopulator;

    @Test
    public void addingIntygAddsToCorrectAisle() {
        JsonNode document = DocumentHelper.prepare(rawDocument, hsaInfo);
        WideLine wideLine = widelineConverter.toWideline(document, hsaInfo, 0, "0", EventType.CREATED);
        factPopulator.accept(wideLine);
        Aisle aisle = warehouse.get("vardgivarid");
        assertEquals(1, aisle.getSize());
    }
}
