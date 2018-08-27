/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.intygsbestallning.service.sprak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.service.sprak.dto.TolkSprak;

@RunWith(MockitoJUnitRunner.class)
public class TolkSprakLookupTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private TolkSprakLookup tolkSprakLookup;

    private static final String FILE_LOCATION = "fileLocation";
    private static final String PATH = "iso-639-3_20180123_test.tab";

    @Test
    public void lookupTolkSprakExists() throws IllegalAccessException {

        FieldUtils.writeField(tolkSprakLookup, FILE_LOCATION, PATH, true);
        final Resource resource = new ClassPathResource(PATH);

        doReturn(resource)
                .when(resourceLoader)
                .getResource(anyString());

        final TolkSprak tolkSprak = tolkSprakLookup.lookupTolkSprak("swe");

        assertThat(tolkSprak).hasFieldOrPropertyWithValue("refName" ,"Swedish");
    }

    @Test
    public void lookupTolkSprakNotExists() throws IllegalAccessException {

        FieldUtils.writeField(tolkSprakLookup, FILE_LOCATION, PATH, true);
        final Resource resource = new ClassPathResource(PATH);

        doReturn(resource)
                .when(resourceLoader)
                .getResource(anyString());

        assertThatThrownBy(() -> tolkSprakLookup.lookupTolkSprak("abc")).isExactlyInstanceOf(IbServiceException.class);
    }
}
