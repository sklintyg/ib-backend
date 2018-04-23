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

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "FORFRAGAN_SVAR")
public class ForfraganSvar {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "SVAR_TYP", nullable = false)
    @Enumerated(EnumType.STRING)
    private SvarTyp svarTyp;

    @Enumerated(EnumType.STRING)
    @Column(name = "UTFORARE_TYP", nullable = false)
    private UtforareTyp utforareTyp;

    @Column(name = "UTFORARE_NAMN", nullable = false)
    private String utforareNamn;

    @Column(name = "UTFORARE_ADRESS", nullable = false)
    private String utforareAdress;

    @Column(name = "UTFORARE_POSTNUMMER", nullable = false)
    private String utforarePostnr;

    @Column(name = "UTFORARE_POSTORT", nullable = false)
    private String utforarePostort;

    @Column(name = "UTFORARE_TELEFONNUMMER")
    private String utforareTelefon;

    @Column(name = "UTFORARE_EPOST")
    private String utforareEpost;

    @Column(name = "KOMMENTAR")
    private String kommentar;

    @Column(name = "BORJA_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    private LocalDate borjaDatum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SvarTyp getSvarTyp() {
        return svarTyp;
    }

    public void setSvarTyp(SvarTyp svarTyp) {
        this.svarTyp = svarTyp;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public UtforareTyp getUtforareTyp() {
        return utforareTyp;
    }

    public void setUtforareTyp(UtforareTyp utforareTyp) {
        this.utforareTyp = utforareTyp;
    }

    public String getUtforareNamn() {
        return utforareNamn;
    }

    public void setUtforareNamn(String utforareNamn) {
        this.utforareNamn = utforareNamn;
    }

    public String getUtforareAdress() {
        return utforareAdress;
    }

    public void setUtforareAdress(String utforareAdress) {
        this.utforareAdress = utforareAdress;
    }

    public String getUtforarePostnr() {
        return utforarePostnr;
    }

    public void setUtforarePostnr(String utforarePostnr) {
        this.utforarePostnr = utforarePostnr;
    }

    public String getUtforarePostort() {
        return utforarePostort;
    }

    public void setUtforarePostort(String utforarePostort) {
        this.utforarePostort = utforarePostort;
    }

    public String getUtforareTelefon() {
        return utforareTelefon;
    }

    public void setUtforareTelefon(String utforareTelefon) {
        this.utforareTelefon = utforareTelefon;
    }

    public String getUtforareEpost() {
        return utforareEpost;
    }

    public void setUtforareEpost(String utforareEpost) {
        this.utforareEpost = utforareEpost;
    }

    public LocalDate getBorjaDatum() {
        return borjaDatum;
    }

    public void setBorjaDatum(LocalDate borjaDatum) {
        this.borjaDatum = borjaDatum;
    }
}
