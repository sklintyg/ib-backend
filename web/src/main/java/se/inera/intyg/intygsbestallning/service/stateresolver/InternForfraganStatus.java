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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InternForfraganStatus implements SortableLabel {
    INKOMMEN("Inkommen", InternForfraganFas.FORFRAGAN, Actor.VARDADMIN),
    ACCEPTERAD_VANTAR_PA_TILLDELNINGSBESLUT("Accepterad, väntar på tilldelningsbeslut", InternForfraganFas.FORFRAGAN, Actor.SAMORDNARE),
    DIREKTTILLDELAD("Direkttilldelad", InternForfraganFas.TILLDELAD, Actor.SAMORDNARE),
    TILLDELAD_VANTAR_PA_BESTALLNING("Tilldelad, väntar på beställning", InternForfraganFas.TILLDELAD, Actor.FK),
    AVVISAD("Avvisad", InternForfraganFas.AVSLUTAD, Actor.NONE),
    EJ_TILLDELAD("Ej tilldelad", InternForfraganFas.AVSLUTAD, Actor.NONE),
    INGEN_BESTALLNING("Ingen beställning", InternForfraganFas.AVSLUTAD, Actor.NONE),
    BESTALLD("Beställd", InternForfraganFas.AVSLUTAD, Actor.NONE);

    private final String id;
    private final String label;
    private final InternForfraganFas internForfraganFas;
    private final Actor nextActor;

    InternForfraganStatus(String label, InternForfraganFas internForfraganFas, Actor nextActor) {
        this.id = this.name();
        this.label = label;
        this.internForfraganFas = internForfraganFas;
        this.nextActor = nextActor;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @JsonIgnore
    public InternForfraganFas getInternForfraganFas() {
        return internForfraganFas;
    }

    @JsonIgnore
    public Actor getNextActor() {
        return nextActor;
    }
}
