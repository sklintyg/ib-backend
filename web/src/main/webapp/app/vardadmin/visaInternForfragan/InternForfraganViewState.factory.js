/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

angular.module('ibApp').factory(
        'InternForfraganSvarViewState',
        function() {
            'use strict';

            var model = {};
            var widgetState = {};

            function _reset(errorKey) {
                widgetState = {};
                widgetState.errorKey = errorKey;

                model.forfraganId = undefined;
                model.svarTyp = undefined;
                model.utforareTyp = undefined;
                model.utforareNamn = undefined;
                model.utforareAdress = undefined;
                model.utforarePostnr = undefined;
                model.utforarePostort = undefined;
                model.utforareTelefon = undefined;
                model.utforareEpost = undefined;
                model.kommentar = undefined;
                model.borjaDatum = undefined;

            }

            function _resetFromPreference(forfraganId, pref) {
                widgetState.isReady = true;

                model.forfraganId = forfraganId;
                model.svarTyp = undefined;
                model.utforareTyp = 'ENHET'; // default?
                model.utforareNamn = pref.mottagarNamn;
                model.utforareAdress = pref.adress;
                model.utforarePostnr = pref.postnummer;
                model.utforarePostort = pref.postort;
                model.utforareTelefon = pref.telefonnummer;
                model.utforareEpost = pref.epost;
                model.kommentar = pref.standardsvar;
                model.borjaDatum = '';
            }

            function _resetFromExisting(svar) {
                model.forfraganId = svar.forfraganId;
                model.svarTyp = svar.svarTyp;
                model.utforareTyp = svar.utforareTyp;
                model.utforareNamn = svar.utforareNamn;
                model.utforareAdress = svar.utforareAdress;
                model.utforarePostnr = svar.utforarePostnr;
                model.utforarePostort = svar.utforarePostort;
                model.utforareTelefon = svar.utforareTelefon;
                model.utforareEpost = svar.utforareEpost;
                model.kommentar = svar.kommentar;
                model.borjaDatum = svar.borjaDatum;
                widgetState.isReady = true;
            }
            function _getModel() {
                return model;
            }

            function _getWidgetState() {
                return widgetState;
            }

            function _isValidToSubmit() {
                return !angular.isDefined(model.svarTyp) && model.utforareTyp && model.utforareNamn && model.utforareAdress && model.utforarePostnr &&
                        model.utforarePostort;
            }

            return {
                reset: _reset,
                resetFromPreference: _resetFromPreference,
                resetFromExisting: _resetFromExisting,
                getModel: _getModel,
                getWidgetState: _getWidgetState,
                isValidToSubmit: _isValidToSubmit
            };
        });
