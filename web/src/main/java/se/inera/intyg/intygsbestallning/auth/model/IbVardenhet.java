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
package se.inera.intyg.intygsbestallning.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class IbVardenhet implements IbSelectableHsaEntity {

    private String id;
    private String name;

    private IbVardgivare parent;

    private IbVardenhet() {
    }

    public IbVardenhet(String id, String name, IbVardgivare parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SelectableHsaEntityType getType() {
        return SelectableHsaEntityType.VE;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public IbVardgivare getParent() {
        return parent;
    }

    public void setParent(IbVardgivare parent) {
        this.parent = parent;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IbVardenhet that = (IbVardenhet) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public String getParentId() {
        if (this.parent != null) {
            return this.parent.getId();
        }
        return null;
    }

    public String getParentName() {
        if (this.parent != null) {
            return this.parent.getName();
        }
        return null;
    }
}
