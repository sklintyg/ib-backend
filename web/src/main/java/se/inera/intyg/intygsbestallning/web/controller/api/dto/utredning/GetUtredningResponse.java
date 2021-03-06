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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning;

import static java.util.Objects.nonNull;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.service.stateresolver.SlutDatumFasResolver;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.HandelseListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.VardenhetListItem;

public class GetUtredningResponse {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private Long utredningsId;
    private UtredningStatus status;
    private UtredningFas fas;
    private UtredningsTyp utredningsTyp;
    private String slutdatumFas;
    private String inkomDatum;
    private String besvarasSenastDatum;
    private String bostadsort;
    private boolean tidigareUtredd;
    private List<VardenhetListItem> tidigareEnheter;
    private boolean behovTolk;

    private String tolkSprak;

    private String sarskildaBehov;

    private String kommentar;

    private String handlaggareNamn;

    private String handlaggareTelefonnummer;

    private String handlaggareEpost;

    private List<UtredningInternForfraganListItem> internForfraganList;

    private List<HandelseListItem> handelseList;

    public static GetUtredningResponse from(Utredning utredning, UtredningStatus status) {

        return GetUtredningResponseBuilder.aGetUtredningResponse()
                .withUtredningsId(utredning.getUtredningId())
                .withStatusAndFas(status)
                .withUtredningsTyp(utredning.getUtredningsTyp())
                .withSlutdatumFas(
                        SlutDatumFasResolver.resolveSlutDatumFas(utredning, status).map(DateTimeFormatter.ISO_DATE::format).orElse(null))
                .withInkomDatum(utredning.getExternForfragan()
                        .map(ef -> ef.getInkomDatum().format(formatter))
                        .orElse(null))
                .withBesvarasSenastDatum(utredning.getExternForfragan()
                        .map(ef -> ef.getBesvarasSenastDatum().format(formatter))
                        .orElse(null))
                .withBostadsort(nonNull(utredning.getInvanare())
                        ? utredning.getInvanare().getPostort() : null)
                .withTidigareUtredd(!utredning.getInvanare().getTidigareUtforare().isEmpty())
                .withTidigareEnheter(utredning.getInvanare().getTidigareUtforare())
                .withBehovTolk(BooleanUtils.toBoolean(utredning.getTolkBehov()))
                .withTolkSprak(utredning.getTolkSprak())
                .withSarskildaBehov(utredning.getInvanare().getSarskildaBehov())
                .withKommentar(utredning.getExternForfragan().map(ExternForfragan::getKommentar).orElse(null))
                .withHandlaggareNamn(utredning.getHandlaggare().getFullstandigtNamn())
                .withHandlaggareTelefonnummer(utredning.getHandlaggare().getTelefonnummer())
                .withHandlaggareEpost(utredning.getHandlaggare().getEmail())
                .withInternForfraganList(utredning.getExternForfragan()
                        .map(ExternForfragan::getInternForfraganList)
                        .orElse(Lists.newArrayList()))
                .withHandelseList(utredning.getHandelseList())
                .build();

    }

    public Long getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(Long utredningsId) {
        this.utredningsId = utredningsId;
    }

    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(UtredningStatus status) {
        this.status = status;
    }

    public UtredningFas getFas() {
        return fas;
    }

    public void setFas(UtredningFas fas) {
        this.fas = fas;
    }

    public String getSlutdatumFas() {
        return slutdatumFas;
    }

    public void setSlutdatumFas(String slutdatumFas) {
        this.slutdatumFas = slutdatumFas;
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

    public List<VardenhetListItem> getTidigareEnheter() {
        return tidigareEnheter;
    }

    public void setTidigareEnheter(List<VardenhetListItem> tidigareEnheter) {
        this.tidigareEnheter = tidigareEnheter;
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

    public List<UtredningInternForfraganListItem> getInternForfraganList() {
        return internForfraganList;
    }

    public void setInternForfraganList(List<UtredningInternForfraganListItem> internForfraganList) {
        this.internForfraganList = internForfraganList;
    }

    public List<HandelseListItem> getHandelseList() {
        return handelseList;
    }

    public void setHandelseList(List<HandelseListItem> handelseList) {
        this.handelseList = handelseList;
    }

    public UtredningsTyp getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(UtredningsTyp utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public static final class GetUtredningResponseBuilder {
        private Long utredningsId;
        private UtredningStatus status;
        private UtredningFas fas;
        private UtredningsTyp utredningsTyp;
        private String slutdatumFas;
        private String inkomDatum;
        private String besvarasSenastDatum;
        private String bostadsort;
        private boolean tidigareUtredd;
        private List<VardenhetListItem> tidigareEnheter;
        private boolean behovTolk;
        private String tolkSprak;
        private String sarskildaBehov;
        private String kommentar;
        private String handlaggareNamn;
        private String handlaggareTelefonnummer;
        private String handlaggareEpost;
        private List<UtredningInternForfraganListItem> internForfraganList;
        private List<HandelseListItem> handelseList;

        private GetUtredningResponseBuilder() {
        }

        public static GetUtredningResponseBuilder aGetUtredningResponse() {
            return new GetUtredningResponseBuilder();
        }

        public GetUtredningResponseBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public GetUtredningResponseBuilder withStatusAndFas(UtredningStatus status) {
            this.status = status;
            this.fas = status.getUtredningFas();
            return this;
        }

        public GetUtredningResponseBuilder withUtredningsTyp(UtredningsTyp utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public GetUtredningResponseBuilder withSlutdatumFas(String slutdatumFas) {
            this.slutdatumFas = slutdatumFas;
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

        public GetUtredningResponseBuilder withTidigareEnheter(List<TidigareUtforare> tidigareEnheter) {
            this.tidigareEnheter = tidigareEnheter.stream()
                    .map(tidigareUtforare -> new VardenhetListItem(tidigareUtforare.getTidigareEnhetId()))
                    .collect(Collectors.toList());
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

        public GetUtredningResponseBuilder withInternForfraganList(List<InternForfragan> internForfraganList) {
            this.internForfraganList = internForfraganList.stream()
                    .map(UtredningInternForfraganListItem::from)
                    .collect(Collectors.toList());
            return this;
        }

        public GetUtredningResponseBuilder withHandelseList(List<Handelse> handelseList) {
            this.handelseList = handelseList.stream()
                    .map(handelse -> HandelseListItem.from(handelse, false))
                    .collect(Collectors.toList());
            return this;
        }

        public GetUtredningResponse build() {
            GetUtredningResponse getUtredningResponse = new GetUtredningResponse();
            getUtredningResponse.setUtredningsId(utredningsId);
            getUtredningResponse.setStatus(status);
            getUtredningResponse.setFas(fas);
            getUtredningResponse.setUtredningsTyp(utredningsTyp);
            getUtredningResponse.setSlutdatumFas(slutdatumFas);
            getUtredningResponse.setInkomDatum(inkomDatum);
            getUtredningResponse.setBesvarasSenastDatum(besvarasSenastDatum);
            getUtredningResponse.setBostadsort(bostadsort);
            getUtredningResponse.setTidigareUtredd(tidigareUtredd);
            getUtredningResponse.setTidigareEnheter(tidigareEnheter);
            getUtredningResponse.setBehovTolk(behovTolk);
            getUtredningResponse.setTolkSprak(tolkSprak);
            getUtredningResponse.setSarskildaBehov(sarskildaBehov);
            getUtredningResponse.setKommentar(kommentar);
            getUtredningResponse.setHandlaggareNamn(handlaggareNamn);
            getUtredningResponse.setHandlaggareTelefonnummer(handlaggareTelefonnummer);
            getUtredningResponse.setHandlaggareEpost(handlaggareEpost);
            getUtredningResponse.setInternForfraganList(internForfraganList);
            getUtredningResponse.setHandelseList(handelseList);
            return getUtredningResponse;
        }

    }
}
