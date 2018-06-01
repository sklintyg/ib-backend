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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by marced on 2016-02-09.
 */
@Controller
@RequestMapping("/maillink")
public class MailIntegrationController {

    private static final Logger LOG = LoggerFactory.getLogger(MailIntegrationController.class);

    @GetMapping(path = "/{utredningId}")
    public ResponseEntity<String> testGetUtredning(@PathVariable("utredningId")  String utredningId) {
        LOG.info("ENTER - MailIntegrationController test method. I got " + utredningId + " as requested id.");
        return ResponseEntity.ok("Requested " + utredningId);
    }

}
