
angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.vardadmin', {
        abstract: true, // jshint ignore:line
        url: '/vardadmin',
        views: {
            'header@': {
                templateUrl: '/app/header/header.html',
                controller: 'HeaderController'
            },
            'footer@': {}
        }
    });

});
