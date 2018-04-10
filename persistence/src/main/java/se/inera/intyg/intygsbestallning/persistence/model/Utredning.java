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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "UTREDNING")
public class Utredning {

    @Id
    @Column(name = "UTREDNING_ID")
    private String forfraganId;

    @Column(name = "UTREDNINGS_TYP")
    private String utredningsTyp;

    @Column(name = "VARDGIVARE_HSA_ID")
    private String vardgivareHsaId;

    @Column(name = "INKOM_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime inkomDatum;

    @Column(name = "BESVARAS_SENAST_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besvarasSenastDatum;

    @Column(name = "INVANARE_PERSON_ID")
    private String invanarePersonId;

    @Column(name = "HANDLAGGARE_NAMN")
    private String handlaggareNamn;

    @Column(name = "HANDLAGGARE_TELEFONNUMMER")
    private String handlaggareTelefonnummer;

    @Column(name = "HANDLAGGARE_EPOST")
    private String handlaggareEpost;

    @Column(name = "BEHOV_TOLK")
    private boolean behovTolk;

    @Column(name = "SPRAK_TOLK")
    private String sprakTolk;

    @OneToMany
    private List<Comment> kommentarer;

    // Gör om till enum
    @Column(name = "STATUS")
    private String status;

    public String getUtredningId() {
        return forfraganId;
    }

    public void setUtredningId(String forfraganId) {
        this.forfraganId = forfraganId;
    }

    public String getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(String utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public LocalDateTime getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(LocalDateTime inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public LocalDateTime getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(LocalDateTime besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getInvanarePersonId() {
        return invanarePersonId;
    }

    public void setInvanarePersonId(String invanarePersonId) {
        this.invanarePersonId = invanarePersonId;
    }

    public String getHandlaggareNamn() {
        return handlaggareNamn;
    }

    public void setHandlaggareNamn(String handlaggareNamn) {
        this.handlaggareNamn = handlaggareNamn;
    }

    public String getHandlaggareTelefonnummer() {
        return handlaggareTelefonnummer;
    }

    public void setHandlaggareTelefonnummer(String handlaggareTelefonnummer) {
        this.handlaggareTelefonnummer = handlaggareTelefonnummer;
    }

    public String getHandlaggareEpost() {
        return handlaggareEpost;
    }

    public void setHandlaggareEpost(String handlaggareEpost) {
        this.handlaggareEpost = handlaggareEpost;
    }

    public boolean isBehovTolk() {
        return behovTolk;
    }

    public void setBehovTolk(boolean behovTolk) {
        this.behovTolk = behovTolk;
    }

    public String getSprakTolk() {
        return sprakTolk;
    }

    public void setSprakTolk(String sprakTolk) {
        this.sprakTolk = sprakTolk;
    }

    public List<Comment> getKommentarer() {
        return kommentarer;
    }

    public void setKommentarer(List<Comment> kommentarer) {
        this.kommentarer = kommentarer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Utredning forfragan = (Utredning) o;
        return Objects.equals(forfraganId, forfragan.forfraganId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(forfraganId);
    }
}
