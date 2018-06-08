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
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType.VE;
import static se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType.VG;
import static se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering.SkickadNotifieringBuilder.aSkickadNotifiering;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp.LANDSTING;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp.VARDENHET;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.AVVIKELSE_MOTTAGEN_AV_FK;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.AVVIKELSE_RAPPORTERAD_AV_VARDEN;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SAMTLIGA_INTERNFORFRAGAN_BESVARATS;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT;
import static se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp.UPPDATERAD_BESTALLNING;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.avvikelseRapporteradAvVardenMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.externForfraganUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.internForfraganUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingAvvikelseRapporteradAvFKMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingNyExternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.landstingSamtligaInternForfraganBesvaradeforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.nyBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.paminnelseSlutdatumUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.slutdatumPasseratUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.uppdateradBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.utredningUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.vardenhetAvvikelseRapporteradAvFKMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.vardenhetNyInternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailMeddelandeUtil.vardenhetTilldeladUtredning;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_AVVIKELSE_RAPPORTERAD_AV_VARDEN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_BESTALLNING_UPPDATERAD;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_FMU_UTREDNING_TILLDELAD_VARDENHETEN;
import static se.inera.intyg.intygsbestallning.service.notifiering.util.NotifieringMailSubjectConstants.SUBJECT_NY_FMU_EXTERN_FORFRAGAN;
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
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.mail.MailService;
import se.inera.intyg.intygsbestallning.service.notifiering.preferens.NotifieringPreferenceService;
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

    @Override
    public void notifieraLandstingNyExternforfragan(Utredning utredning) {
        checkState(nonNull(utredning.getExternForfragan()));
        checkState(nonNull(utredning.getExternForfragan().getLandstingHsaId()));

        final String vardenhetHsaId = utredning.getExternForfragan().getLandstingHsaId();
        final String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
        final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                landstingNyExternforfraganMessage(),
                externForfraganUrl(utredning));

        sendNotifiering(email, SUBJECT_NY_FMU_EXTERN_FORFRAGAN, body);
    }

    @Override
    public void notifieraVardenhetNyInternforfragan(Utredning utredning) {
        checkState(nonNull(utredning.getExternForfragan()));
        checkState(isNotEmpty(utredning.getExternForfragan().getInternForfraganList()));
        checkState(nonNull(utredning.getExternForfragan().getInternForfraganList().get(0).getForfraganSvar()));
        checkState(nonNull(utredning.getExternForfragan().getInternForfraganList().get(0).getForfraganSvar().getUtforareEpost()));

        final InternForfragan internForfragan = utredning.getExternForfragan().getInternForfraganList().get(0);
        final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                vardenhetNyInternforfraganMessage(internForfragan),
                internForfraganUrl(utredning));

        sendNotifiering(internForfragan.getForfraganSvar().getUtforareEpost(), SUBJECT_NY_FMU_EXTERN_FORFRAGAN, body);
    }

    @Override
    public void notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(Utredning utredning) {
        checkState(nonNull(utredning.getExternForfragan()));
        checkState(nonNull(utredning.getExternForfragan().getLandstingHsaId()));

        final String landstingsHsaId = utredning.getExternForfragan().getLandstingHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(landstingsHsaId, VG);
        if (preferens.isEnabled(SAMTLIGA_INTERNFORFRAGAN_BESVARATS, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingSamtligaInternForfraganBesvaradeforfraganMessage(utredning),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_SAMTLIGA_INTERNFORFRAGAN_BESVARATS, body);
        }

    }

    @Override
    public void notifieraVardenhetTilldeladUtredning(Utredning utredning, InternForfragan tillDeladInternForfragan, String landstingNamn) {

        final String utforareEpost = tillDeladInternForfragan.getForfraganSvar().getUtforareEpost();

        if (!Strings.isNullOrEmpty(utforareEpost)
                && notifieringPreferenceService.getNotificationPreference(tillDeladInternForfragan.getVardenhetHsaId(), VE)
                        .isEnabled(NotifieringTyp.UTREDNING_TILLDELAD, NotifieringMottagarTyp.VARDENHET)) {
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    vardenhetTilldeladUtredning(utredning, landstingNamn),
                    utredningUrl(utredning));

            sendNotifiering(utforareEpost, SUBJECT_FMU_UTREDNING_TILLDELAD_VARDENHETEN, body);
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
    public void notifieraLandstingIngenBestallning(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetIngenBestallning(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetNyBestallning(Utredning utredning) {
        checkState(utredning.getBestallning().isPresent(), "Cannot send notification for mottagen handling when  there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for mottagen handling when "
                + "the Bestallning contains no vardenhetHsaId.");
        String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
        String body = notifieringMailBodyFactory.buildBodyForUtredning(
                nyBestallningMessage(utredning),
                utredningUrl(utredning));

        sendNotifiering(email, SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING, body);
    }

    @Override
    public void notifieraLandstingAvslutadPgaJav(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetUppdateradBestallning(Utredning utredning) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, UPPDATERAD_BESTALLNING);
        String vardenhetHsaId = bestallning.getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for uppdaterad utredning when "
                + "the Bestallning contains no vardenhetHsaId.");

        String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
        String body = notifieringMailBodyFactory.buildBodyForUtredning(
                uppdateradBestallningMessage(utredning),
                utredningUrl(utredning));

        sendNotifiering(email, SUBJECT_BESTALLNING_UPPDATERAD, body);
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

            sendNotifiering(email, SUBJECT_AVVIKELSE_RAPPORTERAD_AV_VARDEN, body);
            saveNotifiering(utredning, AVVIKELSE_RAPPORTERAD_AV_VARDEN, LANDSTING);
        }
    }

    @Override
    public void notifieraVardenhetAvvikelseMottagenFranFK(Utredning utredning, Besok besok) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, AVVIKELSE_RAPPORTERAD_AV_VARDEN);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(AVVIKELSE_MOTTAGEN_AV_FK, VARDENHET)) {
            final String email = vardenhetService.getVardEnhetPreference(id).getEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    vardenhetAvvikelseRapporteradAvFKMessage(utredning, besok),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK, body);
            saveNotifiering(utredning, AVVIKELSE_MOTTAGEN_AV_FK, VARDENHET);
        }
    }

    @Override
    public void notifieraLandstingAvvikelseMottagenFranFK(Utredning utredning, Besok besok) {
        final Bestallning bestallning = verifyHasBestallningAndGet(utredning, AVVIKELSE_RAPPORTERAD_AV_VARDEN);
        final String id = bestallning.getTilldeladVardenhetHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(id, VE);

        if (preferens.isEnabled(AVVIKELSE_MOTTAGEN_AV_FK, LANDSTING)) {
            final String email = preferens.getLandstingEpost();
            final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    landstingAvvikelseRapporteradAvFKMessage(utredning, besok),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK, body);
            saveNotifiering(utredning, AVVIKELSE_MOTTAGEN_AV_FK, VARDENHET);
        }
    }

    @Override
    public void notifieraVardenhetAvslutadUtredning(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraLandstingAvslutadUtredning(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetPaminnelseSlutdatumUtredning(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for slutdatum pa vag passeras when "
                + "there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for slutdatum pa vag passeras "
                + "when the Bestallning contains no vardenhetHsaId.");

        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetHsaId, VE);

        if (preferens.isEnabled(NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT, NotifieringMottagarTyp.VARDENHET)) {
            String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
            if (Strings.isNullOrEmpty(email)) {
                return;
            }

            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    paminnelseSlutdatumUtredningMessage(utredning),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PAMINNELSE, body);
            saveNotifiering(utredning, PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS, VARDENHET);

            LOG.info("Sent notification {} for utredning {}.", PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS, utredning.getUtredningId());
        }
    }

    @Override
    public void notifieraVardenhetSlutdatumPasseratUtredning(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for slutdatum passerat when "
                + "there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for slutdatum passerat when "
                + "the Bestallning contains no vardenhetHsaId.");

        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardenhetHsaId, VE);

        if (preferens.isEnabled(NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT, NotifieringMottagarTyp.VARDENHET)) {
            String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
            if (Strings.isNullOrEmpty(email)) {
                return;
            }

            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    slutdatumPasseratUtredningMessage(utredning),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PASSERAT, body);
            saveNotifiering(utredning, SLUTDATUM_UTREDNING_PASSERAT, VARDENHET);

            LOG.info("Sent notification {} for utredning {}.", SLUTDATUM_UTREDNING_PASSERAT, utredning.getUtredningId());
        }
    }

    @Override
    public void notifieraLandstingSlutdatumPasseratUtredning(Utredning utredning) {
        String vardgivareHsaId = utredning.getExternForfragan().getLandstingHsaId();
        final GetNotificationPreferenceResponse preferens = notifieringPreferenceService.getNotificationPreference(vardgivareHsaId, VG);

        if (preferens.isEnabled(NotifieringTyp.SLUTDATUM_UTREDNING_PASSERAT, NotifieringMottagarTyp.LANDSTING)) {
            String email = preferens.getLandstingEpost();
            if (Strings.isNullOrEmpty(email)) {
                return;
            }

            String body = notifieringMailBodyFactory.buildBodyForUtredning(
                    slutdatumPasseratUtredningMessage(utredning),
                    utredningUrl(utredning));

            sendNotifiering(email, SUBJECT_UTREDNING_SLUTDATUM_PASSERAT, body);
            saveNotifiering(utredning, SLUTDATUM_UTREDNING_PASSERAT, LANDSTING);

            LOG.info("Sent notification {} for utredning {}.", SLUTDATUM_UTREDNING_PASSERAT, utredning.getUtredningId());
        }
    }

    @Override
    public void notifieraVardenhetKompletteringBegard(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetPaminnelseSlutdatumKomplettering(Utredning utredning) {
        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }

    private void saveNotifiering(
            final Utredning utredning,
            final NotifieringTyp typ,
            final NotifieringMottagarTyp mottagare) {

        if (mottagare == NotifieringMottagarTyp.ALL) {
            throw new IllegalArgumentException("Do not save a sent notification for mottagare ALL");
        }

        utredning.getSkickadNotifieringList().add(aSkickadNotifiering()
                .withSkickad(LocalDateTime.now())
                .withTyp(typ)
                .withMottagare(mottagare)
                .build());

        utredningRepository.save(utredning);
    }

    private void sendNotifiering(String emailAddress, String subject, String body) {
        try {
            mailService.sendNotificationToUnit(emailAddress, subject, body);
        } catch (MessagingException e) {
            LOG.error("Error sending notification by email: {}", e.getMessage());
        }
    }

    private void verifyBestallningHasVardenhet(String vardenhetHsaId, String errorMessage) {
        if (Strings.isNullOrEmpty(vardenhetHsaId)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, errorMessage);
        }
    }

    private void verifyHasBestallning(Utredning utredning, String errorMessage) {
        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, errorMessage);
        }
    }

    private Bestallning verifyHasBestallningAndGet(final Utredning utredning, final NotifieringTyp notifieringTyp) {
        return utredning.getBestallning().orElseThrow(() -> new IbServiceException(
                IbErrorCodeEnum.BAD_STATE,
                MessageFormat.format("Cannot send notification for {0} when there is no Bestallning.", notifieringTyp.getId())));
    }
}
