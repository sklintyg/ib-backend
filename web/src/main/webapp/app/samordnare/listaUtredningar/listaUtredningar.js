angular.module('ibApp').config(function($stateProvider) {
    'use strict';
    $stateProvider.state('app.samordnare.listaUtredningar', {
        url: '/listaUtredningar',
        views: {
            'navbar@': {
                template: '<ib-main-menu/>'
            },
            'content@app': {
                templateUrl: '/app/samordnare/listaUtredningar/listaUtredningar.html',
                controller: 'ListaUtredningarCtrl'
            }

        }
    });

});
