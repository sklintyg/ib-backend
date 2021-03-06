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
package se.inera.intyg.intygsbestallning.service.utredning;

import com.google.common.collect.ImmutableList;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

public final class ServiceTestUtil {
    private ServiceTestUtil() {
    }

    private static UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    public static IbUser buildUser() {
        return buildUser("careunit-1", "testorgnr");
    }

    public static IbUser buildUser(String enhetId, String vardenhetOrgnr) {
        IbUser user = new IbUser("user-1", "username");
        user.setCurrentlyLoggedInAt(new IbVardenhet(enhetId, "namnet", new IbVardgivare("vg", "namn", false), vardenhetOrgnr));
        return user;
    }

    public static List<Utredning> buildBestallningar(int num) {
        List<Utredning> utredningList = new ArrayList<>();
        for (long a = 0; a < num; a++) {
            Utredning utr = anUtredning()
                    .withUtredningsTyp(AFU)
                    .withUtredningId(a)
                    .withExternForfragan(anExternForfragan()
                            .withInternForfraganList(ImmutableList.of(
                                    anInternForfragan()
                                            .withVardenhetHsaId("enhet")
                                            .build()))
                            .withLandstingHsaId("vg-id")
                            .build())
                    .withBestallning(aBestallning()
                            .withTilldeladVardenhetHsaId("careunit-1")
                            .withTilldeladVardenhetOrgNr("testorgnr")
                            .build())
                    .withInvanare(anInvanare().withPersonId("19121212-121" + a).build())
                    .withIntygList(Collections.singletonList(anIntyg()
                            .withKomplettering(false)
                            .withSistaDatum(LocalDateTime.now().plusDays(10L))
                            .build()))
                    .build();
            // use the resolver to set status even in the test...
            utr.setStatus(utredningStatusResolver.resolveStatus(utr));
            utredningList.add(utr);
        }
        return utredningList;
    }
}
