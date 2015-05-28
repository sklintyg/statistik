/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;
import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.LandstingsVardgivareStatus;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.transform;

@Component
public class LoginServiceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LoginServiceUtil.class);

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private LandstingEnhetHandler landstingEnhetHandler;

    private Kommun kommun = new Kommun();

    private Lan lan = new Lan();

    private VerksamhetsTyp verksamheter = new VerksamhetsTyp();

    private static final Splitter ID_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    public LoginInfo getLoginInfo(HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        if (!(user instanceof AbstractAuthenticationToken)) {
            LOG.error("user object is of wrong type: " + user);
            return new LoginInfo();
        }
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) user;
        final Object details = token.getDetails();
        if (!(details instanceof User)) {
            LOG.warn("details object is of wrong type: " + details);
            return new LoginInfo();
        }
        User realUser = (User) details;
        final Vardenhet valdVardenhet = realUser.getValdVardenhet();
        if (valdVardenhet == null) {
            LOG.warn("valdEnhet may not be null");
            return new LoginInfo();
        }
        final String vardgivarId = valdVardenhet.getVardgivarId();
        List<Enhet> enhetsList = warehouse.getEnhets(vardgivarId);
        Verksamhet defaultVerksamhet = toVerksamhet(valdVardenhet, enhetsList);
        List<Verksamhet> verksamhets = getVerksamhetsList(realUser, enhetsList);
        final LandstingsVardgivareStatus landstingsVardgivareStatus = landstingEnhetHandler.getLandstingsVardgivareStatus(vardgivarId);
        return new LoginInfo(realUser.getHsaId(), realUser.getName(), defaultVerksamhet, realUser.isVerksamhetschef(), realUser.isDelprocessledare(), realUser.isProcessledare(), verksamhets, landstingsVardgivareStatus);
    }

    private List<Verksamhet> getVerksamhetsList(User realUser, final List<Enhet> enhetsList) {
        if (realUser.isProcessledare() && enhetsList != null && !enhetsList.isEmpty()) {
            return transform(enhetsList, new Function<Enhet, Verksamhet>() {
                @Override
                public Verksamhet apply(Enhet enhet) {
                    return toVerksamhet(enhet);
                }
            });
        } else {
            return transform(realUser.getVardenhetList(), new Function<Vardenhet, Verksamhet>() {
                @Override
                public Verksamhet apply(Vardenhet vardEnhet) {
                    return toVerksamhet(vardEnhet, enhetsList);
                }
            });
        }
    }

    private Verksamhet toVerksamhet(Enhet enhet) {
        Kommun kommun = new Kommun();
        Lan lan = new Lan();
        return new Verksamhet(enhet.getEnhetId(), enhet.getNamn(), enhet.getVardgivareId(), enhet.getVardgivareNamn(), enhet.getLansId(),
                lan.getNamn(enhet.getLansId()), enhet.getKommunId(), kommun.getNamn(enhet.getLansId() + enhet.getKommunId()), getVerksamhetsTyper(enhet.getVerksamhetsTyper()));
    }

    private Verksamhet toVerksamhet(final Vardenhet vardEnhet, List<Enhet> enhetsList) {
        Optional<Enhet> enhetOpt = tryFind(enhetsList, new Predicate<Enhet>() {
            @Override
            public boolean apply(Enhet enhet) {
                return enhet.getEnhetId().equals(vardEnhet.getId());
            }
        });

        String lansId = enhetOpt.isPresent() ? enhetOpt.get().getLansId() : Lan.OVRIGT_ID;
        String lansNamn = lan.getNamn(lansId);
        String kommunId = enhetOpt.isPresent() ? lansId + enhetOpt.get().getKommunId() : Kommun.OVRIGT_ID;
        String kommunNamn = kommun.getNamn(kommunId);
        Set<Verksamhet.VerksamhetsTyp> verksamhetsTyper = enhetOpt.isPresent() ? getVerksamhetsTyper(enhetOpt.get().getVerksamhetsTyper()) : Collections.<Verksamhet.VerksamhetsTyp>emptySet();

        return new Verksamhet(vardEnhet.getId(), vardEnhet.getNamn(), vardEnhet.getVardgivarId(), vardEnhet.getVardgivarNamn(), lansId, lansNamn, kommunId, kommunNamn, verksamhetsTyper);
    }

    Set<Verksamhet.VerksamhetsTyp> getVerksamhetsTyper(String verksamhetsTyper) {
        return new HashSet<>(Lists.transform(ID_SPLITTER.splitToList(verksamhetsTyper), new Function<String, Verksamhet.VerksamhetsTyp>() {
            @Override
            public Verksamhet.VerksamhetsTyp apply(String verksamhetsId) {
                String groupId = verksamheter.getGruppId(verksamhetsId);
                String verksamhetsName = verksamheter.getNamn(groupId);
                return new Verksamhet.VerksamhetsTyp(groupId, verksamhetsName);
            }
        }));
    }

}
