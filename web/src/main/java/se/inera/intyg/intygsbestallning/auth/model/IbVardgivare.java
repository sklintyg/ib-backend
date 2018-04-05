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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IbVardgivare implements IbSelectableHsaEntity {

    private String id;
    private String name;
    private boolean isSamordnare = false;

    private List<IbVardenhet> vardenheter = new ArrayList<>();

    public IbVardgivare(String id, String name, boolean isSamordnare) {
        this.id = id;
        this.name = name;
        this.isSamordnare = isSamordnare;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public SelectableHsaEntityType getType() {
        return SelectableHsaEntityType.VG;
    }


    public void setId(String id) {
        this.id = id;
    }

    public boolean isSamordnare() {
        return isSamordnare;
    }

    public void setSamordnare(boolean samordnare) {
        isSamordnare = samordnare;
    }

    public List<IbVardenhet> getVardenheter() {
        return vardenheter;
    }

    public void setVardenheter(List<IbVardenhet> vardenheter) {
        this.vardenheter = vardenheter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IbVardgivare that = (IbVardgivare) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
