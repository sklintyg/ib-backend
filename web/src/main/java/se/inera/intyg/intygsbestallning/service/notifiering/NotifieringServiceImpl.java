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
package se.inera.intyg.intygsbestallning.service.notifiering;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.externForfraganUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.internForfraganUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.landstingNyExternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.nyBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.paminnelseSlutdatumUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.slutdatumPasseratUtredningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.uppdateradBestallningMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.utredningUrl;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailMeddelandeUtil.vardenhetNyInternforfraganMessage;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailSubjectConstants.SUBJECT_BESTALLNING_UPPDATERAD;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailSubjectConstants.SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailSubjectConstants.SUBJECT_NY_FMU_EXTERN_FORFRAGAN;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailSubjectConstants.SUBJECT_UTREDNING_SLUTDATUM_PASSERAT;
import static se.inera.intyg.intygsbestallning.service.notifiering.NotifieringMailSubjectConstants.SUBJECT_UTREDNING_SLUTDATUM_PA_VAG_PASSERAS;

import com.google.common.base.Strings;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import java.lang.invoke.MethodHandles;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.mail.MailService;
import se.inera.intyg.intygsbestallning.service.vardenhet.VardenhetService;

@Service
public class NotifieringServiceImpl implements NotifieringService {

    private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private MailService mailService;

    @Autowired
    private VardenhetService vardenhetService;

    @Autowired
    private NotifieringMailBodyFactory notifieringMailBodyFactory;

    @Override
    public void notifieraLandstingNyExternforfragan(Utredning utredning) {
        checkState(nonNull(utredning.getExternForfragan()));
        checkState(nonNull(utredning.getExternForfragan().getLandstingHsaId()));

        final String vardenhetHsaId = utredning.getExternForfragan().getLandstingHsaId();
        final String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
        final String body = notifieringMailBodyFactory.buildBodyForUtredning(
                landstingNyExternforfraganMessage(),
                externForfraganUrl(utredning));

        send(email, SUBJECT_NY_FMU_EXTERN_FORFRAGAN, body);
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

        send(internForfragan.getForfraganSvar().getUtforareEpost(), SUBJECT_NY_FMU_EXTERN_FORFRAGAN, body);
    }

    @Override
    public void notifieraLandstingSamtligaVardenheterHarSvaratPaInternforfragan(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetTilldeladUtredning(Utredning utredning) {
        throw new NotImplementedException();
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

        send(email, SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING, body);
    }

    @Override
    public void notifieraLandstingAvslutadPgaJav(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetUppdateradBestallning(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for uppdaterad utredning when "
                + "there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for uppdaterad utredning when "
                + "the Bestallning contains no vardenhetHsaId.");

        String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
        String body = notifieringMailBodyFactory.buildBodyForUtredning(
                uppdateradBestallningMessage(utredning),
                utredningUrl(utredning));

        send(email, SUBJECT_BESTALLNING_UPPDATERAD, body);
    }

    @Override
    public void notifieraLandstingAvvikelseRapporteradAvVarden(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraVardenhetAvvikelseMottagenFranFK(Utredning utredning) {
        throw new NotImplementedException();
    }

    @Override
    public void notifieraLandstingAvvikelseMottagenFranFK(Utredning utredning) {
        throw new NotImplementedException();
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
    public void notifieraVardenehtPaminnelseSlutdatumUtredning(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for slutdatum pa vag passeras when "
                + "there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for slutdatum pa vag passeras "
                + "when the Bestallning contains no vardenhetHsaId.");

        String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
        String body = notifieringMailBodyFactory.buildBodyForUtredning(
                paminnelseSlutdatumUtredningMessage(utredning),
                utredningUrl(utredning));

        send(email, SUBJECT_UTREDNING_SLUTDATUM_PA_VAG_PASSERAS, body);
    }

    @Override
    public void notifieraVardenhetSlutdatumPasseratUtredning(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for slutdatum passerat when "
                + "there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for slutdatum passerat when "
                + "the Bestallning contains no vardenhetHsaId.");

        String email = vardenhetService.getVardEnhetPreference(vardenhetHsaId).getEpost();
        String body = notifieringMailBodyFactory.buildBodyForUtredning(
                slutdatumPasseratUtredningMessage(utredning),
                utredningUrl(utredning));

        send(email, SUBJECT_UTREDNING_SLUTDATUM_PASSERAT, body);
    }

    @Override
    public void notifieraLandstingSlutdatumPasseratUtredning(Utredning utredning) {
        throw new NotImplementedException();
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

    private void send(String emailAddress, String subject, String body) {
        try {
            mailService.sendNotificationToUnit(emailAddress, subject, body);
        } catch (MessagingException e) {
            log.error("Error sending notification by email: {}", e.getMessage());
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
}
