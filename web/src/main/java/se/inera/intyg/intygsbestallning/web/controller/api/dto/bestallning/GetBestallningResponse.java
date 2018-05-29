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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning;

import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.EndReason;
import se.inera.intyg.intygsbestallning.service.patient.Gender;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.HandelseListItem;
import se.inera.intyg.schemas.contract.Personnummer;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class GetBestallningResponse {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private Long utredningsId;

    private String utredningsTyp;

    private String vardgivareHsaId;

    private String inkomDatum;

    private String besvarasSenastDatum;

    private InvanareResponse invanare;

    private String handlaggareNamn;

    private String handlaggareTelefonnummer;

    private String handlaggareEpost;

    private boolean behovTolk;

    private String tolkSprak;

    private UtredningStatus status;

    private String intygSistaDatum;

    private String intygSistaDatumKomplettering;

    private String avbrutenDatum;

    private EndReason avbrutenAnledning;

    private String meddelandeFromHandlaggare;

    private List<BesokListItem> besokList;

    private List<HandelseListItem> handelseList;

    private List<AnteckningListItem> anteckningList;

    public static GetBestallningResponse from(Utredning utredning, UtredningStatus utredningStatus) {

        return GetBestallningResponseBuilder.agetBestallningResponse()
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardgivareHsaId(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getLandstingHsaId() : null)
                .withInkomDatum(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getInkomDatum().format(formatter) : null)
                .withBesvarasSenastDatum(!isNull(utredning.getExternForfragan())
                        ? utredning.getExternForfragan().getBesvarasSenastDatum().format(formatter) : null)
                .withInvanare(!isNull(utredning.getInvanare())
                        ? new InvanareResponse(utredning.getInvanare()) : null)
                .withHandlaggareNamn(utredning.getHandlaggare().getFullstandigtNamn())
                .withHandlaggareTelefonnummer(utredning.getHandlaggare().getTelefonnummer())
                .withHandlaggareEpost(utredning.getHandlaggare().getEmail())
                .withBehovTolk(utredning.getTolkBehov() != null)
                .withTolkSprak(utredning.getTolkSprak())
                .withStatus(utredningStatus)
                .withIntygSistaDatum(utredning.getIntygList()
                        .stream()
                        .filter(intyg -> intyg.getSistaDatum() != null)
                        .sorted(Comparator.comparing(Intyg::getSistaDatum))
                        .findFirst()
                        .map(intyg -> intyg.getSistaDatum().format(formatter)).orElse(null))
                .withIntygSistaDatumKomplettering(utredning.getIntygList()
                        .stream()
                        .filter(intyg -> intyg.getSistaDatumKompletteringsbegaran() != null)
                        .sorted(Comparator.comparing(Intyg::getSistaDatumKompletteringsbegaran))
                        .findFirst()
                        .map(intyg -> intyg.getSistaDatumKompletteringsbegaran().format(formatter)).orElse(null))
                .withAvbrutenDatum(!isNull(utredning.getAvbrutenDatum())
                        ? utredning.getAvbrutenDatum().format(formatter) : null)
                .withAvbrutenAnledning(utredning.getAvbrutenAnledning())
                .withMeddelandeFromHandlaggare(utredning.getBestallning().map(bestallning -> bestallning.getKommentar()).orElse(null))
                .withBesokList(utredning.getBesokList().stream().map(BesokListItem::from).collect(Collectors.toList()))
                .withHandelseList(utredning.getHandelseList().stream().map(HandelseListItem::from).collect(Collectors.toList()))
                .withAnteckningList(utredning.getAnteckningList().stream().map(AnteckningListItem::from).collect(Collectors.toList()))
                .build();

    }

    public Long getUtredningsId() {
        return utredningsId;
    }

    public void setUtredningsId(final Long utredningsId) {
        this.utredningsId = utredningsId;
    }

    public String getUtredningsTyp() {
        return utredningsTyp;
    }

    public void setUtredningsTyp(final String utredningsTyp) {
        this.utredningsTyp = utredningsTyp;
    }

    public String getVardgivareHsaId() {
        return vardgivareHsaId;
    }

    public void setVardgivareHsaId(final String vardgivareHsaId) {
        this.vardgivareHsaId = vardgivareHsaId;
    }

    public String getInkomDatum() {
        return inkomDatum;
    }

    public void setInkomDatum(final String inkomDatum) {
        this.inkomDatum = inkomDatum;
    }

    public String getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(final String besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public InvanareResponse getInvanare() {
        return invanare;
    }

    public void setInvanare(InvanareResponse invanare) {
        this.invanare = invanare;
    }

    public String getHandlaggareNamn() {
        return handlaggareNamn;
    }

    public void setHandlaggareNamn(final String handlaggareNamn) {
        this.handlaggareNamn = handlaggareNamn;
    }

    public String getHandlaggareTelefonnummer() {
        return handlaggareTelefonnummer;
    }

    public void setHandlaggareTelefonnummer(final String handlaggareTelefonnummer) {
        this.handlaggareTelefonnummer = handlaggareTelefonnummer;
    }

    public String getHandlaggareEpost() {
        return handlaggareEpost;
    }

    public void setHandlaggareEpost(final String handlaggareEpost) {
        this.handlaggareEpost = handlaggareEpost;
    }

    public boolean isBehovTolk() {
        return behovTolk;
    }

    public void setBehovTolk(final boolean behovTolk) {
        this.behovTolk = behovTolk;
    }

    public String getTolkSprak() {
        return tolkSprak;
    }

    public void setTolkSprak(final String tolkSprak) {
        this.tolkSprak = tolkSprak;
    }

    public UtredningStatus getStatus() {
        return status;
    }

    public void setStatus(final UtredningStatus status) {
        this.status = status;
    }

    public String getIntygSistaDatum() {
        return intygSistaDatum;
    }

    public void setIntygSistaDatum(String intygSistaDatum) {
        this.intygSistaDatum = intygSistaDatum;
    }

    public String getIntygSistaDatumKomplettering() {
        return intygSistaDatumKomplettering;
    }

    public void setIntygSistaDatumKomplettering(String intygSistaDatumKomplettering) {
        this.intygSistaDatumKomplettering = intygSistaDatumKomplettering;
    }

    public String getAvbrutenDatum() {
        return avbrutenDatum;
    }

    public void setAvbrutenDatum(String avbrutenDatum) {
        this.avbrutenDatum = avbrutenDatum;
    }

    public EndReason getAvbrutenAnledning() {
        return avbrutenAnledning;
    }

    public void setAvbrutenAnledning(EndReason avbrutenAnledning) {
        this.avbrutenAnledning = avbrutenAnledning;
    }

    public String getMeddelandeFromHandlaggare() {
        return meddelandeFromHandlaggare;
    }

    public void setMeddelandeFromHandlaggare(String meddelandeFromHandlaggare) {
        this.meddelandeFromHandlaggare = meddelandeFromHandlaggare;
    }

    public List<BesokListItem> getBesokList() {
        return besokList;
    }

    public void setBesokList(List<BesokListItem> besokList) {
        this.besokList = besokList;
    }

    public List<HandelseListItem> getHandelseList() {
        return handelseList;
    }

    public void setHandelseList(List<HandelseListItem> handelseList) {
        this.handelseList = handelseList;
    }

    public List<AnteckningListItem> getAnteckningList() {
        return anteckningList;
    }

    public void setAnteckningList(List<AnteckningListItem> anteckningList) {
        this.anteckningList = anteckningList;
    }

    public static final class GetBestallningResponseBuilder {
        private Long utredningsId;
        private String utredningsTyp;
        private String vardgivareHsaId;
        private String inkomDatum;
        private String besvarasSenastDatum;
        private InvanareResponse invanare;
        private String handlaggareNamn;
        private String handlaggareTelefonnummer;
        private String handlaggareEpost;
        private boolean behovTolk;
        private String tolkSprak;
        private UtredningStatus status;
        private String intygSistaDatum;
        private String intygSistaDatumKomplettering;
        private String avbrutenDatum;
        private EndReason avbrutenAnledning;
        private String meddelandeFromHandlaggare;
        private List<BesokListItem> besokList;
        private List<HandelseListItem> handelseList;
        private List<AnteckningListItem> anteckningList;

        private GetBestallningResponseBuilder() {
        }

        public static GetBestallningResponseBuilder agetBestallningResponse() {
            return new GetBestallningResponseBuilder();
        }

        public GetBestallningResponseBuilder withUtredningsId(Long utredningsId) {
            this.utredningsId = utredningsId;
            return this;
        }

        public GetBestallningResponseBuilder withUtredningsTyp(String utredningsTyp) {
            this.utredningsTyp = utredningsTyp;
            return this;
        }

        public GetBestallningResponseBuilder withVardgivareHsaId(String vardgivareHsaId) {
            this.vardgivareHsaId = vardgivareHsaId;
            return this;
        }

        public GetBestallningResponseBuilder withInkomDatum(String inkomDatum) {
            this.inkomDatum = inkomDatum;
            return this;
        }

        public GetBestallningResponseBuilder withBesvarasSenastDatum(String besvarasSenastDatum) {
            this.besvarasSenastDatum = besvarasSenastDatum;
            return this;
        }

        public GetBestallningResponseBuilder withInvanare(InvanareResponse invanare) {
            this.invanare = invanare;
            return this;
        }

        public GetBestallningResponseBuilder withHandlaggareNamn(String handlaggareNamn) {
            this.handlaggareNamn = handlaggareNamn;
            return this;
        }

        public GetBestallningResponseBuilder withHandlaggareTelefonnummer(String handlaggareTelefonnummer) {
            this.handlaggareTelefonnummer = handlaggareTelefonnummer;
            return this;
        }

        public GetBestallningResponseBuilder withHandlaggareEpost(String handlaggareEpost) {
            this.handlaggareEpost = handlaggareEpost;
            return this;
        }

        public GetBestallningResponseBuilder withBehovTolk(boolean behovTolk) {
            this.behovTolk = behovTolk;
            return this;
        }

        public GetBestallningResponseBuilder withTolkSprak(String tolkSprak) {
            this.tolkSprak = tolkSprak;
            return this;
        }

        public GetBestallningResponseBuilder withStatus(UtredningStatus status) {
            this.status = status;
            return this;
        }

        public GetBestallningResponseBuilder withIntygSistaDatum(String datum) {
            this.intygSistaDatum = datum;
            return this;
        }

        public GetBestallningResponseBuilder withIntygSistaDatumKomplettering(String datum) {
            this.intygSistaDatumKomplettering = datum;
            return this;
        }

        public GetBestallningResponseBuilder withAvbrutenDatum(String avbrutenDatum) {
            this.avbrutenDatum = avbrutenDatum;
            return this;
        }

        public GetBestallningResponseBuilder withAvbrutenAnledning(EndReason avbrutenAnledning) {
            this.avbrutenAnledning = avbrutenAnledning;
            return this;
        }

        public GetBestallningResponseBuilder withMeddelandeFromHandlaggare(String meddelandeFromHandlaggare) {
            this.meddelandeFromHandlaggare = meddelandeFromHandlaggare;
            return this;
        }

        public GetBestallningResponseBuilder withBesokList(List<BesokListItem> besokList) {
            this.besokList = besokList;
            return this;
        }

        public GetBestallningResponseBuilder withHandelseList(List<HandelseListItem> handelseList) {
            this.handelseList = handelseList;
            return this;
        }

        public GetBestallningResponseBuilder withAnteckningList(List<AnteckningListItem> anteckningList) {
            this.anteckningList = anteckningList;
            return this;
        }

        public GetBestallningResponse build() {
            GetBestallningResponse getBestallningResponse = new GetBestallningResponse();
            getBestallningResponse.setUtredningsId(utredningsId);
            getBestallningResponse.setUtredningsTyp(utredningsTyp);
            getBestallningResponse.setVardgivareHsaId(vardgivareHsaId);
            getBestallningResponse.setInkomDatum(inkomDatum);
            getBestallningResponse.setBesvarasSenastDatum(besvarasSenastDatum);
            getBestallningResponse.setInvanare(invanare);
            getBestallningResponse.setHandlaggareNamn(handlaggareNamn);
            getBestallningResponse.setHandlaggareTelefonnummer(handlaggareTelefonnummer);
            getBestallningResponse.setHandlaggareEpost(handlaggareEpost);
            getBestallningResponse.setBehovTolk(behovTolk);
            getBestallningResponse.setTolkSprak(tolkSprak);
            getBestallningResponse.setStatus(status);
            getBestallningResponse.setIntygSistaDatum(intygSistaDatum);
            getBestallningResponse.setIntygSistaDatumKomplettering(intygSistaDatumKomplettering);
            getBestallningResponse.setAvbrutenDatum(avbrutenDatum);
            getBestallningResponse.setAvbrutenAnledning(avbrutenAnledning);
            getBestallningResponse.setMeddelandeFromHandlaggare(meddelandeFromHandlaggare);
            getBestallningResponse.setBesokList(besokList);
            getBestallningResponse.setHandelseList(handelseList);
            getBestallningResponse.setAnteckningList(anteckningList);
            return getBestallningResponse;
        }
    }

    public static final class InvanareResponse {

        private String personId;
        private String name;
        private Gender gender;
        private String sarskildaBehov;

        public InvanareResponse(Invanare invanare) {
            personId = invanare.getPersonId();
            sarskildaBehov = invanare.getSarskildaBehov();
            name = invanare.getFullstandigtNamn();
            gender = Optional.ofNullable(invanare.getPersonId())
                    .flatMap(Personnummer::createPersonnummer)
                    .map(Gender::getGenderFromPersonnummer)
                    .orElse(Gender.UNKNOWN);
        }

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getSarskildaBehov() {
            return sarskildaBehov;
        }

        public void setSarskildaBehov(String sarskildaBehov) {
            this.sarskildaBehov = sarskildaBehov;
        }
    }
}
