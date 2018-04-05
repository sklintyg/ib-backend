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
package se.inera.intyg.intygsbestallning.service.pdl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityPurpose;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.Enhet;
import se.inera.intyg.infra.logmessages.Patient;
import se.inera.intyg.infra.logmessages.PdlLogMessage;
import se.inera.intyg.infra.logmessages.PdlResource;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.model.IbSelectableHsaEntity;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.service.pdl.dto.LogUser;
import se.inera.intyg.intygsbestallning.web.model.PatientData;
import se.inera.intyg.intygsbestallning.web.model.SjukfallEnhet;
import se.inera.intyg.intygsbestallning.web.model.SjukfallPatient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2016-03-03.
 */
@Service
public class PdlLogMessageFactoryImpl implements PdlLogMessageFactory {

    private static final String PDL_TITEL_FMU_VARDADMIN = "FMU Vårdadministratör";
    private static final String PDL_TITEL_FMU_SAMORDNARE = "FMU Samordnare";

    @Value("${pdlLogging.systemId}")
    private String systemId;

    @Value("${pdlLogging.systemName}")
    private String systemName;

    @Override
    public PdlLogMessage buildLogMessage(List<SjukfallEnhet> sjukfallList,
                                         ActivityType activityType,
                                         ResourceType resourceType,
                                         IbUser ibUser) {

        LogUser user = getLogUser(ibUser);

        PdlLogMessage pdlLogMessage = getLogMessage(activityType);
        populateWithCurrentUserAndCareUnit(pdlLogMessage, user);

        // Add resources
        pdlLogMessage.getPdlResourceList().addAll(
                sjukfallList.stream()
                        .map(sf -> buildPdlLogResource(sf, resourceType, user))
                        .collect(Collectors.toList()));

        return pdlLogMessage;
    }

    @Override
    public PdlLogMessage buildLogMessage(SjukfallPatient sjukfallPatient,
                                         ActivityType activityType,
                                         ResourceType resourceType,
                                         IbUser ibUser) {

        LogUser user = getLogUser(ibUser);

        PdlLogMessage pdlLogMessage = getLogMessage(activityType);
        populateWithCurrentUserAndCareUnit(pdlLogMessage, user);

        // Add single resource
        pdlLogMessage.getPdlResourceList().add(buildPdlLogResource(sjukfallPatient, resourceType));

        return pdlLogMessage;
    }

    private PdlResource buildPdlLogResource(SjukfallEnhet sfe, ResourceType resourceType, LogUser user) {
        PdlResource pdlResource = new PdlResource();
        pdlResource.setPatient(getPatient(sfe));
        pdlResource.setResourceOwner(getEnhet(sfe, user));
        pdlResource.setResourceType(resourceType.getResourceTypeName());

        return pdlResource;
    }

    private PdlResource buildPdlLogResource(SjukfallPatient sjp, ResourceType resourceType) {
        // Get log info from first certificate in list
        PatientData first = sjp.getIntyg().get(0);

        PdlResource pdlResource = new PdlResource();
        pdlResource.setPatient(getPatient(first));
        pdlResource.setResourceOwner(getEnhet(first));
        pdlResource.setResourceType(resourceType.getResourceTypeName());

        return pdlResource;
    }

    private Enhet getEnhet(SjukfallEnhet sfe, LogUser user) {
        String vardenhetId = getConditionalValue(sfe.getVardEnhetId(), user.getEnhetsId());
        String vardenhetNamn = getConditionalValue(sfe.getVardEnhetNamn(), user.getEnhetsNamn());
        String vardgivareId = getConditionalValue(sfe.getVardGivareId(), user.getVardgivareId());
        String vardgivareNamn = getConditionalValue(sfe.getVardGivareNamn(), user.getVardgivareNamn());

        return new Enhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);
    }

    private String getConditionalValue(String preferred, String alternative) {
        return preferred != null ? preferred : alternative;
    }

    private Patient getPatient(SjukfallEnhet sfe) {
        return new Patient(
            sfe.getPatient().getId().replace("-", "").replace("+", ""),
            "");
    }

    private Enhet getEnhet(PatientData pd) {
        return new Enhet(pd.getVardenhetId(), pd.getVardenhetNamn(),
            pd.getVardgivareId(), pd.getVardgivareNamn());
    }

    private Patient getPatient(PatientData pd) {
        return new Patient(
            pd.getPatient().getId().replace("-", "").replace("+", ""),
            "");
    }

    private PdlLogMessage getLogMessage(ActivityType activityType) {
        PdlLogMessage pdlLogMessage = new PdlLogMessage(ActivityType.READ, ActivityPurpose.CARE_TREATMENT);
        pdlLogMessage.setSystemId(systemId);
        pdlLogMessage.setSystemName(systemName);
        return pdlLogMessage;
    }

    private LogUser getLogUser(IbUser user) {
        IbSelectableHsaEntity loggedInAt = user.getCurrentlyLoggedInAt();

        if (loggedInAt.getType() == SelectableHsaEntityType.VE) {
            IbVardenhet ve = (IbVardenhet) loggedInAt;
            return new LogUser.Builder(user.getHsaId(), ve.getId(), ve.getParentId())
                    .userName(user.getNamn())
                    .userAssignment(user.getSelectedMedarbetarUppdragNamn())
                    .userTitle(resolveUserTitle(user))
                    .enhetsNamn(ve.getName())
                    .vardgivareNamn(ve.getParentName())
                    .build();
        } else {
            return new LogUser.Builder(user.getHsaId(), null, loggedInAt.getId())
                    .userName(user.getNamn())
                    .userAssignment(user.getSelectedMedarbetarUppdragNamn())
                    .userTitle(resolveUserTitle(user))
                    .enhetsNamn(null)
                    .vardgivareNamn(loggedInAt.getName())
                    .build();
        }


    }

    /**
     * LAKARE if user has LAKARE as current ROLE AND isLakare() is true.
     * REHABKOORDINATOR if user has REHABKOORDINATOR as current role and isLakare is true.
     * REHABKOORDINATOR if user has REHABKOORDINATOR as current role and isLakare is false.
     */
    private String resolveUserTitle(IbUser user) {
        return user.getRoles().containsKey(AuthoritiesConstants.ROLE_FMU_VARDADMIN)
                ? PDL_TITEL_FMU_VARDADMIN : PDL_TITEL_FMU_SAMORDNARE;
    }

    private void populateWithCurrentUserAndCareUnit(PdlLogMessage logMsg, LogUser user) {
        logMsg.setUserId(user.getUserId());
        logMsg.setUserName(user.getUserName());
        logMsg.setUserAssignment(user.getUserAssignment());
        logMsg.setUserTitle(user.getUserTitle());

        Enhet vardenhet = new Enhet(user.getEnhetsId(), user.getEnhetsNamn(), user.getVardgivareId(), user.getVardgivareNamn());
        logMsg.setUserCareUnit(vardenhet);
    }

}
