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
package se.inera.intyg.intygsbestallning.web.controller.api.dto;

import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class GetUtredningResponse {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private String utredningsId;

    private String inkomDatum;

    private String besvarasSenastDatum;

    private String bostadsort;

    private boolean tidigareUtredd;

    List<FelaktigEnhet> felaktigaEnheter;

    private boolean behovTolk;

    private String tolkSprak;

    private String sarskildaBehov;

    private String kommentar;

    private String handlaggareNamn;

    private String handlaggareTelefonnummer;

    private String handlaggareEpost;

    private List<UtredningInternForfraganListItem> internforfraganList;

    private List<UtredningHandelseListItem> handelseList;

    public static GetUtredningResponse from(Utredning utredning) {

        return GetUtredningResponseBuilder.aGetUtredningResponse()
                .withUtredningsId(utredning.getUtredningId())
                .withInkomDatum(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getInkomDatum().format(formatter) : null)
                .withBesvarasSenastDatum(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getBesvarasSenastDatum().format(formatter) : null)
                .withBostadsort(!isNull(utredning.getInvanare())
                        ? utredning.getInvanare().getPostort() : null)
                .withTidigareUtredd(false)
                .withFelaktigaEnheter(new ArrayList<>())
                .withBehovTolk(utredning.getTolkBehov() != null)
                .withTolkSprak(utredning.getTolkSprak())
                .withSarskildaBehov(utredning.getInvanare().getSarskildaBehov())
                .withKommentar(utredning.getExternForfragan().getKommentar())
                .withHandlaggareNamn(utredning.getHandlaggare().getFullstandigtNamn())
                .withHandlaggareTelefonnummer(utredning.getHandlaggare().getTelefonnummer())
                .withHandlaggareEpost(utredning.getHandlaggare().getEmail())
                .withInternForfraganList(utredning, utredning.getExternForfragan().getInternForfraganList())
                .withHandelseList(utredning.getHandelseList())
                .build();

    }

    public String getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(String utredningsId) {
        this.utredningsId = utredningsId;
    }

    public String getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(String inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public String getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(String besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getBostadsort() {
        return bostadsort;
    }

    public void setBostadsort(String bostadsort) {
        this.bostadsort = bostadsort;
    }

    public boolean isTidigareUtredd() {
        return tidigareUtredd;
    }

    public void setTidigareUtredd(boolean tidigareUtredd) {
        this.tidigareUtredd = tidigareUtredd;
    }

    public List<FelaktigEnhet> getFelaktigaEnheter() {
        return felaktigaEnheter;
    }

    public void setFelaktigaEnheter(List<FelaktigEnhet> felaktigaEnheter) {
        this.felaktigaEnheter = felaktigaEnheter;
    }

    public boolean isBehovTolk() {
        return behovTolk;
    }

    public void setBehovTolk(boolean behovTolk) {
        this.behovTolk = behovTolk;
    }

    public String getTolkSprak() {
        return tolkSprak;
    }

    public void setTolkSprak(String tolkSprak) {
        this.tolkSprak = tolkSprak;
    }

    public String getSarskildaBehov() {
        return sarskildaBehov;
    }

    public void setSarskildaBehov(String sarskildaBehov) {
        this.sarskildaBehov = sarskildaBehov;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
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

    public List<UtredningInternForfraganListItem> getInternforfraganList() {
        return internforfraganList;
    }

    public void setInternforfraganList(List<UtredningInternForfraganListItem> internforfraganList) {
        this.internforfraganList = internforfraganList;
    }

    public List<UtredningHandelseListItem> getHandelseList() {
        return handelseList;
    }

    public void setHandelseList(List<UtredningHandelseListItem> handelseList) {
        this.handelseList = handelseList;
    }

    public static final class GetUtredningResponseBuilder {
        private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        private String utredningsId;
        private String inkomDatum;
        private String besvarasSenastDatum;
        private String bostadsort;
        private boolean tidigareUtredd;
        private List<FelaktigEnhet> felaktigaEnheter;
        private boolean behovTolk;
        private String tolkSprak;
        private String sarskildaBehov;
        private String kommentar;
        private String handlaggareNamn;
        private String handlaggareTelefonnummer;
        private String handlaggareEpost;
        private List<UtredningInternForfraganListItem> internForfraganList;
        private List<UtredningHandelseListItem> handelseList;

        private GetUtredningResponseBuilder() {
        }

        public static GetUtredningResponseBuilder aGetUtredningResponse() {
            return new GetUtredningResponseBuilder();
        }

        public GetUtredningResponseBuilder withUtredningsId(String utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public GetUtredningResponseBuilder withInkomDatum(String inkomDatum) {
            this.inkomDatum = inkomDatum;
            return this;
        }

        public GetUtredningResponseBuilder withBesvarasSenastDatum(String besvarasSenastDatum) {
            this.besvarasSenastDatum = besvarasSenastDatum;
            return this;
        }

        public GetUtredningResponseBuilder withBostadsort(String bostadsort) {
            this.bostadsort = bostadsort;
            return this;
        }

        public GetUtredningResponseBuilder withTidigareUtredd(boolean tidigareUtredd) {
            this.tidigareUtredd = tidigareUtredd;
            return this;
        }

        public GetUtredningResponseBuilder withFelaktigaEnheter(List<FelaktigEnhet> felaktigaEnheter) {
            this.felaktigaEnheter = felaktigaEnheter;
            return this;
        }

        public GetUtredningResponseBuilder withBehovTolk(boolean behovTolk) {
            this.behovTolk = behovTolk;
            return this;
        }

        public GetUtredningResponseBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        public GetUtredningResponseBuilder withSarskildaBehov(String sarskildaBehov) {
            this.sarskildaBehov = sarskildaBehov;
            return this;
        }

        public GetUtredningResponseBuilder withKommentar(String kommentar) {
            this.kommentar = kommentar;
            return this;
        }

        public GetUtredningResponseBuilder withHandlaggareNamn(String handlaggareNamn) {
            this.handlaggareNamn = handlaggareNamn;
            return this;
        }

        public GetUtredningResponseBuilder withHandlaggareTelefonnummer(String handlaggareTelefonnummer) {
            this.handlaggareTelefonnummer = handlaggareTelefonnummer;
            return this;
        }

        public GetUtredningResponseBuilder withHandlaggareEpost(String handlaggareEpost) {
            this.handlaggareEpost = handlaggareEpost;
            return this;
        }

        public GetUtredningResponseBuilder withInternForfraganList(Utredning utredning, List<InternForfragan> internForfraganList) {
            this.internForfraganList = internForfraganList.stream()
                    .map(internForfragan -> UtredningInternForfraganListItem.from(utredning, internForfragan))
                    .collect(Collectors.toList());
            return this;
        }

        public GetUtredningResponseBuilder withHandelseList(List<Handelse> handelseList) {
            this.handelseList = handelseList.stream()
                    .map(UtredningHandelseListItem::from)
                    .collect(Collectors.toList());
            return this;
        }

        public GetUtredningResponse build() {
            GetUtredningResponse getUtredningResponse = new GetUtredningResponse();
            getUtredningResponse.setUtredningsId(utredningsId);
            getUtredningResponse.setInkomDatum(inkomDatum);
            getUtredningResponse.setBesvarasSenastDatum(besvarasSenastDatum);
            getUtredningResponse.setBostadsort(bostadsort);
            getUtredningResponse.setTidigareUtredd(tidigareUtredd);
            getUtredningResponse.setFelaktigaEnheter(felaktigaEnheter);
            getUtredningResponse.setBehovTolk(behovTolk);
            getUtredningResponse.setTolkSprak(tolkSprak);
            getUtredningResponse.setSarskildaBehov(sarskildaBehov);
            getUtredningResponse.setKommentar(kommentar);
            getUtredningResponse.setHandlaggareNamn(handlaggareNamn);
            getUtredningResponse.setHandlaggareTelefonnummer(handlaggareTelefonnummer);
            getUtredningResponse.setHandlaggareEpost(handlaggareEpost);
            getUtredningResponse.setInternforfraganList(internForfraganList);
            getUtredningResponse.setHandelseList(handelseList);
            return getUtredningResponse;
        }

    }

    public static final class FelaktigEnhet {
        private String hsaId;
        private String errorMessage;
    }
}
