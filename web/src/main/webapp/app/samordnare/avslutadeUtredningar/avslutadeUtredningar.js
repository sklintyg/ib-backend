angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.samordnare.avslutadeUtredningar', {
        url: '/avslutadeUtredningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/samordnare/avslutadeUtredningar/avslutadeUtredningar.html',
                controller: 'AvslutadeUtredningarCtrl'
            }

        }
    });

});
