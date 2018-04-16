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
package se.inera.intyg.intygsbestallning.service.forfragan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Forfragan;
import se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar;
import se.inera.intyg.intygsbestallning.persistence.model.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.UtforareTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.ForfraganSvarRepository;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganSvarResponse;

@Service
public class ForfraganServiceImpl implements ForfraganService {

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private ForfraganSvarRepository forfraganSvarRepository;

    @Autowired
    private UserService userService;

    @Override
    public ForfraganSvarResponse besvaraForfragan(Long forfraganId, ForfraganSvarRequest svarRequest) {
        IbUser user = userService.getUser();
        Forfragan forfragan = utredningRepository.findForfraganByIdAndVardenhet(forfraganId, user.getCurrentlyLoggedInAt().getId());
        if (forfragan == null) {
            throw new IbServiceException(IbErrorCodeEnum.NOT_FOUND, "No Forfragan found on unit '"
                    + user.getCurrentlyLoggedInAt().getId() + "' for forfraganId " + forfraganId);
        }

        ForfraganSvar forfraganSvar = convert(svarRequest);
        forfraganSvar = forfraganSvarRepository.save(forfraganSvar);
        ForfraganSvarResponse response = new ForfraganSvarResponse();
        response.setInternreferens(forfraganSvar.getInternReferens());
        return response;
    }

    private ForfraganSvar convert(ForfraganSvarRequest request) {
        ForfraganSvar fs = new ForfraganSvar();
        fs.setForfraganId(request.getForfraganId());
        fs.setUtforareTyp(UtforareTyp.valueOf(request.getUtforareTyp()));
        fs.setUtforareNamn(request.getUtforareNamn());
        fs.setUtforareAdress(request.getUtforareAdress());
        fs.setUtforarePostnr(request.getUtforarePostnr());
        fs.setUtforarePostort(request.getUtforarePostort());
        fs.setUtforareTelefon(request.getUtforareTelefon());
        fs.setUtforareEpost(request.getUtforareEpost());
        fs.setSvarTyp(SvarTyp.valueOf(request.getSvarTyp()));
        fs.setKommentar(request.getKommentar());
        return fs;
    }
}
