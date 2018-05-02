angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.vardadmin.pagaendeUtredningar', {
        url: '/pagaendeUtredningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/vardadmin/pagaendeUtredningar/pagaendeUtredningar.html',
                controller: 'PagaendeUtredningarCtrl'
            }
        }
    });

});
