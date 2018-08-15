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
package se.inera.intyg.intygsbestallning.web.controller.integration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.InternForfraganRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.user.UserService;

/**
 * Created by marced on 2018-08-14.
 */
@Transactional(readOnly = true)
@Controller
@RequestMapping("/maillink")
public class MailIntegrationController {

    public static final String ERROR_LINK_ENITY_NOT_FOUND = "mail.link.entity.not.found";
    public static final String ERROR_NO_HSA_AUTH = "mail.no.hsa.auth";
    private static final Logger LOG = LoggerFactory.getLogger(MailIntegrationController.class);
    @Autowired
    private MaillinkRedirectUrlBuilder maillinkRedirectUrlBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private InternForfraganRepository internForfraganRepository;

    @Autowired
    private UtredningRepository utredningRepository;

    @GetMapping(path = "/internforfragan/{internforfraganId}")
    public String vardadminInternforfraganRedirect(@PathVariable("internforfraganId") String internforfraganId) {
        LOG.debug("ENTER - vardadminInternforfraganRedirect for internforfraganId " + internforfraganId);
        final IbUser user = userService.getUser();

        // 1. An internforfragan should exist for the given internforfraganId
        InternForfragan internForfragan = getInternforfragan(internforfraganId);
        if (internForfragan == null) {
            return errorViewRedirect(user, ERROR_LINK_ENITY_NOT_FOUND);
        }

        // 2. Must be able to select internforfragan vardenhet hsaId as current enhet, i.e is authorized to view this vardenhet.
        if (!user.changeValdVardenhet(internForfragan.getVardenhetHsaId())) {
            return errorViewRedirect(user, ERROR_NO_HSA_AUTH);
        }

        return maillinkRedirectUrlBuilder.buildVardadminInternforfraganRedirect(internforfraganId);
    }

    @GetMapping(path = "/bestallning/{utredningId}")
    public String vardadminBestallningRedirect(@PathVariable("utredningId") String utredningId) {
        LOG.debug("ENTER - vardadminBestallningRedirect for utredningId " + utredningId);

        final IbUser user = userService.getUser();

        // 1. An utredning with bestallning should exist for the given utredningId
        Utredning utredning = getUtredning(utredningId);
        if (utredning == null || !utredning.getBestallning().isPresent()) {
            return errorViewRedirect(user, ERROR_LINK_ENITY_NOT_FOUND);
        }

        // 2. Must be able to select tilldelad vardenhet hsaId as current enhet, i.e is authorized to view this.
        if (!user.changeValdVardenhet(utredning.getBestallning().get().getTilldeladVardenhetHsaId())) {
            return errorViewRedirect(user, ERROR_NO_HSA_AUTH);
        }

        return maillinkRedirectUrlBuilder.buildVardadminBestallningRedirect(utredningId);
    }

    @GetMapping(path = "/externforfragan/{utredningId}")
    public String samordnareExternforfraganRedirect(@PathVariable("utredningId") String utredningId) {
        LOG.debug("ENTER - samordnareExternforfraganRedirect for utredningId " + utredningId);
        return handleSamordnarRedirect(utredningId);
    }

    @GetMapping(path = "/utredning/{utredningId}")
    public String samordnareUtredningRedirect(@PathVariable("utredningId") String utredningId) {
        LOG.debug("ENTER - samordnareUtredningRedirect for utredningId " + utredningId);
        return handleSamordnarRedirect(utredningId);
    }

    private String handleSamordnarRedirect(String utredningId) {
        final IbUser user = userService.getUser();

        // 1. An utredning with an externforfragan should exist for the given utredningId
        Utredning utredning = getUtredning(utredningId);
        if (utredning == null || !utredning.getExternForfragan().isPresent()) {
            return errorViewRedirect(user, ERROR_LINK_ENITY_NOT_FOUND);
        }

        // 2. Must be able to select externforfragans landstinghsaId as current enhet, i.e is authorized to view this.
        if (!user.changeValdVardenhet(utredning.getExternForfragan().get().getLandstingHsaId())) {
            return errorViewRedirect(user, ERROR_NO_HSA_AUTH);
        }
        return maillinkRedirectUrlBuilder.buildSamordnareUtredningRedirect(utredningId);
    }

    private String errorViewRedirect(IbUser user, String reason) {
        LOG.info("Mail link for user " + user.getHsaId() + " resulted in error " + reason);
        return maillinkRedirectUrlBuilder.buildErrorRedirect(reason);
    }

    private InternForfragan getInternforfragan(String internforfraganId) {
        if (StringUtils.isNumeric(internforfraganId)) {
            return internForfraganRepository.findById(Long.parseLong(internforfraganId)).orElse(null);
        }
        return null;
    }

    private Utredning getUtredning(String utredningId) {
        if (StringUtils.isNumeric(utredningId)) {
            return utredningRepository.findById(Long.parseLong(utredningId)).orElse(null);
        }
        return null;
    }

}
