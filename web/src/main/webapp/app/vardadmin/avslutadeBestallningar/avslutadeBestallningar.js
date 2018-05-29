angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.vardadmin.avslutadeBestallningar', {
        url: '/avslutadeBestallningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/vardadmin/avslutadeBestallningar/avslutadeBestallningar.html',
                controller: 'AvslutadeBestallningarCtrl'
            }

        }
    });
});
