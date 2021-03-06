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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.besok;

import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;

import java.util.List;

public class RedovisaBesokRequest {

    private List<RedovisaBesokListItem> redovisaBesokList;

    public List<RedovisaBesokListItem> getRedovisaBesokList() {
        return redovisaBesokList;
    }

    public void setRedovisaBesokList(List<RedovisaBesokListItem> redovisaBesokList) {
        this.redovisaBesokList = redovisaBesokList;
    }

    public static class RedovisaBesokListItem {
        private Long besokId;
        private TolkStatusTyp tolkStatus;
        private boolean genomfort;

        public RedovisaBesokListItem() {
        }

        public RedovisaBesokListItem(Long besokId, TolkStatusTyp tolkStatus, boolean genomfort) {
            this.besokId = besokId;
            this.tolkStatus = tolkStatus;
            this.genomfort = genomfort;
        }

        public Long getBesokId() {
            return besokId;
        }

        public void setBesokId(Long besokId) {
            this.besokId = besokId;
        }

        public TolkStatusTyp getTolkStatus() {
            return tolkStatus;
        }

        public void setTolkStatus(TolkStatusTyp tolkStatus) {
            this.tolkStatus = tolkStatus;
        }

        public boolean isGenomfort() {
            return genomfort;
        }

        public void setGenomfort(boolean genomfort) {
            this.genomfort = genomfort;
        }
    }
}
