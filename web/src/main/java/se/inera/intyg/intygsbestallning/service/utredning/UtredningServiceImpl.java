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
package se.inera.intyg.intygsbestallning.service.utredning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Forfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtredningServiceImpl implements UtredningService {

    @Autowired
    private UtredningRepository utredningRepository;

    @Override
    public List<UtredningListItem> findUtredningarByVardgivareHsaId(String vardgivareHsaId) {
        List<Utredning> utredningar = utredningRepository.findByVardgivareHsaId(vardgivareHsaId);
        return utredningar.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public GetUtredningResponse getUtredning(String utredningId, String vardgivareHsaId) {
        Utredning utredning = utredningRepository.findOne(utredningId);
        if (utredning == null) {
            throw new IbServiceException(IbErrorCodeEnum.NOT_FOUND, "No utredning found for id '" + utredningId + "'");
        }

        // Check vårdgivare...
        if (!utredning.getVardgivareHsaId().equals(vardgivareHsaId)) {
            throw new IbServiceException(IbErrorCodeEnum.UNAUTHORIZED, "The user is not logged in at the applicable Vårdgivare");
        }

        // Enrich here if necessary...
        return convertToUtredningDTO(utredning);
    }

    @Override
    public List<ForfraganListItem> findForfragningarForVardenhetHsaId(String vardenhetHsaId) {
        List<Forfragan> forfragningar = utredningRepository.findForfragningarForVardenhetHsaId(vardenhetHsaId);
        return forfragningar.stream().map(this::convertForfragan).collect(Collectors.toList());
    }

    @Override
    public GetForfraganResponse getForfragan(Long forfraganId, String vardenhetHsaId) {
        Forfragan forfragan = utredningRepository.findForfraganByIdAndVardenhet(forfraganId, vardenhetHsaId);
        return convertCompleteForfragan(forfragan);
    }

    // TOOODOOO Fix converter classes..

    private GetForfraganResponse convertCompleteForfragan(Forfragan f) {
        GetForfraganResponse gfr = new GetForfraganResponse();
        gfr.setForfraganId(f.getInternreferens());
        gfr.setBesvarasSenastDatum(f.getBesvarasSenastDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        gfr.setKommentar(f.getKommentar());
        gfr.setStatus(f.getStatus());
        if (f.getTilldeladDatum() != null) {
            gfr.setTilldeladDatum(f.getTilldeladDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        }
        return gfr;
    }

    private ForfraganListItem convertForfragan(Forfragan f) {
        ForfraganListItem fli = new ForfraganListItem();
        fli.setForfraganId(f.getInternreferens());
        fli.setBesvarasSenastDatum(f.getBesvarasSenastDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        fli.setKommentar(f.getKommentar());
        fli.setStatus(f.getStatus());
        if (f.getTilldeladDatum() != null) {
            fli.setTilldeladDatum(f.getTilldeladDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        }
        return fli;
    }

    private GetUtredningResponse convertToUtredningDTO(Utredning utredning) {
        GetUtredningResponse gur = new GetUtredningResponse();
        gur.setUtredningsId(utredning.getUtredningId());
        gur.setUtredningsTyp(utredning.getUtredningsTyp());
        gur.setBesvarasSenastDatum(utredning.getBesvarasSenastDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        gur.setInkomDatum(utredning.getInkomDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        gur.setVardgivareHsaId(utredning.getVardgivareHsaId());

        gur.setHandlaggareNamn(utredning.getHandlaggareNamn());
        gur.setHandlaggareTelefonnummer(utredning.getHandlaggareTelefonnummer());
        gur.setHandlaggareEpost(utredning.getHandlaggareEpost());

        gur.setBehovTolk(utredning.isBehovTolk());
        gur.setSprakTolk(utredning.getSprakTolk());
        return gur;
    }

    private UtredningListItem convert(Utredning u) {
        UtredningListItem uli = new UtredningListItem();
        uli.setUtredningsId(u.getUtredningId());
        uli.setUtredningsTyp(u.getUtredningsTyp());
        uli.setVardgivareNamn(u.getVardgivareHsaId() + "-namnet");
        uli.setBesvarasSenastDatum(u.getBesvarasSenastDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        uli.setInkomDatum(u.getInkomDatum().format(DateTimeFormatter.BASIC_ISO_DATE));
        return uli;
    }
}
