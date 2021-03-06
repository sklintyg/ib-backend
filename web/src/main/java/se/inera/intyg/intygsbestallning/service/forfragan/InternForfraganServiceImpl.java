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
package se.inera.intyg.intygsbestallning.service.forfragan;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtforareTyp;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.service.vardenhet.VardenhetService;
import se.inera.intyg.intygsbestallning.service.vardgivare.VardgivareService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.CreateInternForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.GetInternForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.InternForfraganSvarItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.TilldelaDirektRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.vardenhet.GetVardenheterForVardgivareResponse;

@Service
@Transactional(readOnly = true)
public class InternForfraganServiceImpl extends BaseUtredningService implements InternForfraganService {

    private static final Pattern POSTNR_REGEXP = Pattern.compile("\\d{5}");

    //Taken from angular.js EMAIL_REGEXP (which in turn is based on chromiums <input type="email"> validation.)
    private static final Pattern EPOST_REGEXP = Pattern.compile(
            "^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+/0-9=?A-Z^_`a-z{|}~]+(\\.[-!#$%&'*+/0-9=?A-Z^_`a-z{|}~]+)*"
                   + "@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$");

    private static final Pattern DATUM_REGEXP = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    @Autowired
    protected ExternForfraganService externForfraganService;
    @Autowired
    private BusinessDaysBean businessDays;
    @Autowired
    private InternForfraganListItemFactory internForfraganListItemFactory;
    @Autowired
    private VardenhetService vardenhetService;
    @Autowired
    private VardgivareService vardgivareService;
    @Autowired
    private NotifieringSendService notifieringSendService;

    @Value("${ib.besvara.forfragan.arbetsdagar:2}")
    private int besvaraForfraganArbetsdagar;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @Override
    @Transactional
    public GetUtredningResponse createInternForfragan(Long utredningId, String landstingHsaId, CreateInternForfraganRequest request) {

        Utredning utredning = getUtredningForLandsting(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.FORFRAGAN_INKOMMEN,
                UtredningStatus.VANTAR_PA_SVAR, UtredningStatus.TILLDELA_UTREDNING));

        if (isNull(request.getVardenheter()) || request.getVardenheter().size() == 0) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST,
                    "At least one vardenhet must be selected to create an InternForfragan");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime besvarasSenastDatum = getBesvarasSenastDatum(now);

        // Remove duplicates
        List<String> newVardenheter = request.getVardenheter().stream()
                .filter(vardenhetHsaId -> utredning.getExternForfragan()
                        .map(ExternForfragan::getInternForfraganList)
                        .orElse(Lists.newArrayList()).stream()
                        .map(InternForfragan::getVardenhetHsaId)
                        .noneMatch(vardenhetHsaId::equals))
                .collect(toList());

        utredning.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .orElse(Lists.newArrayList()).addAll(newVardenheter.stream()
                .map(vardenhetHsaId -> anInternForfragan()
                        .withVardenhetHsaId(vardenhetHsaId)
                        .withSkapadDatum(now)
                        .withKommentar(request.getKommentar())
                        .withBesvarasSenastDatum(besvarasSenastDatum)
                        .build())
                .collect(toList()));

        utredning.getHandelseList().addAll(
                newVardenheter.stream()
                        .map(vardenhetHsaId -> HandelseUtil.createInternForfraganSkickad(userService.getUser().getNamn(),
                                hsaOrganizationsService.getVardenhet(vardenhetHsaId).getNamn()))
                        .collect(toList()));

        Utredning sparadUtredning = utredningRepository.saveUtredning(utredning);
        final List<InternForfragan> toBeNotified = sparadUtredning.getExternForfragan().get()
                .getInternForfraganList()
                .stream()
                .filter(internForfragan -> newVardenheter.contains(internForfragan.getVardenhetHsaId())).collect(toList());

        String landstingsNamn = getLandstingNameOrHsaId(landstingHsaId);
        toBeNotified
                .forEach(internForfragan -> notifieringSendService.notifieraVardenhetNyInternforfragan(sparadUtredning, internForfragan,
                        landstingsNamn));

        return createGetUtredningResponse(utredning);
    }

    @Override
    @Transactional
    public GetUtredningResponse tilldelaDirekt(Long utredningId, String landstingHsaId, TilldelaDirektRequest request) {

        Utredning utredning = getUtredningForLandsting(utredningId, landstingHsaId, ImmutableList.of(UtredningStatus.FORFRAGAN_INKOMMEN,
                UtredningStatus.VANTAR_PA_SVAR, UtredningStatus.TILLDELA_UTREDNING));

        if (isNull(request.getVardenhet())) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST,
                    "At least one vardenhet must be selected to create an InternForfragan");
        }

        if (utredning.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .orElse(Lists.newArrayList()).stream()
                .map(InternForfragan::getVardenhetHsaId)
                .anyMatch(request.getVardenhet()::equals)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                    "InternForfragan already exists for assessment with id {0} and vardenhet {1} ", utredningId, request.getVardenhet()));
        }

        LocalDateTime now = LocalDateTime.now();
        utredning.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .orElse(Lists.newArrayList()).add(
                anInternForfragan()
                        .withVardenhetHsaId(request.getVardenhet())
                        .withSkapadDatum(now)
                        .withKommentar(request.getKommentar())
                        .withBesvarasSenastDatum(getBesvarasSenastDatum(now))
                        .withDirekttilldelad(true)
                        .withForfraganSvar(aForfraganSvar()
                                .withSvarTyp(SvarTyp.ACCEPTERA)
                                .withUtforareAdress("")
                                .withUtforareEpost("")
                                .withUtforareNamn("")
                                .withUtforarePostnr("")
                                .withUtforarePostort("")
                                .withUtforareTyp(UtforareTyp.ENHET)
                                .build())
                        .build());

        utredningRepository.saveUtredning(utredning);

        return createGetUtredningResponse(utredning);
    }

    @NotNull
    private LocalDateTime getBesvarasSenastDatum(LocalDateTime now) {
        return LocalDateTime.of(
                businessDays.addBusinessDays(now.toLocalDate(), besvaraForfraganArbetsdagar), now.toLocalTime());
    }

    @Override
    public GetInternForfraganResponse getInternForfragan(Long utredningId, String vardenhetHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        if (!utredning.getExternForfragan().isPresent()) {
            throw new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not have an externforfragan?.");
        }
        // Must have an internforfragan for this vardenhet
        InternForfragan internForfragan = utredning.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .orElse(Lists.newArrayList()).stream()
                .filter(iff -> Objects.equals(iff.getVardenhetHsaId(), vardenhetHsaId))
                .findFirst().orElseThrow(() -> new IbNotFoundException("Utredning with id '" + utredningId
                        + "' does not have an InternForfragan for enhet with id '" + vardenhetHsaId + "'"));

        final GetUtredningResponse utredningsResponse = GetUtredningResponse.from(utredning,
                utredningStatusResolver.resolveStatus(utredning));

        // Vardadmins should not see händelser or InternforfraganList
        utredningsResponse.getHandelseList().clear();
        utredningsResponse.getInternForfraganList().clear();
        enrichWithVardenhetNames(utredningsResponse.getTidigareEnheter());

        final InternForfraganListItem internForfraganListItem = internForfraganListItemFactory.from(utredning,
                internForfragan.getVardenhetHsaId());
        internForfraganListItem.setRejectIsProhibited(rejectIsProhibited(utredning, internForfragan.getVardenhetHsaId()));

        // Either return existing svar or a partial svar based on vardenhet preferences
        InternForfraganSvarItem internForfraganSvarItem = InternForfraganSvarItem.from(internForfragan);
        if (internForfraganSvarItem == null) {
            internForfraganSvarItem = InternForfraganSvarItem.from(internForfragan,
                    vardenhetService.getVardEnhetPreference(vardenhetHsaId));
        }

        return new GetInternForfraganResponse(internForfraganListItem, internForfraganSvarItem, utredningsResponse);

    }

    @Override
    @Transactional
    public InternForfraganSvarItem besvaraInternForfragan(Long utredningId, ForfraganSvarRequest svar) {

        // Sanity check of input
        String requestValidationError = validateSvarRequest(svar);
        if (requestValidationError != null) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, String.format(
                    "ForfraganSvarRequest validation failed with message '%s'",
                    requestValidationError));
        }
        // Utredning must exist...
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Angivet utredningsid existerar inte"));

        // ..and have a internforfragan in answerable state
        InternForfragan internForfragan = getAnswerableInternForfragan(utredning, svar);

        ForfraganSvar forfraganSvar = buildSvarEntity(svar);
        internForfragan.setForfraganSvar(forfraganSvar);

        createInternforfraganBesvaradHandelseLog(utredning, forfraganSvar, internForfragan.getVardenhetHsaId());

        // F004: Alternativflöde 2 - Utredningen skall tilldelas automatiskt till en vårdenhet i egen regi när den accepterats
        // och är ENDA tillfrågade enheten.
        if (shouldTillDelasAutomatiskt(utredning, internForfragan)) {
            externForfraganService.acceptExternForfragan(utredning.getUtredningId(), utredning.getExternForfragan()
                            .map(ExternForfragan::getLandstingHsaId).orElse(null),
                    internForfragan.getVardenhetHsaId());
        } else {
            // F004: Normalflöde 5 - Notifiering till landstinget skall ske när samtliga vårdenheter har besvarat
            // internförfrågningarna - endast notifiera om Alternativflöde 2 ovan inte inträffat
            handleAllInternforfragningarMayBeAnswered(utredning, internForfragan);
        }
        // F004: Alternativflöde 5 - Landstingets enda vårdenheten avvisar internförfrågan
        if (shouldAvvisaExternForfraganAutomatiskt(utredning, internForfragan)) {
            externForfraganService.avvisaExternForfragan(utredning.getUtredningId(),
                    utredning.getExternForfragan().get().getLandstingHsaId(),
                    forfraganSvar.getKommentar());
        }

        return InternForfraganSvarItem.from(internForfragan);
    }

    private boolean rejectIsProhibited(Utredning utredning, String vardenhetHsaId) {
        // Internforfragan får EJ avvisas givet att:
        // - Feature EXTERNFORFRAGAN_FAR_AVVISAS är disabled.
        // - Vårdenheten är den enda registrerade VE i landstinget
        // - Vårdenheten drivs i landstingets egen regi

        boolean avvisaExternForfraganFeatureEnabled = authoritiesValidator.given(userService.getUser())
                .features(AuthoritiesConstants.FEATURE_EXTERNFORFRAGAN_FAR_AVVISAS).isVerified();

        return !avvisaExternForfraganFeatureEnabled
                && isOnlyVardenhetEgenRegiForLandsting(utredning.getExternForfragan().get().getLandstingHsaId(), vardenhetHsaId);
    }

    private boolean shouldAvvisaExternForfraganAutomatiskt(Utredning utredning, InternForfragan saved) {
        // Rule: Vårdenheten avvisar förfrågan
        // Rule: Vårdenheten är den enda registrerade vårdenheten för landstinget
        // Rule: Vårdenheten drivs i landstingets egen regi
        return saved.getForfraganSvar().getSvarTyp().equals(SvarTyp.AVBOJ)
                && isOnlyVardenhetEgenRegiForLandsting(utredning.getExternForfragan().get().getLandstingHsaId(), saved.getVardenhetHsaId());

    }

    private boolean shouldTillDelasAutomatiskt(Utredning utredning, InternForfragan saved) {
        // Rule: Vårdenheten accepterar förfrågan
        // Rule: Vårdenheten är den enda vårdenhet som fått en internförfrågan i utredningen
        // Rule: Vårdenheten drivs i landstingets egen regi
        return saved.getForfraganSvar().getSvarTyp().equals(SvarTyp.ACCEPTERA)
                && utredning.getExternForfragan().map(ExternForfragan::getInternForfraganList).orElse(Lists.newArrayList()).size() == 1
                && isRegiFormEgen(utredning.getExternForfragan()
                .map(ExternForfragan::getLandstingHsaId).orElse(null), saved.getVardenhetHsaId());
    }

    private boolean isOnlyVardenhetEgenRegiForLandsting(String landstingHsaId, String vardenhetHsaId) {
        final GetVardenheterForVardgivareResponse enheter = vardgivareService
                .listVardenheterForVardgivare(landstingHsaId);

        // RegiFormTyp check is redundant since they are already grouped by that
        return enheter.getAnnatLandsting().isEmpty()
                && enheter.getPrivat().isEmpty()
                && enheter.getEgetLandsting().size() == 1
                && enheter.getEgetLandsting().get(0).getId().equals(vardenhetHsaId);
    }

    private boolean isRegiFormEgen(String landstingHsaId, String vardenhetHsaId) {
        final GetVardenheterForVardgivareResponse getVardenheterForVardgivareResponse = vardgivareService
                .listVardenheterForVardgivare(landstingHsaId);
        return getVardenheterForVardgivareResponse.getEgetLandsting().stream().anyMatch(vei -> vei.getId().equals(vardenhetHsaId));
    }

    private InternForfragan getAnswerableInternForfragan(Utredning utredning, ForfraganSvarRequest svar) {

        // Utredning must have a internforfragan matching the ForfraganSvarRequest
        InternForfragan internForfragan = utredning.getExternForfragan().map(ExternForfragan::getInternForfraganList)
                .orElse(Lists.newArrayList()).stream()
                .filter(i -> i.getId().equals(svar.getForfraganId()))
                .findAny()
                .orElseThrow(() -> new IbNotFoundException(String.format(
                        "Could not find internforfragan '%s' in utredning '%s'", svar.getForfraganId(), utredning.getUtredningId())));

        // .. that's in the correct state
        InternForfraganStatus internForfraganStatus = internForfragan.getStatus();
        if (internForfraganStatus != InternForfraganStatus.INKOMMEN) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, String.format(
                    "Internforfragan for vardenhet '%s' in utredning '%s' is in an incorrect state (%s) to answer",
                    internForfragan.getVardenhetHsaId(),
                    utredning.getUtredningId(), internForfraganStatus.getId()));
        }
        // .. and should not already have been answered.
        if (internForfragan.getForfraganSvar() != null) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, String.format(
                    "Internforfragan for vardenhet '%s' in utredning '%s' already have an answer", internForfragan.getVardenhetHsaId(),
                    utredning.getUtredningId()));
        }

        // Are we allowed to reject his?
        if (SvarTyp.AVBOJ.name().equals(svar.getSvarTyp()) && rejectIsProhibited(utredning, internForfragan.getVardenhetHsaId())) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, String.format(
                    "Internforfragan for vardenhet '%s' in utredning '%s' is not allowed to be rejected!",
                    internForfragan.getVardenhetHsaId(),
                    utredning.getUtredningId()));
        }

        return internForfragan;
    }

    private void handleAllInternforfragningarMayBeAnswered(Utredning utredning, InternForfragan internForfragan) {
        // Are there no internforfragan (besides the one we just answered) for this utredning still unanswered?
        if (utredning.getExternForfragan().map(ExternForfragan::getInternForfraganList).orElse(Lists.newArrayList()).stream()
                .noneMatch(iff -> !iff.getId().equals(internForfragan.getId()) && iff.getForfraganSvar() == null)) {
            notifieringSendService.notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(utredning);
        }
    }

    private void createInternforfraganBesvaradHandelseLog(Utredning utredning, ForfraganSvar forfraganSvar, String vardenhetHsaId) {

        String vardenhetNamn = hsaOrganizationsService.getVardenhet(vardenhetHsaId).getNamn();

        final Handelse internForfraganBesvarad = HandelseUtil.createInternForfraganBesvarad(
                SvarTyp.ACCEPTERA.equals(forfraganSvar.getSvarTyp()), userService.getUser().getNamn(), vardenhetNamn,
                forfraganSvar.getKommentar(), forfraganSvar.getBorjaDatum());

        utredning.getHandelseList().add(internForfraganBesvarad);
        utredningRepository.saveUtredning(utredning);
    }

    protected String validateSvarRequest(ForfraganSvarRequest svar) {
        if (svar == null) {
            return "Request was null";
        }
        if (!Arrays.stream(SvarTyp.values()).anyMatch((t) -> t.name().equals(svar.getSvarTyp()))) {
            return "Invalid svarTypValue " + svar.getSvarTyp();
        }

        if (SvarTyp.AVBOJ.name().equals(svar.getSvarTyp())) {
            //Validate ACCEPTERA request
            if (Strings.isNullOrEmpty(svar.getKommentar())) {
                return "Mandatory kommentar missing";
            }
        } else {
            //Validate ACCEPTERA request
            if (!Arrays.stream(UtforareTyp.values()).anyMatch((t) -> t.name().equals(svar.getUtforareTyp()))) {
                return "Invalid UtforareTyp " + svar.getUtforareTyp();
            }

            if (Strings.isNullOrEmpty(svar.getUtforareNamn())) {
                return "Mandatory UtforareNamn missing";
            }
            if (Strings.isNullOrEmpty(svar.getUtforareAdress())) {
                return "Mandatory UtforareAdress missing";
            }
            if (!patternMatches(svar.getUtforarePostnr(), POSTNR_REGEXP, true)) {
                return "Invalid mandatory UtforarePostnr value";
            }
            if (Strings.isNullOrEmpty(svar.getUtforarePostort())) {
                return "Mandatory UtforarePostort missing";
            }
            if (!patternMatches(svar.getUtforareEpost(), EPOST_REGEXP, false)) {
                return "Invalid UtforareEpost format";
            }
            if (!patternMatches(svar.getBorjaDatum(), DATUM_REGEXP, false)) {
                return "Invalid BorjaDatum value '" + svar.getBorjaDatum() + "'";
            } else if (!Strings.isNullOrEmpty(svar.getBorjaDatum()) && LocalDate.parse(svar.getBorjaDatum()).isBefore(LocalDate.now())) {
                return "Invalid BorjaDatum value - must not be before today";
            }
        }



        return null;
    }

    private boolean patternMatches(String value, Pattern pattern, boolean isMandatory) {
        if (Strings.isNullOrEmpty(value)) {
            return !isMandatory;
        } else {
            return pattern.matcher(value).matches();
        }

    }

    private ForfraganSvar buildSvarEntity(ForfraganSvarRequest svarRequest) {
        if (SvarTyp.AVBOJ.name().equals(svarRequest.getSvarTyp())) {
            return ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar()
                    .withSvarTyp(SvarTyp.valueOf(svarRequest.getSvarTyp()))
                    .withKommentar(svarRequest.getKommentar())
                    .build();
        } else {
            return ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar()
                    .withSvarTyp(SvarTyp.valueOf(svarRequest.getSvarTyp()))
                    .withUtforareTyp(UtforareTyp.valueOf(svarRequest.getUtforareTyp()))
                    .withUtforareNamn(svarRequest.getUtforareNamn())
                    .withUtforareAdress(svarRequest.getUtforareAdress())
                    .withUtforarePostnr(svarRequest.getUtforarePostnr())
                    .withUtforarePostort(svarRequest.getUtforarePostort())
                    .withUtforareTelefon(svarRequest.getUtforareTelefon())
                    .withUtforareEpost(svarRequest.getUtforareEpost())
                    .withKommentar(svarRequest.getKommentar())
                    .withBorjaDatum(
                            !Strings.isNullOrEmpty(svarRequest.getBorjaDatum()) ? LocalDate.parse(svarRequest.getBorjaDatum()) : null)
                    .build();
        }

    }

}
