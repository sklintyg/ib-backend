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
package se.inera.intyg.intygsbestallning.service.notification;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.VardenhetPreferenceRepository;
import se.inera.intyg.intygsbestallning.service.mail.MailService;

import javax.mail.MessagingException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

@Service
public class MailNotificationServiceImpl implements MailNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(MailNotificationServiceImpl.class);
    private static final String SUBJECT_BESTALLNING_UPPDATERAD = "Försäkringskassan har uppdaterat en beställning";
    private static final String SUBJECT_HANDLING_MOTTAGEN = "Beställning av Försäkringsmedicinsk utredning";
    private static final String SUBJECT_UTREDNING_SLUTDATUM_PA_VAG_PASSERAS = "Påminnelse: Slutdatum för en utredning "
            + "är på väg att passeras";

    @Value("${mail.ib.host.url}")
    private String ibHostUrl;

    @Autowired
    private VardenhetPreferenceRepository vardenhetPreferenceRepository;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private MailNotificationBodyFactory mailNotificationBodyFactory;

    // Should pick implementation automatically (stub or real) depending on profile settings.
    @Autowired
    private MailService mailService;

    @Override
    public void notifyHandlingMottagen(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for mottagen handling when  there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for mottagen handling when "
                + "the Bestallning contains no vardenhetHsaId.");

        String email = findEmailAddressForVardenhet(vardenhetHsaId);
        String body = mailNotificationBodyFactory.buildBodyForUtredning(
                "Försäkringskassan har skickat en beställning av en Försäkringsmedicinsk utredning (FMU) för utredning "
                        + utredning.getUtredningId(),
                "<URL to utredning>");

        send(email, SUBJECT_HANDLING_MOTTAGEN, body);
    }

    @Override
    public void notifyBestallningUppdaterad(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for uppdaterad utredning when "
                + "there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for uppdaterad utredning when "
                + "the Bestallning contains no vardenhetHsaId.");

        String email = findEmailAddressForVardenhet(vardenhetHsaId);
        String body = mailNotificationBodyFactory.buildBodyForUtredning(
                "Försäkringskassan har uppdaterad beställningen av utredningen <utredning-id> med ny information.",
                "<URL to utredning>");

        send(email, SUBJECT_BESTALLNING_UPPDATERAD, body);
    }

    @Override
    public void notifySlutdatumPaVagPasseras(Utredning utredning) {
        verifyHasBestallning(utredning, "Cannot send notification for slutdatum pa vag passeras when "
                + "there is no Bestallning.");

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        verifyBestallningHasVardenhet(vardenhetHsaId, "Cannot send notification for uppdaterad utredning when "
                + "the Bestallning contains no vardenhetHsaId.");

        String email = findEmailAddressForVardenhet(vardenhetHsaId);

        // Find the last sistaDatum on an intyg on the Utredning.
        Optional<Intyg> sistaDatumOpt = utredning.getIntygList().stream().filter(intyg -> intyg.getSistaDatum() != null)
                .max(Comparator.comparing(Intyg::getSistaDatum));

        // This should never happen...
        if (!sistaDatumOpt.isPresent()) {
            throw new IllegalStateException("Unable to send slutdatum på väg passeras notification, no intyg on Utredning "
                    + "has a sista datum.");
        }

        String sistaDatumForMottagning = sistaDatumOpt.get().getSistaDatum().format(DateTimeFormatter.ISO_DATE);
        String body = mailNotificationBodyFactory.buildBodyForUtredning(
                "Slutdatum " + sistaDatumForMottagning + " för utredning " + utredning.getUtredningId() + " kommer "
                        + "snart att passeras. Om utlåtandet inte är mottaget av Försäkringskassan innan angivet slutdatum så "
                        + "kommer utredningen inte att ersättas.",
                "<URL to utredning>");
        send(email, SUBJECT_UTREDNING_SLUTDATUM_PA_VAG_PASSERAS, body);
    }

    private void verifyHasBestallning(Utredning utredning, String errorMessage) {
        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, errorMessage);
        }
    }

    private void verifyBestallningHasVardenhet(String vardenhetHsaId, String errorMessage) {
        if (Strings.isNullOrEmpty(vardenhetHsaId)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, errorMessage);
        }
    }

    private void send(String email, String subject, String body) {
        try {
            mailService.sendNotificationToUnit(email, subject, body);
        } catch (MessagingException e) {
            LOG.error("Error sending notification by email: {}", e.getMessage());
        }
    }

    private String findEmailAddressForVardenhet(String vardenhetHsaId) {
        Optional<VardenhetPreference> preferenceOptional = vardenhetPreferenceRepository.findByVardenhetHsaId(vardenhetHsaId);

        if (!preferenceOptional.isPresent()) {
            // Must try to fetch from HSA?
            try {
                Vardenhet vardenhet = hsaOrganizationsService.getVardenhet(vardenhetHsaId);
                return vardenhet.getEpost();
            } catch (Exception e) {
                throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                        "Unable to send notification email, email address for " + vardenhetHsaId + " could not be found "
                                + "in VardenhetPreference nor HSA.");
            }
        } else {
            return preferenceOptional.get().getEpost();
        }
    }
}
