/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import se.inera.auth.LoginVisibility;
import se.inera.auth.model.User;
import se.inera.auth.model.UserAccessLevel;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.landsting.LandstingsVardgivareStatus;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.StaticFilterData;
import se.inera.statistics.web.model.UserAccessInfo;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.util.VersionUtil;

import static java.util.stream.Collectors.toMap;

@Component
public class LoginServiceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LoginServiceUtil.class);

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private LandstingEnhetHandler landstingEnhetHandler;

    @Autowired(required = false)
    private LoginVisibility loginVisibility;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private VersionUtil versionUtil;

    @Value("${login.url}")
    private String loginUrl;

    private Kommun kommun = new Kommun();

    private Lan lan = new Lan();

    private VerksamhetsTyp verksamheter = new VerksamhetsTyp();

    private static final Splitter ID_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    public boolean isLoggedIn() {
        try {
            getCurrentUser();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public LoginInfo getLoginInfo() {
        User realUser;
        try {
            realUser = getCurrentUser();
        } catch (IllegalStateException e) {
            LOG.warn("Could not get current user", e);
            return new LoginInfo();
        }
        Map<HsaIdVardgivare, String> allVgNames = realUser.getVardenhetList().stream()
                .collect(toMap(Vardenhet::getVardgivarId, Vardenhet::getVardgivarNamn, (p, q) -> p));

        List<Verksamhet> verksamhets = getVerksamhetsList(realUser);

        final List<LoginInfoVg> loginInfoVgs = allVgNames.entrySet().stream()
                .map(vgidWithName -> toLoginInfoVg(realUser, vgidWithName))
                .collect(Collectors.toList());

        // INTYG-3446: We create a LoginInfoVg entry for each item in vgWithProcessledarStatus
        for (Vardgivare vardgivare : realUser.getVgsWithProcessledarStatus()) {
            HsaIdVardgivare vgHsaId = new HsaIdVardgivare(vardgivare.getId());
            LandstingsVardgivareStatus landstingsVardgivareStatus = landstingEnhetHandler.getLandstingsVardgivareStatus(vgHsaId);

            LoginInfoVg livg = new LoginInfoVg(vgHsaId, vardgivare.getNamn(), landstingsVardgivareStatus, new UserAccessLevel(true, 0));
            if (loginInfoVgs.contains(livg)) {
                loginInfoVgs.remove(livg);
            }
            loginInfoVgs.add(livg);
        }

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

    private User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication object is null");
        }
        final Object details = authentication.getPrincipal();
        if (!(details instanceof User)) {
            throw new IllegalStateException("details object is of wrong type: " + details);
        }
        return (User) details;
    }

    private List<Verksamhet> getVerksamhetsList(User realUser) {
        return Stream.concat(realUser.getVardenhetList().stream()
                .map(Vardenhet::getVardgivarId), realUser.getVgsWithProcessledarStatus().stream()
                        .map(vg -> new HsaIdVardgivare(vg.getId())))
                .distinct()
                .map(hsaIdVardgivare -> {
                    Collection<Enhet> allEnhetsForVg = warehouse.getEnhets(hsaIdVardgivare);
                    if (realUser.isProcessledareForVg(hsaIdVardgivare) && allEnhetsForVg != null && !allEnhetsForVg.isEmpty()) {
                        return allEnhetsForVg.stream().map(this::enhetToVerksamhet);
                    } else {
                        final List<Vardenhet> vardenhetsForVg = realUser.getVardenhetsForVg(hsaIdVardgivare);
                        return vardenhetsForVg.stream().map(vardEnhet -> vardenhetToVerksamhet(vardEnhet, allEnhetsForVg));
                    }
                })
                .flatMap(i -> i)
                .collect(Collectors.toList());
    }

    Verksamhet enhetToVerksamhet(Enhet enhet) {
        Kommun kommun = new Kommun();
        Lan lan = new Lan();
        return new Verksamhet(enhet.getEnhetId(), enhet.getNamn(), enhet.getVardgivareId(), null, enhet.getLansId(),
                lan.getNamn(enhet.getLansId()), enhet.getKommunId(), kommun.getNamn(enhet.getLansId() + enhet.getKommunId()),
                getVerksamhetsTyper(enhet.getVerksamhetsTyper()));
    }

    private Verksamhet vardenhetToVerksamhet(final Vardenhet vardEnhet, Collection<Enhet> enhetsList) {
        Optional<Enhet> enhetOpt = enhetsList.stream()
                .filter(enhet -> enhet.getEnhetId().equals(vardEnhet.getId()))
                .findAny();

        String lansId = enhetOpt.map(Enhet::getLansId).orElse(Lan.OVRIGT_ID);
        String lansNamn = lan.getNamn(lansId);
        String kommunId = enhetOpt.map(enhet -> lansId + enhet.getKommunId()).orElse(Kommun.OVRIGT_ID);
        String kommunNamn = kommun.getNamn(kommunId);
        Set<Verksamhet.VerksamhetsTyp> verksamhetsTyper = enhetOpt
                .map(enhet -> getVerksamhetsTyper(enhet.getVerksamhetsTyper()))
                .orElseGet(() -> Collections.singleton(new Verksamhet.VerksamhetsTyp(VerksamhetsTyp.OVRIGT_ID, VerksamhetsTyp.OVRIGT)));

        return new Verksamhet(vardEnhet.getId(), vardEnhet.getNamn(), vardEnhet.getVardgivarId(), vardEnhet.getVardgivarNamn(), lansId,
                lansNamn, kommunId,
                kommunNamn, verksamhetsTyper);
    }

    private Set<Verksamhet.VerksamhetsTyp> getVerksamhetsTyper(String verksamhetsTyper) {
        return ID_SPLITTER.splitToList(verksamhetsTyper).stream().map(verksamhetsId -> {
            String groupId = verksamheter.getGruppId(verksamhetsId);
            String verksamhetsName = verksamheter.getNamn(groupId);
            return new Verksamhet.VerksamhetsTyp(groupId, verksamhetsName);
        }).collect(Collectors.toSet());
    }

    HsaIdVardgivare getSelectedVgIdForLoggedInUser(HttpServletRequest request) {
        return new HsaIdVardgivare(request.getParameter("vgid"));
    }

    AppSettings getSettings() {
        AppSettings settings = new AppSettings();
        settings.setLoginVisible(loginVisibility.isLoginVisible());
        settings.setLoginUrl(loginUrl);
        settings.setLoggedIn(isLoggedIn());
        settings.setProjectVersion(versionUtil.getProjectVersion());
        return settings;
    }

    StaticFilterData getStaticFilterData() {
        final Map<String, String> sjukskrivningLengths = Arrays
                .stream(SjukfallsLangdGroup.values())
                .collect(toMap(Enum::name, SjukfallsLangdGroup::getGroupName));
        final Map<String, String> ageGroups = Arrays
                .stream(AgeGroup.values())
                .collect(toMap(Enum::name, AgeGroup::getGroupName));
        final List<Icd> icdStructure = icd10.getIcdStructure();
        return new StaticFilterData(sjukskrivningLengths, ageGroups, icdStructure);
    }

    UserAccessInfo getUserAccessInfoForVg(HttpServletRequest request, HsaIdVardgivare vgId) {
        final LoginInfo loginInfo = getLoginInfo();
        final HsaIdUser hsaId = loginInfo.getHsaId();
        final LoginInfoVg vgInfo = loginInfo.getLoginInfoForVg(vgId).orElse(null);
        final List<Verksamhet> businessesForVg = loginInfo.getBusinessesForVg(vgId);
        return new UserAccessInfo(hsaId, vgInfo, businessesForVg);
    }

}
