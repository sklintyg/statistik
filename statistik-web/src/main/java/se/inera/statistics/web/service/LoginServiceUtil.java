/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.transform;
import static java.util.stream.Collectors.toMap;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;

import se.inera.auth.LoginVisibility;
import se.inera.auth.model.User;
import se.inera.auth.model.UserAccessLevel;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.LandstingsVardgivareStatus;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.UserAccessInfo;
import se.inera.statistics.web.model.Verksamhet;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@Component
public class LoginServiceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LoginServiceUtil.class);

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private LandstingEnhetHandler landstingEnhetHandler;

    @Autowired(required = false)
    private LoginVisibility loginVisibility;

    @Value("${highcharts.export.url}")
    private String higchartsExportUrl;
    @Value("${login.url}")
    private String loginUrl;


    private Kommun kommun = new Kommun();

    private Lan lan = new Lan();

    private VerksamhetsTyp verksamheter = new VerksamhetsTyp();

    private static final Splitter ID_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    public boolean isLoggedIn(HttpServletRequest request) {
        return (request.getUserPrincipal() != null)
                && (((AbstractAuthenticationToken) request.getUserPrincipal()).getDetails() != null);
    }

    public LoginInfo getLoginInfo(HttpServletRequest request) {
        User realUser;
        try {
            realUser = getCurrentUser(request);
        } catch (IllegalStateException e) {
            return new LoginInfo();
        }
        Map<HsaIdVardgivare, String> allVgNames = realUser.getVardenhetList().stream()
                .collect(toMap(Vardenhet::getVardgivarId, Vardenhet::getVardgivarNamn, (p, q) -> p));
        List<Verksamhet> verksamhets = getVerksamhetsList(realUser);
        final List<LoginInfoVg> loginInfoVgs = allVgNames.entrySet().stream()
                .map(vgidWithName -> toLoginInfoVg(realUser, vgidWithName))
                .collect(Collectors.toList());
        return new LoginInfo(realUser.getHsaId(), realUser.getName(), verksamhets, loginInfoVgs);
    }

    private LoginInfoVg toLoginInfoVg(User realUser, Map.Entry<HsaIdVardgivare, String> vgidWithName) {
        final HsaIdVardgivare vgId = vgidWithName.getKey();
        final boolean processledare = realUser.isProcessledareForVg(vgId);
        final List<Vardenhet> vardenhetsForVg = realUser.getVardenhetsForVg(vgId);
        final UserAccessLevel userAccessLevel = new UserAccessLevel(processledare, vardenhetsForVg.size());
        final String vgName = vgidWithName.getValue();
        final LandstingsVardgivareStatus landstingsVardgivareStatus = landstingEnhetHandler.getLandstingsVardgivareStatus(vgId);
        return new LoginInfoVg(vgId, vgName, landstingsVardgivareStatus, userAccessLevel);
    }

    private User getCurrentUser(HttpServletRequest request) {
        final Principal user = request.getUserPrincipal();
        if (!(user instanceof AbstractAuthenticationToken)) {
            LOG.error("user object is of wrong type: " + user);
            throw new IllegalStateException("user object is of wrong type: " + user);
        }
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) user;
        final Object details = token.getDetails();
        if (!(details instanceof User)) {
            LOG.warn("details object is of wrong type: " + details);
            throw new IllegalStateException("details object is of wrong type: " + details);
        }
        return (User) details;
    }

    private List<Verksamhet> getVerksamhetsList(User realUser) {
        Collection<Verksamhet> allUserVerksamhets = realUser.getVardenhetList().stream()
                .map(Vardenhet::getVardgivarId)
                .map(hsaIdVardgivare -> {
                    List<Enhet> allEnhetsForVg = warehouse.getEnhets(hsaIdVardgivare);
                    if (realUser.isProcessledareForVg(hsaIdVardgivare) && allEnhetsForVg != null && !allEnhetsForVg.isEmpty()) {
                        return transform(allEnhetsForVg, this::toVerksamhet);
                    } else {
                        return transform(realUser.getVardenhetsForVg(hsaIdVardgivare), vardEnhet -> toVerksamhet(vardEnhet, allEnhetsForVg));
                    }
                })
                .flatMap(List::stream)
                .collect(toMap(Verksamhet::getId, p -> p, (p, q) -> p))
                .values();
        return new ArrayList<>(allUserVerksamhets);
    }

    Verksamhet toVerksamhet(Enhet enhet) {
        Kommun kommun = new Kommun();
        Lan lan = new Lan();
        return new Verksamhet(enhet.getEnhetId(), enhet.getNamn(), enhet.getVardgivareId(), null, enhet.getLansId(),
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
        Set<Verksamhet.VerksamhetsTyp> verksamhetsTyper = enhetOpt.isPresent() ? getVerksamhetsTyper(enhetOpt.get().getVerksamhetsTyper()) : Collections.<Verksamhet.VerksamhetsTyp>singleton(new Verksamhet.VerksamhetsTyp(VerksamhetsTyp.OVRIGT_ID, VerksamhetsTyp.OVRIGT));

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

    HsaIdVardgivare getSelectedVgIdForLoggedInUser(HttpServletRequest request) {
        return getCurrentUser(request).getSelectedVardgivare();
    }

    public AppSettings getSettings(HttpServletRequest request) {
        AppSettings settings = new AppSettings();
        settings.setLoginVisible(loginVisibility.isLoginVisible());
        settings.setHighchartsExportUrl(higchartsExportUrl);
        settings.setLoginUrl(loginUrl);
        settings.setLoggedIn(isLoggedIn(request));
        settings.setSjukskrivningLengths(Arrays.stream(SjukfallsLangdGroup.values()).collect(toMap(Enum::name, SjukfallsLangdGroup::getGroupName)));
        return settings;
    }

    public UserAccessInfo getUserAccessInfo(HttpServletRequest request) {
        final HsaIdVardgivare vgId = getCurrentUser(request).getSelectedVardgivare();
        final LoginInfo loginInfo = getLoginInfo(request);
        final HsaIdUser hsaId = loginInfo.getHsaId();
        final LoginInfoVg vgInfo = loginInfo.getLoginInfoForVg(vgId).orElse(null);
        final List<Verksamhet> businessesForVg = loginInfo.getBusinessesForVg(vgId);
        return new UserAccessInfo(hsaId, vgInfo, businessesForVg);
    }

    public void setSelectedVg(HttpServletRequest request, HsaIdVardgivare vgId) {
        final User user = getCurrentUser(request);
        user.setSelectedVardgivare(vgId);
    }

}
