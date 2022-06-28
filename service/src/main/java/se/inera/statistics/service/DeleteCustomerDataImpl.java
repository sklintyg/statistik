/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
import se.inera.statistics.service.warehouse.db.MeddelandehandelseMessagewidelineResultDao;

@Service
public class DeleteCustomerDataImpl implements DeleteCustomerData {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteCustomerDataImpl.class);

    private DeleteCustomerDataDB deleteCustomerDataDB;

    @Autowired
    public DeleteCustomerDataImpl(DeleteCustomerDataDB deleteCustomerDataDB) {
        this.deleteCustomerDataDB = deleteCustomerDataDB;
    }

    @Override
    public List<DeleteCustomerDataByIntygsIdDao> deleteCustomerDataByIntygsId(List<String> intygsIdList) {
        List<DeleteCustomerDataByIntygsIdDao> list = new ArrayList<>();
        for (String intygsId:intygsIdList) {
            DeleteCustomerDataByIntygsIdDao dbResult = new DeleteCustomerDataByIntygsIdDao();
            dbResult.setIntygsId(intygsId);
            try {
                dbResult.setRowsDeletedFromHsa(deleteCustomerDataDB.deleteFromHsa(intygsId));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            try {
                dbResult.setRowsDeletedFromIntygcommon(deleteCustomerDataDB.deleteFromIntygcommon(intygsId));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            try {
                dbResult.setRowsDeletedFromIntyghandelse(deleteCustomerDataDB.deleteFromIntyghandelse(intygsId));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            try {
                MeddelandehandelseMessagewidelineResultDao result =
                    deleteCustomerDataDB.deleteFromMeddelandehandelseAndMessagewideline(intygsId);
                dbResult.setRowsDeletedFromMeddelandehandelse(result.getMeddelandehandelseResult());
                dbResult.setRowsDeleteMessagewideline(result.getMessagewidelineResult());

            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            try {
                dbResult.setRowsDeleteFromMideline(deleteCustomerDataDB.deleteFromWideline(intygsId));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            try {
                dbResult.setRowsDeleteFromIntygsenthandelse(deleteCustomerDataDB.deleteFromIntygsenthandelse(intygsId));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
            list.add(dbResult);
        }
        return list;
    }

    @Override
    public List<DeletedEnhetDao> deleteCustomerDataByEnhetsId(List<String> enhetsIdList) {
        List<DeletedEnhetDao> deletedEnhetDaos = new ArrayList<>();
        for (String enhetsId: enhetsIdList) {
            DeletedEnhetDao deletedEnhetDao = new DeletedEnhetDao();
            deletedEnhetDao.setEnhetsId(enhetsId);
            try {
                deletedEnhetDao.setRowsDeletedFromEnhet(deleteCustomerDataDB.deleteFromEnhet(enhetsId));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
            deletedEnhetDaos.add(deletedEnhetDao);
        }
        return deletedEnhetDaos;
    }

    @Override
    public List<DeletedVardgivare> deleteCustomerDataByVardgivarId(List<String> vardgivareIdList) {
        List<DeletedVardgivare> deletedVardgivareDaos = new ArrayList<>();
        for (String vardgivarId: vardgivareIdList) {
            DeletedVardgivare deletedVardgivare = new DeletedVardgivare();
            deletedVardgivare.setVardgivareId(vardgivarId);
            try {
                deletedVardgivare.setRowsDeletedFromLakare(deleteCustomerDataDB.deleteFromLakare(vardgivarId));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            deletedVardgivareDaos.add(deletedVardgivare);
        }
        return deletedVardgivareDaos;
    }
}
