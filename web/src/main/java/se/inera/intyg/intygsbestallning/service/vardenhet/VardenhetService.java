package se.inera.intyg.intygsbestallning.service.vardenhet;

import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceResponse;

/**
 * Created by marced on 2018-04-23.
 */
public interface VardenhetService {

    /**
     * Returns the latest vardenhetspreference.
     * If a preference for the hsaId is not already present in repository, it will fetch and return initial data
     * from HSA, so a result is guaranteed.
     *
     * @param hsaId
     *            id for the vardenhet to get preference for
     * @return latest or initial preference
     */
    VardenhetPreferenceResponse getVardEnhetPreference(String hsaId);

    VardenhetPreferenceResponse setVardEnhetPreference(String hsaId, VardenhetPreferenceRequest vardenhetPreferenceRequest);
}
