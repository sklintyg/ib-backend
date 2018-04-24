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
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Forfragan;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtredning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.IIType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UtredningServiceImpl implements UtredningService {

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private LogService logService;

    @Override
    public Utredning registerNewUtredning(RequestHealthcarePerformerForAssessmentType req) {

        validateVardgivareExists(req);

        Utredning u = new Utredning();

        u.setUtredningId(UUID.randomUUID().toString());
        u.setUtredningsTyp(req.getCertificateType().getCode());
        u.setVardgivareHsaId(req.getCoordinatingCountyCouncilId().getExtension());
        u.setInvanareTidigareUtredning(buildTidigareUtredningar(req.getCitizen().getEarlierAssessmentPerformer()));
        u.setBesvarasSenastDatum(LocalDate.parse(req.getLastResponseDate(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay());
        u.setBehovTolk(req.getInterpreterLanguage() != null && req.getInterpreterLanguage().getDisplayName() != null);
        u.setSprakTolk(req.getInterpreterLanguage().getDisplayName());
        u.setHandlaggareNamn(req.getAuthorityAdministrativeOfficial().getFullName());
        u.setHandlaggareEpost(req.getAuthorityAdministrativeOfficial().getEmail());
        u.setHandlaggareTelefonnummer(req.getAuthorityAdministrativeOfficial().getPhoneNumber());
        u.setInvanarePostort(req.getCitizen().getPostalCity().getDisplayName());
        u.setInvanareSpecialbehov(req.getCitizen().getSpecialNeeds());
        u.setKommentar(req.getComment());

        return utredningRepository.save(u);
    }

    private void validateVardgivareExists(RequestHealthcarePerformerForAssessmentType req) {
        try {
            hsaOrganizationsService.getVardgivareInfo(req.getCoordinatingCountyCouncilId().getExtension());
        } catch (Exception e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Could not verify coordinatingCountyCouncilId '" + req.getCoordinatingCountyCouncilId().getExtension() + "' with HSA");
        }
    }

    private List<TidigareUtredning> buildTidigareUtredningar(List<IIType> earlierAssessmentPerformer) {
        return earlierAssessmentPerformer.stream().map(ap -> {
            TidigareUtredning tidigareUtredning = new TidigareUtredning();
            tidigareUtredning.setTidigareUtredningId(ap.getExtension());
            return tidigareUtredning;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UtredningListItem> findUtredningarByVardgivareHsaId(String vardgivareHsaId) {
        List<Utredning> utredningar = utredningRepository.findByVardgivareHsaId(vardgivareHsaId);
        List<UtredningListItem> dtoList = utredningar.stream().map(this::convert).collect(Collectors.toList());

//        logService.logVisaUtredningLista(dtoList.stream()
//                        .filter(uli -> uli.getPatientId() != null)
//                        .collect(Collectors.toList()),
//                ActivityType.READ, ResourceType.RESOURCE_TYPE_FMU_OVERSIKT);

        return dtoList;
    }

    @Override
    public GetUtredningResponse getUtredning(String utredningId, String vardgivareHsaId) {
        Utredning utredning = utredningRepository.findOne(utredningId);
        if (utredning == null) {
            throw new IbNotFoundException("No utredning found for id '" + utredningId + "'");
        }

        // Check vårdgivare...
        if (!utredning.getVardgivareHsaId().equals(vardgivareHsaId)) {
            throw new IbAuthorizationException("The user is not logged in at the applicable Vårdgivare");
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
        if (forfragan == null) {
            throw new IbNotFoundException("No Forfragan found matching ID " + forfraganId);
        }
        return convertCompleteForfragan(forfragan);
    }

    // TOOODOOO Fix converter classes..

    private GetForfraganResponse convertCompleteForfragan(Forfragan f) {
        GetForfraganResponse gfr = new GetForfraganResponse();
        gfr.setForfraganId(f.getInternreferens());
        gfr.setVardenhetHsaId(f.getVardenhetHsaId());
        gfr.setBesvarasSenastDatum(f.getBesvarasSenastDatum().format(DateTimeFormatter.ISO_LOCAL_DATE));
        gfr.setKommentar(f.getKommentar());
        gfr.setStatus(f.getStatus());
        if (f.getTilldeladDatum() != null) {
            gfr.setTilldeladDatum(f.getTilldeladDatum().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        return gfr;
    }

    private ForfraganListItem convertForfragan(Forfragan f) {
        ForfraganListItem fli = new ForfraganListItem();
        fli.setForfraganId(f.getInternreferens());
        fli.setUtredningsId(f.getUtredningId());
        fli.setBesvarasSenastDatum(f.getBesvarasSenastDatum().format(DateTimeFormatter.ISO_LOCAL_DATE));
        fli.setStatus(f.getStatus());
        return fli;
    }

    private GetUtredningResponse convertToUtredningDTO(Utredning utredning) {
        GetUtredningResponse gur = new GetUtredningResponse();
        gur.setUtredningsId(utredning.getUtredningId());
        gur.setUtredningsTyp(utredning.getUtredningsTyp());
        gur.setBesvarasSenastDatum(utredning.getBesvarasSenastDatum().format(DateTimeFormatter.ISO_LOCAL_DATE));
        gur.setInkomDatum(utredning.getInkomDatum().format(DateTimeFormatter.ISO_LOCAL_DATE));
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

        if (u.getBestallning() != null && u.getBestallning().getInvanarePersonId() != null) {
            uli.setPatientId(u.getBestallning().getInvanarePersonId());
        }
        return uli;
    }
}
