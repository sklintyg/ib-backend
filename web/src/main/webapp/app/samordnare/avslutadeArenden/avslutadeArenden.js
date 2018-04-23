angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.samordnare.avslutadeArenden', {
        url: '/avslutadeArenden',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/samordnare/avslutadeArenden/avslutadeArenden.html',
                controller: 'AvslutadeArendenCtrl'
            }

        }
    });

});
