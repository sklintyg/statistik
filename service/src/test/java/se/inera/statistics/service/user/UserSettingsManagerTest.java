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

import static org.junit.Assert.assertNull;

import jakarta.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserSettingsManagerTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserSettingsManager userSettingsManager;

    @Test
    public void findMissingSetting() {
        Mockito.when(entityManager.find(Mockito.eq(UserSettings.class), Mockito.anyString())).thenReturn(null);

        UserSettings settings = userSettingsManager.find("notFound");

        assertNull(settings);
    }

    @Test
    public void save() {
        UserSettings userSettings = new UserSettings();
        userSettings.setHsaId("test");

        userSettingsManager.save(userSettings);

        Mockito.verify(entityManager, Mockito.times(1)).merge(Mockito.any());
    }
}