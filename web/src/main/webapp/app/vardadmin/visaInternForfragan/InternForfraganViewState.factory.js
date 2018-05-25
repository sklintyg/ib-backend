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

angular.module('ibApp').factory('InternForfraganSvarViewState', function() {
    'use strict';
    var formCtrl;
    var model = {};
    var initialModel = {};
    var widgetState = {};

    function _reset(errorKey) {
        formCtrl = null;
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

    function _clearUtforare() {
        model.utforareNamn = undefined;
        model.utforareAdress = undefined;
        model.utforarePostnr = undefined;
        model.utforarePostort = undefined;
        model.utforareTelefon = undefined;
        model.utforareEpost = undefined;

    }

    function _revertModel() {
        widgetState.isReady = true;
        _setModel(initialModel);
    }

    function _setModel(svar) {
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

        //save a copy of the initial model to be able to restore it later.
        initialModel = angular.copy(model);

        widgetState.isReady = true;
    }
    function _getModel() {
        return model;
    }

    function _getWidgetState() {
        return widgetState;
    }
    function _setForm(formController) {
        formCtrl = formController;
    }

    function _isValidToSubmit() {
        return !model.svarTyp && (formCtrl && formCtrl.$valid);
    }

    return {
        reset: _reset,
        getModel: _getModel,
        setModel: _setModel,
        revertModel: _revertModel,
        clearUtforare: _clearUtforare,
        getWidgetState: _getWidgetState,
        setForm: _setForm,
        isValidToSubmit: _isValidToSubmit
    };
});
