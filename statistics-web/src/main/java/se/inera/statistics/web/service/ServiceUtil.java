/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.Verksamhet;

public final class ServiceUtil {

    private ServiceUtil() {
    }

    static List<Integer> getMergedSexData(KonDataRow row) {
        List<Integer> data = new ArrayList<>();
        for (KonField konField : row.getData()) {
            data.add(konField.getFemale());
            data.add(konField.getMale());
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
                return new LoginInfo(realUser.getHsaId(), realUser.getName(), realUser.hasVgAccess(), realUser.hasFullVgAccess(), verksamhets);
            }
        }
        return new LoginInfo();
    }

    static void addSumRow(List<NamedData> rows, boolean includeSumForLastColumn) {
        rows.add(getSumRow(rows, includeSumForLastColumn));
    }

    static NamedData getSumRow(List<NamedData> rows, boolean includeSumForLastColumn) {
        final ArrayList<Integer> sumData = new ArrayList<>();
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
        return new NamedData("Totalt", sumData);
    }

}
