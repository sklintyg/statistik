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
package se.inera.statistics.service.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @Column(name = "hsaid")
    private String hsaId;

    @Column(name = "show_messages_per_lakare")
    private boolean showMessagesPerLakare;

    public UserSettings() {
    }

    public UserSettings(String hsaId, boolean showMessagesPerLakare) {
        this.hsaId = hsaId;
        this.showMessagesPerLakare = showMessagesPerLakare;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public boolean isShowMessagesPerLakare() {
        return showMessagesPerLakare;
    }

    public void setShowMessagesPerLakare(boolean showMessagesPerLakare) {
        this.showMessagesPerLakare = showMessagesPerLakare;
    }
}
