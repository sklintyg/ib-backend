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
package se.inera.intyg.intygsbestallning.service.pdl.dto;

import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;

public enum PdlLogType {
    UTREDNING_VISAD_I_LISTA("Utredning visad i lista", ActivityType.READ, ResourceType.RESOURCE_TYPE_FMU_OVERSIKT),
    UTREDNING_LAST("Utredning läst", ActivityType.READ, ResourceType.RESOURCE_TYPE_FMU),
    UTREDNING_UPPDATERAD("Utredning uppdaterad", ActivityType.UPDATE, ResourceType.RESOURCE_TYPE_FMU),
    BESOK_SKAPAT("Besök skapat", ActivityType.CREATE, ResourceType.RESOURCE_TYPE_FMU_BESOK),
    BESOK_ANDRAT("Besök ändrat", ActivityType.UPDATE, ResourceType.RESOURCE_TYPE_FMU_BESOK),
    BESOK_AVBOKAT("Besök avbokat", ActivityType.UPDATE, ResourceType.RESOURCE_TYPE_FMU_BESOK),
    BESOK_REDOVISAT("Besök redovisat", ActivityType.UPDATE, ResourceType.RESOURCE_TYPE_FMU_BESOK),
    AVVIKELSE_RAPPORTERAD("Avvikelse rapporterad", ActivityType.UPDATE, ResourceType.RESOURCE_TYPE_FMU_AVVIKELSE),
    TOLK_REDOVISAD("Tolk redovisad", ActivityType.UPDATE, ResourceType.RESOURCE_TYPE_FMU_TOLK),
    ANTECKNING_SKAPAD("Anteckning skapad", ActivityType.CREATE, ResourceType.RESOURCE_TYPE_FMU_ANTECKNING);

    private final String id;
    private final String activityArgs;
    private final ActivityType activityType;
    private final ResourceType resourceType;

    PdlLogType(String activityArgs, ActivityType activityType, ResourceType resourceType) {
        this.id = this.name();
        this.activityArgs = activityArgs;
        this.activityType = activityType;
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public String getActivityArgs() {
        return activityArgs;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}
