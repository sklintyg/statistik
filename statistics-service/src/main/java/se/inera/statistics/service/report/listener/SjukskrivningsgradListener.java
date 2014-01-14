package se.inera.statistics.service.report.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.report.api.Sjukskrivningsgrad;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukskrivningsgradListener extends GenericAbstractListener {

    @Autowired
    private Sjukskrivningsgrad api;


    @Override
    boolean accept(GenericHolder token, String period) {
        List<String> arbetsformagor = DocumentHelper.getArbetsformaga(token.getUtlatande());
        for (String formaga: arbetsformagor) {
            String grad = arbetsformagaTillSjukskrivning(formaga);
            api.count(token.getEnhetId(), period, grad, Verksamhet.ENHET, token.getKon());
            api.count(token.getVardgivareId(), period, grad, Verksamhet.VARDGIVARE, token.getKon());
        }
        return false; // TODO: Caching
    }

    private String arbetsformagaTillSjukskrivning(String formaga) {
        switch(formaga) {
        case "0": return "100";
        case "25": return "75";
        case "50": return "50";
        case "75": return "25";
        default:
            return "100";
        }
    }
}
