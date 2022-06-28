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

public class DeleteCustomerDataByIntygsIdDao {

    private Integer rowsDeletedFromHsa;
    private Integer rowsDeletedFromIntygcommon;
    private Integer rowsDeletedFromIntyghandelse;
    private Integer rowsDeletedFromMeddelandehandelse;
    private Integer rowsDeleteMessagewideline;
    private Integer rowsDeleteFromMideline;
    private Integer rowsDeleteFromIntygsenthandelse;

    private String intygsId;

    public DeleteCustomerDataByIntygsIdDao() {
    }

    public Integer getRowsDeletedFromHsa() {
        return rowsDeletedFromHsa;
    }

    public void setRowsDeletedFromHsa(Integer rowsDeletedFromHsa) {
        this.rowsDeletedFromHsa = rowsDeletedFromHsa;
    }

    public Integer getRowsDeletedFromIntygcommon() {
        return rowsDeletedFromIntygcommon;
    }

    public void setRowsDeletedFromIntygcommon(Integer rowsDeletedFromIntygcommon) {
        this.rowsDeletedFromIntygcommon = rowsDeletedFromIntygcommon;
    }

    public Integer getRowsDeletedFromIntyghandelse() {
        return rowsDeletedFromIntyghandelse;
    }

    public void setRowsDeletedFromIntyghandelse(Integer rowsDeletedFromIntyghandelse) {
        this.rowsDeletedFromIntyghandelse = rowsDeletedFromIntyghandelse;
    }

    public Integer getRowsDeletedFromMeddelandehandelse() {
        return rowsDeletedFromMeddelandehandelse;
    }

    public void setRowsDeletedFromMeddelandehandelse(Integer rowsDeletedFromMeddelandehandelse) {
        this.rowsDeletedFromMeddelandehandelse = rowsDeletedFromMeddelandehandelse;
    }

    public Integer getRowsDeleteMessagewideline() {
        return rowsDeleteMessagewideline;
    }

    public void setRowsDeleteMessagewideline(Integer rowsDeleteMessagewideline) {
        this.rowsDeleteMessagewideline = rowsDeleteMessagewideline;
    }

    public Integer getRowsDeleteFromMideline() {
        return rowsDeleteFromMideline;
    }

    public void setRowsDeleteFromMideline(Integer rowsDeleteFromMideline) {
        this.rowsDeleteFromMideline = rowsDeleteFromMideline;
    }

    public Integer getRowsDeleteFromIntygsenthandelse() {
        return rowsDeleteFromIntygsenthandelse;
    }

    public void setRowsDeleteFromIntygsenthandelse(Integer rowsDeleteFromIntygsenthandelse) {
        this.rowsDeleteFromIntygsenthandelse = rowsDeleteFromIntygsenthandelse;
    }

    public void setIntygsId(String intygsId) {
        this.intygsId = intygsId;
    }

    public String getIntygsId() {
        return intygsId;
    }
}
