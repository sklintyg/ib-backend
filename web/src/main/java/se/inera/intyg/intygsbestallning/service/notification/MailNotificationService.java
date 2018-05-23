package se.inera.intyg.intygsbestallning.service.notification;

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

public interface MailNotificationService {
    void notifyHandlingMottagen(Utredning utredning);
}
