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
package se.inera.intyg.intygsbestallning.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.common.integration.json.CustomObjectMapper;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@DependsOn("dbUpdate")
@Profile({ "dev", "ib-init-data" })
public class UtredningBootstrapBean {

    private static final Logger LOG = LoggerFactory.getLogger(UtredningBootstrapBean.class);

    private static final int EXTRA_TEST_DATA = 100;
    private static final int EXTRA_TEST_DATA_MINUS_DAYS = 10;

    @Autowired
    private UtredningRepository utredningRepository;

    @PostConstruct
    public void initData() {
        long count = utredningRepository.count();
        if (utredningRepository.count() > 0) {
            LOG.info(count + " utredningar already exists in DB - skipping bootstrap of utredningar");
        } else {
            List<Resource> files = getResourceListing("bootstrap-utredning/*.json");
            for (Resource res : files) {
                LOG.debug("Loading resource " + res.getFilename());
                addUtredning(res);
            }
        }

    }

    private void addUtredning(Resource res) {

        try {
            Utredning utredning = new CustomObjectMapper().readValue(res.getInputStream(), Utredning.class);
            utredningRepository.saveUtredning(utredning);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Resource> getResourceListing(String classpathResourcePath) {
        try {
            PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
            return Arrays.asList(r.getResources(classpathResourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
