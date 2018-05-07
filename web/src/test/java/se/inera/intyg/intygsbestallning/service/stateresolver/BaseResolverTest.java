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
package se.inera.intyg.intygsbestallning.service.stateresolver;

import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.time.LocalDateTime;

public abstract class BaseResolverTest {

    protected Bestallning buildBestallning(LocalDateTime uppdateradDatum) {
        Bestallning b = new Bestallning();
        b.setUppdateradDatum(uppdateradDatum);
        return b;
    }

    protected ForfraganSvar buildForfraganSvar(SvarTyp svarTyp) {
        ForfraganSvar fs = new ForfraganSvar();
        fs.setSvarTyp(svarTyp);
        return fs;
    }

    protected InternForfragan buildInternForfragan(ForfraganSvar forfraganSvar, LocalDateTime tilldeladDatum) {
        InternForfragan internForfragan = new InternForfragan();
        internForfragan.setForfraganSvar(forfraganSvar);
        internForfragan.setTilldeladDatum(tilldeladDatum);
        return internForfragan;
    }

    protected Utredning buildBaseUtredning() {
        Utredning utr = new Utredning();
        utr.setExternForfragan(buildBaseExternForfragan());
        return utr;
    }

    protected ExternForfragan buildBaseExternForfragan() {
        ExternForfragan externForfragan = new ExternForfragan();

        return externForfragan;
    }
}
