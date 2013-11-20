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
import se.inera.statistics.web.model.Verksamhet;

public final class ServiceUtil {

    private ServiceUtil() {
    }

    static List<Integer> getAppendedSum(List<Integer> data) {
        int sum = 0;
        for (Integer dataField : data) {
            sum += dataField;
        }
        List<Integer> dataWithSum = new ArrayList<Integer>(data);
        dataWithSum.add(sum);
        return dataWithSum;
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

}
