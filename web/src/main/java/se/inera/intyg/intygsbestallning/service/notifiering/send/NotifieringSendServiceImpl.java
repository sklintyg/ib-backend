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
package se.inera.intyg.intygsbestallning.service.notifiering.send;

import static se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType.VE;
import static se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType.VG;
import static se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering.SkickadNotifieringBuilder.aSkickadNotifiering;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp.LANDSTING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp.VARDENHET;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.AVVIKELSE_MOTTAGEN_AV_FK;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.AVVIKELSE_RAPPORTERAD_AV_VARDEN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.INGEN_BESTALLNING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.KOMPLETTERING_BEGARD;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.NY_BESTALLNING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.NY_EXTERNFORFRAGAN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.NY_INTERNFORFRAGAN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSEDATUM_KOMPLETTERING_PASSERAS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_REDOVISA_BESOK;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SAMTLIGA_INTERNFORFRAGAN_BESVARATS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SLUTDATUM_KOMPLETTERING_PASSERAT;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UPPDATERAD_BESTALLNING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UTREDNING_AVSLUTAD_PGA_AVBRUTEN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UTREDNING_AVSLUTAD_PGA_JAV;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UTREDNING_TILLDELAD;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.avslutadPgaJavMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.avvikelseRapporteradAvVardenMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.ingenBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.kompletteringBegardMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingAvslutadUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingAvvikelseRapporteradAvFKMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingNyExternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingSamtligaInternForfraganBesvaradeforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.nyBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseRedovisaBesok;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseSlutDatumKomplettering;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseSlutdatumUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseSvaraExternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.slutdatumPasseratKompletteringMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.slutdatumPasseratUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.uppdateradBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.vardenhetAvslutadUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.vardenhetAvvikelseRapporteradAvFKMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.vardenhetNyInternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.vardenhetTilldeladUtredning;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_AVSLUTAD_PGA_JAV;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_AVSLUTAD_UTREDNING;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_AVVIKELSE_RAPPORTERAD_AV_VARDEN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_BESTALLNING_UPPDATERAD;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_FMU_UTREDNING_TILLDELAD_VARDENHETEN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_INGEN_BESTALLNING;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_KOMPLETTERING_BEGARD;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_KOMPLETTERING_SLUTDATUM_PAMINNELSE;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_KOMPLETTERING_SLUTDATUM_PASSERAT;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_NY_FMU_EXTERN_FORFRAGAN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_NY_FMU_INTERN_FORFRAGAN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_PAMINNELSE_SVARA_EXTERNFORFRAGAN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_REDOVISA_BESOK;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_SAMTLIGA_INTERNFORFRAGAN_BESVARATS;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_UTREDNING_SLUTDATUM_PAMINNELSE;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_UTREDNING_SLUTDATUM_PASSERAT;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.mail.MailService;
import se.inera.intyg.intygsbestallning.service.notifiering.preferens.NotifieringPreferenceService;
import se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringEpostResolver;
import se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailBodyFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.GetNotificationPreferenceResponse;
import se.inera.intyg.intygsbestallning.web.controller.integration.MaillinkRedirectUrlBuilder;

@Service
@Transactional
public class NotifieringSendServiceImpl implements NotifieringSendService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MailService mailService;
    private final NotifieringPreferenceService notifieringPreferenceService;
    private final NotifieringMailBodyFactory notifieringMailBodyFactory;
    private final UtredningRepository utredningRepository;
    private final NotifieringEpostResolver epostResolver;
    private final MaillinkRedirectUrlBuilder maillinkRedirectUrlBuilder;

    public NotifieringSendServiceImpl(
            MailService mailService,
            NotifieringPreferenceService notifieringPreferenceService,
            NotifieringMailBodyFactory notifieringMailBodyFactory,
            UtredningRepository utredningRepository,
            NotifieringEpostResolver epostResolver,
            MaillinkRedirectUrlBuilder maillinkRedirectUrlBuilder) {
        this.mailService = mailService;
        this.notifieringPreferenceService = notifieringPreferenceService;
        this.notifieringMailBodyFactory = notifieringMailBodyFactory;
        this.utredningRepository = utredningRepository;
        this.epostResolver = epostResolver;
        this.maillinkRedirectUrlBuilder = maillinkRedirectUrlBuilder;
    }

    @Override
    public void notifieraLandstingNyExternforfragan(Utredning utredning) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(NY_EXTERNFORFRAGAN, LANDSTING)) {
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingNyExternforfraganMessage(),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(preferens.getLandstingEpost(), SUBJECT_NY_FMU_EXTERN_FORFRAGAN, body, utredning.getUtredningId());
            saveNotifiering(utredning, NY_EXTERNFORFRAGAN, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetNyInternforfragan(Utredning utredning, InternForfragan internForfragan, String landstingsNamn) {
        final String vardenhetsHsaId = internForfragan.getVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(NY_INTERNFORFRAGAN, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(internForfragan.getVardenhetHsaId(), utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForForfragan(
                        vardenhetNyInternforfraganMessage(landstingsNamn),
                        maillinkRedirectUrlBuilder.buildVardadminInternForfraganUrl(utredning.getUtredningId(), internForfragan.getId()));
                sendNotifiering(email, SUBJECT_NY_FMU_INTERN_FORFRAGAN, body, utredning.getUtredningId());
                saveNotifiering(utredning, NY_INTERNFORFRAGAN, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(Utredning utredning) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(SAMTLIGA_INTERNFORFRAGAN_BESVARATS, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingSamtligaInternForfraganBesvaradeforfraganMessage(utredning),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_SAMTLIGA_INTERNFORFRAGAN_BESVARATS, body, utredning.getUtredningId());
            saveNotifiering(utredning, SAMTLIGA_INTERNFORFRAGAN_BESVARATS, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetTilldeladUtredning(Utredning utredning, InternForfragan tillDeladInternForfragan, String landstingNamn) {
        final String vardenhetsHsaId = tillDeladInternForfragan.getVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(UTREDNING_TILLDELAD, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(tillDeladInternForfragan.getVardenhetHsaId(), utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForForfragan(
                        vardenhetTilldeladUtredning(utredning, landstingNamn),
                        maillinkRedirectUrlBuilder.buildVardadminInternForfraganUrl(utredning.getUtredningId(),
                                tillDeladInternForfragan.getId()));
                sendNotifiering(email, SUBJECT_FMU_UTREDNING_TILLDELAD_VARDENHETEN, body, utredning.getUtredningId());
                saveNotifiering(utredning, UTREDNING_TILLDELAD, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetPaminnelseSvaraInternforfragan(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraLandstingPaminnelseSvaraExternforfragan(Utredning utredning) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS, LANDSTING)) {
            String email = preferens.getLandstingEpost();
            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    paminnelseSvaraExternforfraganMessage(utredning),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_PAMINNELSE_SVARA_EXTERNFORFRAGAN, body, utredning.getUtredningId());
            saveNotifiering(utredning, PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS, LANDSTING);
        }
    }

    @Override
    public void notifieraLandstingIngenBestallning(Utredning utredning) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(INGEN_BESTALLNING, LANDSTING)) {
            String email = preferens.getLandstingEpost();
            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    ingenBestallningMessage(utredning),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_INGEN_BESTALLNING, body, utredning.getUtredningId());
            saveNotifiering(utredning, INGEN_BESTALLNING, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetIngenBestallning(Utredning utredning, InternForfragan internForfragan) {
        final String vardenhetsHsaId = internForfragan.getVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(INGEN_BESTALLNING, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(internForfragan.getVardenhetHsaId(), utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForForfragan(
                        ingenBestallningMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminInternForfraganUrl(utredning.getUtredningId(), internForfragan.getId()));
                sendNotifiering(email, SUBJECT_INGEN_BESTALLNING, body, utredning.getUtredningId());
                saveNotifiering(utredning, INGEN_BESTALLNING, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetNyBestallning(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, NY_BESTALLNING);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(NY_BESTALLNING, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        nyBestallningMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING, body, utredning.getUtredningId());
                saveNotifiering(utredning, NY_BESTALLNING, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvslutadPgaJav(final Utredning utredning) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_JAV, LANDSTING)) {
            String email = preferens.getLandstingEpost();
            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    avslutadPgaJavMessage(utredning),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_AVSLUTAD_PGA_JAV, body, utredning.getUtredningId());
            saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_JAV, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetAvslutadPgaJav(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, UTREDNING_AVSLUTAD_PGA_JAV);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_JAV, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        avslutadPgaJavMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_AVSLUTAD_PGA_JAV, body, utredning.getUtredningId());
                saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_JAV, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetUppdateradBestallning(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, UPPDATERAD_BESTALLNING);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(UPPDATERAD_BESTALLNING, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        uppdateradBestallningMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_BESTALLNING_UPPDATERAD, body, utredning.getUtredningId());
                saveNotifiering(utredning, UPPDATERAD_BESTALLNING, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvvikelseRapporteradAvVarden(Utredning utredning, Besok besok) {
        final String vardgivareHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardgivareHsaId, VG);

        if (preferens.isEnabled(AVVIKELSE_RAPPORTERAD_AV_VARDEN, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    avvikelseRapporteradAvVardenMessage(utredning, besok),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_AVVIKELSE_RAPPORTERAD_AV_VARDEN, body, utredning.getUtredningId());
            saveNotifiering(utredning, AVVIKELSE_RAPPORTERAD_AV_VARDEN, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetAvvikelseMottagenFranFK(Utredning utredning, Besok besok) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, AVVIKELSE_MOTTAGEN_AV_FK);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(AVVIKELSE_MOTTAGEN_AV_FK, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        vardenhetAvvikelseRapporteradAvFKMessage(utredning, besok),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK, body, utredning.getUtredningId());
                saveNotifiering(utredning, AVVIKELSE_MOTTAGEN_AV_FK, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvvikelseMottagenFranFK(Utredning utredning, Besok besok) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(AVVIKELSE_MOTTAGEN_AV_FK, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingAvvikelseRapporteradAvFKMessage(utredning, besok),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK, body, utredning.getUtredningId());
            saveNotifiering(utredning, AVVIKELSE_MOTTAGEN_AV_FK, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetAvslutadUtredning(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, UTREDNING_AVSLUTAD_PGA_AVBRUTEN);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_AVBRUTEN, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        vardenhetAvslutadUtredningMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_AVSLUTAD_UTREDNING, body, utredning.getUtredningId());
                saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_AVBRUTEN, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvslutadUtredning(Utredning utredning) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_AVBRUTEN, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingAvslutadUtredningMessage(utredning),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_AVSLUTAD_UTREDNING, body, utredning.getUtredningId());
            saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_AVBRUTEN, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetPaminnelseSlutdatumUtredning(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(PAMINNELSEDATUM_KOMPLETTERING_PASSERAS, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        paminnelseSlutdatumUtredningMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PAMINNELSE, body, utredning.getUtredningId());
                saveNotifiering(utredning, PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetSlutdatumPasseratUtredning(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, SLUTDATUM_UTREDNING_PASSERAT);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(SLUTDATUM_UTREDNING_PASSERAT, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        slutdatumPasseratUtredningMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));

                sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PASSERAT, body, utredning.getUtredningId());
                saveNotifiering(utredning, SLUTDATUM_UTREDNING_PASSERAT, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingSlutdatumPasseratUtredning(Utredning utredning) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(SLUTDATUM_UTREDNING_PASSERAT, NotifieringMottagarTyp.LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    slutdatumPasseratUtredningMessage(utredning),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PASSERAT, body, utredning.getUtredningId());
            saveNotifiering(utredning, SLUTDATUM_UTREDNING_PASSERAT, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetKompletteringBegard(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, KOMPLETTERING_BEGARD);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(KOMPLETTERING_BEGARD, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        kompletteringBegardMessage(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_KOMPLETTERING_BEGARD, body, utredning.getUtredningId());
                saveNotifiering(utredning, KOMPLETTERING_BEGARD, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetPaminnelseSlutdatumKomplettering(Utredning utredning, Intyg intyg) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, SLUTDATUM_UTREDNING_PASSERAT);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(PAMINNELSEDATUM_KOMPLETTERING_PASSERAS, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        paminnelseSlutDatumKomplettering(utredning, intyg),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_KOMPLETTERING_SLUTDATUM_PAMINNELSE, body, utredning.getUtredningId());
                updateUtredningWithNotifiering(utredning, intyg.getId(), PAMINNELSEDATUM_KOMPLETTERING_PASSERAS, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetSlutdatumPasseratKomplettering(Utredning utredning, Intyg intyg) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, SLUTDATUM_UTREDNING_PASSERAT);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(SLUTDATUM_KOMPLETTERING_PASSERAT, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        slutdatumPasseratKompletteringMessage(utredning, intyg),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));

                sendNotifiering(email, SUBJECT_KOMPLETTERING_SLUTDATUM_PASSERAT, body, utredning.getUtredningId());
                saveNotifiering(utredning, intyg.getId(), SLUTDATUM_KOMPLETTERING_PASSERAT, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingSlutdatumPasseratKomplettering(Utredning utredning, Intyg intyg) {
        final String landstingsHsaId = getHsaIdFromExternforfragan(utredning);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(SLUTDATUM_KOMPLETTERING_PASSERAT, NotifieringMottagarTyp.LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    slutdatumPasseratKompletteringMessage(utredning, intyg),
                    maillinkRedirectUrlBuilder.buildSamordnareUtredningUrl(utredning.getUtredningId()));
            sendNotifiering(email, SUBJECT_KOMPLETTERING_SLUTDATUM_PASSERAT, body, utredning.getUtredningId());
            saveNotifiering(utredning, intyg.getId(), SLUTDATUM_KOMPLETTERING_PASSERAT, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetRedovisaBesok(Utredning utredning) {
        final String vardenhetsHsaId = getHsaIdFromBestallning(utredning, PAMINNELSE_REDOVISA_BESOK);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetsHsaId, VE);

        if (preferens.isEnabled(PAMINNELSE_REDOVISA_BESOK, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(vardenhetsHsaId, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        paminnelseRedovisaBesok(utredning),
                        maillinkRedirectUrlBuilder.buildVardadminBestallningUrl(utredning.getUtredningId()));
                sendNotifiering(email, SUBJECT_REDOVISA_BESOK, body, utredning.getUtredningId());
                saveNotifiering(utredning, PAMINNELSE_REDOVISA_BESOK, VARDENHET);
            });
        }
    }

    public void updateUtredningWithNotifiering(
            final Utredning utredning,
            final Long intygId,
            final NotifieringTyp typ,
            final NotifieringMottagarTyp mottagare) {

        if (mottagare == NotifieringMottagarTyp.ALL) {
            throw new IllegalArgumentException("Do not save a sent notification for mottagare ALL");
        }

        utredning.getSkickadNotifieringList().add(aSkickadNotifiering()
                .withSkickad(LocalDateTime.now())
                .withTyp(typ)
                .withIntygId(intygId)
                .withMottagare(mottagare)
                .build());
    }

    private void saveNotifiering(
            final Utredning utredning,
            final NotifieringTyp typ,
            final NotifieringMottagarTyp mottagare) {

        updateUtredningWithNotifiering(utredning, null, typ, mottagare);
        utredningRepository.saveUtredning(utredning);
    }

    private void saveNotifiering(
            final Utredning utredning,
            final Long intygId,
            final NotifieringTyp typ,
            final NotifieringMottagarTyp mottagare) {

        updateUtredningWithNotifiering(utredning, intygId, typ, mottagare);
        utredningRepository.saveUtredning(utredning);
    }

    private void sendNotifiering(String email, String subject, String body, Long utredningId) {
        try {
            mailService.sendNotificationToUnit(email, subject, body);
            LOG.info(MessageFormat.format("Sent notification: \"{0}\" to email: {1} for utredning: {2}.", subject, email, utredningId));
        } catch (MessagingException e) {
            LOG.error(MessageFormat.format("Error sending notification by email: {0}", e.getMessage()));
        }
    }

    private String getHsaIdFromBestallning(final Utredning utredning, final NotifieringTyp notifieringTyp) {
        return utredning.getBestallning()
                .map(Bestallning::getTilldeladVardenhetHsaId)
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.BAD_STATE,
                        MessageFormat.format("Cannot send notification for {0} when there is no Bestallning.", notifieringTyp.getId())));
    }

    private String getHsaIdFromExternforfragan(final Utredning utredning) {
        return utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId)
                .orElseThrow(() -> new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                        "Utredning with id {0} is missing an Externförfrågan with landstingsHsaId", utredning.getUtredningId())));
    }
}
