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
package se.inera.intyg.intygsbestallning.service.patient;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.schemas.contract.Personnummer;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class PatientNameEnricherImpl implements PatientNameEnricher {

    @Autowired
    private PUService puService;

    /**
     * Makes a n-persons call to the PU-service and returns PersonSvar objects. Includes sekretessmarkering
     */
    @Override
    public void enrichWithPatientNames(List<? extends PDLLoggable> paged) {
        Map<Personnummer, PersonSvar> personNames = puService
                .getPersons(paged.stream()
                        .map(bli -> Personnummer.createPersonnummer(bli.getPatientId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid personnummer " + bli.getPatientId())))
                        .collect(toList()));

        for (PDLLoggable bli : paged) {
            Personnummer pnr = Personnummer.createPersonnummer(bli.getPatientId()).get();
            Person person = personNames.get(pnr).getPerson();
            if (personNames.containsKey(pnr) && person != null) {
                bli.setPatientNamn(joinIgnoreNullAndEmpty(" ", person.getFornamn(), person.getMellannamn(), person.getEfternamn()));
            } else {
                bli.setPatientNamn(null);
            }
        }
    }

    private String joinIgnoreNullAndEmpty(String separator, String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (!Strings.isNullOrEmpty(value)) {
                if (builder.length() > 0) {
                    builder.append(separator);
                }
                builder.append(value);
            }
        }
        return builder.toString();
    }
}
