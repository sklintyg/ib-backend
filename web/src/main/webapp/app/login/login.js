angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.login', {
        url: '/login',
        views: {
            'navbar@': {},
            'app@': {
                templateUrl: '/app/login/login.body.html'
            }

        }
    });

});
