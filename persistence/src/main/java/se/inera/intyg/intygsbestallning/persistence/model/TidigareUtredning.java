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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TIDIGARE_UTREDNING")
public class TidigareUtredning {

    @Id
    @GeneratedValue
    @Column(name = "INTERNREFERENS")
    private Long internReferens;

    @Column(name = "UTREDNING_ID")
    private String utredningId;

    @Column(name = "TIDIGARE_UTREDNING_ID")
    private String tidigareUtredningId;

    public Long getInternReferens() {
        return internReferens;
    }

    public void setInternReferens(Long internReferens) {
        this.internReferens = internReferens;
    }

    public String getUtredningId() {
        return utredningId;
    }

    public void setUtredningId(String utredningId) {
        this.utredningId = utredningId;
    }

    public String getTidigareUtredningId() {
        return tidigareUtredningId;
    }

    public void setTidigareUtredningId(String tidigareUtredningId) {
        this.tidigareUtredningId = tidigareUtredningId;
    }
}
