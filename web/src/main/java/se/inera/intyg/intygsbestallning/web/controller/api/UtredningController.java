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
package se.inera.intyg.intygsbestallning.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.service.export.XlsxExportService;
import se.inera.intyg.intygsbestallning.service.forfragan.InternForfraganService;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.CreateInternForfraganRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.forfragan.TilldelaDirektRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListAvslutadeUtredningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.SaveBetalningForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.SaveUtbetalningForUtredningRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/samordnare/utredningar")
public class UtredningController {

    private static final String VIEW_NOT_ALLOWED = "User is not allowed to view the requested resource";
    private static final String EDIT_NOT_ALLOWED = "User is not allowed to edit the requested resource";

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningService utredningService;

    @Autowired
    private InternForfraganService internForfraganService;

    @Autowired
    private XlsxExportService xlsxExportService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @PrometheusTimeMethod
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningListResponse> getUtredningarForUser(@RequestBody ListUtredningRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));

        // Do a SAMORDNARE search...
        GetUtredningListResponse response = utredningService
                .findExternForfraganByLandstingHsaIdWithFilter(user.getCurrentlyLoggedInAt().getId(), req);

        return ResponseEntity.ok(response);
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/avslutade", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningListResponse> getAvslutadeUtredningarForUser(@RequestBody ListAvslutadeUtredningarRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));

        // Do a SAMORDNARE search...
        GetUtredningListResponse response = utredningService
                .findAvslutadeExternForfraganByLandstingHsaIdWithFilter(user.getCurrentlyLoggedInAt().getId(), req);

        return ResponseEntity.ok(response);
    }

    @PrometheusTimeMethod
    @GetMapping(path = "/{utredningsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> getUtredning(@PathVariable("utredningsId") Long utredningsId) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));
        return ResponseEntity.ok(utredningService.getExternForfragan(utredningsId, user.getCurrentlyLoggedInAt().getId()));
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/{utredningsId}/createinternforfragan",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> createInternForfragan(@PathVariable("utredningsId") Long utredningsId,
                                                                      @RequestBody CreateInternForfraganRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));
        return ResponseEntity.ok(internForfraganService.createInternForfragan(utredningsId, user.getCurrentlyLoggedInAt().getId(), req));
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/{utredningsId}/tilldeladirekt",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUtredningResponse> createInternForfragan(@PathVariable("utredningsId") Long utredningsId,
                                                                      @RequestBody TilldelaDirektRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_VISA_UTREDNING)
                .orThrow(new IbAuthorizationException(EDIT_NOT_ALLOWED));
        return ResponseEntity.ok(internForfraganService.tilldelaDirekt(utredningsId, user.getCurrentlyLoggedInAt().getId(), req));
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/{utredningsId}/betald", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveBetalningsIdForUtredning(@PathVariable("utredningsId") Long utredningsId,
                                                         @RequestBody SaveBetalningForUtredningRequest request) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege PRIVILEGE_LISTA_UTREDNINGAR"));

        utredningService.saveBetalningsIdForUtredning(utredningsId, request, user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok().build();
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/{utredningsId}/utbetald", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveUtbetalningsIdForUtredning(@PathVariable("utredningsId") Long utredningsId,
                                                               @RequestBody SaveUtbetalningForUtredningRequest request) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege PRIVILEGE_LISTA_UTREDNINGAR"));

        utredningService.saveUtbetalningsIdForUtredning(utredningsId, request, user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok().build();
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/xlsx", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ByteArrayResource> excelReportUtredningar(@ModelAttribute ListUtredningRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));

        byte[] data = xlsxExportService.export(user.getCurrentlyLoggedInAt().getId(), req);

        HttpHeaders respHeaders = getHttpHeaders("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                data.length, ".xlsx", user);

        return new ResponseEntity<>(new ByteArrayResource(data), respHeaders, HttpStatus.OK);
    }

    @PrometheusTimeMethod
    @PostMapping(path = "/avslutade/xlsx", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ByteArrayResource> excelReportAvslutadeUtredningar(@ModelAttribute ListAvslutadeUtredningarRequest req) {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_UTREDNINGAR)
                .orThrow(new IbAuthorizationException(VIEW_NOT_ALLOWED));

        byte[] data = xlsxExportService.export(user.getCurrentlyLoggedInAt().getId(), req);

        HttpHeaders respHeaders = getHttpHeaders("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                data.length, ".xlsx", user);

        return new ResponseEntity<>(new ByteArrayResource(data), respHeaders, HttpStatus.OK);
    }

    private HttpHeaders getHttpHeaders(String contentType, long contentLength, String filenameExtension, IbUser user) {
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.set(HttpHeaders.CONTENT_TYPE, contentType);
        respHeaders.setContentLength(contentLength);
        respHeaders.setContentDispositionFormData("attachment", getAttachmentFilename(user, filenameExtension));
        return respHeaders;
    }

    private String getAttachmentFilename(IbUser user, String extension) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");
        return "utredningar-" + user.getCurrentlyLoggedInAt().getName() + "-" + LocalDateTime.now().format(dateTimeFormatter) + extension;
    }
}
