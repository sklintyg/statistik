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
package se.inera.statistics.service.warehouse;

import se.inera.statistics.hsa.model.HsaIdAny;

import java.util.List;

public abstract class AbstractWidlineConverter {

    protected void checkField(List<String> errors, String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    protected void checkField(List<String> errors, IntygType field, String fieldName) {
        if (field == null) {
            errors.add(fieldName + " not found.");
        }
    }

    protected void checkField(List<String> errors, HsaIdAny field, String fieldName) {
        if (field == null || field.getId().isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    protected void checkField(List<String> errors, String field, String fieldName, int max) {
        checkField(errors, field, fieldName);
        if (field != null && field.length() > max) {
            errors.add(fieldName + " input too long");
        }
    }

    protected void checkField(List<String> errors, IntygType field, String fieldName, int max) {
        checkField(errors, field, fieldName);
        if (field != null && field.name().length() > max) {
            errors.add(fieldName + " input too long");
        }
    }

    protected void checkField(List<String> errors, HsaIdAny field, String fieldName, int max) {
        checkField(errors, field, fieldName);
        if (field != null && field.getId().length() > max) {
            errors.add(fieldName + " input too long");
        }
    }
}
