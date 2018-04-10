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
package se.inera.intyg.intygsbestallning.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "INVANARE")
public class Invanare {

    @Id
    @Column(name = "PERSON_ID")
    private String personId;

    // Always fetch from PU?
//    @Column(name = "FORNAMN")
//    private String fornamn;
//
//    @Column(name = "MELLANNAMN")
//    private String mellannamn;
//
//    @Column(name = "EFTERNAMN")
//    private String efternamn;

    @Column(name = "SARSKILDA_BEHOV")
    private String sarskildaBehov;

    @Column(name = "BAKGRUND_NULAGE")
    private String bakgrundNulage;

    // tidigare vårdenheter kan plocka fram dynamiskt?


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getSarskildaBehov() {
        return sarskildaBehov;
    }

    public void setSarskildaBehov(String sarskildaBehov) {
        this.sarskildaBehov = sarskildaBehov;
    }

    public String getBakgrundNulage() {
        return bakgrundNulage;
    }

    public void setBakgrundNulage(String bakgrundNulage) {
        this.bakgrundNulage = bakgrundNulage;
    }
}
