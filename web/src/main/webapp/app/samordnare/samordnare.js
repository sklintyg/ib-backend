
angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.samordnare', {
        abstract: true, // jshint ignore:line
        url: '/samordnare',
        views: {
            'header@': {
                templateUrl: '/app/header/header.html',
                controller: 'HeaderController'
            }
        }
    });

});
