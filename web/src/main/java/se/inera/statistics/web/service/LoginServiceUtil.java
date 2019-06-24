/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import se.inera.auth.LoginVisibility;
import se.inera.auth.model.User;
import se.inera.auth.model.UserAccessLevel;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.ia.model.Banner;
import se.inera.intyg.infra.integration.ia.model.BannerPriority;
import se.inera.intyg.infra.integration.ia.services.IABannerService;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.region.RegionEnhetHandler;
import se.inera.statistics.service.region.RegionsVardgivareStatus;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.user.UserSettings;
import se.inera.statistics.service.user.UserSettingsManager;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.StaticData;
import se.inera.statistics.web.model.UserAccessInfo;
import se.inera.statistics.web.model.UserSettingsDTO;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.util.VersionUtil;

import static java.util.stream.Collectors.toMap;

@Component
public class LoginServiceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LoginServiceUtil.class);

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private RegionEnhetHandler regionEnhetHandler;

    @Autowired(required = false)
    private LoginVisibility loginVisibility;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private VersionUtil versionUtil;

    @Autowired
    private UserSettingsManager userSettingsManager;

    @Autowired
    private IABannerService iaBannerService;

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
            RegionsVardgivareStatus regionsVardgivareStatus = regionEnhetHandler.getRegionsVardgivareStatus(vgHsaId);

            LoginInfoVg livg = new LoginInfoVg(vgHsaId, vardgivare.getNamn(), regionsVardgivareStatus, new UserAccessLevel(true, 0));
            if (loginInfoVgs.contains(livg)) {
                loginInfoVgs.remove(livg);
            }
            loginInfoVgs.add(livg);
        }

        String hsaId = realUser.getHsaId().getId();

        UserSettingsDTO userSettingsDTO = getUserSettings(hsaId);

        return new LoginInfo(realUser.getHsaId(), realUser.getName(), verksamhets, loginInfoVgs, userSettingsDTO,
                getAuthenticationMethod());
    }

    private UserSettingsDTO getUserSettings(String hsaId) {
        UserSettings userSettings = userSettingsManager.find(hsaId);

        UserSettingsDTO userSettingsDTO;

        if (userSettings != null) {
            userSettingsDTO = new UserSettingsDTO(userSettings.isShowMessagesPerLakare());
        } else {
            userSettingsDTO = new UserSettingsDTO();
        }

        return userSettingsDTO;
    }

    private LoginInfoVg toLoginInfoVg(User realUser, Map.Entry<HsaIdVardgivare, String> vgidWithName) {
        final HsaIdVardgivare vgId = vgidWithName.getKey();
        final boolean processledare = realUser.isProcessledareForVg(vgId);
        final List<Vardenhet> vardenhetsForVg = realUser.getVardenhetsForVg(vgId);
        final UserAccessLevel userAccessLevel = new UserAccessLevel(processledare, vardenhetsForVg.size());
        final String vgName = vgidWithName.getValue();
        final RegionsVardgivareStatus regionsVardgivareStatus = regionEnhetHandler.getRegionsVardgivareStatus(vgId);
        return new LoginInfoVg(vgId, vgName, regionsVardgivareStatus, userAccessLevel);
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

    private String getAuthenticationMethod() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication object is null");
        }
        Object credentials = authentication.getCredentials();
        if ((credentials instanceof SAMLCredential)) {
            if (!"fake-idp".equals(((SAMLCredential) credentials).getRemoteEntityID())) {
                return "SITHS";
            }
        }
        return "FAKE";
    }

    private List<Verksamhet> getVerksamhetsList(User realUser) {
        return Stream.concat(realUser.getVardenhetList().stream()
                .map(Vardenhet::getVardgivarId),
                realUser.getVgsWithProcessledarStatus().stream()
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

    public Verksamhet enhetToVerksamhet(Enhet enhet) {
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

    public HsaIdVardgivare getSelectedVgIdForLoggedInUser(HttpServletRequest request) {
        return new HsaIdVardgivare(request.getParameter("vgid"));
    }

    public AppSettings getSettings() {
        AppSettings settings = new AppSettings();
        settings.setLoginVisible(loginVisibility.isLoginVisible());
        settings.setLoginUrl(loginUrl);
        settings.setLoggedIn(isLoggedIn());
        settings.setProjectVersion(versionUtil.getProjectVersion());
        settings.setDriftbanners(getBanners());
        return settings;
    }

    private ErrorSeverity mapToSeverity(BannerPriority priority) {
        switch (priority) {
            case HOG:
                return ErrorSeverity.ERROR;
            case MEDEL:
                return ErrorSeverity.WARN;
            case LAG:
                return ErrorSeverity.INFO;
        }

        return ErrorSeverity.INFO;
    }

    private List<Message> getBanners() {
        List<Banner> banners = iaBannerService.getCurrentBanners();

        return banners.stream()
                .map(banner ->
                        Message.create(ErrorType.UNSET, mapToSeverity(banner.getPriority()), banner.getMessage()))
                .collect(Collectors.toList());
    }

    public StaticData getStaticData() {
        final Map<String, String> sjukskrivningLengths = Arrays
                .stream(SjukfallsLangdGroup.values())
                .collect(toMap(Enum::name, SjukfallsLangdGroup::getGroupName));
        final Map<String, String> ageGroups = Arrays
                .stream(AgeGroup.values())
                .collect(toMap(Enum::name, AgeGroup::getGroupName));
        final Map<String, String> intygTypes = IntygType.getInIntygtypFilter().stream()
                .collect(toMap(Enum::name, IntygType::getText));
        final Map<String, String> intygTypeTooltips = Arrays.stream(IntygType.values())
                .collect(toMap(IntygType::getText, IntygType::getShortText));

        final List<Icd> icdStructure = icd10.getIcdStructure();
        return new StaticData(sjukskrivningLengths, ageGroups, intygTypes, intygTypeTooltips, icdStructure);
    }

    public UserAccessInfo getUserAccessInfoForVg(HsaIdVardgivare vgId) {
        final LoginInfo loginInfo = getLoginInfo();
        final HsaIdUser hsaId = loginInfo.getHsaId();
        final LoginInfoVg vgInfo = loginInfo.getLoginInfoForVg(vgId).orElse(null);
        final List<Verksamhet> businessesForVg = loginInfo.getBusinessesForVg(vgId);
        return new UserAccessInfo(hsaId, vgInfo, businessesForVg);
    }

    public UserSettingsDTO saveUserSettings(UserSettingsDTO userSettingsDTO) {
        User realUser = getCurrentUser();
        String hsaId = realUser.getHsaId().getId();

        UserSettings userSettings = new UserSettings(hsaId, userSettingsDTO.isShowMessagesPerLakare());

        userSettingsManager.save(userSettings);

        return userSettingsDTO;
    }

}
