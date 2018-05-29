angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.vardadmin.pagaendeBestallningar', {
        url: '/pagaendeBestallningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/vardadmin/pagaendeBestallningar/pagaendeBestallningar.html',
                controller: 'PagaendeBestallningarCtrl'
            }
        }
    });

});
