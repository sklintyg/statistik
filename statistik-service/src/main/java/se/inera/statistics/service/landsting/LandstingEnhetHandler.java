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
package se.inera.statistics.service.landsting;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaId;
import se.inera.statistics.service.landsting.persistance.landsting.Landsting;
import se.inera.statistics.service.landsting.persistance.landsting.LandstingManager;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhetManager;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdate;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateManager;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateOperation;

import java.util.Collections;
import java.util.List;

@Component
public class LandstingEnhetHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LandstingEnhetHandler.class);

    @Autowired
    private LandstingManager landstingManager;

    @Autowired
    private LandstingEnhetManager landstingEnhetManager;

    @Autowired
    private LandstingEnhetUpdateManager landstingEnhetUpdateManager;

    public void update(LandstingEnhetFileData data) throws NoLandstingSetForVgException {
        final Optional<Landsting> landstingOptional = landstingManager.getForVg(data.getVgId());
        if (!landstingOptional.isPresent()) {
            LOG.warn("There is no landsting connected to vg: " + data.getVgId());
            throw new NoLandstingSetForVgException();
        }
        final long landstingId = landstingOptional.get().getId();
        landstingEnhetManager.update(landstingId, data.getRows());
        landstingEnhetUpdateManager.update(landstingId, data.getUserName(), data.getUserId(), removeInvalidChars(data.getFileName()), LandstingEnhetUpdateOperation.Update);
    }

    public void clear(HsaId vgId, String username, HsaId userId) throws NoLandstingSetForVgException {
        final Optional<Landsting> landstingOptional = landstingManager.getForVg(vgId);
        if (!landstingOptional.isPresent()) {
            LOG.warn("There is no landsting connected to vg: " + vgId);
            throw new NoLandstingSetForVgException();
        }
        final long landstingId = landstingOptional.get().getId();
        landstingEnhetManager.update(landstingId, Collections.<LandstingEnhetFileDataRow>emptyList());
        landstingEnhetUpdateManager.update(landstingId, username, userId, "-", LandstingEnhetUpdateOperation.Remove);
    }

    private String removeInvalidChars(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9åäöÅÄÖ.]", "_");
    }

    public Optional<LandstingEnhetUpdate> getLastUpdateInfo(HsaId vardgivarId) {
        final Optional<Landsting> landstingOptional = landstingManager.getForVg(vardgivarId);
        if (landstingOptional.isPresent()) {
            return landstingEnhetUpdateManager.getByLandstingId(landstingOptional.get().getId());
        }
        return Optional.absent();
    }

    public LandstingsVardgivareStatus getLandstingsVardgivareStatus(HsaId vardgivarId) {
        final Optional<Landsting> landstingOptional = landstingManager.getForVg(vardgivarId);
        if (landstingOptional.isPresent()) {
            if (landstingEnhetManager.getByLandstingId(landstingOptional.get().getId()).isEmpty()) {
                return LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITHOUT_UPLOAD;
            } else {
                return LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITH_UPLOAD;
            }
        } else {
            return LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE;
        }
    }

    public List<HsaId> getAllEnhetsForVardgivare(HsaId vgid) {
        final List<LandstingEnhet> allLandstingEnhetsForVardgivare = getAllLandstingEnhetsForVardgivare(vgid);
        return Lists.transform(allLandstingEnhetsForVardgivare, new Function<LandstingEnhet, HsaId>() {
            @Override
            public HsaId apply(LandstingEnhet landstingEnhet) {
                return landstingEnhet.getEnhetensHsaId();
            }
        });
    }

    public List<LandstingEnhet> getAllLandstingEnhetsForVardgivare(HsaId vgid) {
        final Optional<Landsting> landsting = landstingManager.getForVg(vgid);
        if (landsting.isPresent()) {
            final long landstingId = landsting.get().getId();
            return landstingEnhetManager.getByLandstingId(landstingId);
        }
        return Collections.emptyList();
    }

}
