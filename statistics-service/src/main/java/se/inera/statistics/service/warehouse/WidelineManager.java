package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class WidelineManager {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineManager.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private WidelineConverter widelineConverter;

    @Transactional
    public void accept(JsonNode intyg, JsonNode hsa, long logId) {
        WideLine line = widelineConverter.toWideline(intyg, hsa, logId);
        List<String> errors  = widelineConverter.validate(line);

        if (errors.isEmpty()) {
            manager.persist(line);
        } else {
            String intygid = DocumentHelper.getIntygId(intyg);
            StringBuilder errorBuilder = new StringBuilder("Faulty intyg logid").append(logId).append(" id ").append(intygid);
            for (String error : errors) {
                errorBuilder.append('\n').append(error);
            }
            LOG.error(errorBuilder.toString());
        }
    }

    @Transactional
    public void saveWideline(WideLine line) {
        manager.persist(line);
    }

    public int count() {
        return manager.createQuery("SELECT wl FROM WideLine wl").getResultList().size();
    }
}
