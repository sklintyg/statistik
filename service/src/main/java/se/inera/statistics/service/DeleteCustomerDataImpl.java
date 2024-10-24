/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.statistics.service.warehouse.db.DeleteCustomerDataDB;

@Service
public class DeleteCustomerDataImpl implements DeleteCustomerData {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteCustomerDataImpl.class);

    private DeleteCustomerDataDB deleteCustomerDataDB;

    @Autowired
    public DeleteCustomerDataImpl(DeleteCustomerDataDB deleteCustomerDataDB) {
        this.deleteCustomerDataDB = deleteCustomerDataDB;
    }

    @Override
    public List<String> deleteCustomerDataByIntygsId(List<String> intygsIdList) {
        List<String> deletedIntygsIdList = new ArrayList<>();
        for (String intygsId : intygsIdList) {
            try {
                deleteCustomerDataDB.deleteFromHsa(intygsId);
                deleteCustomerDataDB.deleteFromIntygcommon(intygsId);
                deleteCustomerDataDB.deleteFromIntyghandelse(intygsId);
                deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(intygsId);
                deleteCustomerDataDB.deleteFromWideline(intygsId);
                deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsId);
            } catch (Exception e) {
                LOG.error(e.getMessage());
                continue;
            }
            deletedIntygsIdList.add(intygsId);
        }
        return deletedIntygsIdList;
    }

    @Override
    public List<String> deleteCustomerDataByVardgivarId(List<String> vardgivareIdList) {
        List<String> deletedVardgivareIdList = new ArrayList<>();
        for (String vardgivarId : vardgivareIdList) {
            try {
                deleteCustomerDataDB.deleteFromLakare(vardgivarId);
                deleteCustomerDataDB.deleteFromEnhet(vardgivarId);
            } catch (Exception e) {
                LOG.error(e.getMessage());
                continue;
            }
            deletedVardgivareIdList.add(vardgivarId);
        }
        return deletedVardgivareIdList;
    }
}