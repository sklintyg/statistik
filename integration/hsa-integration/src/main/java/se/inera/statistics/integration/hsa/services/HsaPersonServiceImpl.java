package se.inera.statistics.integration.hsa.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.services.HsatkEmployeeService;
import se.inera.statistics.integration.hsa.model.StatisticsPersonInformation;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HsaPersonServiceImpl implements HsaPersonService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaPersonServiceImpl.class);
    @Autowired
    HsatkEmployeeService hsatkEmployeeService;

    @Override
    public List<StatisticsPersonInformation> getHsaPersonInfo(String personHsaId) {
        try {
            LOG.info("HsatkEmployeeService is {}.", hsatkEmployeeService.toString());
            LOG.info("Fetching PersonInformation for {} from HsatkEmployeeService.", personHsaId);
            List<PersonInformation> personInformationList = hsatkEmployeeService.getEmployee("", personHsaId, "");

            return toStatisticPersonInformationList(personInformationList);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
    }

    private List<StatisticsPersonInformation> toStatisticPersonInformationList(List<PersonInformation> personInformationList) {
        LOG.info("Converting {}", personInformationList);
        List<StatisticsPersonInformation> statisticsPersonInformationList = new ArrayList<>();

        for (PersonInformation personInformation : personInformationList) {
            StatisticsPersonInformation statisticsPersonInformation = new StatisticsPersonInformation();
            statisticsPersonInformation.setGivenName(personInformation.getGivenName());
            statisticsPersonInformation.setGivenName(personInformation.getGivenName());
            statisticsPersonInformationList.add(statisticsPersonInformation);
        }
        return statisticsPersonInformationList;
    }
}
