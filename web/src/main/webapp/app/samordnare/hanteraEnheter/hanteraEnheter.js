angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.samordnare.hanteraEnheter', {
        url: '/hanteraEnheter',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'app@': {
                templateUrl: '/app/samordnare/hanteraEnheter/hanteraEnheter.html',
                controller: 'HanteraEnheterCtrl'
            }

        }
    });

});
