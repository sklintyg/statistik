package se.inera.statistics.web.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.Verksamhet;

public final class ServiceUtil {

    private ServiceUtil() {
    }

    static List<Integer> getMergedSexData(DualSexDataRow row) {
        List<Integer> data = new ArrayList<>();
        for (DualSexField dualSexField : row.getData()) {
            data.add(dualSexField.getFemale());
            data.add(dualSexField.getMale());
        }
        return data;
    }

    static LoginInfo getLoginInfo(HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        if (user instanceof AbstractAuthenticationToken) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) user;
            if (token.getDetails() instanceof User) {
                List<Verksamhet> verksamhets = new ArrayList<>();
                User realUser = (User) token.getDetails();
                for (Vardenhet enhet: realUser.getVardenhetList()) {
                    verksamhets.add(new Verksamhet(enhet.getId(), enhet.getNamn()));
                }
                return new LoginInfo(realUser.getHsaId(), realUser.getName(), true, verksamhets);
            }
        }
        return new LoginInfo("", "", false, Collections.<Verksamhet>emptyList());
    }

    static void addSumRow(List<NamedData> rows, boolean includeSumForLastColumn) {
        rows.add(getSumRow(rows, includeSumForLastColumn));
    }

    static NamedData getSumRow(List<NamedData> rows, boolean includeSumForLastColumn) {
        final ArrayList<Integer> sumData = new ArrayList<Integer>();
        if (!rows.isEmpty()) {
            for (int i = 0; i < rows.get(0).getData().size(); i++) {
                sumData.add(0);
            }
            for (NamedData namedData : rows) {
                List<Object> datas = namedData.getData();
                for (int i = 0; i < datas.size(); i++) {
                    Object data = datas.get(i);
                    sumData.set(i, sumData.get(i) + Integer.parseInt(data.toString()));
                }
            }
            if (!includeSumForLastColumn) {
                sumData.remove(sumData.size() - 1);
            }
        }
        NamedData sumRow = new NamedData("Totalt", sumData);
        return sumRow;
    }

}
