angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.login', {
        url: '/login',
        views: {
            'header@': {
                templateUrl: '/app/login/login.header.html'
            },
            'navbar@': {},
            'app@': {
                templateUrl: '/app/login/login.body.html'
            }

        }
    });

});
