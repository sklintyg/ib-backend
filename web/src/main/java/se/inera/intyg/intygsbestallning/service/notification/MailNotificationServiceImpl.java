package se.inera.intyg.intygsbestallning.service.notification;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.VardenhetPreferenceRepository;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

@Service
public class MailNotificationServiceImpl implements MailNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(MailNotificationServiceImpl.class);

    @Value("${mail.admin}")
    private String adminMailAddress;

    @Value("${mail.from}")
    private String fromAddress;

    @Value("${mail.ib.host.url}")
    private String ibHostUrl;

    @Autowired
    private VardenhetPreferenceRepository vardenhetPreferenceRepository;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageFactory messageFactory;

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

        String body = messageFactory.buildBodyForUtredning(
                "Försäkringskassan har skickat en beställning av en Försäkringsmedicinsk utredning (FMU) för utredning "
                        + utredning.getUtredningId(),
                "<URL to utredning>");
        try {
            sendNotificationToUnit(email, subject, body);
        } catch (MessagingException e) {
            LOG.error("Error sending notification by email: {}", e.getMessage());
        }
    }

    private String findEmailAddressForVardenhet(String vardenhetHsaId) {
        Optional<VardenhetPreference> preferenceOptional = vardenhetPreferenceRepository.findByVardenhetHsaId(vardenhetHsaId);

        if (!preferenceOptional.isPresent()) {
            // Must try to fetch from HSA?
            Vardenhet vardenhet = hsaOrganizationsService.getVardenhet(vardenhetHsaId);
            return vardenhet.getEpost();
        } else {
            return preferenceOptional.get().getEpost();
        }
    }

    private void sendNotificationToUnit(String mailAddress, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(fromAddress));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress));

        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");
        mailSender.send(message);
    }
}
