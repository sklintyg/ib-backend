package se.inera.intyg.intygsbestallning.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.intygsbestallning.auth.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetBestallningListResponse;

import java.util.List;

@RestController
@RequestMapping("/api/vardadmin/bestallningar")
public class BestallningController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtredningService utredningService;

    private AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetBestallningListResponse> getBestallningarForVardenhet() {
        IbUser user = userService.getUser();
        authoritiesValidator.given(user).privilege(AuthoritiesConstants.PRIVILEGE_LISTA_BESTALLNINGAR)
                .orThrow(new IbAuthorizationException("User does not have required privilege LISTA_BESTALLNINGAR"));

        if (user.getCurrentlyLoggedInAt().getType() != SelectableHsaEntityType.VE) {
            throw new IbAuthorizationException("User is not logged in at a Vårdenhet");
        }

        List<BestallningListItem> bestallningar = utredningService.findOngoingBestallningarForVardenhet(user.getCurrentlyLoggedInAt().getId());
        return ResponseEntity.ok(new GetBestallningListResponse(bestallningar, bestallningar.size()));
    }
}
