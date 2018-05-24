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
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.VardenhetPreferenceRepository;
import se.inera.intyg.intygsbestallning.service.mail.MailService;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class MailNotificationServiceImpl implements MailNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(MailNotificationServiceImpl.class);

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
        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, "Cannot send notification for mottagen handling when "
                    + "there is no Bestallning.");
        }

        String vardenhetHsaId = utredning.getBestallning().get().getTilldeladVardenhetHsaId();
        if (Strings.isNullOrEmpty(vardenhetHsaId)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, "Cannot send notification for mottagen handling when "
                    + "the Bestallning contains no vardenhetHsaId.");
        }

        String email = findEmailAddressForVardenhet(vardenhetHsaId);
        String subject = "Beställning av Försäkringsmedicinsk utredning";

        String body = mailNotificationBodyFactory.buildBodyForUtredning(
                "Försäkringskassan har skickat en beställning av en Försäkringsmedicinsk utredning (FMU) för utredning "
                        + utredning.getUtredningId(),
                "<URL to utredning>");
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
