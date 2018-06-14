
angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.bpadmin', {
        abstract: true, // jshint ignore:line
        url: '/bpadmin'
    });

});
