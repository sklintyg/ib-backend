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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "BESTALLNING")
public class Bestallning {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TILLDELAD_VARDENHET_HSA_ID", nullable = false)
    private String tilldeladVardenhetHsaId;

    @Column(name = "INTYG_KLART_SENAST")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime intygKlartSenast;

    @Column(name = "ORDER_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime orderDatum;

    @Column(name = "SYFTE")
    private String syfte;

    @Column(name = "PLANERADE_AKTIVITETER")
    private String planeradeAktiviteter;

    @Column(name = "KOMMENTAR")
    private String kommentar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTilldeladVardenhetHsaId() {
        return tilldeladVardenhetHsaId;
    }

    public void setTilldeladVardenhetHsaId(String tilldeladVardenhetHsaId) {
        this.tilldeladVardenhetHsaId = tilldeladVardenhetHsaId;
    }

    public LocalDateTime getIntygKlartSenast() {
        return intygKlartSenast;
    }

    public void setIntygKlartSenast(LocalDateTime intygKlartSenast) {
        this.intygKlartSenast = intygKlartSenast;
    }

    public LocalDateTime getOrderDatum() {
        return orderDatum;
    }

    public void setOrderDatum(LocalDateTime orderDatum) {
        this.orderDatum = orderDatum;
    }

    public String getSyfte() {
        return syfte;
    }

    public void setSyfte(String syfte) {
        this.syfte = syfte;
    }

    public String getPlaneradeAktiviteter() {
        return planeradeAktiviteter;
    }

    public void setPlaneradeAktiviteter(String planeradeAktiviteter) {
        this.planeradeAktiviteter = planeradeAktiviteter;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }
}
