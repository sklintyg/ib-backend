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
package se.inera.intyg.intygsbestallning.monitoring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable the use of {@link PrometheusTimeMethod} annotation on classes or methods.
 *
 * Usage: Add this annotation to any Spring {@link org.springframework.context.annotation.Configuration} class to enable
 * the use of the {@link PrometheusTimeMethod} annotation.
 *
 * <pre>
 * <code>
 * {@literal @}Configuration
 * {@literal @}EnablePrometheusEndpoint
 * {@literal @}EnablePrometheusTiming
 *  public class MyAppConfig {
 *    // Other configuration items...
 *  }
 * </code>
 * </pre>
 *
 * @author Andrew Stuart
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MethodTimer.class)
@Documented
public @interface EnablePrometheusTiming {
}
