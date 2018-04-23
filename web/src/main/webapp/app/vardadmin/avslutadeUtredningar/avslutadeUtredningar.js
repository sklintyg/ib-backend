angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.vardadmin.avslutadeUtredningar', {
        url: '/avslutadeUtredningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/vardadmin/avslutadeUtredningar/avslutadeUtredningar.html',
                controller: 'AvslutadeUtredningarCtrl'
            }

        }
    });

});
