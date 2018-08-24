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
package se.inera.intyg.intygsbestallning.service.sprak;

import static org.apache.commons.collections4.MapUtils.isEmpty;

import com.glaforge.i18n.io.CharsetToolkit;
import com.google.common.collect.Maps;
import io.vavr.control.Try;
import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.service.sprak.dto.TolkSprak;

@Component
public class TolkSprakLookup {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ResourceLoader resourceLoader;

    private Map<String, TolkSprak> sprakMap;

    @Value("${language.file.location}")
    private String fileLocation;

    public TolkSprakLookup(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    private Try<Void> init() {
        LOG.info("Getting available Language codes from Resource file");
        return Try.run(() -> {
            sprakMap = Maps.newHashMap();
            final Resource fileResource = resourceLoader.getResource(fileLocation);
            final Charset charset = CharsetToolkit.guessEncoding(fileResource.getFile(), 4096, StandardCharsets.UTF_8);
            final InputStreamReader inputStreamReader = new InputStreamReader(fileResource.getInputStream(), charset);

            CsvConfiguration config = new CsvConfiguration();
            config.setFieldDelimiter('\t');
            config.setUseDelimiterAfterLastField(true);
            config.setLineFilter(new HeaderAndFooterFilter(1, false, true));

            Deserializer deserializer = CsvIOFactory.createFactory(config, TolkSprak.class).createDeserializer();
            deserializer.open(inputStreamReader);
            while (deserializer.hasNext()) {
                final TolkSprak tolkSprak = deserializer.next();
                LOG.debug(MessageFormat.format("Tolkspråk: {0}, {1}", tolkSprak.getId(), tolkSprak.getRefName()));
                sprakMap.put(tolkSprak.getId(), tolkSprak);
            }
            deserializer.close(true);
            LOG.info("Language codes from Resource file is available for lookup");
        });
    }

    public TolkSprak lookupTolkSprak(final String sprakId) {

        if (isEmpty(sprakMap)) {
            Try<Void> init = init();
            if (init.isFailure()) {
                throw new RuntimeException(
                        MessageFormat.format("Could not load TolkSpråk from file: {0}, cause: {1}", fileLocation, init.getCause()));
            }
        }

        return Optional.ofNullable(sprakMap.get(Strings.toLowerCase(sprakId)))
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.BAD_REQUEST, MessageFormat.format("\"{0}\" is not a valid tolksprak-id", sprakId)));
    }
}
