package se.inera.intyg.intygsbestallning.service.utredning;

import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentType;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;

import java.util.List;

@Service
public class UtredningServiceImpl implements UtredningService {
    @Override public Utredning registerNewUtredning(RequestHealthcarePerformerForAssessmentType req) {
        return null;
    }

    @Override public List<UtredningListItem> findUtredningarByVardgivareHsaId(String vardgivareHsaId) {
        return null;
    }

    @Override public GetUtredningResponse getUtredning(String utredningId, String vardgivareHsaId) {
        return null;
    }

    @Override public List<ForfraganListItem> findForfragningarForVardenhetHsaId(String vardenhetHsaId) {
        return null;
    }

    @Override public GetForfraganResponse getForfragan(Long forfraganId, String vardenhetHsaId) {
        return null;
    }

    @Override public Utredning registerOrder(OrderMedicalAssessmentType order) {
        return null;
    }
}
