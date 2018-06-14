angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.bpadmin.aktivaBestallningar', {
        url: '/aktivaBestallningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/bpadmin/aktivaBestallningar/aktivaBestallningar.html',
                controller: 'aktivaBestallningarCtrl'
            }

        }
    });
});
