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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.VardgivareManager;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoginServiceUtil {

    @Autowired
    VardgivareManager vardgivareManager;

    Kommun kommun = new Kommun();

    Lan lan = new Lan();

    LoginInfo getLoginInfo(HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        if (user instanceof AbstractAuthenticationToken) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) user;
            if (token.getDetails() instanceof User) {
                User realUser = (User) token.getDetails();
                List<Enhet> enhetsList = vardgivareManager.getEnhets(realUser.getValdVardenhet().getVardgivarId());
                List<Verksamhet> verksamhets = getVerksamhetsList(realUser, enhetsList);

                // TODO: perhaps put something more specific than OVRIGT here
                Verksamhet defaultVerksamhet = toVerksamhet(realUser.getValdVardenhet(), Lan.OVRIGT_ID, Lan.OVRIGT, Kommun.OVRIGT_ID, Kommun.OVRIGT);
                return new LoginInfo(realUser.getHsaId(), realUser.getName(), defaultVerksamhet, realUser.hasVgAccess(), realUser.hasFullVgAccess(), verksamhets);
            }
        }
        return new LoginInfo();
    }

    private List<Verksamhet> getVerksamhetsList(User realUser, final List<Enhet> enhetsList) {
        List<Verksamhet> returnList = new ArrayList<>();
        for (final Vardenhet vardEnhet: realUser.getVardenhetList()) {
            Optional<Enhet> enhetOptional = Iterables.tryFind(enhetsList, new Predicate<Enhet>() {
                @Override
                public boolean apply(Enhet enhet) {
                    return enhet.getEnhetId().equals(vardEnhet.getId());
                }
            });
            String lansId = Lan.OVRIGT_ID;
            String lansNamn = Lan.OVRIGT;
            String kommunId = Kommun.OVRIGT_ID;
            String kommunNamn = Kommun.OVRIGT;
            if (enhetOptional.isPresent()) {
                lansId = enhetOptional.get().getLansId();
                lansNamn = lan.getNamn(lansId);
                kommunId = enhetOptional.get().getKommunId();
                kommunNamn = kommun.getNamn(kommunId);
            }
            returnList.add(toVerksamhet(vardEnhet, lansId, lansNamn, kommunId, kommunNamn));
        }

        return returnList;
    }

    private Verksamhet toVerksamhet(Vardenhet enhet, String lansId, String lansNamn, String kommunId, String kommunNamn) {
        return new Verksamhet(enhet.getId(), enhet.getNamn(), enhet.getVardgivarId(), enhet.getVardgivarNamn(), lansId, lansNamn, kommunId, kommunNamn);
    }

}
