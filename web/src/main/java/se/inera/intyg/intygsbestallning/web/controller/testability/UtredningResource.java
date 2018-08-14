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
package se.inera.intyg.intygsbestallning.web.controller.testability;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.util.EntityTxMapper;

@RestController
@RequestMapping("/api/test/utredningar")
@Profile({ "dev", "testability-api" })
public class UtredningResource {

    @Autowired
    EntityTxMapper entityTxMapper;

    @Autowired
    UtredningRepository utredningRepository;

    @GetMapping(path = "/{utredningId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUtredning(@PathVariable("utredningId") Long utredningId) {
        return entityTxMapper.jsonResponseEntity(() ->
                utredningRepository.findById(utredningId).orElseThrow(
                        () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist.")));
    }

    @GetMapping(path = "/withstatus/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUtredningWithStatus(@PathVariable("status") String status) {
        return entityTxMapper.jsonResponseEntity(() ->
                utredningRepository.findByStatus(UtredningStatus.valueOf(status)));
    }

    @GetMapping(path = "/withfas/{fas}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUtredningInUtredningFas(@PathVariable("fas") String fas) {
        return entityTxMapper.jsonResponseEntity(() -> {
            UtredningFas utredningFas = UtredningFas.valueOf(fas);
            final List<UtredningStatus> statuses = Stream.of(UtredningStatus.values())
                    .filter(us -> us.getUtredningFas().equals(utredningFas))
                    .collect(Collectors.toList());
            return utredningRepository.findByStatusIn(statuses);
        });
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response createUtredning(@RequestBody Utredning utredning) {
        return entityTxMapper.jsonResponse(() -> utredningRepository.saveUtredning(utredning));
    }

    @DeleteMapping(path = "/{utredningId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteUtredning(@PathVariable("utredningId") Long utredningId) {
        return entityTxMapper.jsonResponse(() -> {
            Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                    () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));
            utredningRepository.delete(utredning);
            return EntityTxMapper.OK;
        });
    }

    @DeleteMapping(path = "/vardgivare/{landstingHsaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response deleteUtredningarForVardgivare(@PathVariable("landstingHsaId") String landstingHsaId) {
        return entityTxMapper.jsonResponse(() -> {
            utredningRepository.findAllByExternForfragan_LandstingHsaId(landstingHsaId)
                    .stream()
                    .forEach(utredning -> utredningRepository.delete(utredning));
            return EntityTxMapper.OK;
        });
    }
}
