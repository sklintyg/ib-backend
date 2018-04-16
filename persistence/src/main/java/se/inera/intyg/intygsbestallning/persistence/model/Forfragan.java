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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "FORFRAGAN")
public class Forfragan {

    @Id
    @GeneratedValue
    @Column(name = "INTERNREFERENS", nullable = false)
    private Long internreferens;

    @Column(name = "UTREDNING_ID")
    private String utredningId;

    @Column(name = "VARDENHET_HSA_ID", nullable = false)
    private String vardenhetHsaId;

    @Column(name = "TILLDELAD_DATUM", nullable = true)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime tilldeladDatum;

    @Column(name = "BESVARAS_SENAST_DATUM", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besvarasSenastDatum;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "KOMMENTAR", nullable = true)
    private String kommentar;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "FORFRAGAN_ID")
    private ForfraganSvar forfraganSvar;


    public Long getInternreferens() {
        return internreferens;
    }

    public void setInternreferens(Long internreferens) {
        this.internreferens = internreferens;
    }

    public String getUtredningId() {
        return utredningId;
    }

    public void setUtredningId(String utredningId) {
        this.utredningId = utredningId;
    }

    public String getVardenhetHsaId() {
        return vardenhetHsaId;
    }

    public void setVardenhetHsaId(String vardenhetHsaId) {
        this.vardenhetHsaId = vardenhetHsaId;
    }

    public LocalDateTime getTilldeladDatum() {
        return tilldeladDatum;
    }

    public void setTilldeladDatum(LocalDateTime tilldeladDatum) {
        this.tilldeladDatum = tilldeladDatum;
    }

    public LocalDateTime getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(LocalDateTime besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public ForfraganSvar getForfraganSvar() {
        return forfraganSvar;
    }

    public void setForfraganSvar(ForfraganSvar forfraganSvar) {
        this.forfraganSvar = forfraganSvar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Forfragan forfragan = (Forfragan) o;
        return Objects.equals(internreferens, forfragan.internreferens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internreferens);
    }
}
