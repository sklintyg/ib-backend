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

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType.VE;
import static se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType.VG;
import static se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering.SkickadNotifieringBuilder.aSkickadNotifiering;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp.LANDSTING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp.VARDENHET;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.AVVIKELSE_MOTTAGEN_AV_FK;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.AVVIKELSE_RAPPORTERAD_AV_VARDEN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.INGEN_BESTALLNING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.NY_BESTALLNING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.NY_EXTERNFORFRAGAN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.NY_INTERNFORFRAGAN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSEDATUM_KOMPLETTERING_PASSERAS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_REDOVISA_BESOK;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SAMTLIGA_INTERNFORFRAGAN_BESVARATS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UPPDATERAD_BESTALLNING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UTREDNING_AVSLUTAD_PGA_AVBRUTEN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UTREDNING_AVSLUTAD_PGA_JAV;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UTREDNING_TILLDELAD;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.avslutadPgaJavMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.avvikelseRapporteradAvVardenMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.externForfraganUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.ingenBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.internForfraganUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingAvslutadUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingAvvikelseRapporteradAvFKMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingNyExternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingSamtligaInternForfraganBesvaradeforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.nyBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseRedovisaBesok;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseSlutDatumKomplettering;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseSlutdatumUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.slutdatumPasseratUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.uppdateradBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.utredningUrl;
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
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_NY_FMU_EXTERN_FORFRAGAN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_NY_FMU_INTERN_FORFRAGAN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_PAMINNELSE_SLUTDATUM_KOMPLETTERING;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_REDOVISA_BESOK;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_SAMTLIGA_INTERNFORFRAGAN_BESVARATS;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_UTREDNING_SLUTDATUM_PAMINNELSE;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_UTREDNING_SLUTDATUM_PASSERAT;

import com.google.common.base.Strings;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.mail.MailService;
import se.inera.intyg.intygsbestallning.service.notifiering.preferens.NotifieringPreferenceService;
import se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringEpostResolver;
import se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailBodyFactory;
import se.inera.intyg.intygsbestallning.service.vardenhet.VardenhetService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.notification.GetNotificationPreferenceResponse;

@Service
@Transactional
public class NotifieringSendServiceImpl implements NotifieringSendService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private MailService mailService;

    @Autowired
    private VardenhetService vardenhetService;

    @Autowired
    private NotifieringPreferenceService notifieringPreferenceService;

    @Autowired
    private NotifieringMailBodyFactory notifieringMailBodyFactory;

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private NotifieringEpostResolver epostResolver;

    @Override
    public void notifieraLandstingNyExternforfragan(Utredning utredning) {
        checkState(utredning.getExternForfragan().isPresent());
        checkState(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).isPresent());

        final String id = utredning.getExternForfragan().get().getLandstingHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VG);

        if (preferens.isEnabled(NY_EXTERNFORFRAGAN, LANDSTING)) {
            final String email = vardenhetService.getVardEnhetPreference(id).getEpost();
            if (isNotEmpty(email)) {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        landstingNyExternforfraganMessage(),
                        externForfraganUrl(utredning));
                sendNotifiering(email, SUBJECT_NY_FMU_EXTERN_FORFRAGAN, body, utredning.getUtredningId());
                saveNotifiering(utredning, NY_EXTERNFORFRAGAN, LANDSTING);
            }
        }
    }

    @Override
    public void notifieraVardenhetNyInternforfragan(Utredning utredning) {
        checkState(utredning.getExternForfragan().isPresent());
        checkState(isNotEmpty(utredning.getExternForfragan().get().getInternForfraganList()));
        checkState(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).isPresent());

        String landstingsHsaId = utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).get();

        final InternForfragan internForfragan = utredning.getExternForfragan().get().getInternForfraganList().get(0);
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);

        if (preferens.isEnabled(NY_INTERNFORFRAGAN, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(landstingsHsaId, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        vardenhetNyInternforfraganMessage(internForfragan),
                        internForfraganUrl(utredning));

                sendNotifiering(email, SUBJECT_NY_FMU_INTERN_FORFRAGAN, body, utredning.getUtredningId());
                saveNotifiering(utredning, NY_INTERNFORFRAGAN, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(Utredning utredning) {
        checkState(utredning.getExternForfragan().isPresent());
        checkState(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).isPresent());

        final String landstingsHsaId = utredning.getExternForfragan().get().getLandstingHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);
        if (preferens.isEnabled(SAMTLIGA_INTERNFORFRAGAN_BESVARATS, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingSamtligaInternForfraganBesvaradeforfraganMessage(utredning),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_SAMTLIGA_INTERNFORFRAGAN_BESVARATS, body, utredning.getUtredningId());
            saveNotifiering(utredning, SAMTLIGA_INTERNFORFRAGAN_BESVARATS, LANDSTING);
        }

    }

    @Override
    public void notifieraVardenhetTilldeladUtredning(Utredning utredning, InternForfragan tillDeladInternForfragan, String landstingNamn) {
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(
                tillDeladInternForfragan.getVardenhetHsaId(), VG);

        if (preferens.isEnabled(UTREDNING_TILLDELAD, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(tillDeladInternForfragan.getVardenhetHsaId(), utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        vardenhetTilldeladUtredning(utredning, landstingNamn),
                        utredningUrl(utredning));

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
        throw new NotImplementedException();
    }

    @Override
    public void notifieraLandstingIngenBestallning(final Utredning utredning, final InternForfragan internForfragan) {
        final GetNotificationPreferenceResponse preferens =
                notifieringPreferenceService.getNotificationPreference(internForfragan.getVardenhetHsaId(), VE);

        if (preferens.isEnabled(INGEN_BESTALLNING, LANDSTING)) {
            String email = preferens.getLandstingEpost();
            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    ingenBestallningMessage(utredning),
                    utredningUrl(utredning));
            sendNotifiering(email, SUBJECT_INGEN_BESTALLNING, body, utredning.getUtredningId());
            saveNotifiering(utredning, INGEN_BESTALLNING, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetIngenBestallning(final Utredning utredning, final InternForfragan internForfragan) {
        final GetNotificationPreferenceResponse preferens =
                notifieringPreferenceService.getNotificationPreference(internForfragan.getVardenhetHsaId(), VE);

        if (preferens.isEnabled(INGEN_BESTALLNING, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(internForfragan.getVardenhetHsaId(), utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        ingenBestallningMessage(utredning),
                        internForfraganUrl(utredning));

                sendNotifiering(email, SUBJECT_INGEN_BESTALLNING, body, utredning.getUtredningId());
                saveNotifiering(utredning, INGEN_BESTALLNING, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetNyBestallning(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, NY_BESTALLNING);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(NY_BESTALLNING, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        nyBestallningMessage(utredning),
                        utredningUrl(utredning));

                sendNotifiering(email, SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING, body, utredning.getUtredningId());
                saveNotifiering(utredning, NY_BESTALLNING, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvslutadPgaJav(final Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, UTREDNING_AVSLUTAD_PGA_JAV);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VG);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_JAV, LANDSTING)) {
            String email = preferens.getLandstingEpost();
            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    avslutadPgaJavMessage(utredning),
                    utredningUrl(utredning));
            sendNotifiering(email, SUBJECT_AVSLUTAD_PGA_JAV, body, utredning.getUtredningId());
            saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_JAV, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetAvslutadPgaJav(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, UTREDNING_AVSLUTAD_PGA_JAV);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_JAV, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        avslutadPgaJavMessage(utredning),
                        utredningUrl(utredning));
                sendNotifiering(email, SUBJECT_AVSLUTAD_PGA_JAV, body, utredning.getUtredningId());
                saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_JAV, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetUppdateradBestallning(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, UPPDATERAD_BESTALLNING);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(UPPDATERAD_BESTALLNING, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        uppdateradBestallningMessage(utredning),
                        utredningUrl(utredning));

                sendNotifiering(email, SUBJECT_BESTALLNING_UPPDATERAD, body, utredning.getUtredningId());
                saveNotifiering(utredning, UPPDATERAD_BESTALLNING, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvvikelseRapporteradAvVarden(Utredning utredning, Besok besok) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, AVVIKELSE_RAPPORTERAD_AV_VARDEN);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(AVVIKELSE_RAPPORTERAD_AV_VARDEN, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    avvikelseRapporteradAvVardenMessage(utredning, besok),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_AVVIKELSE_RAPPORTERAD_AV_VARDEN, body, utredning.getUtredningId());
            saveNotifiering(utredning, AVVIKELSE_RAPPORTERAD_AV_VARDEN, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetAvvikelseMottagenFranFK(Utredning utredning, Besok besok) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, AVVIKELSE_MOTTAGEN_AV_FK);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(AVVIKELSE_MOTTAGEN_AV_FK, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        vardenhetAvvikelseRapporteradAvFKMessage(utredning, besok),
                        utredningUrl(utredning));

                sendNotifiering(email, SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK, body, utredning.getUtredningId());
                saveNotifiering(utredning, AVVIKELSE_MOTTAGEN_AV_FK, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvvikelseMottagenFranFK(Utredning utredning, Besok besok) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, AVVIKELSE_MOTTAGEN_AV_FK);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(AVVIKELSE_MOTTAGEN_AV_FK, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingAvvikelseRapporteradAvFKMessage(utredning, besok),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK, body, utredning.getUtredningId());
            saveNotifiering(utredning, AVVIKELSE_MOTTAGEN_AV_FK, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetAvslutadUtredning(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, UTREDNING_AVSLUTAD_PGA_AVBRUTEN);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_AVBRUTEN, VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        vardenhetAvslutadUtredningMessage(utredning),
                        utredningUrl(utredning));

                sendNotifiering(email, SUBJECT_AVSLUTAD_UTREDNING, body, utredning.getUtredningId());
                saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_AVBRUTEN, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingAvslutadUtredning(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, UTREDNING_AVSLUTAD_PGA_AVBRUTEN);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(UTREDNING_AVSLUTAD_PGA_AVBRUTEN, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingAvslutadUtredningMessage(utredning),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_AVSLUTAD_UTREDNING, body, utredning.getUtredningId());
            saveNotifiering(utredning, UTREDNING_AVSLUTAD_PGA_AVBRUTEN, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetPaminnelseSlutdatumUtredning(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(PAMINNELSEDATUM_KOMPLETTERING_PASSERAS, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        paminnelseSlutdatumUtredningMessage(utredning),
                        utredningUrl(utredning));

                sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PAMINNELSE, body, utredning.getUtredningId());
                saveNotifiering(utredning, PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraVardenhetSlutdatumPasseratUtredning(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, SLUTDATUM_UTREDNING_PASSERAT);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(SLUTDATUM_UTREDNING_PASSERAT, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> {
                String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        slutdatumPasseratUtredningMessage(utredning),
                        utredningUrl(utredning));

                sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PASSERAT, body, utredning.getUtredningId());
                saveNotifiering(utredning, SLUTDATUM_UTREDNING_PASSERAT, VARDENHET);
            });
        }
    }

    @Override
    public void notifieraLandstingSlutdatumPasseratUtredning(Utredning utredning) {
        String vardgivareHsaId = utredning.getExternForfragan().get().getLandstingHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardgivareHsaId, VG);

        if (preferens.isEnabled(SLUTDATUM_UTREDNING_PASSERAT, NotifieringMottagarTyp.LANDSTING)) {
            String email = preferens.getLandstingEpost();
            if (Strings.isNullOrEmpty(email)) {
                return;
            }

            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    slutdatumPasseratUtredningMessage(utredning),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PASSERAT, body, utredning.getUtredningId());
            saveNotifiering(utredning, SLUTDATUM_UTREDNING_PASSERAT, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetKompletteringBegard(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetPaminnelseSlutdatumKomplettering(Utredning utredning, List<Long> intygIds) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, SLUTDATUM_UTREDNING_PASSERAT);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(PAMINNELSEDATUM_KOMPLETTERING_PASSERAS, NotifieringMottagarTyp.VARDENHET)) {
            epostResolver.resolveVardenhetNotifieringEpost(id, utredning).ifPresent(email -> utredning.getIntygList().stream()
                    .filter(intyg -> intygIds.contains(intyg.getId()))
                    .forEach(intyg -> {
                        String body = notifieringMailBodyFactory.buildBodyForUtredning(
                                paminnelseSlutDatumKomplettering(utredning, intyg),
                                utredningUrl(utredning));
                        sendNotifiering(email, SUBJECT_PAMINNELSE_SLUTDATUM_KOMPLETTERING, body, utredning.getUtredningId());
                        updateUtredningWithNotifiering(utredning, intyg.getId(), PAMINNELSEDATUM_KOMPLETTERING_PASSERAS, VARDENHET);
                    }));
            utredningRepository.saveUtredning(utredning);
        }
    }

    @Override
    public void notifieraVardenhetSlutdatumPasseratKomplettering(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraLandstingSlutdatumPasseratKomplettering(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetRedovisaBesok(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, PAMINNELSE_REDOVISA_BESOK);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(PAMINNELSE_REDOVISA_BESOK, VARDENHET)) {
            final String email = vardenhetService.getVardEnhetPreference(id).getEpost();
            if (isNotEmpty(email)) {
                final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                        paminnelseRedovisaBesok(utredning),
                        utredningUrl(utredning));

                sendNotifiering(email, SUBJECT_REDOVISA_BESOK, body, utredning.getUtredningId());
                saveNotifiering(utredning, PAMINNELSE_REDOVISA_BESOK, VARDENHET);
            }
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

    private void sendNotifiering(String emailAddress, String subject, String body, Long utredningId) {
        try {
            mailService.sendNotificationToUnit(emailAddress, subject, body);
            LOG.info(MessageFormat.format("Sent notification {0} for utredning {1}.", subject, utredningId));
        } catch (MessagingException e) {
            LOG.error(MessageFormat.format("Error sending notification by email: {0}", e.getMessage()));
        }
    }

    private Bestallning verifyHasBestallningAndGet(final Utredning utredning, final NotifieringTyp notifieringTyp) {
        return utredning.getBestallning().orElseThrow(() -> new IbServiceException(
                IbErrorCodeEnum.BAD_STATE,
                MessageFormat.format("Cannot send notification for {0} when there is no Bestallning.", notifieringTyp.getId())));
    }
}
