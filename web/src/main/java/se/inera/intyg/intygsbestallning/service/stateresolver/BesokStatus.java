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

import com.fasterxml.jackson.annotation.JsonIgnore;

import static se.inera.intyg.intygsbestallning.service.stateresolver.Actor.VARDADMIN;

public enum BesokStatus implements SortableLabel {
    BOKAT("Bokat", null),
    OMBOKAT("Ombokat", null),
    AVVIKELSE_RAPPORTERAD("Avvikelse rapporterad", null),
    AVVIKELSE_MOTTAGEN("Avvikelse mottagen", VARDADMIN),
    AVBOKAT("Avbokat", null),
    PATIENT_UTEBLEV("Patient uteblev", null),
    GENOMFORT("Genomfört", null),
    EJ_GENOMFORT("Ej genomfört", null);

    private final String id;
    private final String label;
    private final Actor nextActor;

    BesokStatus(String label, Actor nextActor) {
        this.id = this.name();
        this.label = label;
        this.nextActor = nextActor;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @JsonIgnore
    public Actor getNextActor() {
        return nextActor;
    }
}