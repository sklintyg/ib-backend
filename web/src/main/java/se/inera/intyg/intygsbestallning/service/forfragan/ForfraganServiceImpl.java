package se.inera.intyg.intygsbestallning.service.forfragan;

import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarResponse;

@Service
public class ForfraganServiceImpl implements ForfraganService {

    @Override
    public ForfraganSvarResponse besvaraForfragan(Long forfraganId, ForfraganSvarRequest svarRequest) {
        return null;
    }
}
