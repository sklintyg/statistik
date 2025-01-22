/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.region;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.region.persistance.region.Region;
import se.inera.statistics.service.region.persistance.region.RegionManager;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhet;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhetManager;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdate;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdateManager;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdateOperation;

@Component
public class RegionEnhetHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RegionEnhetHandler.class);

    @Autowired
    private RegionManager regionManager;

    @Autowired
    private RegionEnhetManager regionEnhetManager;

    @Autowired
    private RegionEnhetUpdateManager regionEnhetUpdateManager;

    public void update(RegionEnhetFileData data) throws NoRegionSetForVgException {
        final Optional<Region> regionOptional = regionManager.getForVg(data.getVgId());
        if (!regionOptional.isPresent()) {
            LOG.warn("There is no region connected to vg: " + data.getVgId());
            throw new NoRegionSetForVgException();
        }
        final long regionId = regionOptional.get().getId();
        regionEnhetManager.update(regionId, data.getRows());
        regionEnhetUpdateManager.update(regionId, data.getUserName(), data.getUserId(), removeInvalidChars(data.getFileName()),
            RegionEnhetUpdateOperation.UPDATE);
    }

    public void clear(HsaIdVardgivare vgId, String username, HsaIdUser userId) throws NoRegionSetForVgException {
        final Optional<Region> regionOptional = regionManager.getForVg(vgId);
        if (!regionOptional.isPresent()) {
            LOG.warn("There is no region connected to vg: " + vgId);
            throw new NoRegionSetForVgException();
        }
        final long regionId = regionOptional.get().getId();
        regionEnhetManager.update(regionId, Collections.emptyList());
        regionEnhetUpdateManager.update(regionId, username, userId, "-", RegionEnhetUpdateOperation.REMOVE);
    }

    private String removeInvalidChars(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9åäöÅÄÖ.]", "_");
    }

    public Optional<RegionEnhetUpdate> getLastUpdateInfo(HsaIdVardgivare vardgivarId) {
        final Optional<Region> regionOptional = regionManager.getForVg(vardgivarId);
        return regionOptional.flatMap(region -> regionEnhetUpdateManager.getByRegionId(region.getId()));
    }

    public RegionsVardgivareStatus getRegionsVardgivareStatus(HsaIdVardgivare vardgivarId) {
        final Optional<Region> regionOptional = regionManager.getForVg(vardgivarId);
        if (regionOptional.isPresent()) {
            if (regionEnhetManager.getByRegionId(regionOptional.get().getId()).isEmpty()) {
                return RegionsVardgivareStatus.REGIONSVARDGIVARE_WITHOUT_UPLOAD;
            } else {
                return RegionsVardgivareStatus.REGIONSVARDGIVARE_WITH_UPLOAD;
            }
        } else {
            return RegionsVardgivareStatus.NO_REGIONSVARDGIVARE;
        }
    }

    public List<HsaIdEnhet> getAllEnhetsForVardgivare(HsaIdVardgivare vgid) {
        final List<RegionEnhet> allRegionEnhetsForVardgivare = getAllRegionEnhetsForVardgivare(vgid);
        return allRegionEnhetsForVardgivare.stream().map(RegionEnhet::getEnhetensHsaId).collect(Collectors.toList());
    }

    public List<RegionEnhet> getAllRegionEnhetsForVardgivare(HsaIdVardgivare vgid) {
        final Optional<Region> region = regionManager.getForVg(vgid);
        if (region.isPresent()) {
            final long regionId = region.get().getId();
            return regionEnhetManager.getByRegionId(regionId);
        }
        return Collections.emptyList();
    }

}
