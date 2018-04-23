angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.vardadmin.listaForfragningar', {
        url: '/listaForfragningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/vardadmin/listaForfragningar/listaForfragningar.html',
                controller: 'ListaForfragningarCtrl'
            }

        }
    });

});
