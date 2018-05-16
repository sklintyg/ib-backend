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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Created by eriklupander on 2016-06-22.
 * <p>
 * Note that unique constraint is handled by liquibase DB setup.
 */
@Entity
@Table(name = "ANVANDARE_PREFERENCE")
public final class AnvandarPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "HSA_ID", nullable = false)
    private String hsaId;

    @Column(name = "PREF_KEY", nullable = false)
    private String key;

    @Column(name = "PREF_VALUE")
    private String value;

    public AnvandarPreference() {
        // default constructor for hibernate
    }

    public AnvandarPreference(String hsaId, String key, String value) {
        this.hsaId = hsaId;
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(final String hsaId) {
        this.hsaId = hsaId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnvandarPreference)) {
            return false;
        }
        final AnvandarPreference that = (AnvandarPreference) o;
        return Objects.equals(id, that.id)
                && Objects.equals(hsaId, that.hsaId)
                && Objects.equals(key, that.key)
                && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, hsaId, key, value);
    }
}
